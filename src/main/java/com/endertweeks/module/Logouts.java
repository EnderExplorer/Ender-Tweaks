package com.endertweeks.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Util;
import com.endertweeks.render.Draw;
import com.endertweeks.render.Projection;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Logouts extends Feature {
	private static final int BLUE = 0xFF3399FF;

	private static final class Seen {
		final String name; final AABB box; int missing;
		Seen(String name, AABB box) { this.name = name; this.box = box; }
	}

	private static final class Logout {
		final UUID id; final String name; final AABB box; int age;
		Logout(UUID id, String name, AABB box) { this.id = id; this.name = name; this.box = box; }
	}

	private final Map<UUID, Seen> seen = new HashMap<>();
	private final List<Logout> logouts = new ArrayList<>();

	public Logouts() {
		super("Logouts", "Marks (blue) where players logged out inside your render distance.", false);
	}

	@Override
	public void onDisable() { seen.clear(); logouts.clear(); }

	@Override
	public void onTick() {
		if (mc.level == null || mc.player == null) return;

		Set<UUID> present = new HashSet<>();
		for (Player p : mc.level.players()) {
			if (p == mc.player) continue;
			present.add(p.getUUID());
			seen.put(p.getUUID(), new Seen(p.getScoreboardName(), p.getBoundingBox()));
			logouts.removeIf(l -> l.id.equals(p.getUUID()));
		}

		ClientPacketListener conn = mc.getConnection();
		Iterator<Map.Entry<UUID, Seen>> it = seen.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Seen> e = it.next();
			if (present.contains(e.getKey())) continue;
			Seen s = e.getValue();
			s.missing++;
			boolean stillOnline = conn != null && conn.getPlayerInfo(e.getKey()) != null;
			if (!stillOnline) {
				logouts.add(new Logout(e.getKey(), s.name, s.box));
				mc.player.displayClientMessage(Component.literal("[Ender Tweeks] " + s.name + " logged out"), false);
				it.remove();
			} else if (s.missing > 100) {
				it.remove(); // just walked out of range
			}
		}

		logouts.removeIf(l -> ++l.age > 6000);
		while (logouts.size() > 30) logouts.remove(0);
	}

	@Override
	public void onHud(GuiGraphics g, float partial) {
		double[] o = new double[2];
		for (Logout l : logouts) {
			Vec3 c = l.box.getCenter();
			Draw.box(g, l.box, BLUE);
			Draw.tracer(g, c, BLUE);
			if (Projection.project(c.add(0, l.box.getYsize() / 2 + 0.3, 0), o)) {
				Draw.centeredText(g, l.name + " logged out", (int) o[0], (int) o[1], 0xFF66B2FF);
			}
		}
	}
}
