/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin.mobs;

import de.myphate.anothermobplugin.AMP;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.EntityType;

/**
 *
 * @author myPhate
 */

public class MobCommands implements CommandExecutor {
    final AMP plugin;
    
    private final static List<String> commandlist = Arrays.asList(new String[]{"cm", "createmob", "sm", "spawnmob"});
    
    public MobCommands(AMP instance) {
        plugin = instance;
    }
    
    public void Load(){
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!commandlist.contains(cmd.getName().toLowerCase())){
            return true;
        }
        
        String com = cmd.getName().toLowerCase();
        
        switch (com){
                case "sm":
                    
                return  true;
        }
        
        return false;
    }
    
    public void RegisterCommands() {
        plugin.getCommand("cm").setExecutor(this);
        plugin.getCommand("createmob").setExecutor(this);
    }    
    
}
