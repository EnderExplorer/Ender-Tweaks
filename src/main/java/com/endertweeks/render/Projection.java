package com.endertweeks.render;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/**
 * Captures the camera matrices every frame and projects world positions to GUI (screen) coordinates.
 * All ESP / tracer / name tag drawing is done in 2D on the HUD with these.
 */
public final class Projection {
	private static final Matrix4f VIEW = new Matrix4f();
	private static final Matrix4f PROJ = new Matrix4f();
	private static Vec3 cam = Vec3.ZERO;
	public static float partial = 1f;
	public static boolean valid = false;

	private Projection() {}

	public static void capture(WorldExtractionContext ctx) {
		VIEW.set(ctx.viewMatrix());
		PROJ.set(ctx.cullProjectionMatrix());
		cam = ctx.camera().position();
		partial = ctx.tickCounter().getGameTimeDeltaPartialTick(false);
		valid = true;
	}

	public static double screenW() { return Minecraft.getInstance().getWindow().getGuiScaledWidth(); }
	public static double screenH() { return Minecraft.getInstance().getWindow().getGuiScaledHeight(); }

	/**
	 * @param out receives GUI x/y. If the point is behind the camera, out points to the screen edge
	 *            in the direction of the object (good for tracers) and false is returned.
	 * @return true when the point is in front of the camera.
	 */
	public static boolean project(Vec3 world, double[] out) {
		double w = screenW(), h = screenH();
		Vector4f v = new Vector4f((float) (world.x - cam.x), (float) (world.y - cam.y), (float) (world.z - cam.z), 1f);
		VIEW.transform(v);
		PROJ.transform(v);
		if (v.w <= 0.0001f) {
			double nx = v.x, ny = v.y;
			double len = Math.sqrt(nx * nx + ny * ny);
			if (len < 1e-6) { nx = 0; ny = -1; len = 1; }
			out[0] = w / 2 + nx / len * 2000;
			out[1] = h / 2 - ny / len * 2000;
			return false;
		}
		double ndcX = v.x / v.w, ndcY = v.y / v.w;
		out[0] = (ndcX * 0.5 + 0.5) * w;
		out[1] = (1.0 - (ndcY * 0.5 + 0.5)) * h;
		return true;
	}
}
