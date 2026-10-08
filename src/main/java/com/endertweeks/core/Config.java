package com.endertweeks.core;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;

public final class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private Config() {}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("ender_tweeks.json");
	}

	public static void load() {
		try {
			Path f = file();
			if (!Files.exists(f)) return;
			JsonObject root = JsonParser.parseString(Files.readString(f)).getAsJsonObject();

			if (root.has("friends")) {
				Friends.clear();
				for (JsonElement e : root.getAsJsonArray("friends")) Friends.add(e.getAsString());
			}
			if (root.has("modules")) {
				JsonObject mods = root.getAsJsonObject("modules");
				for (Feature feat : Features.ALL) {
					if (!mods.has(feat.name)) continue;
					JsonObject o = mods.getAsJsonObject(feat.name);
					if (o.has("key")) feat.key = o.get("key").getAsInt();
					if (o.has("settings")) {
						JsonObject s = o.getAsJsonObject("settings");
						for (Feature.Setting st : feat.settings) {
							if (s.has(st.name)) st.load(s.get(st.name).getAsString());
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[Ender Tweeks] Failed to load config: " + e);
		}
	}

	public static void save() {
		try {
			JsonObject root = new JsonObject();
			JsonArray friends = new JsonArray();
			for (String s : Friends.list()) friends.add(s);
			root.add("friends", friends);

			JsonObject mods = new JsonObject();
			for (Feature feat : Features.ALL) {
				JsonObject o = new JsonObject();
				o.addProperty("key", feat.key);
				JsonObject s = new JsonObject();
				for (Feature.Setting st : feat.settings) s.addProperty(st.name, st.save());
				o.add("settings", s);
				mods.add(feat.name, o);
			}
			root.add("modules", mods);
			Files.writeString(file(), GSON.toJson(root));
		} catch (Exception e) {
			System.err.println("[Ender Tweeks] Failed to save config: " + e);
		}
	}
}
