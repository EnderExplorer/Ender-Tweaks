package com.endertweeks.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.endertweeks.core.BlockMatch;
import com.endertweeks.core.Feature;
import com.endertweeks.render.Draw;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockEsp extends Feature {
	private final Text block = add(new Text("Block", "diamond_ore"));
	private final Num range = add(new Num("Range", 8, 64, 4, 24));
	private final Bool lines = add(new Bool("Tracers", true));

	private final List<BlockPos> found = new ArrayList<>();
	private int timer;

	public BlockEsp() {
		super("Block ESP", "Highlights a chosen block through walls. Right-click to choose the block.", false);
	}

	@Override
	public void onEnable() { timer = 0; }

	@Override
	public void onDisable() { found.clear(); }

	@Override
	public void onTick() {
		if (mc.level == null || mc.player == null) return;
		if (timer++ % 20 != 0) return;

		BlockMatch match = BlockMatch.parse(block.value);
		found.clear();
		if (match.isEmpty()) return;

		int r = (int) range.value;
		BlockPos c = mc.player.blockPosition();
		Map<Block, Boolean> cache = new HashMap<>();
		BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
		List<BlockPos> out = new ArrayList<>();

		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				int wy = c.getY() + y;
				if (wy < mc.level.getMinY() || wy >= mc.level.getMaxY()) continue;
				for (int z = -r; z <= r; z++) {
					m.set(c.getX() + x, wy, c.getZ() + z);
					BlockState st = mc.level.getBlockState(m);
					if (st.isAir()) continue;
					Block b = st.getBlock();
					if (cache.computeIfAbsent(b, match::matches)) out.add(m.immutable());
				}
			}
			if (out.size() > 3000) break;
		}

		out.sort(Comparator.comparingDouble(p -> p.distSqr(c)));
		for (int i = 0; i < Math.min(out.size(), 300); i++) found.add(out.get(i));
	}

	@Override
	public void onHud(GuiGraphics g, float partial) {
		int lineCount = 0;
		for (BlockPos p : found) {
			Draw.box(g, new AABB(p), 0xFF00E5FF);
			if (lines.value && lineCount++ < 25) Draw.tracer(g, Vec3.atCenterOf(p), 0xFF00E5FF);
		}
	}
}
