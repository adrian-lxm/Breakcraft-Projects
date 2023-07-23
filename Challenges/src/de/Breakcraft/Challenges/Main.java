package de.Breakcraft.Challenges;

import de.Breakcraft.Challenges.Challenges.Challenge;
import de.Breakcraft.Challenges.Commands.Challenges;
import de.Breakcraft.Challenges.Commands.ForcedSpawn;
import de.Breakcraft.Challenges.Listeners.InventoryListener;
import de.Breakcraft.Challenges.Listeners.JoinListener;
import de.Breakcraft.Challenges.Listeners.PlayerListener;
import de.Breakcraft.Challenges.Utils.ChallengeManager;
import de.Breakcraft.Challenges.Utils.WorldManager;
import de.Breakcraft.Challenges.Utils.config.ConfigManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import java.sql.*;
import java.util.*;

public class Main extends JavaPlugin {
	public static final String PREFIX = "[§bBreakcraft§f]";
	private static Main instance;

	private Connection sqlConnection;

	public Connection getSqlConnection() {
		return sqlConnection;
	}

	private UUID currentHoster;

	public UUID getCurrentHoster() {
		return currentHoster;
	}

	public void setCurrentHoster(UUID currentHoster) {
		this.currentHoster = currentHoster;
	}

	private boolean challengeRunning = false;

	public boolean isChallengeRunning() {
		return challengeRunning;
	}

	public void setChallengeRunning(boolean challengeRunning) {
		this.challengeRunning = challengeRunning;
	}

	Timer timer = new Timer();

	public Timer getTimer() {
		return timer;
	}

	private ChallengeManager challengeManager = new ChallengeManager();

	public ChallengeManager getChallengeManager() {
		return challengeManager;
	}

	private ConfigManager configManager;

	public ConfigManager getConfigManager() {
		return configManager;
	}

	private WorldManager worldManager;

	public WorldManager getWorldManager() {
		return worldManager;
	}

	private List<Integer> schedulers = new ArrayList<>();
	private HashMap<Player, Integer> infos = new HashMap<>();

	public HashMap<Player, Integer> getInfos() {
		return infos;
	}

	private Location forcedSpawn;

	public Location getForcedSpawn() {
		return forcedSpawn;
	}

	private Object taskID;

	public Object getTaskID() {
		return taskID;
	}

	private final String sqlUsername = "";
	private final String sqlPassword = "";

	@Override
	public void onEnable() {
		instance = this;
		configManager = new ConfigManager();
		worldManager = new WorldManager();

		try {
			Class.forName("com.mysql.jdbc.Driver");

			sqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Breakcraft?characterEncoding=utf8"
					+ "&autoReconnect=true", sqlUsername, sqlPassword);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}


		getCommand("forcedspawn").setExecutor(new ForcedSpawn());
		getCommand("challenges").setExecutor(new Challenges());

		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

		forcedSpawn = (Location) getConfig().get("forced-spawn");

		startPlayerScheduler();
		startChangeScheduler();
	}

	@Override
	public void onDisable() {
		for (Challenge challenge : challengeManager.challenges) {
			if (challenge.enabled) {
				challenge.enabled = false;
				challenge.onChallengeDisable();
			}
		}
	}

	private void startPlayerScheduler() {
		int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().size() > 0) {
					int count = 0;

					try {
						String sql = "SELECT * from ServerInfo WHERE id = 1";
						PreparedStatement preparedStatement = Main.getInstance().sqlConnection.prepareStatement(sql);
						ResultSet set = preparedStatement.executeQuery(sql);
						set.next();
						count = set.getInt("value");
						set.close();
					} catch (SQLException | NumberFormatException e) {
						e.printStackTrace();
						try {
							Main.getInstance().sqlConnection = DriverManager.getConnection("jdbc:mysql://localhost" +
									":3306/Breakcraft" + "?characterEncoding=utf8&autoReconnect=true",
									"client_minecraft", "nAuE@&3d]!(h");
						} catch (SQLException throwables) {
							throwables.printStackTrace();
						}
					}

					if (challengeRunning) timer.next();
					for (Player p : Bukkit.getOnlinePlayers()) {
						boolean updated = false;
						Scoreboard board = p.getScoreboard();
						Objective objective = (Objective) board.getObjectives().toArray()[0];
						Score players = objective.getScore("   §e" + count + " §b/ §e1000");
						if (!players.isScoreSet()) {
							board.resetScores("   §e" + infos.get(p) + " §b/ §e1000");
							players.setScore(5);
							infos.replace(p, count);
							updated = true;
						}

						if (updated) {
							p.setScoreboard(board);
						}


						if (challengeRunning) {
							if (currentHoster.equals(p.getUniqueId()))
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
										TextComponent.fromLegacyText("§a" + timer.formatToString() + " §5| §eHoster"));
							else
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
										TextComponent.fromLegacyText("§a" + timer.formatToString() + " §5| §eSpieler"
										));
						} else {
							if (currentHoster.equals(p.getUniqueId()))
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aDu "
										+ "bist der §eHoster"));
							else
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aDu "
										+ "bist ein §eSpieler"));
						}

					}
				}
			}
		}, 0, 20);
		schedulers.add(i);
	}

	public static Main getInstance() {
		return instance;
	}

	public void startChangeScheduler() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(getInstance(), new Runnable() {
			@Override
			public void run() {
				Random random = new Random();
				int i = random.nextInt(Bukkit.getOfflinePlayers().length);
				currentHoster = Bukkit.getOfflinePlayers()[i].getUniqueId();
			}
		}, 30 * 60 * 20, 30 * 60 * 20);
	}

}
