/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin.scheduler;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.myphate.anothermobplugin.AMP;
import de.myphate.anothermobplugin.mobs.Mob;
import de.myphate.anothermobplugin.mobs.MobType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;


/**
 *
 * @author myPhate
 */

public class SpawnMobsinRegion extends MobScheduler {

    HashMap<String, SpawnMob> mmap = new HashMap<>();   // MobType-Class-Map
    HashMap<String, UUID> rwm = new HashMap<>(); // Region-World-Map
    HashMap<String, String> rmtm = new HashMap<>(); // Region-MobType-Map
    
    public SpawnMobsinRegion(AMP instance) {
        super(instance);
    }
    
    @Override
    public void Load(){
        
        for(World w : plugin.getServer().getWorlds()){
            RegionManager rgm = plugin.wg.getRegionManager(plugin.getServer().getWorld(w.getUID()));
            Map<String,ProtectedRegion> rmap = rgm.getRegions();
            for(ProtectedRegion r : rmap.values()){
                Map<Flag<?>,Object> flagmap = r.getFlags();
                
                if(!flagmap.containsKey(plugin.MobSpawn)){
                    continue;
                }
                
                String fvalue = (String)flagmap.get(plugin.MobSpawn);
                MobType mbt = plugin.MobTypeList.get(fvalue.toLowerCase());
            
                if (mbt == null) {
                    continue;
                }
                
                rwm.put(r.getId(), w.getUID());
                
                rmtm.put(r.getId(), mbt.getName());
            }
        }
        
        mmap = new HashMap<>();
        Set<String> keys = rmtm.keySet();
        
        for(String s : keys) {
            String MobTyp = rmtm.get(s);
            
            if (mmap.containsKey(MobTyp)){
                SpawnMob m = mmap.get(s);
                m.addRegion(s, rwm.get(s));
            }
            else {
                SpawnMob m = new SpawnMob(plugin.MobTypeList.get(MobTyp.toLowerCase()));
                mmap.put(MobTyp, m);
                m.addRegion(s, rwm.get(s));
            }
        }
        rmtm.clear();
        rmtm = null;
        rwm.clear();
        rwm = null;
    }
    
    @Override
    public void Unload(){
        for(SpawnMob m : mmap.values()){
            m.unLoad();
        }
    }
    
    public class SpawnMob{
        private final MobType mbtype;
        private HashMap<String, UUID> region;
        private final BukkitTask spawnTask;
        
        public SpawnMob(MobType mt){
            mbtype = mt;
            region = new HashMap<>();
            spawnTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0, mbtype.getSpawnRate());
        }
        
        public void addRegion(String r, UUID w){
            region.put(r, w);
        }
        
        public void unLoad(){
            spawnTask.cancel();
        }
        
        private final Runnable runnable = new Runnable(){
            @Override
            public void run() {
                Set<String> keys = region.keySet();
                
                for(String k : keys){
                    RegionManager rgm = plugin.wg.getRegionManager(plugin.getServer().getWorld(region.get(k)));
                    ProtectedRegion r = rgm.getRegion(k);
                    ThreadLocalRandom spawnnr = ThreadLocalRandom.current();
                    int i = 0;
                    int nr = spawnnr.nextInt(3);
                    if (nr < 0){
                        nr = 1;
                    }
                    while (i < nr){
                        spawnMobsinRegio(r, region.get(k));
                        i++;
                    }
                }
            }
        };
                
        public void spawnMobsinRegio(ProtectedRegion r, UUID wkey){
            Double maxXp = r.getMaximumPoint().getX();
            Double maxZp = r.getMaximumPoint().getZ();
            Double minXp = r.getMinimumPoint().getX();
            Double minZp = r.getMinimumPoint().getZ();
            
            Double yP = r.getMinimumPoint().getY();
            
            World world = plugin.getServer().getWorld(wkey);
            ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current(); 
            
            int zp = threadLocalRandom.nextInt(minZp.intValue(), maxZp.intValue());
            int xp = threadLocalRandom.nextInt(minXp.intValue(), maxXp.intValue());
            
            if (zp < minZp){
                zp = minZp.intValue();
            }
            if (xp < minXp){
                xp = minXp.intValue();
            }            
            
            Block b = plugin.getServer().getWorld(world.getUID()).getBlockAt(xp, yP.intValue(), zp); 
            Block bloc = world.getHighestBlockAt(b.getLocation());
            
            Location loc = bloc.getLocation();
            loc.setY(loc.getY() + 2);  
            
            Creature ent = (Creature)world.spawnEntity(loc, mbtype.getType());
            Mob m = new Mob(plugin, ent.getUniqueId(), ent, mbtype, mbtype.getHealth(), r.getId(),  world.getUID());
            
            List<Entity> ents = ent.getNearbyEntities(20, 20, 20);
            int count = 0;
            
            HashMap<UUID, Mob> ml = plugin.MobList;
            
            for(Entity en : ents){
                if (ml.containsKey(en.getUniqueId())){
                    count++;
                }
                if (count == mbtype.getMaxMobs()){
                    ent.remove();
                    m = null;
                    return;
                }
            }
            
            plugin.MobList.put(ent.getUniqueId(), m);
            if (mbtype.getAggressiv()) {
                ent.setCustomName(ChatColor.RED + mbtype.getName());
            }
            else {
                ent.setCustomName(mbtype.getName());
            }
            
            ent.setCustomNameVisible(true);
        }
        
    }
    
    @Override
    public String getPluginName() {
        return "spawnmobsinregion";
    }
}
