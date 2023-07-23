package de.Breakcraft.Survival.Utils;

import de.Breakcraft.Survival.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ChunkClaimManager {
  public List<ChunkClaim> claims = new ArrayList<>();
  public List<ChunkFlag> allFlags = new ArrayList<>();
  public int lastId = 0;
  
  public HashMap<OfflinePlayer, Integer> claimCount = new HashMap<>();
  
  public void initManager() {
    try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from ChunkClaims")) {
      allFlags.addAll(Arrays.asList(ChunkFlag.values()));
      ResultSet set = preparedStatement.executeQuery();
      while (set.next()) {
        UUID owner = UUID.fromString(set.getString("owner"));
        lastId = set.getInt("id");
        int chunkX = set.getInt("chunkX");
        int chunkZ = set.getInt("chunkZ");
        List<UUID> trusted = new ArrayList<>();
        String world = set.getString("world");
        ChunkFlag[] flagsArray = new ChunkFlag[0];
        if (set.getString("trusted") != null) {
          String raw = set.getString("trusted");
          if (!raw.equals(""))
            if (raw.contains(",")) {
              String[] uuids = set.getString("trusted").split(",");
              for (String uuid : uuids)
                trusted.add(UUID.fromString(uuid)); 
            } else {
              trusted.add(UUID.fromString(raw));
            }  
        }
        this.claims.add(new ChunkClaim(Bukkit.getWorld(world).getChunkAt(chunkX, chunkZ), owner, trusted));
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    }

    for (ChunkClaim claim : this.claims) {
      OfflinePlayer p = Bukkit.getOfflinePlayer(claim.owner);
      if (!this.claimCount.containsKey(p)) {
        this.claimCount.put(p, 1);
        continue;
      } 
      this.claimCount.replace(p, this.claimCount.get(p) + 1);
    } 
  }
  
  public boolean isChunkClaimed(Chunk chunk) {
    for (ChunkClaim claim : this.claims) {
      Chunk chunk1 = Bukkit.getWorld(claim.world).getChunkAt(claim.chunkX, claim.chunkZ);
      if (chunk1.equals(chunk))
        return true; 
    } 
    return false;
  }
  
  public ChunkClaim getClaimByChunk(Chunk chunk) {
    ChunkClaim claim = null;
    for (ChunkClaim claim2 : this.claims) {
      Chunk chunk1 = Bukkit.getWorld(claim2.world).getChunkAt(claim2.chunkX, claim2.chunkZ);
      if (chunk1.equals(chunk)) {
        claim = claim2;
        break;
      } 
    } 
    return claim;
  }
  
  public void createClaim(Player owner, Chunk chunk) {
    ChunkClaim cc = new ChunkClaim(chunk, owner.getUniqueId(), null);
    cc.saveToDatabase();
    this.claims.add(cc);
  }

}
