package com.endertweeks.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import org.lwjgl.glfw.GLFW;

import com.endertweeks.core.Config;
import com.endertweeks.core.Feature;
import com.endertweeks.core.Features;
import com.endertweeks.core.Friends;
import com.endertweeks.core.Keys;
import com.endertweeks.render.Draw;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Right Ctrl menu. Left-click a module = toggle, right-click = settings + keybind.
 * Immediate-mode: every frame we rebuild the list of clickable rectangles.
 */
public class MenuScreen extends Screen {
	private static final int ACCENT = 0xFF8A4FFF;

	private record Hit(int x, int y, int w, int h, IntConsumer click) {}

	private final List<Hit> hits = new ArrayList<>();
	private int tab = 0; // 0 = modules, 1 = friends
	private Feature selected;
	private Feature capturing;
	private Feature.Text typing;
	private final StringBuilder friendInput = new StringBuilder();
	private boolean friendFocus;

	public MenuScreen() {
		super(Component.literal("Ender Tweeks"));
	}

	@Override
	public boolean isPauseScreen() { return false; }

	@Override
	public void onClose() {
		Config.save();
		super.onClose();
	}

	// ---------------------------------------------------------------- drawing
	private void button(GuiGraphics g, int mx, int my, int x, int y, int w, int h, String label, int bg, IntConsumer click) {
		boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
		g.fill(x, y, x + w, y + h, hover ? (bg | 0xFF000000) + 0x00181818 : bg);
		Draw.rect(g, x, y, x + w, y + h, 0xFF000000 | (hover ? 0x00B28CFF : 0x00403858));
		g.drawString(font, label, x + (w - font.width(label)) / 2, y + (h - 8) / 2, 0xFFFFFFFF, true);
		hits.add(new Hit(x, y, w, h, click));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float delta) {
		hits.clear();
		g.fill(0, 0, width, height, 0x88000000);

		int pw = 400, ph = 300;
		int px = Math.max(0, (width - pw) / 2), py = Math.max(0, (height - ph) / 2);
		g.fill(px, py, px + pw, py + ph, 0xF0100E1A);
		Draw.rect(g, px, py, px + pw, py + ph, ACCENT);
		g.drawString(font, "ENDER TWEEKS", px + 10, py + 10, 0xFFB28CFF, true);

		button(g, mx, my, px + pw - 150, py + 6, 70, 18, "Modules", tab == 0 ? 0xFF4B2E99 : 0xFF2A2740, b -> tab = 0);
		button(g, mx, my, px + pw - 76, py + 6, 70, 18, "Friends", tab == 1 ? 0xFF4B2E99 : 0xFF2A2740, b -> tab = 1);

		if (tab == 0) drawModules(g, mx, my, px, py, pw, ph);
		else drawFriends(g, mx, my, px, py, pw, ph);
	}

	private void drawModules(GuiGraphics g, int mx, int my, int px, int py, int pw, int ph) {
		int bw = 92, bh = 20, gap = 4;
		int startX = px + 10, startY = py + 32;
		String hoverDesc = null;

		for (int i = 0; i < Features.ALL.size(); i++) {
			Feature f = Features.ALL.get(i);
			int x = startX + (i % 4) * (bw + gap);
			int y = startY + (i / 4) * (bh + gap);
			int bg = f.enabled ? 0xFF2E7D32 : f.action ? 0xFF2F3E6B : 0xFF2A2740;
			if (f == selected) bg = (bg & 0x00FFFFFF) | 0xFF000000;
			button(g, mx, my, x, y, bw, bh, f.name, bg, b -> {
				if (b == 0) f.toggle();
				else if (b == 1) { selected = f; capturing = null; typing = null; }
			});
			if (f == selected) Draw.rect(g, x - 1, y - 1, x + bw + 1, y + bh + 1, ACCENT);
			if (mx >= x && mx < x + bw && my >= y && my < y + bh) hoverDesc = f.description;
		}

		int rows = (Features.ALL.size() + 3) / 4;
		int sy = startY + rows * (bh + gap) + 6;
		g.fill(px + 10, sy - 3, px + pw - 10, sy - 2, 0xFF403858);

		if (selected != null) {
			g.drawString(font, selected.name + " settings", px + 10, sy + 2, 0xFFFFFFFF, true);
			int y = sy + 16;

			String keyLabel = capturing == selected ? "press a key... (Esc = none)" : "Keybind: " + Keys.name(selected.key);
			button(g, mx, my, px + 10, y, 190, 18, keyLabel, 0xFF2A2740, b -> {
				if (b == 0 || b == 1) { capturing = selected; typing = null; }
			});
			button(g, mx, my, px + 204, y, 50, 18, "Clear", 0xFF402A2A, b -> selected.key = -1);
			if (selected.action) {
				g.drawString(font, "Runs when you press the keybind", px + 262, y + 5, 0xFF9A9AB0, true);
			}
			y += 22;

			for (Feature.Setting s : selected.settings) {
				if (s instanceof Feature.Num n) {
					button(g, mx, my, px + 10, y, 190, 18, n.name + ": " + n.value + "   (L +  /  R -)", 0xFF2A2740, b -> {
						if (b == 0) n.nudge(1); else if (b == 1) n.nudge(-1);
					});
				} else if (s instanceof Feature.Bool bo) {
					button(g, mx, my, px + 10, y, 190, 18, bo.name + ": " + (bo.value ? "ON" : "OFF"), bo.value ? 0xFF2E7D32 : 0xFF2A2740, b -> bo.value = !bo.value);
				} else if (s instanceof Feature.Text t) {
					String shown = t.name + ": " + t.value + (typing == t && (System.currentTimeMillis() / 500) % 2 == 0 ? "_" : "");
					button(g, mx, my, px + 10, y, 380, 18, shown, typing == t ? 0xFF4B2E99 : 0xFF2A2740, b -> { typing = t; capturing = null; });
				}
				y += 22;
			}
		} else {
			g.drawString(font, "Right-click a module to set its keybind and settings.", px + 10, sy + 4, 0xFF9A9AB0, true);
		}

		String footer = hoverDesc != null ? hoverDesc : "Left-click: toggle   |   Right-click: settings / keybind   |   Right Ctrl: close";
		g.drawString(font, footer, px + 10, py + ph - 14, 0xFF9A9AB0, true);
	}

	private void drawFriends(GuiGraphics g, int mx, int my, int px, int py, int pw, int ph) {
		int y = py + 36;
		g.drawString(font, "Add a friend by username:", px + 10, y - 2, 0xFFFFFFFF, true);
		y += 12;

		int fx = px + 10, fw = 220;
		g.fill(fx, y, fx + fw, y + 18, friendFocus ? 0xFF2B2150 : 0xFF1A1830);
		Draw.rect(g, fx, y, fx + fw, y + 18, friendFocus ? ACCENT : 0xFF403858);
		String txt = friendInput + (friendFocus && (System.currentTimeMillis() / 500) % 2 == 0 ? "_" : "");
		g.drawString(font, txt, fx + 4, y + 5, 0xFFFFFFFF, true);
		hits.add(new Hit(fx, y, fw, 18, b -> friendFocus = true));
		button(g, mx, my, fx + fw + 6, y, 60, 18, "Add", 0xFF2E7D32, b -> addFriend());

		y += 30;
		List<String> list = Friends.list();
		g.drawString(font, "Friends (" + list.size() + ")   - middle-click a player in game to add / remove", px + 10, y, 0xFF9A9AB0, true);
		y += 14;

		int shown = 0;
		for (String name : list) {
			if (shown++ >= 9) break;
			g.fill(px + 10, y, px + 290, y + 18, 0xFF1A1830);
			g.drawString(font, name, px + 16, y + 5, 0xFF55FF55, true);
			button(g, mx, my, px + 294, y, 70, 18, "Remove", 0xFF402A2A, b -> Friends.remove(name));
			y += 21;
		}
		if (list.size() > 9) g.drawString(font, "+" + (list.size() - 9) + " more", px + 10, y + 2, 0xFF9A9AB0, true);
	}

	private void addFriend() {
		if (friendInput.length() > 0) {
			Friends.add(friendInput.toString());
			friendInput.setLength(0);
			Config.save();
		}
	}

	// ---------------------------------------------------------------- input
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x(), my = event.y();
		int button = event.button();
		typing = null;
		friendFocus = false;
		capturing = null;

		for (int i = hits.size() - 1; i >= 0; i--) {
			Hit h = hits.get(i);
			if (mx >= h.x() && mx < h.x() + h.w() && my >= h.y() && my < h.y() + h.h()) {
				h.click().accept(button);
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int k = event.key();

		if (capturing != null) {
			if (k == GLFW.GLFW_KEY_ESCAPE) capturing.key = -1;
			else if (k != GLFW.GLFW_KEY_RIGHT_CONTROL) capturing.key = k;
			capturing = null;
			return true;
		}
		if (typing != null) {
			if (k == GLFW.GLFW_KEY_ESCAPE || k == GLFW.GLFW_KEY_ENTER || k == GLFW.GLFW_KEY_KP_ENTER) typing = null;
			else if (k == GLFW.GLFW_KEY_BACKSPACE && !typing.value.isEmpty()) typing.value = typing.value.substring(0, typing.value.length() - 1);
			return true;
		}
		if (friendFocus) {
			if (k == GLFW.GLFW_KEY_ESCAPE) friendFocus = false;
			else if (k == GLFW.GLFW_KEY_ENTER || k == GLFW.GLFW_KEY_KP_ENTER) addFriend();
			else if (k == GLFW.GLFW_KEY_BACKSPACE && friendInput.length() > 0) friendInput.setLength(friendInput.length() - 1);
			return true;
		}
		if (k == GLFW.GLFW_KEY_RIGHT_CONTROL || k == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		int cp = event.codepoint();
		if (cp < 32 || cp == 127) return false;
		if (typing != null) {
			if (typing.value.length() < 60) typing.value += new String(Character.toChars(cp));
			return true;
		}
		if (friendFocus) {
			if ((Character.isLetterOrDigit(cp) || cp == '_') && friendInput.length() < 16) friendInput.appendCodePoint(cp);
			return true;
		}
		return false;
	}
}
