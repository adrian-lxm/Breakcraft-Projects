package de.Breakcraft.Challenges.Utils.config;

import de.Breakcraft.Challenges.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {


	public enum ConfigPath {

		WORLDS("worlds", null, null),
		WORLDS_LOBBY("lobby", "lobby", WORLDS),
		WORLDS_OVERWORLD("overworld", "overworld", WORLDS),
		WORLDS_NETHER("nether", "nether", WORLDS),
		WORLDS_END("end", "end", WORLDS),
		;


		ConfigPath(String name, String defaultValue, ConfigPath parent) {
			this.parent = parent;
			this.name = name;
			this.defaultValue = defaultValue;
			if (parent != null) this.fullPath = parent.fullPath + name;
			else this.fullPath = name;
		}

		private final ConfigPath parent;

		public ConfigPath getParent() {
			return parent;
		}

		private final String name;

		public String getName() {
			return name;
		}

		private final String fullPath;

		public String getFullPath() {
			return fullPath;
		}

		private String defaultValue;

		public String getDefaultValue() {
			return defaultValue;
		}
	}

	private final FileConfiguration config;

	public ConfigManager() {
		config = Main.getInstance().getConfig();
		init();
	}

	public void init() {
		for (ConfigPath configPath : ConfigPath.values()) {
			config.addDefault(configPath.getFullPath(), configPath.getDefaultValue());
		}
		config.options().copyDefaults(true);
		Main.getInstance().saveConfig();
	}

	public void set(ConfigPath path, String value) {
		config.set(path.getFullPath(), value);
	}

	public String get(ConfigPath path) {
		return config.getString(path.getFullPath());
	}

}
