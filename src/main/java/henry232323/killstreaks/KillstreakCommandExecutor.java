package henry232323.killstreaks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KillstreakCommandExecutor implements CommandExecutor {
    Killstreaks plugin;

    KillstreakCommandExecutor(Killstreaks plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (args.length != 1) {
                String version = plugin.getDescription().getVersion();
                sender.sendMessage(ChatColor.GOLD + "Killstreaks Version " + version);
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.initConfig();
                sender.sendMessage(ChatColor.GOLD + "Killstreaks reloaded");
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
