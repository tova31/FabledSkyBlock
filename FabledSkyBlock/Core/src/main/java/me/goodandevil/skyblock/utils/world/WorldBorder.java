package me.goodandevil.skyblock.utils.world;

import me.goodandevil.skyblock.utils.version.NMSUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class WorldBorder {

	private static Class<?> packetPlayOutWorldBorder, packetPlayOutWorldBorderEnumClass, worldBorderClass,
			craftWorldClass;
	private static Constructor<?> packetPlayOutWorldBorderConstructor;

	static {
		try {
			packetPlayOutWorldBorder = NMSUtil.getNMSClass("PacketPlayOutWorldBorder");

			if (NMSUtil.getVersionNumber() > 10) {
				packetPlayOutWorldBorderEnumClass = packetPlayOutWorldBorder.getDeclaredClasses()[0];
			} else {
				packetPlayOutWorldBorderEnumClass = packetPlayOutWorldBorder.getDeclaredClasses()[1];
			}

			worldBorderClass = NMSUtil.getNMSClass("WorldBorder");
			craftWorldClass = NMSUtil.getCraftClass("CraftWorld");

			packetPlayOutWorldBorderConstructor = packetPlayOutWorldBorder.getConstructor(worldBorderClass,
					packetPlayOutWorldBorderEnumClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void send(Player player, Color color, double size, Location centerLocation) {
		try {
			if (centerLocation == null || centerLocation.getWorld() == null)
				return;

			if (size % 2 == 1)
				size++;

			Object worldBorder = worldBorderClass.getConstructor().newInstance();

			if (NMSUtil.getVersionNumber() < 9) {
				Field borderSize = worldBorder.getClass().getDeclaredField("d");
				borderSize.setAccessible(true);
				borderSize.set(worldBorder, size);
			} else {
				Object craftWorld = craftWorldClass.cast(centerLocation.getWorld());
				Method getHandleMethod = craftWorld.getClass().getMethod("getHandle", new Class<?>[0]);
				Object worldServer = getHandleMethod.invoke(craftWorld, new Object[0]);
				NMSUtil.setField(worldBorder, "world", worldServer, false);
			}

			Method setCenter = worldBorder.getClass().getMethod("setCenter", double.class, double.class);
			setCenter.invoke(worldBorder, centerLocation.getBlockX(), centerLocation.getBlockZ());

			Method setSize = worldBorder.getClass().getMethod("setSize", double.class);
			setSize.invoke(worldBorder, size);

			Method setWarningTime = worldBorder.getClass().getMethod("setWarningTime", int.class);
			setWarningTime.invoke(worldBorder, 0);

			Method transitionSizeBetween = worldBorder.getClass().getMethod("transitionSizeBetween", double.class,
					double.class, long.class);

			if (color == Color.Green) {
				transitionSizeBetween.invoke(worldBorder, size - 0.1D, size, 20000000L);
			} else if (color == Color.Red) {
				transitionSizeBetween.invoke(worldBorder, size, size - 1.0D, 20000000L);
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			Object packet = packetPlayOutWorldBorderConstructor.newInstance(worldBorder,
					Enum.valueOf((Class<Enum>) packetPlayOutWorldBorderEnumClass, "INITIALIZE"));
			NMSUtil.sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum Color {

		Blue, Green, Red;

	}
}