package dev.khoa.plugin.pluginspoofer.listeners;

import dev.khoa.plugin.pluginspoofer.PluginSpoofer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LegacyChatFilterListener implements Listener {

    private final PluginSpoofer plugin;

    public LegacyChatFilterListener(PluginSpoofer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLegacyChat(AsyncPlayerChatEvent event) {
        if (ChatFilterListener.handleBlockedPrefix(plugin, event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
