/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin.scheduler;

import de.myphate.anothermobplugin.AMP;
import de.myphate.anothermobplugin.mobs.Mob;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author myPhate
 */
public class BringMobBack extends MobScheduler{    
    private BukkitTask sendBackTask;
    
    public BringMobBack(AMP instance) {
        super(instance);
    }
    
    @Override
    public void Load(){
        sendBackTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0, 80L);    
    } 
    
    @Override
    public void Unload(){
        sendBackTask.cancel();
    }
    
    private final Runnable runnable = new Runnable(){
        @Override
        public void run() {
            HashMap<UUID, Mob> mblist = plugin.MobList;
            for(Mob m : mblist.values()){
                Creature cr = m.getEnt();
                
                Location loc = m.returnCenter();

                if (cr.getLocation().distance(loc) <= 35){
                    continue;
                }
                
                ThreadLocalRandom randInt = ThreadLocalRandom.current();
                Location randLoc = new Location(cr.getWorld(), loc.getX() + randInt.nextDouble(-5, 5) , cr.getEyeHeight() + cr.getLocation().getY() + 1, loc.getZ() + randInt.nextDouble(-5, 5) );
                if (cr.getTarget() != null){
                    if (cr.getLocation().distance(loc) > 40) {
                        m.setMobHealt(m.getMobType().getHealth());
                        cr.setTarget(null);
                        cr.teleport(randLoc);
                    }
                    continue;
                }
                
                cr.teleport(randLoc);
            }
        }
        
    };

    @Override
    public String getPluginName() {
        return "bringmobback";
    }
}
