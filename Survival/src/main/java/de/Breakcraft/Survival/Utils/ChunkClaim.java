package de.Breakcraft.Survival.Utils;

import de.Breakcraft.Survival.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class ChunkClaim {
  public int id;
  
  public String world;
  
  public int chunkX;
  
  public int chunkZ;
  
  public UUID owner;
  
  public List<UUID> trusted;
  
  public ChunkClaim(Chunk claim, UUID owner, List<UUID> trusted) {
    this.id = (Main.ccm.lastId + 1);
    Main.ccm.lastId = (Main.ccm.lastId + 1);
    this.world = claim.getWorld().getName();
    this.chunkX = claim.getX();
    this.chunkZ = claim.getZ();
    this.owner = owner;
    if (trusted != null) {
      this.trusted = trusted;
    } else {
      this.trusted = new ArrayList<>();
    }
  }
  
  public OfflinePlayer getOwner() {
    return Bukkit.getOfflinePlayer(this.owner);
  }
  
  public boolean isTrusted(Player p) {
    return isTrusted(p.getUniqueId());
  }
  
  public boolean isTrusted(UUID id) {
    return this.trusted.contains(id);
  }
  
  public void addTrusted(Player p) {
    addTrusted(p.getUniqueId());
  }
  
  public void addTrusted(UUID id) {
    this.trusted.add(id);
    updateDatabase();
  }
  
  public void removeTrusted(Player p) {
    removeTrusted(p.getUniqueId());
  }
  
  public void removeTrusted(UUID id) {
    if (isTrusted(id)) {
      this.trusted.remove(id);
      updateDatabase();
    } 
  }
  
  public void saveToDatabase() {
    try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into ChunkClaims values ( ?, ?, ?, ?, ?, ?)")) {
      preparedStatement.setInt(1, this.id);
      preparedStatement.setString(2, String.valueOf(this.owner));
      preparedStatement.setInt(3, this.chunkX);
      preparedStatement.setInt(4, this.chunkZ);
      preparedStatement.setString(5, this.world);
      StringJoiner joiner = new StringJoiner(",");
      for (UUID uuid : this.trusted)
        joiner.add(String.valueOf(uuid)); 
      preparedStatement.setString(6, joiner.toString());
      preparedStatement.executeUpdate();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } 
  }
  
  public void updateDatabase() {
    try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("update ChunkClaims set trusted = ? where id = ?")) {
      StringJoiner joiner = new StringJoiner(",");
      for (UUID uuid : this.trusted)
        joiner.add(String.valueOf(uuid)); 
      preparedStatement.setString(1, joiner.toString());
      preparedStatement.setInt(2, this.id);
      preparedStatement.executeUpdate();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } 
  }
}
