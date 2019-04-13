package me.goodandevil.skyblock.command.commands.island;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.levelling.LevellingMaterial;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ValueCommand extends SubCommand {

	@SuppressWarnings("deprecation")
	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		LevellingManager levellingManager = skyblock.getLevellingManager();
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (player.getItemInHand() == null) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Value.Hand.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		} else {
			Materials materials = null;

			if (NMSUtil.getVersionNumber() < 13) {
				materials = Materials.requestMaterials(player.getItemInHand().getType().name(),
						(byte) player.getItemInHand().getDurability());
			} else {
				materials = Materials.fromString(player.getItemInHand().getType().name());
			}

			if (materials != null && levellingManager.containsMaterial(materials)) {
				LevellingMaterial material = levellingManager.getMaterial(materials);
				double level = (double) material.getPoints()
						/ (double) fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getInt("Island.Levelling.Division");

				messageManager.sendMessage(player,
						configLoad.getString("Command.Island.Value.Value.Message")
								.replace("%material",
										WordUtils.capitalizeFully(materials.name().toLowerCase().replace("_", " ")))
								.replace("%points", "" + material.getPoints())
								.replace("%level", "" + NumberUtil.formatNumberByDecimal(level)));
				soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Value.None.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "value";
	}

	@Override
	public String getInfoMessagePath() {
		return "Command.Island.Value.Info.Message";
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}
}