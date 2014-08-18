/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin.mobs;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.myphate.anothermobplugin.AMP;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;

/**
 *
 * @author myPhate
 */

public class Mob {
    private final AMP plugin;
    private final UUID uuid;
    private final Creature creat;
    private final MobType mtype;
    private int health = 120;
    private String region;
    private UUID world;
    private Location center;
    private Boolean isSpecial = false;
    
    public Mob(AMP ins, UUID u, Creature c, MobType mt, int h, String r, UUID w) {
        uuid = u;
        health = h;
        mtype = mt;
        region = r;
        plugin = ins;
        world = w;
        center = getCenter();
        creat = c;
    }
    
    private Location getCenter(){
        RegionManager rgm = plugin.wg.getRegionManager(plugin.getServer().getWorld(world));
        ProtectedRegion r = rgm.getRegion(region);
        Double maxXp = r.getMaximumPoint().getX();
        Double maxZp = r.getMaximumPoint().getZ();
        Double minXp = r.getMinimumPoint().getX();
        Double minZp = r.getMinimumPoint().getZ();
        
        Double yP = r.getMinimumPoint().getY();
        Double xP = (maxXp - minXp) / 2 + minXp;
        Double zP = (maxZp - minZp) / 2 + minZp;
                        
        Block b = plugin.getServer().getWorld(world).getBlockAt(xP.intValue(), yP.intValue(), zP.intValue());
        return b.getLocation();
    }
        
    public UUID getUUID(){
        return uuid;
    }
                 
    public int getMobHealth(){
        return health;
    }
    
    public void setMobHealt(int h){
        health = h;
    }
    
    public Boolean getSpecial(){
        return isSpecial;
    }
    
    public void setSpecial(Boolean s){
        isSpecial = s;
    }
    
    public String getRegion(){
        return region;
    }
    
    public Creature getEnt(){
        return creat;
    }
    
    public Location returnCenter(){
        return center;
    }
    
    public MobType getMobType(){
        return mtype;
    }
    
    public UUID getWorld(){
        return world;
    }
}
