package dev.khoa.plugin.pluginspoofer.listeners;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import dev.khoa.plugin.pluginspoofer.PluginSpoofer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

public class PaperAsyncTabCompleteListener implements Listener {

    private final PluginSpoofer plugin;

    public PaperAsyncTabCompleteListener(PluginSpoofer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
        Optional<List<String>> completions = TabCompleteListener.resolveCompletions(plugin, event.getSender(), event.getBuffer());
        if (completions.isPresent()) {
            event.setCompletions(completions.get());
            event.setHandled(true);
        }
    }
}
