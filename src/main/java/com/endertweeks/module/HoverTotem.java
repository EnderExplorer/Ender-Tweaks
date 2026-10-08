package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.mixin.AbstractContainerScreenAccessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;

public class HoverTotem extends Feature {
	private int cooldown;

	public HoverTotem() {
		super("Hover Totem", "Hover a totem in your inventory with an empty offhand to move it there.", false);
	}

	@Override
	public void onTick() {
		if (cooldown > 0) { cooldown--; return; }
		if (mc.player == null || !(mc.screen instanceof AbstractContainerScreen<?> screen)) return;
		if (!mc.player.getOffhandItem().isEmpty()) return;

		Slot slot = ((AbstractContainerScreenAccessor) screen).endertweeks$getHoveredSlot();
		if (slot == null || !slot.getItem().is(Items.TOTEM_OF_UNDYING)) return;

		// button 40 + SWAP = swap with the offhand
		mc.gameMode.handleInventoryMouseClick(screen.getMenu().containerId, slot.index, 40, ClickType.SWAP, mc.player);
		cooldown = 3;
	}
}
