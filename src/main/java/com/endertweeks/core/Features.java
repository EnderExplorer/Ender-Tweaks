package com.endertweeks.core;

import java.util.ArrayList;
import java.util.List;

import com.endertweeks.module.*;

public final class Features {
	public static final List<Feature> ALL = new ArrayList<>();

	private Features() {}

	public static void init() {
		ALL.add(new Flight());
		ALL.add(new ShieldBreaker());
		ALL.add(new TriggerBot());
		ALL.add(new Tracers());
		ALL.add(new NameTags());
		ALL.add(new FastBreak());
		ALL.add(new AnchorHelper());
		ALL.add(new SafeAnchor());
		ALL.add(new WebDrainer());
		ALL.add(new WebPlacer());
		ALL.add(new BlockEsp());
		ALL.add(new Xray());
		ALL.add(new StorageEsp());
		ALL.add(new Logouts());
		ALL.add(new HoverTotem());
	}

	@SuppressWarnings("unchecked")
	public static <T extends Feature> T get(Class<T> type) {
		for (Feature f : ALL) {
			if (type.isInstance(f)) return (T) f;
		}
		return null;
	}
}
