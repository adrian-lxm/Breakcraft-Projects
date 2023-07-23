package de.Breakcraft.Bungee.Commands;

import de.Breakcraft.Bungee.Main;
import de.Breakcraft.Bungee.MojangAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ban extends Command implements TabExecutor {

    public Ban() {
        super("ban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (p.hasPermission("breakcraft.ban")) {
                if (args.length == 1) {
                    UUID uuid = null;
                    if(ProxyServer.getInstance().getPlayer(args[0]) != null) uuid = ProxyServer.getInstance().getPlayer(args[0]).getUniqueId();
                    else uuid = MojangAPI.getUUIDByUsername(args[0]);
                    if (uuid == null) {
                        p.sendMessage(TextComponent.fromLegacyText("§cEin solcher Spieler existiert nicht !"));
                        return;
                    }
                    try (Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into `Bans` values (?, ?, ?, ?, ?, ?)")) {
                        preparedStatement.setInt(1, Main.bans + 1);
                        preparedStatement.setString(2, String.valueOf(uuid));
                        preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                        preparedStatement.setNull(4, Types.VARCHAR);
                        preparedStatement.setBoolean(5, false);
                        preparedStatement.setNull(6, Types.DOUBLE);
                        preparedStatement.executeUpdate();
                        if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                            String disallow = "§5----------------------------- Breakcraft Netzwerk -----------------------------\n\n\n" +
                                    "§cDu wurdest gebannt !\n\n" +
                                    "§cGrund: §eNicht angegeben";
                            disallow += "\n\n\n§5----------------------------- Breakcraft Netzwerk -----------------------------";
                            ProxyServer.getInstance().getPlayer(args[0]).disconnect(TextComponent.fromLegacyText(disallow));
                        }
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                        p.sendMessage(TextComponent.fromLegacyText("§cFehler bei SQL Verbindung! Versuch es später erneut..."));
                        System.out.println("SQL Fehler reconnecting");
                    }
                } else if (args.length > 1) {
                    String reason = "";
                    for (int i = 1; i < args.length; i++) {
                        if ((i + 1) == args.length) reason += args[i];
                        else reason += args[i] + " ";
                    }
                    UUID uuid = MojangAPI.getUUIDByUsername(args[0]);
                    if (uuid == null) {
                        p.sendMessage(TextComponent.fromLegacyText("§cEin solcher Spieler existiert nicht !"));
                        return;
                    }
                    try (Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into `Bans` values (?, ?, ?, ?, ?, ?)")) {
                        preparedStatement.setInt(1, Main.bans + 1);
                        preparedStatement.setString(2, String.valueOf(uuid));
                        preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                        preparedStatement.setString(4, reason);
                        preparedStatement.setBoolean(5, false);
                        preparedStatement.setNull(6, Types.DOUBLE);
                        preparedStatement.executeUpdate();
                        if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                            String disallow = "§5----------------------------- Breakcraft Netzwerk -----------------------------\n\n\n" +
                                    "§cDu wurdest gebannt !\n\n" +
                                    "§cGrund: §e" + reason;
                            disallow += "\n\n\n§5----------------------------- Breakcraft Netzwerk -----------------------------";
                            ProxyServer.getInstance().getPlayer(args[0]).disconnect(TextComponent.fromLegacyText(disallow));
                        }
                    } catch (SQLException e2) {
                        p.sendMessage(TextComponent.fromLegacyText("§cFehler bei SQL Verbindung! Versuch es später erneut..."));
                        e2.printStackTrace();
                    }
                } else p.sendMessage(TextComponent.fromLegacyText("§cNutze §e/ban [Spieler] [Reason]"));
            } else p.sendMessage(TextComponent.fromLegacyText("§cDazu hast du nicht die Rechte !"));
        }
    }


    @Override
    public Iterable<String> onTabComplete (CommandSender commandSender, String[]strings){
        List<String> tabcomplete = new ArrayList<>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) tabcomplete.add(p.getName());
        return tabcomplete;
    }

}