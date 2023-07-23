package de.Breakcraft.Bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Listeners implements Listener {
    public static List<Integer> takedPins = new ArrayList<>();
    public static HashMap<ProxiedPlayer, Boolean> verified = new HashMap<>();
    public static List<UUID> joined = new ArrayList<>();
    public static List<UUID> banned = new ArrayList<>();
    private static Random random = new Random();

    public Listeners(Plugin plugin) {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if(e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
            boolean allowed = true;
            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from Bans where uuid = ?")) {
                preparedStatement.setString(1, String.valueOf(p.getUniqueId()));
                ResultSet set = preparedStatement.executeQuery();
                while(set.next()) {
                    if(!set.getBoolean("passing")) {
                        String disallow = "§5----------------------------- Breakcraft Netzwerk -----------------------------\n\n\n" +
                                "§cDu wurdest gebannt !\n\n" +
                                "§cGrund: §e";
                        String reason = set.getString("reason");
                        double endsUp = set.getDouble("ends-up");
                        if(reason != null) disallow += reason;
                        else disallow += "Nicht angegeben";
                        if(endsUp != 0) {
                            disallow +="\n\n§aAblauf des Bannes: §e" + ConvertMilliSecondsToFormattedDate((long) endsUp);
                        }
                        disallow += "\n\n\n§5----------------------------- Breakcraft Netzwerk -----------------------------";
                        banned.add(p.getUniqueId());
                        e.getPlayer().disconnect(TextComponent.fromLegacyText(disallow));
                        allowed = false;
                    }
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            if(allowed) {
                joined.add(p.getUniqueId());
                ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§e[§a+§e] " + p.getName()));
                try(Connection connection = Main.dataSource.getConnection(); PreparedStatement selectStatement = connection.prepareStatement("UPDATE `ServerInfo` SET value = ? WHERE id = ?")) {
                    selectStatement.setString(1, String.valueOf(ProxyServer.getInstance().getOnlineCount()));
                    selectStatement.setInt(2, 1);
                    selectStatement.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } /** else {
            if(!verified.get(p)) {
                e.setCancelled(true);
                p.sendMessage(TextComponent.fromLegacyText("§cDu bist nicht verifiziert !"));
            }
        }
         **/
    }

    /**
    @EventHandler
    public void onChat(ChatEvent e) {
        if(e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            if(!verified.get(p)) {
                String message = e.getMessage();
                if(!(message.startsWith(" "))) {
                    if(message.contains("#")) {
                        boolean exists = false;
                        try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Verifications` where `discord-tag` = ?")) {
                            preparedStatement.setString(1, message);
                            ResultSet set = preparedStatement.executeQuery();
                            if((exists = set.next())) {
                                p.sendMessage(TextComponent.fromLegacyText("§aDieser Discord Tag ist schon vergeben !"));
                                e.setCancelled(true);
                            }
                            set.close();
                            preparedStatement.close();
                        } catch (SQLException e2) {
                            e2.printStackTrace();
                        }
                        if(!exists) {
                            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("update `Verifications` set `discord-tag` = ? where uuid = ?")) {
                                preparedStatement.setString(1, message);
                                preparedStatement.setString(2, String.valueOf(p.getUniqueId()));
                                preparedStatement.executeUpdate();
                                p.sendMessage(TextComponent.fromLegacyText("§aDein Tag wurde auf §e" + message + " §agesetzt !"));
                                preparedStatement.close();
                            } catch (SQLException e2) {
                                e2.printStackTrace();
                            }
                        }
                    } else {
                        p.sendMessage(TextComponent.fromLegacyText("§cDas ist definitiv kein Discord Tag"));
                        e.setCancelled(true);
                    }
                } else {
                    p.sendMessage(TextComponent.fromLegacyText("§cDas ist definitiv kein Discord Tag"));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectedEvent e) {
        if(joined.contains(e.getPlayer().getUniqueId())) {
            joined.remove(e.getPlayer().getUniqueId());
            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Verifications` where uuid = ?")) {
                preparedStatement.setString(1, String.valueOf(e.getPlayer().getUniqueId()));
                ResultSet set = preparedStatement.executeQuery();
                if(set.next()) {
                    boolean verified2 = set.getBoolean("verified");
                    if(!verified2) {
                        verified.put(e.getPlayer(), false);
                        TextComponent component = new TextComponent(ChatColor.DARK_PURPLE + "Discord Server");
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_PURPLE + "https://discord.gg/8emvMcT8u5").create()));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/8emvMcT8u5"));
                        component.addExtra(" " + ChatColor.GREEN + "um dich zu verifizieren !");
                        TextComponent component2 = new TextComponent(ChatColor.GREEN + "Gehe auf unseren ");
                        component2.addExtra(component);
                        e.getPlayer().sendMessage(component2);
                        int pin = set.getInt("pin");
                        e.getPlayer().sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Deine Verifizierungspin: " + ChatColor.YELLOW + pin));
                        e.getPlayer().sendMessage(TextComponent.fromLegacyText("§aSchreibe deinen vollen Discord-Tag in den Chat !"));
                        e.getPlayer().sendMessage(TextComponent.fromLegacyText("§aBeispiel: §eDiscord#0001"));
                    } else verified.put(e.getPlayer(), true);
                } else {
                    verified.put(e.getPlayer(), false);
                    TextComponent component = new TextComponent(ChatColor.DARK_PURPLE + "Discord Server");
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_PURPLE + "https://breakcraft.de/discord").create()));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://breakcraft.de/discord"));
                    component.addExtra(" " + ChatColor.GREEN + "um dich zu verifizieren !");
                    TextComponent component2 = new TextComponent(ChatColor.GREEN + "Gehe auf unseren ");
                    component2.addExtra(component);
                    e.getPlayer().sendMessage(component2);
                    int pin = 0;
                    while(pin < 1000) {
                        pin = random.nextInt(9999);
                        while(takedPins.contains(pin)) pin = random.nextInt(9999);
                    }
                    takedPins.add(pin);
                    e.getPlayer().sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Deine Verifizierungspin: " + ChatColor.YELLOW + pin));
                    e.getPlayer().sendMessage(TextComponent.fromLegacyText("§aSchreibe deinen vollen Discord-Tag in den Chat !"));
                    e.getPlayer().sendMessage(TextComponent.fromLegacyText("§aBeispiel: §eDiscord#0001"));
                    PreparedStatement preparedStatement2 = connection.prepareStatement("insert into Verifications values (?, ?, ?, ?)");
                    preparedStatement2.setString(1, String.valueOf(e.getPlayer().getUniqueId()));
                    preparedStatement2.setNull(2, Types.VARCHAR);
                    preparedStatement2.setInt(3, pin);
                    preparedStatement2.setBoolean(4, false);
                    preparedStatement2.executeUpdate();
                    preparedStatement2.close();
                }
                set.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }
     **/

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        if(!(banned.contains(e.getPlayer().getUniqueId()))) {
            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement selectStatement = connection.prepareStatement("UPDATE `ServerInfo` SET value = ? WHERE id = ?")) {
                selectStatement.setString(1, String.valueOf(ProxyServer.getInstance().getOnlineCount() - 1));
                selectStatement.setInt(2, 1);
                selectStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§e[§c-§e] " + e.getPlayer().getName()));
        } else banned.remove(e.getPlayer().getUniqueId());
    }

    public static String ConvertMilliSecondsToFormattedDate(long milliSeconds) {
        Instant instant = Instant.ofEpochMilli(milliSeconds);
        ZoneId id = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , id );
        String dateFormat = "dd-MM-yyyy hh:mm a";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return zdt.format(formatter);
    }

}
