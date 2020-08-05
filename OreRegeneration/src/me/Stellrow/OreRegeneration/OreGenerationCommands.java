package me.Stellrow.OreRegeneration;

import me.Stellrow.OreRegeneration.oremanager.GeneratingOre;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OreGenerationCommands implements CommandExecutor {
    private final OreRegeneration pl;

    public OreGenerationCommands(OreRegeneration pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String sa, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED+"You must be a player to use this!");
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("oreregeneration.admin")){
            p.sendMessage(ChatColor.RED+"No permission to use this,if this is a mistake contact an administrator");
            return true;
        }
        // /oregen type time(seconds)
        if(args.length==2){
            Integer time;
            Material type;
            try{
                time = Integer.parseInt(args[1]);
                type = Material.valueOf(args[0]);
                p.getInventory().addItem(pl.buildOreAsItemStack(type,time));
                return true;
            }catch (IllegalArgumentException exception){
            p.sendMessage(ChatColor.RED+"Wrong type/time argument!");
            return true;
            }
        }
        if(args.length==1){
            if(args[0].equalsIgnoreCase("remove")){
                Block targetBlock = p.getTargetBlock(null,10);
                Integer possibleID = pl.getOreID(targetBlock.getLocation());
                if(possibleID==0){
                    p.sendMessage(ChatColor.RED+"No generating block found at that location!");
                    return true;
                }
                GeneratingOre targetOre = pl.getGeneratingOreManager().getOreByLocation(targetBlock.getLocation());
                targetOre.shutDown();
                pl.getGeneratingOreManager().removeOre(targetOre);
                pl.removeOreRegenerationEntry(possibleID);
                p.sendMessage(ChatColor.GREEN+"Removed the generating ore!");
                return true;
            }
        }
        p.sendMessage(ChatColor.GRAY+"Usage: /oregen type(DIAMOND_ORE) time(in seconds)");
        p.sendMessage(ChatColor.GRAY+"Usage: /oregen remove(Look at block)");
        return true;
    }
}
