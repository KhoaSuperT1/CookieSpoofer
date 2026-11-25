<div align="center">

<img src=".github/logo.jpg" alt="Java-Cookieee" width="180" />

# CookieSpoofer

**Plugin cloaking, fake plugin spoofing, and command-tree filtering for Paper, Folia, and Spigot.**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21--1.26.3-16a34a?style=flat-square)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21%20%7C%2025-ea580c?style=flat-square)](https://adoptium.net)
[![Platforms](https://img.shields.io/badge/Platform-Paper%20%7C%20Folia%20%7C%20Spigot-dc2626?style=flat-square)](https://papermc.io)
[![Tests](https://img.shields.io/badge/Unit_Tests-17%2F17_Passing-22c55e?style=flat-square)](https://github.com/KhoaSuperT1/CookieSpoofer)

[Setup Guide](GUIDE.md) · [Support Discord](https://discord.gg/NZycRMWrSS) · [MIT License](LICENSE)

</div>

---

> [!WARNING]
> **~80% AI-Assisted & Not Live-Tested Yet:**
> Roughly **80%** of **CookieSpoofer** (built under internal plugin ID `PluginSpoofer`) was written with AI assistance, and **it has NOT been tested on a live Minecraft server yet**.
> Only automated JUnit 5 unit tests (`17/17` passing) and compilation checks against Paper `1.21.4` (Java 21) and `26.3` (Java 25) have been run so far. Please test thoroughly on a local/staging server before using it in production.

## Why Blocking `/plugins` Isn't Enough

Modern hacked clients (Meteor, LiquidBounce, Wurst, ThunderHack, BleachHack, UI-Utils) do not rely solely on `/plugins`. They inspect the Brigadier command graph sent on join for `plugin:command` prefixes, probe `/ver <tab>`, check `/bukkit:ver` or `/paper:ver`, wrap commands in `/execute run ...`, or test whether a command replies with `"No permission"` instead of `"Unknown command"`.

CookieSpoofer intercepts those reconnaissance paths across the command tree, tab completion, command preprocessing, chat prefixes, and plugin messaging channels with **zero dependencies**.

### Changes in v1.1.0

- Updated for **Minecraft 1.21 – 1.26.3** across Paper, Folia, and Spigot (Java 21 & Java 25).
- Added `paper:ver`, `paper:version`, `paper:about`, `paper:plugins`, `paper:pl`, `spigot:ver`, and `spigot:version` to the spoofed/cloaked info command roots.
- Split Paper (`AsyncChatEvent`, `AsyncTabCompleteEvent`) and Spigot (`AsyncPlayerChatEvent`, `TabCompleteEvent`) listeners so Paper/Folia servers do not fire duplicate alerts and Spigot servers avoid `NoClassDefFoundError`.
- Fixed `ChannelFilterListener` throwing `UnsupportedOperationException` on Paper 1.21–26.3 when inspecting unmodifiable listening channel sets.
- Expanded automated unit tests to **17 cases** (all passing on both 1.21.4 and 26.3 APIs).

## Defense Layers

| Layer | Component | Target | Behavior |
| :---: | :--- | :--- | :--- |
| **L1** | `CommandSendListener` | Brigadier Command Tree | Strips real `:` colon nodes, hides version commands and unauthorized commands, and injects fake namespaces in `SPOOF` mode. |
| **L2** | `PaperAsyncTabCompleteListener` / `TabCompleteListener` | Tab Completion | Intercepts `/ver <tab>`, `/plugins <tab>`, and hidden namespaces; returns fake names in `SPOOF` mode or empty list in `HIDE` mode. |
| **L3** | `CommandPreprocessListener` | Direct Commands & Wrappers | Spoofs `/plugins` and `/ver <plugin>`, recursively unwraps `/execute ... run ...`, and blocks blacklisted/regex-matched commands. |
| **L4** | `PluginSpoofManager` | Error Oracles | Normalizes unauthorized/hidden probes to the vanilla `Unknown or incomplete command` response. |
| **L5** | `PaperChatFilterListener` / `LegacyChatFilterListener` | Client Chat Prefixes | Cancels chat messages starting with configured cheat prefixes (`.`, `#`, `@`, `,`, `;`, `!`) and notifies staff. |
| **L6** | `ChannelFilterListener` | Plugin Messaging | Monitors sensitive channel registrations (`wdl:init`, `wdl\|init`, `worlddownloader`) and notifies staff. |

## Configuration

<details>
<summary>Default <code>config.yml</code></summary>

```yaml
enabled: true
mode: "SPOOF"
bypass-permission: "pluginspoofer.bypass"

fake-plugins:
  - name: "WorldEdit"
    version: "7.3.0"
    authors: ["sk89q", "EngineHub"]
    description: "WorldEdit in-game map editor"
    fake-commands: ["worldedit:wand", "worldedit:set", "worldedit:cut", "worldedit:paste"]
  - name: "Essentials"
    version: "2.20.1"
    authors: ["Zenexer", "ementalo"]
    description: "Provides essential commands and utilities"
    fake-commands: ["essentials:spawn", "essentials:tp", "essentials:home", "essentials:warp"]
  - name: "Vault"
    version: "1.7.3-b131"
    authors: ["cossinater", "Kevlar"]
    description: "Vault is a Permissions & Economy API"
    fake-commands: ["vault:vault-info"]
  - name: "LuckPerms"
    version: "5.4.102"
    authors: ["Luck"]
    description: "A permissions plugin"
    fake-commands: ["luckperms:lp", "luckperms:luckperms"]

spoof-plugins-format: "&fPlugins (%count%): %plugins%"
plugin-name-format: "&a%name%"
plugin-separator: "&f, "

fake-version-format:
  - "&a%name% version %version% by %authors%"
  - "&f%description%"

unknown-plugin-version-message: "&cThis server is not running any plugin by that name.\n&cUse /plugins to get a list of plugins."

hidden-plugin-names:
  - "AntiOpsec"
  - "asp"
  - "AntiSpoofing"
  - "GrimAC"
  - "grim"
  - "Vulcan"
  - "Matrix"
  - "Polar"
  - "Intave"
  - "Karhu"
  - "Spartan"
  - "NoCheatPlus"
  - "ncp"
  - "AAC"
  - "CoreProtect"
  - "co"
  - "nLogin"
  - "AuthMe"
  - "ExploitsX"
  - "IllegalStack"
  - "Spark"
  - "ViaVersion"
  - "ViaBackwards"
  - "ProtocolLib"
  - "Geyser-Spigot"
  - "Floodgate"
  - "PluginSpoofer"

command-tree:
  filter-colon-commands: true
  hide-version-commands: true
  spoof-fake-namespaces: true

unknown-command-message: "&cUnknown or incomplete command, see below for error\n&c<--[HERE]"

strict-permission-cloaking:
  hide-unauthorized-commands: true
  mask-no-permission-errors: true

command-filter:
  enabled: true
  block-response-mode: "VANILLA_UNKNOWN"
  custom-block-message: "&cYou do not have permission to execute this command."
  blocked-commands:
    - "op"
    - "deop"
    - "stop"
    - "reload"
    - "rl"
    - "restart"
    - "icanhasbukkit"
    - "version"
    - "ver"
    - "about"
    - "plugins"
    - "pl"
    - "bukkit:ver"
    - "bukkit:version"
    - "bukkit:about"
    - "bukkit:plugins"
    - "bukkit:pl"
    - "bukkit:help"
    - "bukkit:?"
    - "paper:version"
    - "paper:ver"
    - "spigot:version"
    - "minecraft:tell"
    - "minecraft:me"
    - "minecraft:w"
    - "minecraft:msg"
  blocked-patterns:
    - "^(bukkit|minecraft|spigot|paper):.*"
  blocked-prefixes:
    - "."
    - "#"
    - "@"
    - ","
    - ";"
    - "!"
  unwrap-execute-commands: true
```

</details>

## Commands & Permissions

> Note: Internal plugin ID, commands, and data folder keep `PluginSpoofer` / `/pluginspoofer` in v1.1.0.

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/pluginspoofer` | Show version, active mode, and status | `pluginspoofer.admin` |
| `/pluginspoofer reload` | Reload `config.yml` and caches | `pluginspoofer.admin` |

Aliases: `/pspoof`, `/spoofer`

| Permission | Description | Default |
| :--- | :--- | :---: |
| `pluginspoofer.admin` | Run `/pluginspoofer` and receive staff alerts | `op` |
| `pluginspoofer.bypass` | View real plugins, tab completions, and full command tree | `op` |

## Building from Source

Requires **Java 21+** (Java 25 for the `verify-26.3` profile) and **Apache Maven 3.9+**.

```bash
git clone https://github.com/KhoaSuperT1/CookieSpoofer.git
cd CookieSpoofer
mvn test
mvn package
```

Compiled artifact: `target/PluginSpoofer-1.1.0.jar`

## Support

- **Discord:** https://discord.gg/NZycRMWrSS

---

<div align="center"><sub>Made with love and care by <b>Java-Cookieee</b> &amp; AI.</sub></div>
