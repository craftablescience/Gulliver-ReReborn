package com.camellias.gulliverreborn.server.command;

import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;
import com.camellias.gulliverreborn.config.GulliverRebornConfig;
import com.camellias.gulliverreborn.GulliverReborn;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class MyResizeCommand extends GulliverRebornCommandBase {

	static {
		aliases = Lists.newArrayList(GulliverReborn.MODID, "mysize", "ms");
		name = "mysize";
		nameKey = "gulliverreborn.commands.mysize.usage";
		argumentLength = 1;
		permissionLevel = GulliverRebornConfig.COMMAND_MYRESIZE_PERMISSION_LEVEL;
	}

	@Override
	public void run(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) {
		String s = args[0];
		float size;

		try {
			size = MathHelper.clamp(Float.parseFloat(s), 0.125f, GulliverRebornConfig.MAX_SIZE);
		} catch (NumberFormatException e) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Size Invalid"));
			return;
		}

		if (sender instanceof EntityPlayer) {
			Multimap<String, AttributeModifier> attributes = HashMultimap.create();
			Multimap<String, AttributeModifier> removeableAttributes = HashMultimap.create();
			Multimap<String, AttributeModifier> removeableAttributes2 = HashMultimap.create();

			attributes.put(ArtemisLibAttributes.ENTITY_HEIGHT.getName(), new AttributeModifier(uuidHeight, "Player Height", size - 1, 2));
			attributes.put(ArtemisLibAttributes.ENTITY_WIDTH.getName(), new AttributeModifier(uuidWidth, "Player Width", MathHelper.clamp(size - 1, 0.4 - 1, GulliverRebornConfig.MAX_SIZE), 2));

			if (GulliverRebornConfig.SPEED_MODIFIER) attributes.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(uuidSpeed, "Player Speed", (size - 1) / 2, 2));
			if (GulliverRebornConfig.REACH_MODIFIER) removeableAttributes.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(uuidReach1, "Player Reach 1", size - 1, 2));
			if (GulliverRebornConfig.REACH_MODIFIER) removeableAttributes2.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(uuidReach2, "Player Reach 2", -MathHelper.clamp(size - 1, 0.33, Double.MAX_VALUE), 2));
			if (GulliverRebornConfig.STRENGTH_MODIFIER) attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(uuidStrength, "Player Strength", size - 1, 0));
			if (GulliverRebornConfig.HEALTH_MODIFIER) attributes.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(uuidHealth, "Player Health", (size - 1) * GulliverRebornConfig.HEALTH_MULTIPLIER, 2));

			if (size > 1)
				((EntityPlayer) sender).getAttributeMap().applyAttributeModifiers(removeableAttributes);
			else
				((EntityPlayer) sender).getAttributeMap().removeAttributeModifiers(removeableAttributes);

			if (size < 1)
				((EntityPlayer) sender).getAttributeMap().applyAttributeModifiers(removeableAttributes2);
			else
				((EntityPlayer) sender).getAttributeMap().removeAttributeModifiers(removeableAttributes2);

			((EntityPlayer) sender).getAttributeMap().applyAttributeModifiers(attributes);
			((EntityPlayer) sender).setHealth(((EntityPlayer) sender).getMaxHealth());

			GulliverReborn.LOGGER.info(((EntityPlayer) sender).getDisplayNameString() + " set their size to " + size);
		} else if (sender instanceof TileEntity)
			GulliverReborn.LOGGER.info("Cannot run /mysize command inside nonplayer! Ran at " + ((TileEntity) sender).getPos());
	}
}
