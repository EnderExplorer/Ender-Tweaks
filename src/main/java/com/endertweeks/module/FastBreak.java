package com.endertweeks.module;

import com.endertweeks.core.Feature;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FastBreak extends Feature {
	public FastBreak() {
		super("Fast Break", "Gives you Haste III.", false);
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		MobEffectInstance cur = mc.player.getEffect(MobEffects.HASTE);
		if (cur == null || cur.getAmplifier() < 2 || cur.getDuration() < 10) {
			mc.player.addEffect(new MobEffectInstance(MobEffects.HASTE, 40, 2, false, false, false));
		}
	}

	@Override
	public void onDisable() {
		if (mc.player != null) mc.player.removeEffect(MobEffects.HASTE);
	}
}
