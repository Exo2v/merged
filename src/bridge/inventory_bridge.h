#pragma once
/**
 * inventory_bridge.h — Unified inventory (OoT gSaveContext.inventory + MC Inventory)
 */

#include <stdint.h>

#define MERGED_HOTBAR_SIZE 8
#define MERGED_MAIN_SIZE   27
#define MERGED_ARMOR_SIZE  4
#define MERGED_TOTAL (MERGED_HOTBAR_SIZE + MERGED_MAIN_SIZE + MERGED_ARMOR_SIZE + 2) // + offhand + ocarina

// Merged item — superset of both games' items
typedef struct MergedItem {
    int16_t ootItemId;   // ITEM_NONE if not an OoT item
    int16_t mcItemId;    // 0 if not an MC item
    int16_t count;
    int16_t maxStack;
    // OoT equipment flags: sword/shield/tunic/boots bits
    uint32_t equipMask;
    // For mapping OoT button → MC hotbar
    int preferredHotbarSlot; // 0-7 maps to C-buttons + dpad
} MergedItem;

typedef struct MergedInventory {
    MergedItem slots[MERGED_TOTAL];
    // OoT compatibility shims — these pointers alias slots[0..]
    // so legacy OoT code reading gSaveContext.inventory still works
    int cButtonSlots[3]; // indices into slots[]
    int dpadSlots[4];
    // MC compatibility
    int selectedHotbar; // 0-7
    // Ocarina quick slot always 40
    int ocarinaSlot;
} MergedInventory;

extern MergedInventory gMergedInventory;

// Translate legacy calls
void MergedInventory_Init(MergedInventory* inv);
bool MergedInventory_AddItem(int16_t ootId, int16_t mcId, int count);
bool MergedInventory_HasItem(int16_t ootId, int16_t mcId);
void MergedInventory_MapButtonToSlot(int ootButton, int slotIdx);

// Crafting — 3x3 grid that can produce either game's items
typedef struct CraftingGrid {
    int16_t items[9]; // ootItemId or mcItemId depending on context
    int16_t counts[9];
} CraftingGrid;

int16_t MergedInventory_TryCraft(CraftingGrid* grid); // returns oot or mc id, or -1
