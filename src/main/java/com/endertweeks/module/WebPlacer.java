package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;

public class WebPlacer extends Feature {
	private int back = -1;

	public WebPlacer() {
		super("Web Placer", "Places a cobweb at your feet.", false);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null || mc.screen != null) return;
		if (back != -1) { Util.select(back); back = -1; return; }

		BlockPos feet = mc.player.blockPosition();
		if (!mc.level.getBlockState(feet).isAir()) return;
		BlockPos below = feet.below();
		if (mc.level.getBlockState(below).isAir()) return;

		int web = Util.findHotbar(Items.COBWEB);
		if (web < 0) return;

		back = Util.selected();
		Util.select(web);
		Util.useOnBlock(below, Direction.UP);
	}

	@Override
	public void onDisable() {
		if (back != -1 && mc.player != null) Util.select(back);
		back = -1;
	}
}
