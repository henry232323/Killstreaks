package henry232323.killstreaks;

import com.destroystokyo.paper.Title;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Killstreaks extends JavaPlugin implements Listener {
    private List<World> worlds;
    private HashMap<Player, Integer> streaks = new HashMap<>();

    private void initConfig() {
        saveDefaultConfig();

        List<String> worldnames = getConfig().getStringList("worlds");

        worlds = new ArrayList<>();
        for (String w : worldnames) {
            World cworld = getServer().getWorld(w);
            if (cworld != null)
                worlds.add(cworld);
            else
                getLogger().warning("Cannot enable killstreaks in world " + w + ", because it does not exist!");
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        initConfig();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        streaks.remove(p);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player killed = event.getEntity();

        if (killer != null) {
            World eworld = event.getEntity().getWorld();

            if (worlds.contains(eworld)) {
                if (!streaks.containsKey(killer))
                    streaks.put(killer, 0);
                int killCount = streaks.get(killer) + 1;
                streaks.put(killer, killCount);

                List<String> titleMessages = getConfig().getStringList("title_messages." + killCount);
                if (titleMessages.size() > 0) {
                    Title t;
                    if (titleMessages.size() == 1) {
                        t = new Title(String.format(titleMessages.get(0), killer.getName(), killed.getName(), killer.getDisplayName(), killed.getDisplayName()));
                    } else {
                        t = new Title(
                                String.format(titleMessages.get(0), killer.getName(), killed.getName(), killer.getDisplayName(), killed.getDisplayName()),
                                String.format(titleMessages.get(1), killer.getName(), killed.getName(), killer.getDisplayName(), killed.getDisplayName()));
                    }
                    for (Player mplayer : eworld.getPlayers()) {
                        mplayer.sendTitle(t);
                    }
                }

                String chatFmt = getConfig().getString("chat_messages." + killCount);
                if (chatFmt != null) {
                    String chatMessage = String.format(chatFmt, killer.getName(), killed.getName(), killer.getDisplayName(), killed.getDisplayName());
                    for (Player mplayer : eworld.getPlayers()) {
                        mplayer.sendMessage(chatMessage);
                    }
                }

                List<String> commands = getConfig().getStringList("commands." + killCount);
                if (commands.size() != 0) {
                    for (String command : commands) {
                        getServer().dispatchCommand(getServer().getConsoleSender(), String.format(command, killer.getName(), killed.getName(), killer.getDisplayName(), killed.getDisplayName()));
                    }
                }
            }
        }
        streaks.put(killed, 0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("killstreaks")) {
                if (args.length != 1) {
                    String version = getDescription().getVersion();
                    sender.sendMessage(ChatColor.GOLD + "Killstreaks Version " + version);
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    initConfig();
                    sender.sendMessage(ChatColor.GOLD + "Killstreaks reloaded");
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
