package de.Breakcraft.Bungee.Commands;

import de.Breakcraft.Bungee.Main;
import de.Breakcraft.Bungee.MojangAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.*;
import java.util.UUID;

public class Tempban extends Command {

    public Tempban() {
        super("tempban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(p.hasPermission("breakcraft.tempban")) {
                UUID uuid = null;
                boolean isPermBanned = false;
                int id = -1;
                double endsup = 0;
                if(args.length >= 1) {
                    if(ProxyServer.getInstance().getPlayer(args[0]) != null) uuid = ProxyServer.getInstance().getPlayer(args[0]).getUniqueId();
                    else uuid = MojangAPI.getUUIDByUsername(args[0]);
                    if(uuid == null) {
                        p.sendMessage(TextComponent.fromLegacyText("§cDieser Spieler existiert nicht !"));
                        return;
                    }
                    try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Bans` where uuid = ?")) {
                        preparedStatement.setString(1, String.valueOf(uuid));
                        ResultSet set = preparedStatement.executeQuery();
                        while (set.next()) {
                            if(!set.getBoolean("passing")) {
                                if(set.getDouble("ends-up") != 0) {
                                    id = set.getInt("id");
                                    endsup = set.getDouble("ends-up");
                                } else isPermBanned = true;
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if(!isPermBanned) {
                    if(args.length == 2) {
                        if(id != -1) {
                            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("update `Bans` set `ends-up` = ? where id = ?")) {
                                String numberRaw = args[1].substring(0, args[1].length() - 1);
                                int number = Integer.parseInt(numberRaw);
                                char timeunit = args[1].toCharArray()[args[1].length() - 1];
                                if(timeunit == 'm') {
                                    preparedStatement.setDouble(1, endsup + (number * 60000));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Ban des Spieler §e" + args[0] + " §awurde um §e" + number + " Minuten §averlängert !"));
                                } else if(timeunit == 'h') {
                                    preparedStatement.setDouble(1, endsup + (number * 60000 * 60));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Ban des Spieler §e" + args[0] + " §awurde um §e" + number + " Stunden §averlängert !"));
                                } else if(timeunit == 'd') {
                                    preparedStatement.setDouble(1, endsup + (number * 60000 * 60 * 24));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Ban des Spieler §e" + args[0] + " §awurde um §e" + number + " Tage §averlängert !"));
                                } else p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (NumberFormatException e) {
                                p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into `Bans` values (?, ?, ?, ?, ?, ?)")) {
                                String numberRaw = args[1].substring(0, args[1].length() - 1);
                                int number = Integer.parseInt(numberRaw);
                                char timeunit = args[1].toCharArray()[args[1].length() - 1];
                                if(timeunit == 'm') {
                                    Main.bans++;
                                    preparedStatement.setInt(1, Main.bans);
                                    preparedStatement.setString(2, String.valueOf(uuid));
                                    preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                                    preparedStatement.setNull(4, Types.VARCHAR);
                                    preparedStatement.setBoolean(5, false);
                                    preparedStatement.setDouble(6, System.currentTimeMillis() + (number * 60000));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Spieler §e" + args[0] + " §awurde für §e" + number + " Minuten §agebannt !"));
                                    if(ProxyServer.getInstance().getPlayer(uuid) != null) ProxyServer.getInstance().getPlayer(uuid).disconnect(TextComponent.fromLegacyText("Error during listening"));
                                } else if(timeunit == 'h') {
                                    Main.bans++;
                                    preparedStatement.setInt(1, Main.bans);
                                    preparedStatement.setString(2, String.valueOf(uuid));
                                    preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                                    preparedStatement.setNull(4, Types.VARCHAR);
                                    preparedStatement.setBoolean(5, false);
                                    preparedStatement.setDouble(6, System.currentTimeMillis() + (number * 60000 * 60));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Spieler §e" + args[0] + " §awurde für §e" + number + " Stunden §agebannt !"));
                                    if(ProxyServer.getInstance().getPlayer(uuid) != null) ProxyServer.getInstance().getPlayer(uuid).disconnect(TextComponent.fromLegacyText("Error during listening"));
                                } else if(timeunit == 'd') {
                                    Main.bans++;
                                    preparedStatement.setInt(1, Main.bans);
                                    preparedStatement.setString(2, String.valueOf(uuid));
                                    preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                                    preparedStatement.setNull(4, Types.VARCHAR);
                                    preparedStatement.setBoolean(5, false);
                                    preparedStatement.setDouble(6, System.currentTimeMillis() + (number * 60000 * 60 * 24));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Spieler §e" + args[0] + " §awurde für §e" + number + " Tage §agebannt !"));
                                    if(ProxyServer.getInstance().getPlayer(uuid) != null) ProxyServer.getInstance().getPlayer(uuid).disconnect(TextComponent.fromLegacyText("Error during listening"));
                                } else p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (NumberFormatException e) {
                                p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if(args.length > 2) {
                        if(id != -1) {
                            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("update `Bans` set `ends-up` = ? where id = ?")) {
                                String numberRaw = args[1].substring(0, args[1].length() - 1);
                                int number = Integer.parseInt(numberRaw);
                                char timeunit = args[1].toCharArray()[args[1].length() - 1];
                                if(timeunit == 'm') {
                                    preparedStatement.setDouble(1, endsup + (number * 60000));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Ban des Spieler §e" + args[0] + " §awurde um §e" + number + " Minuten §averlängert !"));
                                } else if(timeunit == 'h') {
                                    preparedStatement.setDouble(1, endsup + (number * 60000 * 60));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Ban des Spieler §e" + args[0] + " §awurde um §e" + number + " Stunden §averlängert !"));
                                } else if(timeunit == 'd') {
                                    preparedStatement.setDouble(1, endsup + (number * 60000 * 60 * 24));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Ban des Spieler §e" + args[0] + " §awurde um §e" + number + " Tage §averlängert !"));
                                } else p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (NumberFormatException e) {
                                p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into `Bans` values (?, ?, ?, ?, ?, ?)")) {
                                String numberRaw = args[1].substring(0, args[1].length() - 1);
                                int number = Integer.parseInt(numberRaw);
                                char timeunit = args[1].toCharArray()[args[1].length() - 1];
                                String reason = "";
                                for(int i = 2; i < args.length; i++) {
                                    if((i + 1) == args.length) reason += args[i];
                                    else reason += args[i] + " ";
                                }
                                if(timeunit == 'm') {
                                    Main.bans++;
                                    preparedStatement.setInt(1, Main.bans);
                                    preparedStatement.setString(2, String.valueOf(uuid));
                                    preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                                    preparedStatement.setString(4, reason);
                                    preparedStatement.setBoolean(5, false);
                                    preparedStatement.setDouble(6, System.currentTimeMillis() + (number * 60000));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Spieler §e" + args[0] + " §awurde für §e" + number + " Minuten §agebannt !"));
                                    if(ProxyServer.getInstance().getPlayer(uuid) != null) ProxyServer.getInstance().getPlayer(uuid).disconnect(TextComponent.fromLegacyText("Error during listening"));
                                } else if(timeunit == 'h') {
                                    Main.bans++;
                                    preparedStatement.setInt(1, Main.bans);
                                    preparedStatement.setString(2, String.valueOf(uuid));
                                    preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                                    preparedStatement.setString(4, reason);
                                    preparedStatement.setBoolean(5, false);
                                    preparedStatement.setDouble(6, System.currentTimeMillis() + (number * 60000 * 60));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Spieler §e" + args[0] + " §awurde für §e" + number + " Stunden §agebannt !"));
                                    if(ProxyServer.getInstance().getPlayer(uuid) != null) ProxyServer.getInstance().getPlayer(uuid).disconnect(TextComponent.fromLegacyText("Error during listening"));
                                } else if(timeunit == 'd') {
                                    Main.bans++;
                                    preparedStatement.setInt(1, Main.bans);
                                    preparedStatement.setString(2, String.valueOf(uuid));
                                    preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                                    preparedStatement.setString(4, reason);
                                    preparedStatement.setBoolean(5, false);
                                    preparedStatement.setDouble(6, System.currentTimeMillis() + (number * 60000 * 60 * 24));
                                    preparedStatement.executeUpdate();
                                    p.sendMessage(TextComponent.fromLegacyText("§cDer Spieler §e" + args[0] + " §awurde für §e" + number + " Tage §agebannt !"));
                                    if(ProxyServer.getInstance().getPlayer(uuid) != null) ProxyServer.getInstance().getPlayer(uuid).disconnect(TextComponent.fromLegacyText("Error during listening"));
                                } else p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (NumberFormatException e) {
                                p.sendMessage(TextComponent.fromLegacyText("§cUngültiges Argument: §e" + args[1]));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    } else p.sendMessage(TextComponent.fromLegacyText("§cNutze §e/tempban [Spieler] [Time] [Reason]"));
                } else p.sendMessage(TextComponent.fromLegacyText("§cDieser Spieler hat einen Permaban !"));
            } else p.sendMessage(TextComponent.fromLegacyText("§cDazu hast du nicht die Rechte "));
        }
    }

}
