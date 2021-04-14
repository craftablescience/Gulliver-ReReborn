package com.camellias.gulliverreborn.config;

import java.io.File;

import com.camellias.gulliverreborn.GulliverReborn;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class GulliverRebornConfig {
	public static Configuration config;

	public static int COMMAND_MYRESIZE_PERMISSION_LEVEL;
	public static int COMMAND_OTHERSRESIZE_PERMISSION_LEVEL;
	public static float MAX_SIZE;
	public static float HEALTH_MULTIPLIER;
	public static boolean SPEED_MODIFIER;
	public static boolean REACH_MODIFIER;
	public static boolean STRENGTH_MODIFIER;
	public static boolean HEALTH_MODIFIER;
	public static boolean HARVEST_MODIFIER;
	public static boolean JUMP_MODIFIER;
	public static float JUMP_MODIFIER_STRENGTH;
	public static boolean DO_ADJUSTED_RENDER;
	public static boolean PICKUP_SMALL_ENTITIES;
	public static boolean RIDE_BIG_ENTITIES;
	public static boolean CLIMB_SOME_BLOCKS;
	public static boolean CLIMB_WITH_SLIME;
	public static boolean GLIDE_WITH_PAPER;
	public static boolean HOT_BLOCKS_GIVE_LIFT;
	public static boolean ROSES_HURT;
	public static boolean PLANTS_SLOW_SMALL_DOWN;
	public static boolean SMALL_IS_INVISIBLE_TO_NONCATS_OR_NONSPIDERS;
	public static boolean GIANTS_CRUSH_ENTITIES;
	public static boolean SCALED_FALL_DAMAGE;
	
	public static void init(File file) {
		config = new Configuration(file);
		String category = "Gulliver Reborn Config Options";
		config.addCustomCategoryComment(category, "");

		MAX_SIZE = config.getFloat("Set the maximum player size", category, 50f, 1F, 100f, "Max player size");
		HEALTH_MULTIPLIER = config.getFloat("Set the health multiplier", category, 1.0F, Float.MIN_VALUE, Float.MAX_VALUE, "Health Multiplier");
		DO_ADJUSTED_RENDER = config.getBoolean("Player render is more normal at small sizes, but may cause problems with other mods", category, true, "Enable the re-scaled player render?");
		PICKUP_SMALL_ENTITIES = config.getBoolean("Enable/disable the ability to pick up small entities", category, true, "Can players pick up smaller entities?");
		RIDE_BIG_ENTITIES = config.getBoolean("Enable/disable the ability to ride large entities with String", category, true, "Can small players ride bigger entities with String?");
		CLIMB_SOME_BLOCKS = config.getBoolean("Enable/disable the ability to climb some blocks (dirt, grass, leaves, etc)", category, true, "Are some blocks naturally climbable?");
		CLIMB_WITH_SLIME = config.getBoolean("Enable/disable the ability to climb with Slimeballs or Slime Blocks", category, true, "Can small players climb with Slimeballs/Slime Blocks?");
		GLIDE_WITH_PAPER = config.getBoolean("Enable/disable the ability to glide with paper", category, true, "Can small players glide with paper?");
		HOT_BLOCKS_GIVE_LIFT = config.getBoolean("Enable/disable hot blocks giving gliding players lift (requires the ability to glide with paper to be enabled)", category, true, "Do hot blocks give lift?");
		ROSES_HURT = config.getBoolean("Enable/disable rose/poppy thorns", category, true, "Do Rose Bushes/Poppies hurt small players?");
		PLANTS_SLOW_SMALL_DOWN = config.getBoolean("Enable/disable plants slowing down small players", category, true, "Do small players get slowed by plants?");
		SMALL_IS_INVISIBLE_TO_NONCATS_OR_NONSPIDERS = config.getBoolean("Enable/disable the ability for small players to be unnoticed by non-ocelots and non-spiders", category, true, "Are small players undetected by non-ocelots/non-spiders?");
		GIANTS_CRUSH_ENTITIES = config.getBoolean("Enable/disable the ability for giants to crush small entities", category, true, "Can giants crush small entities?");
		SCALED_FALL_DAMAGE = config.getBoolean("Enable/disable scaled fall damage", category, true, "Does fall damage scale with size?");
		SPEED_MODIFIER = config.getBoolean("Enable/disable the speed modifier", category, true, "Speed changes on resize");
		REACH_MODIFIER = config.getBoolean("Enable/disable the reach modifier", category, true, "Reach distance changes on resize");
		STRENGTH_MODIFIER = config.getBoolean("Enable/disable the strength modifier", category, true, "Strength changes on resize");
		HEALTH_MODIFIER = config.getBoolean("Enable/disable the health modifier", category, true, "Health changes on resize");
		HARVEST_MODIFIER = config.getBoolean("Enable/disable the harvest speed modifier", category, true, "Harvest speed is scaled with size");
		JUMP_MODIFIER = config.getBoolean("Enable/disable the jump height modifier", category, true, "Jump height is scaled with size");
		JUMP_MODIFIER_STRENGTH = config.getFloat("Amount to change the jump height by proportional to size", category, 0.5f, 0.01f, 100f, "Only active when Jump Modifier enabled");
		COMMAND_MYRESIZE_PERMISSION_LEVEL = config.getInt("Permission level for the /mysize command", category, 0, 0, 3, "See the Minecraft wiki for permission level info");
		COMMAND_OTHERSRESIZE_PERMISSION_LEVEL = config.getInt("Permission level for the /basesize command", category, 2, 0, 3, "See the Minecraft wiki for permission level info");

		config.save();
	}
	
	public static void registerConfig(FMLPreInitializationEvent event) {
		GulliverReborn.config = new File(event.getModConfigurationDirectory() + "/" + GulliverReborn.MODID);
		//noinspection ResultOfMethodCallIgnored
		GulliverReborn.config.mkdirs();
		init(new File(GulliverReborn.config.getPath(), GulliverReborn.MODID + ".cfg"));
	}
}
