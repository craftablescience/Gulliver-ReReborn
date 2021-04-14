package com.camellias.gulliverreborn.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public abstract class GulliverRebornCommandBase extends CommandBase {
	protected static List<String> aliases;
	protected static String name;
	protected static String nameKey;
	protected static int argumentLength;
	protected static int permissionLevel;
	protected static final UUID uuidHeight = UUID.fromString("5440b01a-974f-4495-bb9a-c7c87424bca4");
	protected static final UUID uuidWidth = UUID.fromString("3949d2ed-b6cc-4330-9c13-98777f48ea51");
	protected static final UUID uuidReach1 = UUID.fromString("854e0004-c218-406c-a9e2-590f1846d80b");
	protected static final UUID uuidReach2 = UUID.fromString("216080dc-22d3-4eff-a730-190ec0210d5c");
	protected static final UUID uuidHealth = UUID.fromString("3b901d47-2d30-495c-be45-f0091c0f6fb2");
	protected static final UUID uuidStrength = UUID.fromString("558f55be-b277-4091-ae9b-056c7bc96e84");
	protected static final UUID uuidSpeed = UUID.fromString("f2fb5cda-3fbe-4509-a0af-4fc994e6aeca");
	
	@Override
	public @Nonnull String getName() {
		return name;
	}

	@Override
	public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
		return nameKey;
	}
	
	@Override
	public @Nonnull List<String> getAliases() {
		return aliases;
	}
	
	@Override
	public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) {
		return true;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return permissionLevel;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
		if (args.length < argumentLength) return;
		run(server, sender, args);
	}

	public abstract void run(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException;
}
