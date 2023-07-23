package de.Breakcraft.Citybuild.Listeners;

import de.Breakcraft.Citybuild.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        PermissionUser user = PermissionsEx.getUser(e.getPlayer());
        e.setJoinMessage(null);
        e.getPlayer().setPlayerListName(user.getPrefix().replace('&', '§') + " " + e.getPlayer().getName());
        ScoreboardManager sbm = Bukkit.getScoreboardManager();
        Scoreboard board = sbm.getNewScoreboard();
        Objective objective = board.registerNewObjective("dummy", "");
        objective.setDisplayName(ChatColor.DARK_PURPLE + "Breakcraft.de");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score blank = objective.getScore("  ");
        blank.setScore(1);
        double money = Main.getInstance().econ.getBalance(e.getPlayer());
        Score balance = objective.getScore("   §e" + money + " €");
        balance.setScore(2);
        Score balanceDesc = objective.getScore("§bKontostand:");
        balanceDesc.setScore(3);
        Score blank2 = objective.getScore("   ");
        blank2.setScore(4);
        Score rank = objective.getScore("   " + user.getPrefix().replace('&', '§'));
        rank.setScore(5);
        Score rankDesc = objective.getScore("§bRank:");
        rankDesc.setScore(6);
        Score blank3 = objective.getScore(" ");
        blank3.setScore(7);
        int count = 0;
        try {
            String sql = "SELECT * from ServerInfo WHERE id = 1";
            PreparedStatement preparedStatement = Main.connection.prepareStatement(sql);
            ResultSet set = preparedStatement.executeQuery(sql);
            set.next();
            count = Integer.parseInt(set.getString("value"));
            set.close();
            preparedStatement.close();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        Score players = objective.getScore("   §e" + count + " §b/ §e1000");
        players.setScore(8);
        Score playersDesc = objective.getScore("§bSpieler:");
        playersDesc.setScore(9);
        Score blank4 = objective.getScore("    ");
        blank4.setScore(10);
        Score Gamemode = objective.getScore("   §eCitybuild");
        Gamemode.setScore(11);
        Score GamemodeDesc = objective.getScore("§bGamemode:");
        GamemodeDesc.setScore(12);
        Score blank5 = objective.getScore("     ");
        blank5.setScore(13);

        e.getPlayer().setScoreboard(board);
        Main.infos.put(e.getPlayer(), new Score[] { players, balance });

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Main.infos.remove(e.getPlayer());
    }

}
