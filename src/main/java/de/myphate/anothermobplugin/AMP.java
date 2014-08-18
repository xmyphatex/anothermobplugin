/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.myphate.anothermobplugin;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StringFlag;
import de.myphate.anothermobplugin.listeners.SpawnListener;
import de.myphate.anothermobplugin.mobs.Mob;
import de.myphate.anothermobplugin.mobs.MobType;
import de.myphate.anothermobplugin.scheduler.BringMobBack;
import de.myphate.anothermobplugin.scheduler.MobScheduler;
import de.myphate.anothermobplugin.scheduler.SpawnMobsinRegion;
import de.myphate.bandersnatch.BS;
import de.myphate.bandersnatch.chat.ChatReplacer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
 *
 * @author myPhate
 */
public class AMP extends JavaPlugin {
    
    private static AMP plugin;
    
    public BS bs;
    public static ChatReplacer cr;
    public Configuration cfile;
    public File mobsPath;
    public File quests;
    
    public HashMap<String, MobType> MobTypeList = new HashMap<>();
    public HashMap<UUID, Mob> MobList = new HashMap<>();
        
    public WorldGuardPlugin wg;
    public WGCustomFlagsPlugin wgcf;
    
    public final StringFlag MobSpawn = new StringFlag("MobSpawn");
    
    public final PluginManager pm = getServer().getPluginManager();
    
    private final ArrayList<MobScheduler> mscheduler = new ArrayList<>();
    
    
    public AMP(){
        plugin = this;
    }   
    
    public static AMP getPlugin() {
        return plugin;
    }
    
    @Override
    public void onEnable(){
        System.out.println("[AMP] Starte Plugin.");
        
        /* Load and set configfile */
        Boolean loadConfig = true;
        Config config = new Config(this);
        if ((loadConfig = config.loadConfig()) == false){
            System.out.println(ChatColor.RED + "[AMP] Fehler beim Laden der Konfiguration.");
            plugin.setEnabled(false);
            return;
        }
        
        cfile = config.getConfig();  
        
        bs = BS.getPlugin();
        if (bs.chr != null) {
            cr = bs.chr;

            if (!CreateDirectories()) {
                System.out.println(cr.ReplaceColor("#0[AMP] Die Ordner konnten nicht erstellt werden."));
            }
        }
        
        System.out.println(cr.ReplaceColor("[AMP] Lade WorldGuard und CustomFlag."));
        wg = getWorldGuard();
        wgcf = getCustomFlagsPlugin();
        wgcf.addCustomFlag(MobSpawn);
        
        iniMobTypes();
        
        Boolean schedError = iniScheduler();
        if (!schedError){
            plugin.setEnabled(false);
            return;
        }
        
        System.out.println(cr.ReplaceColor("[AMP] Lade Listener."));
        SpawnListener spl =  new SpawnListener(this);
        pm.registerEvents(spl, plugin);
    }
        
    @Override
    public void onDisable(){
        
        File mobfile = new File(plugin.getDataFolder() + "/mobs/moblist.yml");
        System.out.println(cr.ReplaceColor("[AMP] Lade MobFile: " + mobfile.getPath()));
        if (mobfile.exists()){
            mobfile.delete();
        }
        
        YamlConfiguration mobconf = YamlConfiguration.loadConfiguration(mobfile);
        
        for(Mob m : MobList.values()){
            System.out.println(cr.ReplaceColor("[AMP] Speicher Mob.")); 
            mobconf.set("mobs." + m.getUUID().toString() + ".mobtype", m.getMobType());
            mobconf.set("mobs." + m.getUUID().toString() + ".health", m.getMobHealth());
            mobconf.set("mobs." + m.getUUID().toString() + ".region", m.getRegion());
            mobconf.set("mobs." + m.getUUID().toString() + ".world", m.getWorld());
            mobconf.set("mobs." + m.getUUID().toString() + ".isspecial", m.getSpecial());
        }
        
        try {
            mobconf.save(mobfile);
        } catch (IOException ex) {
            System.out.println(cr.ReplaceColor("#0[AMP] MobListFile konnte nicht gespeichert werden."));
        }

        if (!mscheduler.isEmpty()) {
            for(MobScheduler ms : mscheduler){
                ms.Unload();
            }
        }
        
        super.onDisable();
    }
    
    private void loadCreatures(){
        File mobfile = new File(plugin.getDataFolder() + "/mobs/moblist.yml");
        System.out.println(cr.ReplaceColor("[AMP] Lade MobFile: " + mobfile.getPath()));
        if (!mobfile.exists()){
            System.out.println(cr.ReplaceColor("#0[AMP] MobListFile konnte nicht geladen werden."));
            return;
        }
        
        YamlConfiguration mobconf = YamlConfiguration.loadConfiguration(mobfile);
        Set<String> cse = mobconf.getConfigurationSection("mobs").getKeys(false);
        List<World> allworlds = this.getServer().getWorlds();
        List<Entity> allent = new ArrayList<>();
        for(World w : allworlds){
            allent.addAll(this.getServer().getWorld(w.getUID()).getEntities());
        }
        
        for(String sec : cse){
            UUID uid = UUID.fromString(sec);
            MobType mobtype = (MobType)mobconf.get("mobs." + sec + ".mobtype");
            int h = (Integer)mobconf.get("mobs." + sec + ".health");
            String region = (String)mobconf.get("mobs." + sec + ".region");
            UUID wuuid = UUID.fromString((String)mobconf.get("mobs." + sec + ".world"));
            Boolean isSpec = (Boolean)mobconf.get("mobs." + sec + ".isspecial");
            Creature c = null;
            for(Entity e : allent){
                if (e.getUniqueId().equals(uid)){
                    c = (Creature)e;
                    break;
                }
            }
            
            Mob m = new Mob(this, uid, c, mobtype, h, region, uid);
            m.setSpecial(isSpec);
            
        }
    }
    
    private WGCustomFlagsPlugin getCustomFlagsPlugin(){
        Plugin wr = getServer().getPluginManager().getPlugin("WGCustomFlags");

        // WorldGuard may not be loaded
        if (wr == null || !(wr instanceof WGCustomFlagsPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WGCustomFlagsPlugin) wr;
    }
    
    private WorldGuardPlugin getWorldGuard(){
        Plugin wog = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (wog == null || !(wog instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) wog;
    }
    
    public boolean CreateDirectories() {
        String path = plugin.getDataFolder().toString();
        path = path.replace("/", File.separator);
        mobsPath = new File(path + File.separator + "mobs");
        if (!mobsPath.exists()) {
            mobsPath.mkdir();
        }
        return true;
    }
    
    public void iniMobTypes(){
        System.out.println(cr.ReplaceColor("[AMP] Lade Mobliste."));
        
        /* Warge */
        
        MobType warg = new MobType("Warg", EntityType.WOLF, 120, 20, 13, 18);
        warg.setSpawnRate(1200L);
        warg.setAggro(true);
        warg.setSpecialSpawn(10);
        warg.setMaxMobs(10);
        MobTypeList.put("warg", warg); 
        
        
        /* Wölfe */
        
        MobType wolf = new MobType("Wolf", EntityType.WOLF, 120, 20, 13, 18);
        wolf.setSpawnRate(2400L);
        wolf.setAggro(false);
        wolf.setSpecialSpawn(15);
        wolf.setMaxMobs(5);
        MobTypeList.put("wolf", wolf); 
    }
    
    public Boolean iniScheduler(){
        System.out.println(cr.ReplaceColor("[AMP] Lade Scheduler."));
        
        if (MobTypeList == null){
            return true;
        }
        
        try {
            mscheduler.add(new SpawnMobsinRegion(this));
            mscheduler.add(new BringMobBack(this));

            for(MobScheduler ms : mscheduler){
                ms.Load();
            }
        }
        catch (Exception e){
            System.out.println(cr.ReplaceColor("#0[AMP] Fehler beim Laden der Scheduler."));
            System.out.println(cr.ReplaceColor("#0[AMP] " + e.getMessage()));
            return false;
        }
        
        return true;
    }
    
    public Boolean iniListener(){
        
        return true;
    }
}
