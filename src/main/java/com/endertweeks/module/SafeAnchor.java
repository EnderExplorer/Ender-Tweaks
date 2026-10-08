package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Steps;
import com.endertweeks.core.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SafeAnchor extends Feature {
	private final Steps steps = new Steps();

	public SafeAnchor() {
		super("Safe Anchor", "Keybind: place + charge anchor, block with glowstone on the ground toward you, totem, detonate.", true);
	}

	@Override
	public void onTick() {
		steps.tick();
	}

	@Override
	public void onKey() {
		if (mc.player == null || mc.level == null || steps.busy()) return;
		if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
			Util.msg("Look at a block to place the anchor on");
			return;
		}

		int anchor = Util.findHotbar(Items.RESPAWN_ANCHOR);
		int glow = Util.findHotbar(Items.GLOWSTONE);
		boolean offhandTotem = mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);
		int totem = Util.findHotbar(Items.TOTEM_OF_UNDYING);
		if (totem < 0 && offhandTotem) totem = anchor;
		if (anchor < 0 || glow < 0 || totem < 0 || Util.countHotbar(Items.GLOWSTONE) < 2) {
			Util.msg("Need anchor, 2 glowstone and a totem (hotbar or offhand)");
			return;
		}

		final int fAnchor = anchor, fGlow = glow, fTotem = totem;
		final BlockPos against = hit.getBlockPos();
		final Direction face = hit.getDirection();
		final BlockPos anchorPos = against.relative(face);

		// glowstone goes next to the anchor on the side facing you, placed on the ground
		Vec3 toPlayer = mc.player.position().subtract(Vec3.atCenterOf(anchorPos));
		Direction side = Direction.getApproximateNearest(toPlayer.x, 0, toPlayer.z);
		final BlockPos shield = anchorPos.relative(side);

		steps.add(() -> { Util.select(fAnchor); Util.useOnBlock(against, face); });
		steps.add(() -> { Util.select(fGlow); Util.useOnBlock(anchorPos, Direction.UP); });
		steps.add(() -> Util.useOnBlock(shield.below(), Direction.UP));
		steps.add(() -> Util.select(fTotem));
		steps.add(() -> Util.useOnBlock(anchorPos, Direction.UP));
	}
}
