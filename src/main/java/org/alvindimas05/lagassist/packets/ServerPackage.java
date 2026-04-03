package org.alvindimas05.lagassist.packets;

import org.alvindimas05.lagassist.Main;
import org.bukkit.Bukkit;

public enum ServerPackage {

	MINECRAFTSERVER("net.minecraft.server", getServerVersion()),
	CRAFTBUKKIT("org.bukkit.craftbukkit", getServerVersion()),
	MINECRAFT("net.minecraft", getServerVersion());

	private final String path;

	ServerPackage(String basePath, String version) {
		this.path = version.isEmpty() ? basePath : basePath + "." + version;
	}

    public static String getServerVersion() {
        String craftPkg = Bukkit.getServer().getClass().getPackage().getName();

        // If the package already contains v1_* just return it (legacy versioned CraftBukkit)
        if (craftPkg.contains("v1_")) {
            return craftPkg.substring(craftPkg.lastIndexOf('.') + 1);
        }

        // Modern Paper (1.20.5+) — CraftBukkit is no longer versioned.
        // Parse version from Bukkit.getBukkitVersion() (e.g. "1.21.4-R0.1-SNAPSHOT" or "26.1-R0.1-SNAPSHOT")
        String bukkitVersion = Bukkit.getBukkitVersion(); // e.g. "26.1-R0.1-SNAPSHOT"
        String mcVersion = bukkitVersion.split("-")[0];    // e.g. "26.1"
        String[] split = mcVersion.split("\\.");
        String versionKey;
        if (split.length >= 2) {
            versionKey = split[0] + "_" + split[1] + "_";
        } else {
            versionKey = mcVersion.replace(".", "_") + "_";
        }

        // Try R1..R15
        for (int i = 1; i <= 15; i++) {
            String version = "v" + versionKey + "R" + i;
            String nmsPath = "org.bukkit.craftbukkit." + version + ".CraftServer";

            try {
                Class.forName(nmsPath);
                return version;
            } catch (ClassNotFoundException ignored) {}
        }

        // On modern Paper, CraftBukkit has no version suffix — return empty string
        Bukkit.getLogger().info(Main.PREFIX + "Modern Paper detected (no versioned CraftBukkit package), version: " + mcVersion);
        return "";
    }


	@Override
	public String toString() {
		return path;
	}

	public Class<?> getClass(String className) throws ClassNotFoundException {
		return Class.forName(this.toString() + "." + className);
	}

}
