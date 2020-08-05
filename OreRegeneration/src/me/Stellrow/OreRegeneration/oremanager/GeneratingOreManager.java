package me.Stellrow.OreRegeneration.oremanager;

import me.Stellrow.OreRegeneration.OreRegeneration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;

public class GeneratingOreManager {
    private final OreRegeneration pl;

    public GeneratingOreManager(OreRegeneration pl) {
        this.pl = pl;
    }
    private HashSet<GeneratingOre> ores = new HashSet<GeneratingOre>();

    public void addOre(GeneratingOre generatingOre){
        ores.add(generatingOre);
    }
    public void removeOre(GeneratingOre toDelete){
        ores.remove(toDelete);
    }
    public GeneratingOre getOreByLocation(Location target){
    for(GeneratingOre go : ores){
        if(go.getOreLocation().equals(target)){
            return go;
        }
    }
    return null;
    }
    //Should be called once at onDisable
    public void shutDownOres(){
        for(GeneratingOre go : ores){
            go.shutDown();
        }
    }
    //Should only be called once at the start of the server
    public void buildOresFromConfig(){
        if(pl.getLocations().contains("Ores")){
            FileConfiguration cfg = pl.getLocations();
            for(String s : pl.getLocations().getConfigurationSection("Ores").getKeys(false)){
                Material type = Material.valueOf(cfg.getString("Ores."+s+".Type"));
                Integer time = cfg.getInt("Ores."+s+".Time");
                Location location = cfg.getLocation("Ores."+s+".Location");
                GeneratingOre created = new GeneratingOre(time,location,type,pl);
                created.start();
                ores.add(created);
            }
        }
    }


}
