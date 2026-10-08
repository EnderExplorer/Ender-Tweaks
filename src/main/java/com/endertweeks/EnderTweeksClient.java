package com.endertweeks;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.endertweeks.core.Config;
import com.endertweeks.core.Feature;
import com.endertweeks.core.Features;
import com.endertweeks.core.Friends;
import com.endertweeks.core.Keys;
import com.endertweeks.core.Util;
import com.endertweeks.gui.MenuScreen;
import com.endertweeks.render.Projection;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public class EnderTweeksClient implements ClientModInitializer {
	public static final String MOD_ID = "ender_tweeks";

	private static boolean prevMenu, prevMiddle;
	private static final Map<Feature, Boolean> prevKeys = new HashMap<>();

	@Override
	public void onInitializeClient() {
		Features.init();
		Config.load();

		ClientTickEvents.END_CLIENT_TICK.register(EnderTweeksClient::tick);
		WorldRenderEvents.END_EXTRACTION.register(Projection::capture);
		HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
				Identifier.fromNamespaceAndPath(MOD_ID, "overlay"), EnderTweeksClient::renderHud);
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.save());
	}

	private static void tick(Minecraft mc) {
		if (mc.player == null || mc.level == null) return;
		long window = mc.getWindow().handle();

		// Right Ctrl opens the menu
		boolean menuDown = Keys.down(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
		if (menuDown && !prevMenu && mc.screen == null) mc.setScreen(new MenuScreen());
		prevMenu = menuDown;

		// Middle click = add / remove friend
		boolean middle = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
		if (middle && !prevMiddle && mc.screen == null) {
			if (mc.hitResult instanceof EntityHitResult r && r.getEntity() instanceof Player p && p != mc.player) {
				String name = p.getScoreboardName();
				boolean now = Friends.toggle(name);
				Util.msg(name + (now ? " added to friends" : " removed from friends"));
				Config.save();
			}
		}
		prevMiddle = middle;

		// Module keybinds
		for (Feature f : Features.ALL) {
			boolean down = Keys.down(window, f.key);
			boolean was = prevKeys.getOrDefault(f, false);
			if (down && !was && mc.screen == null) {
				if (f.action) f.onKey(); else f.toggle();
			}
			prevKeys.put(f, down);
		}

		for (Feature f : Features.ALL) {
			if (f.enabled || f.action) f.onTick();
		}
	}

	private static void renderHud(GuiGraphics g, DeltaTracker tracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null || mc.options.hideGui || !Projection.valid) return;

		float pt = Projection.partial;
		for (Feature f : Features.ALL) {
			if (f.enabled) f.onHud(g, pt);
		}

		// small list of enabled modules, top right
		int y = 3;
		int w = mc.getWindow().getGuiScaledWidth();
		for (Feature f : Features.ALL) {
			if (!f.enabled) continue;
			g.drawString(mc.font, f.name, w - mc.font.width(f.name) - 3, y, 0xFFB28CFF, true);
			y += 10;
		}
	}
}
