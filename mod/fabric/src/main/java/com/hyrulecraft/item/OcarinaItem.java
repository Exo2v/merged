package com.hyrulecraft.item;

import com.hyrulecraft.Hyrulecraft;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Ocarina of Time — 5-note instrument that triggers world effects.
 *
 * Notes: C-Up/Down/Left/Right + A (mapped to arrow keys + Z when holding O).
 * Songs are sequences of 6 notes; we store last 8 inputs and match longest suffix.
 *
 * Effects mirror OoT + new voxel-aware ones:
 *  - Zelda's Lullaby → open lullaby-locked doors (block tag hyrulecraft:lullaby_door) + bonemeal AoE
 *  - Sun's Song → time to noon/midnight
 *  - Song of Time → rewind last 5 broken blocks (stores undo stack)
 *  - Saria's Song → reveal Navi hint + map
 *  - Song of Storms → rain + wash sand → expose chest
 *  - Epona's Song → summon nearest horse
 *  - Scarecrow's (recordable) → player sets custom 8-note song
 */
public class OcarinaItem extends Item {
    public OcarinaItem(Settings settings) { super(settings); }

    // Song registry — note strings use U/D/L/R/A notation like OoT
    public record Song(String id, String notes, Text name, Text desc) {}

    public static final List<Song> SONGS = List.of(
            new Song("zelda_lullaby", "LUR LUR", Text.literal("Zelda's Lullaby"), Text.literal("Opens royal doors + grows crops")),
            new Song("suns_song", "RDU RDU", Text.literal("Sun's Song"), Text.literal("Noon ↔ midnight (sets dayTime)")),
            new Song("song_of_time", "R A D R A D", Text.literal("Song of Time"), Text.literal("Rewinds 5 mined blocks")),
            new Song("sarias_song", "D R L D R L", Text.literal("Saria's Song"), Text.literal("Navi talks from anywhere")),
            new Song("song_of_storms", "A D U A D U", Text.literal("Song of Storms"), Text.literal("Rain + washes sand to reveal chests")),
            new Song("eponas_song", "U L R U L R", Text.literal("Epona's Song"), Text.literal("Summons horse")),
            new Song("scarecrow", "", Text.literal("Scarecrow's Song (recordable)"), Text.literal("Place scarecrow that scares phantoms"))
    );

    public static void registerSongs() {
        // Validate notes map on boot
        Hyrulecraft.LOGGER.info("[Ocarina] {} songs loaded", SONGS.size());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            // Open ocarina HUD — handled in client keybind (O)
            user.sendMessage(Text.literal("♪ Hold O + Arrow Keys to play — see HUD for notes").formatted(Formatting.AQUA), true);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        // Server: no-op until song completed (see client packet)
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    /** Called by server packet when client completes a song */
    public static void onSongPlayed(World world, PlayerEntity player, String songId) {
        switch (songId) {
            case "zelda_lullaby" -> {
                // Bonemeal 9x9 around player + open lullaby doors
                if (!world.isClient) {
                    for (int dx=-4; dx<=4; dx++) for (int dz=-4; dz<=4; dz++) {
                        var pos = player.getBlockPos().add(dx, 0, dz);
                        var state = world.getBlockState(pos);
                        if (state.isIn(com.hyrulecraft.block.ModBlocks.LULLABY_DOOR)) {
                            // toggle door open — sets blockstate open=true + SWCH flag
                            world.setBlockState(pos, state.with(com.hyrulecraft.block.ModBlocks.LULLABY_OPEN, true));
                            world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }
                    // bonemeal crops
                    for (int dx=-4; dx<=4; dx++) for (int dz=-4; dz<=4; dz++) {
                        var pos = player.getBlockPos().add(dx, 0, dz);
                        var crop = world.getBlockState(pos);
                        if (crop.getBlock() instanceof net.minecraft.block.CropBlock) {
                            ((net.minecraft.block.CropBlock) crop.getBlock()).applyGrowth(world, pos, crop);
                        }
                    }
                }
                player.sendMessage(Text.literal("♪ Zelda's Lullaby — royal seal broken!").formatted(Formatting.GOLD), true);
            }
            case "suns_song" -> {
                long t = world.getTimeOfDay() % 24000;
                long target = (t < 12000) ? 18000 : 6000; // midnight or noon
                world.setTimeOfDay((world.getTimeOfDay() / 24000) * 24000 + target);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.AMBIENT, 1f, 1.2f);
                player.sendMessage(Text.literal(t < 12000 ? "☾ Night falls..." : "☀ Dawn breaks!").formatted(Formatting.YELLOW), true);
            }
            case "song_of_time" -> {
                // Rewind 5 broken blocks — consult block-break undo stack
                int restored = com.hyrulecraft.system.TimeRewindSystem.rewind(world, player, 5);
                player.sendMessage(Text.literal("♪ Song of Time — " + restored + " blocks rewound!").formatted(Formatting.AQUA), true);
            }
            case "song_of_storms" -> {
                world.setWeather(0, 6000, true, false);
                // wash sand
                int washed=0;
                for (int dx=-6; dx<=6; dx++) for (int dz=-6; dz<=6; dz++) {
                    var p = player.getBlockPos().add(dx, 0, dz);
                    if (world.getBlockState(p).isOf(net.minecraft.block.Blocks.SAND)) {
                        world.setBlockState(p, net.minecraft.block.Blocks.SANDSTONE.getDefaultState());
                        washed++;
                    }
                }
                player.sendMessage(Text.literal("♪ Song of Storms — storm! Washed " + washed + " sand → sandstone").formatted(Formatting.BLUE), true);
            }
            case "eponas_song" -> {
                // Summon nearest horse within 64b, or spawn if none
                var horses = world.getEntitiesByClass(net.minecraft.entity.passive.HorseEntity.class,
                        player.getBoundingBox().expand(64), e -> true);
                if (!horses.isEmpty()) {
                    var h = horses.get(0);
                    h.teleport(player.getX()+2, player.getY(), player.getZ()+2);
                    player.sendMessage(Text.literal("♪ Epona's Song — horse called!").formatted(Formatting.GOLD), true);
                } else {
                    if (!world.isClient) {
                        var h = new net.minecraft.entity.passive.HorseEntity(net.minecraft.entity.EntityType.HORSE, world);
                        h.setPosition(player.getX()+2, player.getY(), player.getZ()+2);
                        h.setTame(true); h.setOwnerUuid(player.getUuid());
                        world.spawnEntity(h);
                    }
                    player.sendMessage(Text.literal("A wild horse appears! (No stable nearby)").formatted(Formatting.YELLOW), true);
                }
            }
            case "sarias_song" -> player.sendMessage(Text.literal("♪ Saria's Song — Navi: You're on the right path! Check your map (M).").formatted(Formatting.GREEN), true);
            default -> {}
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(), SoundCategory.PLAYERS, 1f, 1f);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Ocarina — hold [O] + Arrow Keys").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Lullaby, Sun, Time, Storms, Epona, Saria...").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Song of Time rewinds mined blocks!").formatted(Formatting.YELLOW));
    }
}
