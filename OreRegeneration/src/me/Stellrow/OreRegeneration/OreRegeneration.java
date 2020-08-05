package me.Stellrow.OreRegeneration;

import com.sun.istack.internal.NotNull;
import me.Stellrow.OreRegeneration.oremanager.GeneratingOre;
import me.Stellrow.OreRegeneration.oremanager.GeneratingOreManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class OreRegeneration extends JavaPlugin {
    public final NamespacedKey oreregenkey = new NamespacedKey(this,"oreregen.ore");
    private File locations;
    private FileConfiguration locationscfg;
    private GeneratingOreManager generatingOreManager;
    public void onEnable(){
        loadConfig();
        getCommand("oregen").setExecutor(new OreGenerationCommands(this));
        getServer().getPluginManager().registerEvents(new OreRegenerationEvents(this),this);
        generatingOreManager = new GeneratingOreManager(this);
        generatingOreManager.buildOresFromConfig();
    }
    public void onDisable(){
        generatingOreManager.shutDownOres();
    }
    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        createLocations();
    }



    //Locations.yml
    private void createLocations() {
        locations = new File(getDataFolder(),"locations.yml");
        if(!locations.exists()) {
            locations.getParentFile().mkdirs();
            saveResource("locations.yml",true);
        }
        reloadLocations();
    }
    public void reloadLocations() {
        locationscfg = YamlConfiguration.loadConfiguration(locations);
    }
    public void saveLocations() {
        try {
            locationscfg.save(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getLocations() {
        return locationscfg;
    }
    public ItemStack buildOreAsItemStack(@NotNull Material type,Integer time){
        ItemStack i = new ItemStack(type);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.GREEN+"GeneratingOre "+ type.toString()+" "+time);
        im.getPersistentDataContainer().set(oreregenkey, PersistentDataType.STRING,"OreRegeneration Item");
        i.setItemMeta(im);
        return i;
    }
    public GeneratingOreManager getGeneratingOreManager(){
        return generatingOreManager;
    }
    public void addOreRegenerationEntry(GeneratingOre generatingOre){
        Integer id = returnAvailableId();
        getLocations().set("Ores."+id+".Type",generatingOre.getType().toString());
        getLocations().set("Ores."+id+".Time",generatingOre.returnTime());
        getLocations().set("Ores."+id+".Location",generatingOre.getOreLocation());
        saveLocations();
    }
    public void removeOreRegenerationEntry(Integer id){
        getLocations().set("Ores."+id,null);
        saveLocations();
    }
    private Integer returnAvailableId(){
        int toReturn;
        Random rnd = new Random();
        //10,000 available ids
        //Never 0
        toReturn=rnd.nextInt(10000)+1;
        if(getLocations().contains("Ores."+toReturn)){
            return returnAvailableId();
        }
        return toReturn;
    }
    public Integer getOreID(Location loc){
        if(getLocations().contains("Ores")){
            for(String s : getLocations().getConfigurationSection("Ores").getKeys(false)){
                Location toCheck = getLocations().getLocation("Ores."+s+".Location");
                if(toCheck.equals(loc)){
                    return Integer.parseInt(s);
                }
            }
        }
        return 0;
    }
}
