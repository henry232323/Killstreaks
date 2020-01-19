package henry232323.killstreaks;

import com.destroystokyo.paper.Title;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class Killstreaks extends JavaPlugin implements Listener {
    private List<String> worlds;
    private HashMap<Player, Integer> streaks = new HashMap<>();
    private HashMap<Integer, List<String>> titles;
    private HashMap<Integer, String> messages;
    private HashMap<Integer, List<String>> commands;

    public void initConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        worlds = config.getStringList("worlds");

        titles = new HashMap<>();
        ConfigurationSection tsect = config.getConfigurationSection("title_messages");
        if (tsect != null) {
            Set<String> stringTitles = tsect.getKeys(false);
            for (String t : stringTitles) {
                System.out.println(t);
                titles.put(Integer.parseInt(t), config.getStringList("title_messages." + t));
            }
        }

        messages = new HashMap<>();
        ConfigurationSection msect = config.getConfigurationSection("chat_messages");
        if (msect != null) {
            Set<String> stringMessages = msect.getKeys(false);
            for (String m : stringMessages) {
                messages.put(Integer.parseInt(m), config.getString("chat_messages." + m));
            }
        }

        commands = new HashMap<>();
        ConfigurationSection csect = config.getConfigurationSection("commands");
        if (csect != null) {
            Set<String> stringCommands = csect.getKeys(false);
            for (String c : stringCommands) {
                commands.put(Integer.parseInt(c), config.getStringList("commands." + c));
            }
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("killstreaks").setExecutor(new KillstreakCommandExecutor(this));
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

            if (worlds.contains(eworld.getName())) {
                if (!streaks.containsKey(killer))
                    streaks.put(killer, 0);
                int killCount = streaks.get(killer) + 1;
                streaks.put(killer, killCount);


                if (titles.containsKey(killCount)) {
                    List<String> titleMessages = titles.get(killCount);
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

                if (messages.containsKey(killCount)) {
                    String chatFmt = messages.get(killCount);
                    String chatMessage = String.format(chatFmt, killer.getName(), killed.getName(), killer.getDisplayName(), killed.getDisplayName());
                    for (Player mplayer : eworld.getPlayers()) {
                        mplayer.sendMessage(chatMessage);
                    }
                }

                if (commands.containsKey(killCount)) {
                    List<String> lcommands = commands.get(killCount);
                    for (String command : lcommands) {
                        getServer().dispatchCommand(getServer().getConsoleSender(), String.format(command, killer.getName(), killed.getName(), killer.getDisplayName(), killed.getDisplayName()));
                    }
                }
            }
        }
        streaks.remove(killed);
    }
}
