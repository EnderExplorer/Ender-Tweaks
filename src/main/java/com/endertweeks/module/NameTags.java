package com.endertweeks.module;

import com.endertweeks.core.Feature;
import com.endertweeks.core.Friends;
import com.endertweeks.render.Projection;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class NameTags extends Feature {
	private final Num scale = add(new Num("Scale", 1.0, 3.0, 0.1, 1.5));

	public NameTags() {
		super("Name Tags", "Bigger name tags with armor, held items and durability.", false);
	}

	@Override
	public void onHud(GuiGraphics g, float partial) {
		double[] o = new double[2];
		float s = (float) scale.value;

		for (Player p : mc.level.players()) {
			if (p == mc.player || p.isSpectator()) continue;
			Vec3 head = p.getPosition(partial).add(0, p.getBbHeight() + 0.45, 0);
			if (!Projection.project(head, o)) continue;

			g.pose().pushMatrix();
			g.pose().translate((float) o[0], (float) o[1]);
			g.pose().scale(s, s);

			// name + health
			String text = p.getName().getString() + " " + (int) Math.ceil(p.getHealth() + p.getAbsorptionAmount());
			int w = mc.font.width(text);
			g.fill(-w / 2 - 2, -11, w / 2 + 2, 0, 0x88000000);
			int color = Friends.is(p) ? 0xFF55FF55 : 0xFFFFFFFF;
			g.drawString(mc.font, text, -w / 2, -9, color, true);

			// armor + hands
			ItemStack[] items = {
				p.getItemBySlot(EquipmentSlot.HEAD),
				p.getItemBySlot(EquipmentSlot.CHEST),
				p.getItemBySlot(EquipmentSlot.LEGS),
				p.getItemBySlot(EquipmentSlot.FEET),
				p.getMainHandItem(),
				p.getOffhandItem()
			};
			int total = items.length * 18;
			int x0 = -total / 2;
			int y0 = -33;
			for (int i = 0; i < items.length; i++) {
				ItemStack st = items[i];
				if (st.isEmpty()) continue;
				int ix = x0 + i * 18;
				g.renderItem(st, ix, y0);
				g.renderItemDecorations(mc.font, st, ix, y0);

				if (st.isDamageableItem()) {
					int max = st.getMaxDamage();
					int pct = Math.max(0, (max - st.getDamageValue()) * 100 / Math.max(1, max));
					int c = pct > 60 ? 0xFF55FF55 : pct > 30 ? 0xFFFFFF55 : 0xFFFF5555;
					String d = pct + "%";
					g.pose().pushMatrix();
					g.pose().translate(ix + 8, y0 + 17);
					g.pose().scale(0.6f, 0.6f);
					g.drawString(mc.font, d, -mc.font.width(d) / 2, 0, c, true);
					g.pose().popMatrix();
				}
			}

			g.pose().popMatrix();
		}
	}
}
