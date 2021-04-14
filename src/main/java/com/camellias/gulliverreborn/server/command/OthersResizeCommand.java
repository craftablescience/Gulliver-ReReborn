package com.camellias.gulliverreborn.server.command;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;
import com.camellias.gulliverreborn.config.GulliverRebornConfig;
import com.camellias.gulliverreborn.GulliverReborn;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class OthersResizeCommand extends GulliverRebornCommandBase {

	static {
		aliases = Lists.newArrayList(GulliverReborn.MODID, "basesize", "bs");
		name = "basesize";
		nameKey = "gulliverreborn.commands.basesize.usage";
		argumentLength = 2;
		permissionLevel = GulliverRebornConfig.COMMAND_OTHERSRESIZE_PERMISSION_LEVEL;
	}
	
	@Override
	public boolean isUsernameIndex(@Nonnull String[] args, int index) {
		return index == 0;
	}
	
	@Override
	public @Nonnull List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, BlockPos targetPos) {
		if(args.length == 0)
			return Collections.emptyList();
		else if(isUsernameIndex(args, args.length - 1))
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		return super.getTabCompletions(server, sender, args, targetPos);
	}
	
	@Override
	public void run(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
		String s = args[1];
		float size;
		
		try {
			size = MathHelper.clamp(Float.parseFloat(s), 0.125f, GulliverRebornConfig.MAX_SIZE);
		} catch(NumberFormatException e) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Size Invalid"));
			return;
		}
		
		EntityPlayer player = getPlayer(server, sender, args[0]);
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
			player.getAttributeMap().applyAttributeModifiers(removeableAttributes);
		else
			player.getAttributeMap().removeAttributeModifiers(removeableAttributes);
		
		if (size < 1)
			player.getAttributeMap().applyAttributeModifiers(removeableAttributes2);
		else
			player.getAttributeMap().removeAttributeModifiers(removeableAttributes2);

		player.getAttributeMap().applyAttributeModifiers(attributes);
		player.setHealth(player.getMaxHealth());
		
		GulliverReborn.LOGGER.info(sender.getDisplayName() + " set " + player.getDisplayNameString() +"'s size to " + size);
	}
}
