package com.endertweeks.core;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class Util {
	private Util() {}

	private static Minecraft mc() { return Minecraft.getInstance(); }

	public static void msg(String text) {
		if (mc().player != null) mc().player.displayClientMessage(Component.literal("[Ender Tweeks] " + text), true);
	}

	public static int findHotbar(Predicate<ItemStack> test) {
		for (int i = 0; i < 9; i++) {
			if (test.test(mc().player.getInventory().getItem(i))) return i;
		}
		return -1;
	}

	public static int findHotbar(Item item) {
		return findHotbar(s -> s.is(item));
	}

	public static int countHotbar(Item item) {
		int n = 0;
		for (int i = 0; i < 9; i++) {
			ItemStack s = mc().player.getInventory().getItem(i);
			if (s.is(item)) n += s.getCount();
		}
		return n;
	}

	public static void select(int slot) {
		if (slot >= 0 && slot < 9) mc().player.getInventory().setSelectedSlot(slot);
	}

	public static int selected() {
		return mc().player.getInventory().getSelectedSlot();
	}

	/** Right-click on a block face (places the held block next to it, or interacts with it). */
	public static void useOnBlock(BlockPos pos, Direction face) {
		Minecraft mc = mc();
		Vec3 loc = Vec3.atCenterOf(pos).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
		BlockHitResult hit = new BlockHitResult(loc, face, pos, false);
		mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
		mc.player.swing(InteractionHand.MAIN_HAND);
	}

	/** The player under the crosshair, ignoring friends, yourself and spectators. */
	public static Player crosshairPlayer() {
		Minecraft mc = mc();
		if (mc.hitResult instanceof EntityHitResult r) {
			Entity e = r.getEntity();
			if (e instanceof Player p && p != mc.player && p.isAlive() && !p.isSpectator() && !Friends.is(p)) return p;
		}
		return null;
	}
}
