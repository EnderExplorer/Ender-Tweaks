package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Steps;
import com.endertweeks.core.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class AnchorHelper extends Feature {
	private final Steps steps = new Steps();

	public AnchorHelper() {
		super("Anchor Helper", "Keybind: place anchor, charge with 1 glowstone, switch to totem, detonate.", true);
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
		if (anchor < 0 || glow < 0 || totem < 0) {
			Util.msg("Need respawn anchor, glowstone and a totem (hotbar or offhand)");
			return;
		}

		final int fAnchor = anchor, fGlow = glow, fTotem = totem;
		final BlockPos against = hit.getBlockPos();
		final Direction face = hit.getDirection();
		final BlockPos anchorPos = against.relative(face);

		steps.add(() -> { Util.select(fAnchor); Util.useOnBlock(against, face); });
		steps.add(() -> { Util.select(fGlow); Util.useOnBlock(anchorPos, Direction.UP); });
		steps.add(() -> Util.select(fTotem));
		steps.add(() -> Util.useOnBlock(anchorPos, Direction.UP));
	}
}
