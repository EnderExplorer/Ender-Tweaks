package com.endertweeks.module;

import com.endertweeks.core.Feature;

import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;

public class Flight extends Feature {
	private final Num speed = add(new Num("Speed", 0.1, 5.0, 0.1, 1.0));

	public Flight() {
		super("Flight", "Creative-style flight. Jump = up, Sneak = down. Right-click to set speed.", false);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc.player;
		if (p == null || p.isPassenger() || p.isSpectator()) return;
		if (mc.screen != null) { p.setDeltaMovement(0, 0, 0); return; }

		double s = speed.value * 0.5;
		Options o = mc.options;
		double forward = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
		double strafe = (o.keyLeft.isDown() ? 1 : 0) - (o.keyRight.isDown() ? 1 : 0);
		double yaw = Math.toRadians(p.getYRot());

		double mx = -Math.sin(yaw) * forward + Math.cos(yaw) * strafe;
		double mz = Math.cos(yaw) * forward + Math.sin(yaw) * strafe;
		double len = Math.sqrt(mx * mx + mz * mz);
		if (len > 1e-6) { mx = mx / len * s; mz = mz / len * s; } else { mx = 0; mz = 0; }
		double my = (o.keyJump.isDown() ? s : 0) - (o.keyShift.isDown() ? s : 0);

		p.setNoGravity(true);
		p.setDeltaMovement(mx, my, mz);
		p.resetFallDistance();
	}

	@Override
	public void onDisable() {
		if (mc.player != null) mc.player.setNoGravity(false);
	}
}
