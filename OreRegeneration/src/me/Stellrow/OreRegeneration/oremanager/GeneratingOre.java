package me.Stellrow.OreRegeneration.oremanager;

import com.sun.istack.internal.NotNull;
import me.Stellrow.OreRegeneration.OreRegeneration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class GeneratingOre {
    private int time;
    private Location oreLocation;
    private final OreRegeneration pl;
    private Material type;
    private boolean toCancel = false;
    public GeneratingOre(@NotNull Integer time, @NotNull Location blockLocation,@NotNull Material oreType,@NotNull OreRegeneration pl){
        this.time=time;
        this.oreLocation=blockLocation;
        this.type=oreType;
        this.pl = pl;
    }
    public void respawn(){
        new BukkitRunnable(){

            @Override
            public void run() {
                oreLocation.getBlock().setType(type);
            }
        }.runTaskLater(pl,time*20);
    }
    public Location getOreLocation(){
        return oreLocation;
    }
    public int returnTime(){
        return time;
    }
    public Material getType(){
        return type;
    }
    public void shutDown(){
        toCancel=true;
        oreLocation.getBlock().setType(Material.AIR);
    }
    public void start(){
        oreLocation.getBlock().setType(type);
    }
}
