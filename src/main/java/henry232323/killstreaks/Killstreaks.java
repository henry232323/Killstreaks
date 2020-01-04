package henry232323.killstreaks;

import com.destroystokyo.paper.Title;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Killstreaks extends JavaPlugin implements Listener {
    private List<World> worlds;
    private HashMap<Player, Integer> streaks = new HashMap<>();
    FileConfiguration config;


    @Override
    public void onEnable() {
        // Plugin startup logic
        File dfolder = getDataFolder();
        if (!dfolder.exists()) {
            dfolder.mkdir();
        }

        getServer().getPluginManager().registerEvents(this, this);
        config = getConfig();

        List<String> worldnames = config.getStringList("worlds");
        if (worlds == null) {
            worldnames = new ArrayList<String>();
            config.options().copyDefaults(true);
        }

        for (String w : worldnames) {
            World cworld = getServer().getWorld(w);
            if (cworld != null)
                worlds.add(cworld);
            else
                getLogger().warning("Cannot enable killstreaks in world " + w + ", because it does not exist!");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            World eworld = event.getEntity().getWorld();

            if (worlds.contains(eworld)) {
                if (!streaks.containsKey(killer))
                    streaks.put(killer, 0);
                int killCount = streaks.get(killer) + 1;
                streaks.put(killer, killCount);

                List<String> titleMessages = getConfig().getStringList("title_messages." + String.valueOf(killCount));
                if (titleMessages != null && titleMessages.size() > 0) {
                    Title t;
                    if (titleMessages.size() == 1) {
                        t = new Title(String.format(titleMessages.get(0), killer.getDisplayName()));
                    } else {
                        t = new Title(
                                String.format(titleMessages.get(0), killer.getDisplayName()),
                                String.format(titleMessages.get(1), killer.getDisplayName()));
                    }
                    for (Player mplayer : eworld.getPlayers()) {
                        mplayer.sendTitle(t);
                    }
                }

                String chatFmt = getConfig().getString("chat_messages." + String.valueOf(killCount));
                if (chatFmt != null) {
                    String chatMessage = String.format(chatFmt, killer.getDisplayName());
                    for (Player mplayer : eworld.getPlayers()) {
                        mplayer.sendMessage(chatMessage);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
