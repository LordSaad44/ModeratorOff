package me.lordsaad.modeoff.server;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class ServerProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		File configFolder = new File(event.getModConfigurationDirectory(), "/plots/");

		if (!configFolder.exists()) {
			ModeratorOff.logger.info(configFolder.getName() + " not found. Creating directory...");
			if (!configFolder.mkdirs()) {
				ModeratorOff.logger.error("SOMETHING WENT WRONG! Could not create config directory " + configFolder.getName());
				return;
			}
			ModeratorOff.logger.info(configFolder.getName() + " has been created successfully!");
		}

		directory = configFolder;

		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

		if (ConfigValues.enablePlotsAndRanks) {
			PlotRegistry.INSTANCE.setDirectory(directory);
			PlotRegistry.INSTANCE.loadPlots();
			fetchRanks();
		}
	}

	public static void fetchRanks() {
		ModeratorOff.logger.info("<<========================================================================>>");
		ModeratorOff.logger.info("About to fetch ranks...");
		try {
			for (IRank rank : RankRegistry.INSTANCE.ranks.inverse().keySet()) {
				String name = rank.getName().toLowerCase();

				ModeratorOff.logger.info("> Fetching '" + name + "' players");

				Resources.asCharSource(new URL("https://raw.githubusercontent.com/Demoniaque/ModeratorOff/master/ranks/" + name + ".txt"), Charsets.UTF_8)
						.readLines()
						.forEach(line -> {

							ModeratorOff.logger.info("    > Looking up '" + line + "'");

							// GET PLAYER UUID FROM MOJANG
							StringBuilder jsonString = new StringBuilder();
							try {
								Resources.asCharSource(new URL("https://api.mojang.com/users/profiles/minecraft/" + line), Charsets.UTF_8)
										.readLines()
										.forEach(jsonString::append);
							} catch (IOException e) {
								e.printStackTrace();
							}

							JsonElement element = new JsonParser().parse(jsonString.toString());
							if (element == null || !element.isJsonObject()) {
								ModeratorOff.logger.info("      > Could not find uuid for " + line + ".");
								return;
							}

							JsonObject object = element.getAsJsonObject();

							if (object.has("id") && object.get("id").isJsonPrimitive()) {
								String uuid = object.getAsJsonPrimitive("id").getAsJsonPrimitive().getAsString();

								// Format uuid
								uuid = uuid.substring(0, 8) + "-"
										+ uuid.substring(8, 12) + "-"
										+ uuid.substring(12, 16) + "-"
										+ uuid.substring(16, 20) + "-"
										+ uuid.substring(20);

								RankRegistry.INSTANCE.rankMap.put(rank, UUID.fromString(uuid));

								ModeratorOff.logger.info("      > Found uuid for " + line + " -> " + uuid + ". Success!");
							} else {
								ModeratorOff.logger.info("      > Could not find uuid for " + line + ".");
							}

						});

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ModeratorOff.logger.info("<<========================================================================>>");
	}
}
