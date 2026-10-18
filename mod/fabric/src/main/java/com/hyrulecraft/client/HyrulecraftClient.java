package com.hyrulecraft.client;

import com.hyrulecraft.Hyrulecraft;
import com.hyrulecraft.item.ModItems;
import com.hyrulecraft.system.ZTargetingSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

/**
 * Client entry — keybinds + HUD + Navi hints.
 *
 * Keys: Z=Z-target, R=hookshot (or use item), F=spin, O=ocarina, N=Navi hint
 */
public class HyrulecraftClient implements ClientModInitializer {
    private static KeyBinding ZTARGET, HOOKSHOT, OCARINA, NAVI;
    private static int ocarinaHoldTicks = 0;
    private static final StringBuilder ocarinaBuffer = new StringBuilder();

    @Override
    public void onInitializeClient() {
        ZTARGET = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.hyrulecraft.ztarget", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.hyrulecraft"));
        HOOKSHOT = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.hyrulecraft.hookshot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.hyrulecraft"));
        OCARINA = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.hyrulecraft.ocarina", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.hyrulecraft"));
        NAVI = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.hyrulecraft.navi", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "category.hyrulecraft"));

        ZTargetingSystem.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (ZTARGET.wasPressed()) ZTargetingSystem.toggle(client);
            while (HOOKSHOT.wasPressed()) {
                // Trigger hookshot use if held
                var hand = client.player.getMainHandStack();
                if (hand.isOf(ModItems.HOOKSHOT) || hand.isOf(ModItems.LONGSHOT)) {
                    client.interactionManager.interactItem(client.player, client.player.getActiveHand());
                } else {
                    client.player.sendMessage(Text.literal("Equip Hookshot first! (craft: chain×2 + iron + hook + redstone)").formatted(Formatting.GRAY), true);
                }
            }
            while (OCARINA.wasPressed()) {
                client.player.sendMessage(Text.literal("♪ Ocarina — hold O + arrows for notes, release to play").formatted(Formatting.AQUA), true);
            }
            // Navi hint
            while (NAVI.wasPressed()) {
                client.player.sendMessage(Text.literal("Navi: Hey! Look for cracked stone — bomb or spin it! Try the Deku Tree stump.").formatted(Formatting.GREEN), false);
            }
            // Ocarina note buffer (O held) — simplified: when O down, arrows append
            boolean oDown = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_O);
            if (oDown) {
                ocarinaHoldTicks++;
                // check arrow taps
                if (client.options.forwardKey.wasPressed()) appendNote("U");
                if (client.options.backKey.wasPressed()) appendNote("D");
                if (client.options.leftKey.wasPressed()) appendNote("L");
                if (client.options.rightKey.wasPressed()) appendNote("R");
                if (ZTARGET.wasPressed()) appendNote("A"); // Z as A button
            } else if (ocarinaHoldTicks > 0 && ocarinaBuffer.length() >= 6) {
                // try match song
                String seq = ocarinaBuffer.toString();
                tryPlaySong(client, seq);
                ocarinaBuffer.setLength(0);
                ocarinaHoldTicks = 0;
            } else if (!oDown && ocarinaHoldTicks > 18) {
                // released without full song
                ocarinaBuffer.setLength(0);
                ocarinaHoldTicks = 0;
            }
        });

        HudRenderCallback.EVENT.register(this::renderHud);
        Hyrulecraft.LOGGER.info("[Hyrulecraft:client] Keybinds registered");
    }

    private void appendNote(String n) {
        if (ocarinaBuffer.length() < 8) ocarinaBuffer.append(n).append(" ");
        if (ocarinaBuffer.length() >= 12) { // 6 notes
            // play tick
        }
    }

    private void tryPlaySong(net.minecraft.client.MinecraftClient client, String seq) {
        seq = seq.trim().replace(" ", "");
        // normalize spaces for our song defs ("LUR LUR" -> "LURLUR")
        for (var song : com.hyrulecraft.item.OcarinaItem.SONGS) {
            String notes = song.notes().replace(" ", "");
            if (notes.equalsIgnoreCase(seq) || seq.endsWith(notes)) {
                client.player.sendMessage(Text.literal("♪ " + song.name().getString() + " — " + song.desc().getString()).formatted(Formatting.GOLD), false);
                // send packet to server to apply effect
                // In singleplayer, directly call
                if (client.player.getWorld() != null) {
                    com.hyrulecraft.item.OcarinaItem.onSongPlayed(client.player.getWorld(), client.player, song.id());
                }
                return;
            }
        }
        client.player.sendMessage(Text.literal("♫ Wrong notes (" + seq + ") — try again!").formatted(Formatting.GRAY), true);
    }

    private void renderHud(DrawContext drawContext, float tickDelta) {
        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        int w = drawContext.getScaledWindowWidth(), h = drawContext.getScaledWindowHeight();

        // Z-target reticle
        if (ZTargetingSystem.isLocked()) {
            var t = ZTargetingSystem.getTarget();
            if (t != null) {
                String label = "Z-target: " + t.getName().getString();
                drawContext.drawTextWithShadow(client.textRenderer, Text.literal("◉ " + label).formatted(Formatting.GOLD), w/2 - 46, h/2 - 28, 0xffffff);
                // draw diamond reticle (text-based for now — replace with texture)
                drawContext.drawTextWithShadow(client.textRenderer, "◆", w/2 - 3, h/2 - 4, 0xFFD700);
            }
        }
        // Navi orb hint near hotbar
        drawContext.drawTextWithShadow(client.textRenderer, Text.literal("Navi [N] ").formatted(Formatting.AQUA), 8, h - 20, 0xffffff);

        // Ocarina buffer
        if (ocarinaBuffer.length() > 0) {
            drawContext.fill(w/2 - 60, h - 36, w/2 + 60, h - 20, 0x88000000);
            drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("♪ " + ocarinaBuffer).formatted(Formatting.YELLOW), w/2, h - 30, 0xffffff);
        }
    }
}
