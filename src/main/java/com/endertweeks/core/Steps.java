package com.endertweeks.core;

import java.util.ArrayDeque;

/** Runs queued actions one per game tick (used by the anchor macros). */
public final class Steps {
	private final ArrayDeque<Runnable> queue = new ArrayDeque<>();

	public void add(Runnable r) { queue.add(r); }
	public boolean busy() { return !queue.isEmpty(); }
	public void clear() { queue.clear(); }

	public void tick() {
		Runnable r = queue.poll();
		if (r != null) r.run();
	}
}
