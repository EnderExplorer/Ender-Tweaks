package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;

public class WebDrainer extends Feature {
	private final Num range = add(new Num("Range", 1, 5, 1, 4));
	private int back = -1;

	public WebDrainer() {
		super("Web Drainer", "When water is near, switches to cobwebs and places one inside it to drain it.", false);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null || mc.screen != null) return;
		if (back != -1) { Util.select(back); back = -1; return; }

		int web = Util.findHotbar(Items.COBWEB);
		if (web < 0) return;

		int r = (int) range.value;
		BlockPos c = mc.player.blockPosition();
		BlockPos best = null;
		double bestDist = Double.MAX_VALUE;
		BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				for (int z = -r; z <= r; z++) {
					m.set(c.getX() + x, c.getY() + y, c.getZ() + z);
					if (!mc.level.getFluidState(m).is(FluidTags.WATER)) continue;
					double d = mc.player.getEyePosition().distanceToSqr(m.getX() + 0.5, m.getY() + 0.5, m.getZ() + 0.5);
					if (d < bestDist && d <= 4.5 * 4.5) { bestDist = d; best = m.immutable(); }
				}
			}
		}
		if (best == null) return;

		back = Util.selected();
		Util.select(web);
		Util.useOnBlock(best, Direction.UP);
	}

	@Override
	public void onDisable() {
		if (back != -1 && mc.player != null) Util.select(back);
		back = -1;
	}
}
