package com.endertweeks.module;

import java.util.ArrayList;
import java.util.List;

import com.endertweeks.core.Feature;
import com.endertweeks.render.Draw;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class StorageEsp extends Feature {
	private static final int BROWN = 0xFF8B5A2B;

	private record Target(AABB box, int color) {}

	private final List<Target> blocks = new ArrayList<>();
	private int timer;

	public StorageEsp() {
		super("Storage ESP", "Highlights chests, barrels, hoppers, spawners and chest minecarts with brown lines.", false);
	}

	@Override
	public void onEnable() { timer = 0; }

	@Override
	public void onDisable() { blocks.clear(); }

	private static int colorFor(Block b) {
		if (b == Blocks.CHEST) return BROWN;
		if (b == Blocks.TRAPPED_CHEST) return 0xFFB5651D;
		if (b == Blocks.BARREL) return 0xFFA0522D;
		if (b == Blocks.HOPPER) return 0xFF9A9A9A;
		if (b == Blocks.SPAWNER) return 0xFFB04CFF;
		return 0;
	}

	@Override
	public void onTick() {
		if (mc.level == null || mc.player == null) return;
		if (timer++ % 20 != 0) return;

		blocks.clear();
		ChunkPos cp = mc.player.chunkPosition();
		int r = mc.options.getEffectiveRenderDistance();
		for (int dx = -r; dx <= r; dx++) {
			for (int dz = -r; dz <= r; dz++) {
				ChunkAccess chunk = mc.level.getChunkSource().getChunk(cp.x + dx, cp.z + dz, ChunkStatus.FULL, false);
				if (chunk == null) continue;
				for (BlockEntity be : chunk.getBlockEntities().values()) {
					int color = colorFor(be.getBlockState().getBlock());
					if (color == 0) continue;
					blocks.add(new Target(new AABB(be.getBlockPos()), color));
				}
			}
		}
	}

	@Override
	public void onHud(GuiGraphics g, float partial) {
		int lineCount = 0;
		for (Target t : blocks) {
			Draw.box(g, t.box(), t.color());
			if (lineCount++ < 30) Draw.tracer(g, t.box().getCenter(), BROWN);
		}
		for (Entity e : mc.level.entitiesForRendering()) {
			if (e.getType() != EntityType.CHEST_MINECART) continue;
			AABB box = e.getBoundingBox();
			Draw.box(g, box, BROWN);
			if (lineCount++ < 30) Draw.tracer(g, box.getCenter(), BROWN);
		}
	}
}
