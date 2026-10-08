package com.endertweeks.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.minecraft.world.entity.player.Player;

public final class Friends {
	private static final Set<String> NAMES = new LinkedHashSet<>();

	private Friends() {}

	public static boolean is(String name) {
		return name != null && NAMES.contains(name.toLowerCase(Locale.ROOT));
	}

	public static boolean is(Player p) {
		return p != null && is(p.getScoreboardName());
	}

	public static boolean add(String name) {
		if (name == null || name.isBlank()) return false;
		return NAMES.add(name.trim().toLowerCase(Locale.ROOT));
	}

	public static void remove(String name) {
		NAMES.remove(name.toLowerCase(Locale.ROOT));
	}

	/** @return true if the player is now a friend. */
	public static boolean toggle(String name) {
		String n = name.toLowerCase(Locale.ROOT);
		if (NAMES.remove(n)) return false;
		NAMES.add(n);
		return true;
	}

	public static List<String> list() { return new ArrayList<>(NAMES); }
	public static void clear() { NAMES.clear(); }
}
