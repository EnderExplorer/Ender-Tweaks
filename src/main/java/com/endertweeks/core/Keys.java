package com.endertweeks.core;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

public final class Keys {
	private Keys() {}

	public static boolean down(long window, int key) {
		if (key < 0) return false;
		return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
	}

	public static String name(int key) {
		if (key < 0) return "NONE";
		try {
			return InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString().toUpperCase();
		} catch (Throwable t) {
			return "KEY " + key;
		}
	}
}
