package de.Breakcraft.Challenges.Listeners;

import de.Breakcraft.Challenges.Challenges.Challenge;
import de.Breakcraft.Challenges.Challenges.LowLive;
import de.Breakcraft.Challenges.Challenges.NoneArmor;
import de.Breakcraft.Challenges.Commands.Challenges;
import de.Breakcraft.Challenges.Main;
import de.Breakcraft.Challenges.Utils.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinListener implements Listener {
    private Object taskID;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        if(Main.getInstance().getCurrentHoster() == null) {
            Main.getInstance().setCurrentHoster(e.getPlayer().getUniqueId());
        }

        PermissionUser user = PermissionsEx.getUser(e.getPlayer());
        e.getPlayer().setPlayerListName(user.getPrefix().replace('&', '§') + " " + e.getPlayer().getName());
        if(Main.getInstance().getConfig().get("forced-spawn") != null) {
            Location forcedSpawn = (Location) Main.getInstance().getConfig().get("forced-spawn");
            e.getPlayer().teleport(forcedSpawn);
        }
        e.setJoinMessage(null);

        ScoreboardManager sbm = Bukkit.getScoreboardManager();
        Scoreboard board = sbm.getNewScoreboard();
        Objective objective = board.registerNewObjective("dummy", "");
        objective.setDisplayName(ChatColor.DARK_PURPLE + "Breakcraft.de");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score blank = objective.getScore("  ");
        blank.setScore(1);
        Score rank = objective.getScore("   " + user.getPrefix().replace('&', '§'));
        rank.setScore(2);
        Score rankDesc = objective.getScore("§bRank:");
        rankDesc.setScore(3);
        Score blank3 = objective.getScore(" ");
        blank3.setScore(4);
        int count = 0;
        try {
            String sql = "SELECT * from ServerInfo WHERE name = 'PlayerCount'";
            PreparedStatement preparedStatement = Main.getInstance().getSqlConnection().prepareStatement(sql);
            ResultSet set = preparedStatement.executeQuery(sql);
            set.next();
            count = set.getInt("value");
            set.close();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        Score players = objective.getScore("   §e" + count + " §b/ §e1000");
        players.setScore(5);
        Score playersDesc = objective.getScore("§bSpieler:");
        playersDesc.setScore(6);
        Score blank4 = objective.getScore("    ");
        blank4.setScore(7);
        Score Gamemode = objective.getScore("   §eChallenges");
        Gamemode.setScore(8);
        Score GamemodeDesc = objective.getScore("§bGamemode:");
        GamemodeDesc.setScore(9);
        Score blank5 = objective.getScore("     ");
        blank5.setScore(10);

        e.getPlayer().setScoreboard(board);
        Main.getInstance().getInfos().put(e.getPlayer(), count);

        if(!(NoneArmor.instance.enabled)) {
            if(e.getPlayer().getInventory().getBoots() != null) {
                if(e.getPlayer().getInventory().getBoots().getType() == Material.BARRIER) {
                    e.getPlayer().getInventory().setHelmet(null);
                    e.getPlayer().getInventory().setChestplate(null);
                    e.getPlayer().getInventory().setLeggings(null);
                    e.getPlayer().getInventory().setBoots(null);
                }
            }
        }

        if(!(LowLive.instance.enabled)) {
            if(e.getPlayer().getMaxHealth() == (2 * 6)) {
                e.getPlayer().resetMaxHealth();
            }
        }

        if(Main.getInstance().getConfig().get("forced-spawn") != null) {
            e.getPlayer().teleport((Location) Main.getInstance().getConfig().get("forced-spawn"));
        }

        if(taskID != null) {
            Bukkit.getScheduler().cancelTask((Integer) taskID);
            taskID = null;
        }
        PlayerListener.startedPlayers.add(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Main.getInstance().getInfos().remove(e.getPlayer());
        if(!(e.getPlayer().getWorld().equals("world"))) {
            if(Challenges.lastLocations.containsKey(e.getPlayer().getUniqueId())) {
                Challenges.lastLocations.replace(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
            } else {
                Challenges.lastLocations.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
            }
        }
        if(Bukkit.getOnlinePlayers().size() == 0) {
            taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage(Main.PREFIX + " §aChallenge wurde durch AFK beendet !");
                    for(Challenge challenge : Main.getInstance().getChallengeManager().challenges) {
                        if(challenge.enabled) {
                            challenge.enabled = false;
                            challenge.onChallengeDisable();
                        }
                    }
                    for(Player p : Bukkit.getOnlinePlayers()) p.getInventory().clear();
                    PlayerListener.startedPlayers.clear();
                    Main.getInstance().getWorldManager().deleteGameWorld();
                    Bukkit.broadcastMessage(Main.PREFIX + " §aWelten wurden gelöscht und Challenges deaktiviert !");
                    taskID = null;
                    Main.getInstance().setChallengeRunning(false);
                    Main.getInstance().getTimer().reset();
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(Main.getInstance().getForcedSpawn() != null)  {
                            p.teleport(Main.getInstance().getForcedSpawn());
                        } else {
                            p.teleport(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY).getSpawnLocation());
                        }
                        p.setGameMode(GameMode.SURVIVAL);
                    }
                }
            }, 86400 * 20);
        }
        if(!(e.getPlayer().getUniqueId().equals(Main.getInstance().getCurrentHoster()))) PlayerListener.startedPlayers.remove(e.getPlayer());
    }

}
