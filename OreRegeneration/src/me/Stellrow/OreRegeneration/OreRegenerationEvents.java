package me.Stellrow.OreRegeneration;

import me.Stellrow.OreRegeneration.oremanager.GeneratingOre;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class OreRegenerationEvents implements Listener {
    private final OreRegeneration pl;

    public OreRegenerationEvents(OreRegeneration pl) {
        this.pl = pl;
    }



    //Break possible regenerating ore
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Block b = e.getBlock();
        if(pl.getGeneratingOreManager().getOreByLocation(b.getLocation())==null){
            return;
        }
        if(b.getType()== Material.COBBLESTONE){
            e.setCancelled(true);
            return;
        }
        pl.getGeneratingOreManager().getOreByLocation(b.getLocation()).respawn();
        setBlock(b.getLocation());
    }
    //Set block later
    private void setBlock(Location toSet){
        new BukkitRunnable(){

            @Override
            public void run() {
                toSet.getBlock().setType(Material.COBBLESTONE);
            }
        }.runTaskLater(pl,2);
    }
    //Place regenerating ore
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Player player = e.getPlayer();
        ItemStack blockUsed = e.getItemInHand();
        if(!blockUsed.hasItemMeta()){
            return;
        }
        if(!isGeneratingOre(blockUsed)){
            return;
        }
        Integer time;
        try{
            time = Integer.parseInt(ChatColor.stripColor(blockUsed.getItemMeta().getDisplayName().split(" ")[2]));
            GeneratingOre created = new GeneratingOre(time,e.getBlockPlaced().getLocation(), blockUsed.getType(),pl);
            pl.getGeneratingOreManager().addOre(created);
            pl.addOreRegenerationEntry(created);
            created.start();
            player.sendMessage(ChatColor.GREEN+"Added a regenerating block!");
            return;
        }catch (IllegalArgumentException exception){
            return;
        }
    }



    private boolean isGeneratingOre(ItemStack targetItem){
        ItemMeta im = targetItem.getItemMeta();
        if(im.getPersistentDataContainer().has(pl.oreregenkey, PersistentDataType.STRING)){
            return true;
        }
        return false;
    }
}
