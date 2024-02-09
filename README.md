# Multicount Mod Overview

Multicount allows players to have multiple in-game characters (accounts), switching between them with simple commands. The mod's primary focus is to allow multiple character roleplay or multiple people to share a world within a single Minecraft instance. Servers can set a default limit on the number of accounts per player, and players can easily manage their current account status.

Mod can be utilized fully server side, but singleplayer and LAN games are also supported client side. The mod swaps NBT data, advancements, and statistics for each account, with configuration and data files that store relevant settings and information.

**Disclaimer!** Backups are advised before using the mod to avoid potential data loss. Players should undertake necessary precautions when dealing with game data.

## Functionality:

### Singleplayer:
- Change accounts via buttons beside the main menu screen. Account selection applies to all worlds.

### Multiplayer:
- Settings specified by commands and config files.

**Non-OP Commands:**
- `/account [<account number>]`: Switches between your multiple game accounts/saves. Player will disconnect during account switches as part of normal operation.
- `/account`: Displays your currently active account number.

**Properties and Data Files:**
- `multicount.properties`: A configuration file that lists max account settings for each player in the format `<UUID>=<max accounts>`. Also includes entries for default maximum(Default: 3) and whether to allow `accountmax` command(Default: false).
- `multicount.dat`: Stores each player's UUID and their active account.

**Operator commands:**
- `/accountmax query [<targets>]`: Returns the max accounts allowed for specified players.
- `/accountmax query`: Returns the default max accounts allowed.

**Optional OP commands (Activation specified in multicount.properties):**
- `/accountmax <maxCount> [<targets>]`: Sets a specific player's account limit to <maxCount>. Omitting <targets> sets the default server limit.
- `/accountmax 0`: Resets a player's max accounts to server's default.

### LAN:
Offers `/account` command access to guests, without any max account limits. Properties file usage is omitted for client-side.

### Saving and Compatibility:
- Other accounts' data is saved as `<uuid>.dat[<account number>]` in the usual folder.
- Will switch data from most mods that use player NBT data.
- The mod only swaps NBT, advancements, and statistics, so mod-specific features will not switch if not stored in the NBT data.
- The mod doesn't manage username changes on its own.
