package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Features;
import com.endertweeks.core.Friends;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public class TriggerBot extends Feature {
	private final Bool mobs = add(new Bool("Hit mobs", false));

	public TriggerBot() {
		super("Trigger Bot", "Auto-hits whatever you aim at when holding a sword/axe and the cooldown is ready.", false);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc.player;
		if (p == null || mc.screen != null || p.isUsingItem()) return;

		ItemStack held = p.getMainHandItem();
		if (!(held.is(ItemTags.SWORDS) || held.is(ItemTags.AXES))) return;
		if (!(mc.hitResult instanceof EntityHitResult r)) return;

		Entity e = r.getEntity();
		if (!e.isAlive()) return;

		if (e instanceof Player target) {
			if (target == p || target.isSpectator() || Friends.is(target)) return;
			// let Shield Breaker handle blockers
			ShieldBreaker sb = Features.get(ShieldBreaker.class);
			if (sb != null && sb.enabled && target.isBlocking()) return;
		} else if (!(mobs.value && e instanceof LivingEntity)) {
			return;
		}

		if (p.getAttackStrengthScale(0f) < 1f) return;
		mc.gameMode.attack(p, e);
		p.swing(InteractionHand.MAIN_HAND);
	}
}
