package com.endertweeks.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Base class for every module. "action" modules only run when their keybind is pressed. */
public abstract class Feature {
	public final String name;
	public final String description;
	public final boolean action;
	public boolean enabled;
	public int key = -1;
	public final List<Setting> settings = new ArrayList<>();
	protected final Minecraft mc = Minecraft.getInstance();

	protected Feature(String name, String description, boolean action) {
		this.name = name;
		this.description = description;
		this.action = action;
	}

	protected <T extends Setting> T add(T setting) {
		settings.add(setting);
		return setting;
	}

	public void toggle() {
		if (action) return;
		setEnabled(!enabled);
	}

	public void setEnabled(boolean value) {
		if (value == enabled) return;
		enabled = value;
		if (value) onEnable(); else onDisable();
	}

	public void onEnable() {}
	public void onDisable() {}
	public void onTick() {}
	public void onHud(GuiGraphics g, float partial) {}
	public void onKey() {}

	// ---------------- settings ----------------
	public abstract static class Setting {
		public final String name;
		protected Setting(String name) { this.name = name; }
		public abstract String save();
		public abstract void load(String s);
	}

	public static final class Num extends Setting {
		public double value;
		public final double min, max, step;

		public Num(String name, double min, double max, double step, double value) {
			super(name);
			this.min = min; this.max = max; this.step = step; this.value = value;
		}

		public void nudge(int dir) {
			double v = Math.max(min, Math.min(max, value + dir * step));
			value = Math.round(v * 1000.0) / 1000.0;
		}

		@Override public String save() { return Double.toString(value); }
		@Override public void load(String s) {
			try { value = Math.max(min, Math.min(max, Double.parseDouble(s))); } catch (NumberFormatException ignored) {}
		}
	}

	public static final class Bool extends Setting {
		public boolean value;
		public Bool(String name, boolean value) { super(name); this.value = value; }
		@Override public String save() { return Boolean.toString(value); }
		@Override public void load(String s) { value = Boolean.parseBoolean(s); }
	}

	public static final class Text extends Setting {
		public String value;
		public Text(String name, String value) { super(name); this.value = value; }
		@Override public String save() { return value; }
		@Override public void load(String s) { value = s == null ? "" : s; }
	}
}
