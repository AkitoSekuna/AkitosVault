# AkitosVault

Vault compatibility bridge for the Akitos plugin network. Registers an economy provider with Vault and delegates all balance calls to AkitosCore, so other Vault-integrated plugins (EssentialsX, LuckPerms, and similar) can read and modify Akitos balances.

## Requirements

- Paper 1.21.1+
- Java 21+
- AkitosCore v21.2.0+
- Vault (soft dependency; required only if you need third-party economy integration)

## Installation

1. Install AkitosCore first.
2. Install [Vault](https://www.spigotmc.org/resources/vault.34315/) if you want EssentialsX/LuckPerms (or any other Vault-integrated plugin) to read Akitos balances.
3. Drop `AkitosVault.jar` into your `plugins/` folder.
4. Restart the server.

[NOTE: Vault is a soft dependency. If Vault is not installed, AkitosVault will log a warning on startup and will not register an economy provider. AkitosCore's own economy will keep working normally either way.]

## How It Works

On enable, AkitosVault registers an `Economy` service with Vault's services manager at the highest priority, backed by AkitosCore's economy. Any plugin that reads or writes balances through Vault's API is transparently reading and writing AkitosCore balances.

No commands, permissions, or configuration are exposed by this plugin. It runs entirely in the background.

## Part of the Akitos Plugin Network

- [AkitosCore](https://github.com/AkitoSekuna/AkitosCore) (required)
- [Vault](https://github.com/milkbowl/Vault) (optional)
