package de.Breakcraft.Bungee.Commands;

import de.Breakcraft.Bungee.Main;
import de.Breakcraft.Bungee.MojangAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Unban extends Command {

    public Unban() {
        super("unban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(p.hasPermission("breakcraft.unban")) {
                if(args.length == 1) {
                    UUID uuid = MojangAPI.getUUIDByUsername(args[0]);
                    if(uuid == null) {
                        p.sendMessage(TextComponent.fromLegacyText("§cDieser Spieler existiert nicht !"));
                        return;
                    }
                    try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Bans` where `uuid` = ?")) {
                        preparedStatement.setString(1, String.valueOf(uuid));
                        ResultSet set = preparedStatement.executeQuery();
                        boolean isValid = false;
                        while(set.next()) {
                            if(set.getDouble("ends-up") == 0 && !(set.getBoolean("passing"))) {
                                PreparedStatement preparedStatement2 = connection.prepareStatement("update Bans set passing = ? where id = ?");
                                preparedStatement2.setBoolean(1, true);
                                preparedStatement2.setInt(2, set.getInt("id"));
                                preparedStatement2.executeUpdate();
                                isValid = true;
                                preparedStatement2.close();
                                p.sendMessage(TextComponent.fromLegacyText("§aDer Spieler wurde entbannt !"));
                                break;
                            }
                        }
                        if(!isValid) {
                            p.sendMessage(TextComponent.fromLegacyText("§cDieser Spieler ist nicht gebannt oder hat einen Tempban !"));
                        }
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                        p.sendMessage(TextComponent.fromLegacyText("§cFehler bei SQL Verbindung! Versuch es später erneut..."));
                        System.out.println("SQL Fehler reconnecting");
                    }
                } else p.sendMessage(TextComponent.fromLegacyText("§cNutze §e/ban [Spieler] §c!"));
            } else p.sendMessage(TextComponent.fromLegacyText("§cDazu hast du nicht die Rechte !"));
        }
    }

}
