/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin.listeners;

import de.myphate.anothermobplugin.AMP;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 *
 * @author myPhate
 */

public class SpawnListener implements Listener {
    final AMP plugin;
    
        
    public SpawnListener(AMP instance) {
        plugin = instance;
    }   
    
    @EventHandler (priority = EventPriority.HIGH)
    public void onSpawn(final CreatureSpawnEvent e) {
        if (e.getSpawnReason().equals(SpawnReason.SPAWNER_EGG)){
            return;
        }
        if (!e.getSpawnReason().equals(SpawnReason.CUSTOM)){
            e.setCancelled(true);
        }
    }
}
