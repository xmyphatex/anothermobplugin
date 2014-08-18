/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin.mobs;

import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;

/**
 *
 * @author myPhate
 */
public class MobType {
    private final String MobTypeName;
    
    private final EntityType etype;
    private int maxMobs = 10;

    private int health = 120;
    private int armor = 20;
    private int mindmg = 8;
    private int maxdmg = 13;
    
    private int specialspawn = 5;
    private Boolean isAggressiv = false;
    
    private long spawnrate = 1200L;
    
    public MobType(String n, EntityType et, int h, int a, int midmg, int madimg){
        MobTypeName = n;
        etype = et;
        health = h;
        armor = a;
        mindmg = midmg;
        maxdmg = madimg;
    }
    
    public String getName(){
        return MobTypeName;
    }
    
    public int getHealth(){
        return health;
    }
        
    public EntityType getType(){
        return etype;
    }
    
    public Boolean getAggressiv(){
        return isAggressiv;
    }
    
    public void setAggro(Boolean a){
        isAggressiv = a;
    }
    
    public long getSpawnRate(){
        return spawnrate;
    } 
    
    public void setSpawnRate(long spr){
        spawnrate = spr;
    }
    
    public int getSpecialSpawn(){
        return specialspawn;
    }
    
    public void setSpecialSpawn(int s){
        specialspawn = s;
    }
    
    public int getMaxMobs(){
        return maxMobs;
    }
    
    public void setMaxMobs(int mm){
        maxMobs = mm;
    }
}
