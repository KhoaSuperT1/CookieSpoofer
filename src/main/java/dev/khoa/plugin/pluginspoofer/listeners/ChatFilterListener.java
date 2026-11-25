package dev.khoa.plugin.pluginspoofer.listeners;

import dev.khoa.plugin.pluginspoofer.PluginSpoofer;
import dev.khoa.plugin.pluginspoofer.config.SpoofConfig;
import org.bukkit.entity.Player;

/**
 * Intercepts chat messages starting with hacked client prefixes (e.g. '.', '#', '@', ',', ';', '!')
 * and triggers Admin Security Alerts.
 */
public final class ChatFilterListener {

    private ChatFilterListener() {
    }

    public static boolean handleBlockedPrefix(PluginSpoofer plugin, Player player, String rawMessage) {
        SpoofConfig config = plugin.getSpoofConfig();
        if (!config.isEnabled() || !config.isCommandFilterEnabled()) {
            return false;
        }

        if (player.hasPermission(config.getBypassPermission())) {
            return false;
        }

        String message = rawMessage != null ? rawMessage.trim() : "";
        if (plugin.getManager().hasBlockedPrefix(config, message)) {
            player.sendMessage(plugin.getManager().getBlockResponseMessage(config));
            if (config.isAdminAlertsEnabled()) {
                plugin.getManager().alertStaff(plugin.getManager().formatClientPrefixAlert(config, player.getName(), message));
            }
            return true;
        }

        return false;
    }
}
