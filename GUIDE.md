<div align="center">

# CookieSpoofer — Setup & Hardening Guide

**Configuration, command-filter tuning, and server hardening notes for Paper, Folia, and Spigot (`1.21 – 1.26.3`).**

[![Version](https://img.shields.io/badge/Version-1.1.0-dc2626?style=flat-square)](https://github.com/KhoaSuperT1/CookieSpoofer)
[![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Folia%20%7C%20Spigot-334155?style=flat-square)](https://papermc.io)

[Back to README](README.md) · [Support Discord](https://discord.gg/NZycRMWrSS)

</div>

---

> [!WARNING]
> **Notice (~80% AI & Untested Live):**
> Roughly **80%** of CookieSpoofer was written with AI assistance and has **not** been tested on a live Minecraft server yet (only 17/17 JUnit 5 unit tests and Paper 1.21.4 / 26.3 compilation checks have been run). Test on a local or staging server first. Internal plugin name, commands, and folder remain `PluginSpoofer` / `/pluginspoofer`.

## Quick Start

1. Drop `PluginSpoofer-1.1.0.jar` into your server's `plugins/` folder.
2. Start the server once to generate `plugins/PluginSpoofer/config.yml`.
3. Pick `mode: "SPOOF"` or `mode: "HIDE"` in `config.yml`.
4. Run `/pluginspoofer reload` to apply changes without restarting.

## How Packet & Command Filtering Flows

```text
1. Join / Sync  : Server builds Brigadier tree -> L1 strips real ':' & unauthorized nodes, injects fake namespaces
2. Tab Complete : Client probes '/ver '        -> L2 returns fake plugins (SPOOF) or empty list (HIDE)
3. Command Run  : Client runs '/grim reload'   -> L3/L4 unwraps '/execute', matches blacklist/hidden/perms -> Vanilla unknown error
4. Chat / Chan  : Client sends '.plugins'      -> L5/L6 drops chat prefix or monitors channel & alerts staff
```

## Configuration Breakdown

### 1. Operation Modes (`mode`)

- **`SPOOF` (Default):** Responds to `/plugins` and `/ver <FakePlugin>` with configured fake plugins, suggests fake plugins on `/ver <tab>`, and injects fake namespace nodes (`worldedit:wand`, `essentials:spawn`) into the Brigadier command graph.
- **`HIDE`:** Strips plugin info completely and returns the standard vanilla `Unknown or incomplete command` error with empty tab completions.

### 2. Fake Plugins (`fake-plugins`)

Keep the fake list plausible for your server type (Survival, Skyblock, SMP) and make sure `fake-commands` match plugins your server might reasonably run:

```yaml
fake-plugins:
  - name: "WorldEdit"
    version: "7.3.0"
    authors: ["sk89q", "EngineHub"]
    description: "WorldEdit in-game map editor"
    fake-commands:
      - "worldedit:wand"
      - "worldedit:set"
      - "worldedit:cut"
      - "worldedit:paste"
```

### 3. Command Filter (`command-filter`)

- `block-response-mode`:
  - `VANILLA_UNKNOWN`: Sends the exact vanilla error (`&cUnknown or incomplete command, see below for error\n&c<--[HERE]`) so probes cannot tell a blocked command from a nonexistent one.
  - `CUSTOM`: Sends `custom-block-message`.
- `blocked-commands`: Case-insensitive exact root/command blocklist.
- `blocked-patterns`: Regex blocklist (default blocks `^(bukkit|minecraft|spigot|paper):.*`).
- `blocked-prefixes`: Cancels normal chat messages starting with client command prefixes (`.`, `#`, `@`, `,`, `;`, `!`).
- `unwrap-execute-commands`: Unwraps nested `/execute ... run ...` chains before evaluating filters.

### 4. Strict Permission Cloaking (`strict-permission-cloaking`)

- `hide-unauthorized-commands`: Removes commands from the Brigadier tree when `registeredCmd.testPermissionSilent(player)` is false.
- `mask-no-permission-errors`: Intercepts commands the player has no permission for and replaces the denial with the configured unknown command message.

## Extra Server Hardening (Outside the Plugin)

CookieSpoofer only controls in-game command/chat/channel surfaces. Combine it with these server settings:

1. **Disable UDP Query (`server.properties`):**
   ```properties
   enable-query=false
   ```
   When `enable-query=true`, external tools can query the server's UDP port directly for the real plugin list.

2. **Rate-Limit Tab Completions (`config/paper-global.yml`):**
   ```yaml
   command-suggestion:
     max-completions-per-second: 10
   ```

3. **Verify Without OP:**
   Players with `pluginspoofer.bypass` (default `op`) see the real plugin list and command tree. Always test with a non-OP account that does not have `pluginspoofer.bypass`.

## Expected Behavior Matrix (Unit-Test Verified)

| Probe | Expected Behavior |
| :--- | :--- |
| `/plugins` or `/pl` | Shows formatted `fake-plugins` (`SPOOF`) or unknown command (`HIDE`). |
| `/ver WorldEdit` | Shows fake `WorldEdit` version lines (`SPOOF`). |
| `/ver GrimAC` | Returns `This server is not running any plugin by that name.` (`SPOOF`) or unknown command (`HIDE`). |
| `/execute as @p run ver WorldEdit` | Unwraps and handles `ver WorldEdit` as above. |
| `/grim reload` (no permission) | Normalized to vanilla unknown command error. |
| `.plugins` in chat | Chat cancelled and staff alerted. |

## Support

- **Discord:** https://discord.gg/NZycRMWrSS

---

<div align="center"><sub>Made with love and care by <b>Java-Cookieee</b> &amp; AI.</sub></div>
