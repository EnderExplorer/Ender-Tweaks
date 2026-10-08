package com.endertweeks.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.endertweeks.core.BlockMatch;
import com.endertweeks.core.Feature;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Xray extends Feature {
	/** Read by the render mixins (chunk meshing happens on worker threads, so keep this lock-free). */
	public static volatile boolean active = false;
	private static volatile BlockMatch current = BlockMatch.parse("");
	private static volatile Map<Block, Boolean> cache = new ConcurrentHashMap<>();

	private final Text block = add(new Text("Block", "diamond_ore"));
	private final Bool fullbright = add(new Bool("Fullbright", true));
	private String applied = null;

	public Xray() {
		super("Xray", "Hides every block except the one you choose. Right-click to choose (comma separated).", false);
	}

	public static boolean matches(BlockState state) {
		Block b = state.getBlock();
		return cache.computeIfAbsent(b, current::matches);
	}

	private void apply() {
		applied = block.value;
		current = BlockMatch.parse(applied);
		cache = new ConcurrentHashMap<>();
	}

	@Override
	public void onEnable() {
		apply();
		active = true;
		if (mc.levelRenderer != null) mc.levelRenderer.allChanged();
	}

	@Override
	public void onDisable() {
		active = false;
		if (mc.levelRenderer != null) mc.levelRenderer.allChanged();
		if (mc.player != null) mc.player.removeEffect(MobEffects.NIGHT_VISION);
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		if (!block.value.equals(applied)) {
			apply();
			if (mc.levelRenderer != null) mc.levelRenderer.allChanged();
		}
		if (fullbright.value && !mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
			mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 0, false, false, false));
		}
	}
}
