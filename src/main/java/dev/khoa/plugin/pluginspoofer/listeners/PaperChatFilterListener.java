package dev.khoa.plugin.pluginspoofer.listeners;

import dev.khoa.plugin.pluginspoofer.PluginSpoofer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PaperChatFilterListener implements Listener {

    private final PluginSpoofer plugin;

    public PaperChatFilterListener(PluginSpoofer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPaperChat(AsyncChatEvent event) {
        String plainText = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (ChatFilterListener.handleBlockedPrefix(plugin, event.getPlayer(), plainText)) {
            event.setCancelled(true);
        }
    }
}
