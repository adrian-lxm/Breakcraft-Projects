package de.Breakcraft.Lobby.Listener;


import de.Breakcraft.Lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinListener implements Listener {
    private Scoreboard prefixManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        PermissionUser user = PermissionsEx.getUser(e.getPlayer());
        if(prefixManager == null) prefixManager = Bukkit.getScoreboardManager().getNewScoreboard();
        if(!(user.inGroup("Spieler"))) user.addGroup("Spieler");
        if(prefixManager.getTeam(user.getGroups()[0].getName()) == null) {
            prefixManager.registerNewTeam(user.getGroups()[0].getName()).setPrefix(" " + user.getPrefix().replace('&', '§') + " ");
            prefixManager.getTeam(user.getGroups()[0].getName()).addPlayer(e.getPlayer());
        }
        e.getPlayer().setPlayerListName(" " + user.getPrefix().replace('&', '§') + " " + e.getPlayer().getName());
        e.getPlayer().setDisplayName(" " + user.getPrefix().replace('&', '§') + " " + e.getPlayer().getName());
        if(Main.getInstance().getConfig().get("forced-spawn") != null) {
            Location forcedSpawn = (Location) Main.getInstance().getConfig().get("forced-spawn");
            e.getPlayer().teleport(forcedSpawn);
        }
        e.getPlayer().getInventory().setItem(0, Main.getCompassItem());


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
        try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from ServerInfo WHERE `id` = 1")) {
            ResultSet set = preparedStatement.executeQuery();
            set.next();
            count = Integer.parseInt(set.getString(2));
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        Score players = objective.getScore("   §e" + count + " §b/ §e1000");
        players.setScore(5);
        Score playersDesc = objective.getScore("§bSpieler:");
        playersDesc.setScore(6);
        Score blank4 = objective.getScore("    ");
        blank4.setScore(7);
        Score Gamemode = objective.getScore("   §eLobby");
        Gamemode.setScore(8);
        Score GamemodeDesc = objective.getScore("§bGamemode:");
        GamemodeDesc.setScore(9);
        Score blank5 = objective.getScore("     ");
        blank5.setScore(10);

        e.getPlayer().setScoreboard(board);
        Main.infos.put(e.getPlayer(), count);
        if(e.getPlayer().hasPermission("breakcraft.doublejump")) {
            e.getPlayer().setAllowFlight(true);
            DoubleJumpListeners.doubleJump.put(e.getPlayer(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        if(DoubleJumpListeners.doubleJump.containsKey(e.getPlayer())) DoubleJumpListeners.doubleJump.remove(e.getPlayer());
        PermissionUser user = PermissionsEx.getUser(e.getPlayer());
        prefixManager.getTeam(user.getGroups()[0].getName()).removePlayer(e.getPlayer());
    }

}
