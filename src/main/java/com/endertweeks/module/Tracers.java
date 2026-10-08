package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Friends;
import com.endertweeks.render.Draw;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Tracers extends Feature {
	public Tracers() {
		super("Tracers", "Red line to players, green line to friends.", false);
	}

	@Override
	public void onHud(GuiGraphics g, float partial) {
		for (Player p : mc.level.players()) {
			if (p == mc.player || p.isSpectator()) continue;
			Vec3 pos = p.getPosition(partial).add(0, p.getBbHeight() * 0.5, 0);
			Draw.tracer(g, pos, Friends.is(p) ? 0xFF00FF40 : 0xFFFF2020);
		}
	}
}
