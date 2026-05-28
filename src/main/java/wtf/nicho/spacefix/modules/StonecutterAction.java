package wtf.nicho.spacefix.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wtf.nicho.spacefix.SpacebarAction;

public class StonecutterAction implements SpacebarAction {
    private static final int INV_START = 2;
    private static final int INV_END = 37;

    private Item lastMaterial;
    private int lastRecipeIndex = -1;

    @Override
    public boolean handles(Screen screen) {
        return screen instanceof StonecutterScreen;
    }

    @Override
    public void reset() {
        lastMaterial = null;
        lastRecipeIndex = -1;
    }

    @Override
    public void observe(Screen screen) {
        StonecutterMenu menu = ((StonecutterScreen) screen).getMenu();
        int selected = menu.getSelectedRecipeIndex();
        if (selected < 0) {
            return;
        }
        Slot inputSlot = menu.getSlot(StonecutterMenu.INPUT_SLOT);
        if (inputSlot.hasItem()) {
            lastMaterial = inputSlot.getItem().getItem();
            lastRecipeIndex = selected;
        }
    }

    @Override
    public boolean doOne(Minecraft mc, Screen screen) {
        if (mc.player == null || mc.gameMode == null) {
            return false;
        }

        StonecutterMenu menu = ((StonecutterScreen) screen).getMenu();
        int containerId = menu.containerId;
        Player player = mc.player;
        Slot inputSlot = menu.getSlot(StonecutterMenu.INPUT_SLOT);

        Item rememberedMaterial = lastMaterial;
        int rememberedRecipe = lastRecipeIndex;

        Item material = inputSlot.hasItem() ? inputSlot.getItem().getItem() : rememberedMaterial;
        if (material == null) {
            return false;
        }

        int capacity = Math.min(inputSlot.getMaxStackSize(), new ItemStack(material).getMaxStackSize());
        while (inputSlot.getItem().getCount() < capacity) {
            int source = findMaterialSlot(menu, material);
            if (source < 0) {
                break;
            }
            int before = inputSlot.getItem().getCount();
            mc.gameMode.handleInventoryMouseClick(containerId, source, 0, ClickType.QUICK_MOVE, player);
            if (inputSlot.getItem().getCount() == before) {
                break;
            }
        }

        if (!inputSlot.hasItem()) {
            return false;
        }
        Item actualMaterial = inputSlot.getItem().getItem();

        if (menu.getSelectedRecipeIndex() < 0) {
            if (rememberedRecipe < 0 || rememberedMaterial != actualMaterial) {
                return false;
            }
            menu.clickMenuButton(player, rememberedRecipe);
            mc.gameMode.handleInventoryButtonClick(containerId, rememberedRecipe);
        }

        int selected = menu.getSelectedRecipeIndex();
        if (selected < 0) {
            return false;
        }
        lastMaterial = actualMaterial;
        lastRecipeIndex = selected;

        if (!menu.getSlot(StonecutterMenu.RESULT_SLOT).hasItem()) {
            return false;
        }

        int before = inputSlot.getItem().getCount();
        mc.gameMode.handleInventoryMouseClick(containerId, StonecutterMenu.RESULT_SLOT, 0, ClickType.QUICK_MOVE, player);
        return inputSlot.getItem().getCount() != before;
    }

    private static int findMaterialSlot(StonecutterMenu menu, Item material) {
        for (int i = INV_START; i <= INV_END; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty() && stack.is(material)) {
                return i;
            }
        }
        return -1;
    }
}
