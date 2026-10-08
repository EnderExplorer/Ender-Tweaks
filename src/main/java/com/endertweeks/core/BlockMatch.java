package com.endertweeks.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

/** Matches blocks by id. "diamond_ore, ancient_debris" matches any block whose id contains one of the parts. */
public final class BlockMatch {
	private final List<String> parts = new ArrayList<>();

	public static BlockMatch parse(String text) {
		BlockMatch m = new BlockMatch();
		if (text == null) return m;
		for (String raw : text.split(",")) {
			String s = raw.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
			if (s.startsWith("minecraft:")) s = s.substring("minecraft:".length());
			if (!s.isEmpty()) m.parts.add(s);
		}
		return m;
	}

	public boolean isEmpty() { return parts.isEmpty(); }

	public boolean matches(Block block) {
		String id = BuiltInRegistries.BLOCK.getKey(block).getPath();
		for (String p : parts) {
			if (id.contains(p)) return true;
		}
		return false;
	}
}
