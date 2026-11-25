package dev.khoa.plugin.pluginspoofer;

import dev.khoa.plugin.pluginspoofer.config.SpoofConfig;
import dev.khoa.plugin.pluginspoofer.listeners.ChannelFilterListener;
import dev.khoa.plugin.pluginspoofer.listeners.CommandPreprocessListener;
import dev.khoa.plugin.pluginspoofer.listeners.CommandSendListener;
import dev.khoa.plugin.pluginspoofer.listeners.LegacyChatFilterListener;
import dev.khoa.plugin.pluginspoofer.listeners.PaperAsyncTabCompleteListener;
import dev.khoa.plugin.pluginspoofer.listeners.PaperChatFilterListener;
import dev.khoa.plugin.pluginspoofer.listeners.TabCompleteListener;
import dev.khoa.plugin.pluginspoofer.manager.PluginSpoofManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Main plugin class for PluginSpoofer.
 * Provides complete plugin cloaking, fake plugin spoofing, and reconnaissance defense.
 */
public final class PluginSpoofer extends JavaPlugin implements CommandExecutor, TabCompleter {

    private volatile SpoofConfig spoofConfig;
    private PluginSpoofManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.manager = new PluginSpoofManager();
        reloadSpoofConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CommandSendListener(this), this);
        if (isClassPresent("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent")) {
            pm.registerEvents(new PaperAsyncTabCompleteListener(this), this);
        } else {
            pm.registerEvents(new TabCompleteListener(this), this);
        }
        pm.registerEvents(new CommandPreprocessListener(this), this);
        pm.registerEvents(new ChannelFilterListener(this), this);
        if (isClassPresent("io.papermc.paper.event.player.AsyncChatEvent")) {
            pm.registerEvents(new PaperChatFilterListener(this), this);
        } else {
            pm.registerEvents(new LegacyChatFilterListener(this), this);
        }

        var cmd = getCommand("pluginspoofer");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }

        printBanner();
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, PluginSpoofer.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private String getPluginVersion() {
        try {
            return getPluginMeta().getVersion();
        } catch (Throwable ignored) {
            return getDescription().getVersion();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("PluginSpoofer disabled.");
    }

    private void printBanner() {
        var logger = getLogger();
        logger.info("");
        logger.info("  ██████╗  ██████╗   ██████╗  ██╗  ██╗ ██╗ ███████╗");
        logger.info(" ██╔════╝ ██╔═══██╗ ██╔═══██╗ ██║ ██╔╝ ██║ ██╔════╝");
        logger.info(" ██║      ██║   ██║ ██║   ██║ █████═╝  ██║ █████╗  ");
        logger.info(" ██║      ██║   ██║ ██║   ██║ ██╔═██╗  ██║ ██╔══╝  ");
        logger.info(" ╚██████╗ ╚██████╔╝ ╚██████╔╝ ██║ ╚██╗ ██║ ███████╗");
        logger.info("  ╚═════╝  ╚═════╝   ╚═════╝  ╚═╝  ╚═╝ ╚═╝ ╚══════╝");
        logger.info("  ──────────────────────────────────────────────────");
        logger.info("   • Plugin: PluginSpoofer [v1.1.0]");
        logger.info("   • Author: Cookieee | Platform: Paper, Folia, Spigot 1.21 - 1.26.3");
        logger.info("   • System: Active | Mode: " + spoofConfig.getMode());
        logger.info("  ──────────────────────────────────────────────────");
        logger.info("");
    }

    public synchronized void reloadSpoofConfig() {
        reloadConfig();
        this.spoofConfig = SpoofConfig.fromBukkitConfig(getConfig());
    }

    public SpoofConfig getSpoofConfig() {
        return spoofConfig;
    }

    public PluginSpoofManager getManager() {
        return manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pluginspoofer.admin")) {
            sender.sendMessage(PluginSpoofManager.colorize("&cYou do not have permission to execute this command."));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadSpoofConfig();
            sender.sendMessage(PluginSpoofManager.colorize(
                    "&a[PluginSpoofer] Configuration reloaded successfully! Mode: &f" + spoofConfig.getMode() +
                            "&a, Fake plugins: &f" + spoofConfig.getFakePlugins().size()
            ));
            return true;
        }

        sender.sendMessage(PluginSpoofManager.colorize(
                "&b[PluginSpoofer] &7Version: &f" + getPluginVersion() +
                        " &7| Mode: &e" + spoofConfig.getMode() +
                        " &7| Enabled: &a" + spoofConfig.isEnabled() + "\n" +
                        "&7Use &f/pluginspoofer reload &7to reload configuration."
        ));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("pluginspoofer.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String query = args[0].toLowerCase(Locale.ROOT);
            return List.of("reload", "info").stream()
                    .filter(s -> s.startsWith(query))
                    .toList();
        }

        return Collections.emptyList();
    }
}
