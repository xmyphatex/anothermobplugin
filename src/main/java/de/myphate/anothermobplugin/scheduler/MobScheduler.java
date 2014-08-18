/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin.scheduler;

import de.myphate.anothermobplugin.AMP;

/**
 *
 * @author myPhate
 */

public abstract class MobScheduler {
    
    final AMP plugin;
    
    public MobScheduler(AMP instance) {
        this.plugin = instance;
    }
    
    public void Load(){} 
    public void Unload() {}
    
    public abstract String getPluginName();
}
