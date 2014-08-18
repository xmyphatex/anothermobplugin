/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin;

import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author myPhate
 */

public class Config {
    final AMP plugin;
    public FileConfiguration config;    
    
    public Config(AMP instance) {
        this.plugin = instance;
    }
    
    public Boolean loadConfig(){
        config = plugin.getConfig();
        if (config == null)
            return false;
        
        setDefaults();
        return true;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public void setDefaults() {
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
    
    public Object getDefault(String path){
        return null;
    }
            
}
