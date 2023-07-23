package de.Breakcraft.Survival.Utils;

import de.Breakcraft.Survival.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.*;

public class BlockLog implements Listener {
    private int id = 0;

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from BlockLog", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet set = preparedStatement.executeQuery();
            if(set.last()) id = set.getInt("id");
            set.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into BlockLog values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
            id++;
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, String.valueOf(e.getPlayer().getUniqueId()));
            preparedStatement.setString(3, e.getPlayer().getName());
            preparedStatement.setString(4, e.getBlockPlaced().getType().name());
            preparedStatement.setString(5, "Block Placed");
            preparedStatement.setDouble(6, System.currentTimeMillis());
            preparedStatement.setInt(7, e.getBlockPlaced().getX());
            preparedStatement.setInt(8, e.getBlockPlaced().getY());
            preparedStatement.setInt(9, e.getBlockPlaced().getZ());
            preparedStatement.setNull(10, Types.VARCHAR);
            preparedStatement.executeUpdate();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into BlockLog values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            id++;
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, String.valueOf(e.getPlayer().getUniqueId()));
            preparedStatement.setString(3, e.getPlayer().getName());
            preparedStatement.setString(4, e.getBlock().getType().name());
            preparedStatement.setString(5, "Block broken");
            preparedStatement.setDouble(6, System.currentTimeMillis());
            preparedStatement.setInt(7, e.getBlock().getX());
            preparedStatement.setInt(8, e.getBlock().getY());
            preparedStatement.setInt(9, e.getBlock().getZ());
            preparedStatement.setNull(10, Types.VARCHAR);
            preparedStatement.executeUpdate();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || !(e.isBlockInHand())) {
            try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into BlockLog values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                id++;
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, String.valueOf(e.getPlayer().getUniqueId()));
                preparedStatement.setString(3, e.getPlayer().getName());
                if(e.getClickedBlock() != null) preparedStatement.setString(4, e.getClickedBlock().getType().name());
                else preparedStatement.setString(4, "null");
                preparedStatement.setString(5, "Block interacted");
                preparedStatement.setDouble(6, System.currentTimeMillis());
                if(e.getClickedBlock() != null) {
                    preparedStatement.setInt(7, e.getClickedBlock().getX());
                    preparedStatement.setInt(8, e.getClickedBlock().getY());
                    preparedStatement.setInt(9, e.getClickedBlock().getZ());
                } else {
                    preparedStatement.setInt(7, (int) e.getPlayer().getLocation().getX());
                    preparedStatement.setInt(8, (int) e.getPlayer().getLocation().getY());
                    preparedStatement.setInt(9, (int) e.getPlayer().getLocation().getZ());
                }
                String further = "";
                if(e.getItem() != null) further += " | Interacted with: " + e.getItem().getType().name();
                preparedStatement.setString(10, further);
                preparedStatement.executeUpdate();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

}
