# AkitosVault

Vault compatibility bridge for the Akitos plugin network. Registers an economy provider with Vault and delegates every balance and bank call to AkitosCore, so other Vault-integrated plugins (EssentialsX, LuckPerms, and similar) can read and modify Akitos balances directly.

## Requirements

* Paper 1.21.11+
* Java 21+
* AkitosCore v21.2.0+
* Vault (soft dependency, required only if you need third-party economy integration)

## Installation

1. Install AkitosCore first.
2. Install [Vault](https://www.spigotmc.org/resources/vault.34315/) if you want EssentialsX/LuckPerms (or any other Vault-integrated plugin) to read Akitos balances.
3. Drop `AkitosVault-21.2.0.jar` into your `plugins/` folder.
4. Restart the server.

Vault is a soft dependency. If it isn't installed, AkitosVault logs a warning on startup and does not register an economy provider, AkitosCore's own economy keeps working normally either way.

## How it works

On enable, AkitosVault registers an `Economy` service with Vault's services manager at the highest priority, backed entirely by AkitosCore. Any plugin that reads or writes balances through Vault's API is transparently reading and writing AkitosCore balances, and any plugin that calls Vault's bank methods is transparently reading and writing AkitosCore's named bank accounts.

No commands, permissions, or configuration are exposed by this plugin. It runs entirely in the background.

## Known limitations

Worth knowing if you're integrating a third-party Vault-consumer plugin against this bridge:

* `currencyNamePlural()`/`currencyNameSingular()` are currently hardcoded to "Pixels"/"Pixel" rather than reading AkitosCore's configurable `currency-name`. If you change Core's currency name, this bridge's reported currency name won't follow automatically yet.
* `getBanks()` always returns an empty list. AkitosCore's bank API doesn't expose a full account listing yet, so anything relying on Vault's bank-enumeration methods won't see Akitos banks.
* Vault's deprecated String-based player-lookup overloads (`hasAccount(String)`, `getBalance(String)`, etc., the pre-`OfflinePlayer` era of the Vault API) are not implemented and return `NOT_IMPLEMENTED`/`false` rather than silently no-oping. Any plugin still calling those specific legacy methods (most modern Vault consumers use the `OfflinePlayer` overloads instead) won't work against this bridge.

## Part of the Akitos Plugin Network

* [AkitosCore](https://github.com/AkitoSekuna/AkitosCore) (required)
* [Vault](https://github.com/milkbowl/Vault) (optional)
