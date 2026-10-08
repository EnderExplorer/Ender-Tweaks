package com.endertweeks.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Tiny 2D drawing helpers on top of GuiGraphics. */
public final class Draw {
	private Draw() {}

	public static void rect(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
		g.fill(x1, y1, x2, y1 + 1, color);
		g.fill(x1, y2 - 1, x2, y2, color);
		g.fill(x1, y1, x1 + 1, y2, color);
		g.fill(x2 - 1, y1, x2, y2, color);
	}

	public static void line(GuiGraphics g, double x1, double y1, double x2, double y2, int color) {
		double dx = x2 - x1, dy = y2 - y1;
		double len = Math.sqrt(dx * dx + dy * dy);
		if (len < 1) return;
		int steps = (int) Math.min(len / 2.0, 1500);
		double w = Projection.screenW(), h = Projection.screenH();
		for (int i = 0; i <= steps; i++) {
			double t = (double) i / steps;
			int x = (int) (x1 + dx * t);
			int y = (int) (y1 + dy * t);
			if (x < 0 || y < 0 || x >= w || y >= h) continue;
			g.fill(x, y, x + 2, y + 2, color);
		}
	}

	/** Line from the middle of the screen (the crosshair) to a world position. */
	public static void tracer(GuiGraphics g, Vec3 target, int color) {
		double[] o = new double[2];
		Projection.project(target, o);
		line(g, Projection.screenW() / 2, Projection.screenH() / 2, o[0], o[1], color);
	}

	/** Draws a 2D box around the projected AABB. Returns false (and draws nothing) if any corner is behind the camera. */
	public static boolean box(GuiGraphics g, AABB b, int color) {
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
		double[] o = new double[2];
		for (int i = 0; i < 8; i++) {
			Vec3 c = new Vec3((i & 1) == 0 ? b.minX : b.maxX, (i & 2) == 0 ? b.minY : b.maxY, (i & 4) == 0 ? b.minZ : b.maxZ);
			if (!Projection.project(c, o)) return false;
			minX = Math.min(minX, o[0]); maxX = Math.max(maxX, o[0]);
			minY = Math.min(minY, o[1]); maxY = Math.max(maxY, o[1]);
		}
		if (maxX - minX > 3000 || maxY - minY > 3000) return false;
		int x1 = (int) minX, y1 = (int) minY, x2 = (int) Math.ceil(maxX), y2 = (int) Math.ceil(maxY);
		if (x2 - x1 < 3) { x1 -= 1; x2 += 2; }
		if (y2 - y1 < 3) { y1 -= 1; y2 += 2; }
		g.fill(x1, y1, x2, y2, (color & 0x00FFFFFF) | 0x33000000);
		rect(g, x1, y1, x2, y2, color);
		return true;
	}

	public static void centeredText(GuiGraphics g, String text, int x, int y, int color) {
		Minecraft mc = Minecraft.getInstance();
		g.drawString(mc.font, text, x - mc.font.width(text) / 2, y, color, true);
	}
}
