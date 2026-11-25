package dev.khoa.plugin.pluginspoofer.listeners;

import dev.khoa.plugin.pluginspoofer.PluginSpoofer;
import dev.khoa.plugin.pluginspoofer.config.SpoofConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Layer 2: Tab-Completion Spoofing.
 * Intercepts tab-completion packets (/ver <tab>, /plugins <tab>, /<hidden>: <tab>),
 * returning fake plugin suggestions in SPOOF mode and masking hidden commands.
 */
public class TabCompleteListener implements Listener {

    private final PluginSpoofer plugin;

    public TabCompleteListener(PluginSpoofer plugin) {
        this.plugin = plugin;
    }

    public static Optional<List<String>> resolveCompletions(PluginSpoofer plugin, CommandSender sender, String buffer) {
        SpoofConfig config = plugin.getSpoofConfig();
        if (!config.isEnabled()) {
            return Optional.empty();
        }

        if (sender.hasPermission(config.getBypassPermission())) {
            return Optional.empty();
        }

        if (buffer == null || !buffer.startsWith("/")) {
            return Optional.empty();
        }

        String clean = buffer.substring(1).trim();
        String[] parts = clean.split("\\s+", 2);
        String commandRoot = parts[0].toLowerCase(Locale.ROOT);

        if (plugin.getManager().isVersionCommand(commandRoot)) {
            if (config.getMode() == SpoofConfig.SpoofMode.SPOOF) {
                return Optional.of(plugin.getManager().getTabCompletions(config, buffer));
            }
            return Optional.of(Collections.emptyList());
        }

        if (plugin.getManager().isPluginsCommand(commandRoot)) {
            return Optional.of(Collections.emptyList());
        }

        if (plugin.getManager().isHiddenCommandOrPlugin(config, clean)) {
            return Optional.of(Collections.emptyList());
        }

        return Optional.empty();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSyncTabComplete(TabCompleteEvent event) {
        Optional<List<String>> completions = resolveCompletions(plugin, event.getSender(), event.getBuffer());
        if (completions.isPresent()) {
            event.setCompletions(completions.get());
            event.setCancelled(true);
        }
    }
}
