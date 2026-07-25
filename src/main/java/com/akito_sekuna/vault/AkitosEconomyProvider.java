package com.akito_sekuna.vault;

import com.akito_sekuna.core.api.EconomyResult;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * Thin Vault Economy bridge. All calls delegate to AkitosCore's IEconomyAPI and IBankAPI.
 * No logic lives here -- this is purely an adapter.
 */
public class AkitosEconomyProvider implements Economy {

    private final Main plugin;

    public AkitosEconomyProvider(Main plugin) {
        this.plugin = plugin;
    }

    // --- Meta ---

    @Override
    public boolean isEnabled() {
        return plugin.getCoreAPI() != null;
    }

    @Override
    public String getName() {
        return "AkitosCore";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 1;
    }

    @Override
    public String format(double amount) {
        return plugin.getCoreAPI().getEconomy().format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "Pixels";
    }

    @Override
    public String currencyNameSingular() {
        return "Pixel";
    }

    // --- Player economy ---

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return player.getUniqueId() != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return plugin.getCoreAPI().getEconomy().getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return plugin.getCoreAPI().getEconomy().has(player.getUniqueId(), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        boolean success = plugin.getCoreAPI().getEconomy().take(player.getUniqueId(), amount);
        if (success) {
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Insufficient funds or invalid amount.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        EconomyResult result = plugin.getCoreAPI().getEconomy().give(player.getUniqueId(), amount);
        if (result == EconomyResult.SUCCESS) {
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, result.name());
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    // --- Bank ---

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        com.akito_sekuna.core.api.BankResult result = plugin.getCoreAPI().getBank().create(name);
        if (result == com.akito_sekuna.core.api.BankResult.SUCCESS) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, result.name());
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        com.akito_sekuna.core.api.BankResult result = plugin.getCoreAPI().getBank().delete(name);
        if (result == com.akito_sekuna.core.api.BankResult.SUCCESS) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, result.name());
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        if (!plugin.getCoreAPI().getBank().exists(name)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found.");
        }
        double balance = plugin.getCoreAPI().getBank().getBalance(name);
        return new EconomyResponse(balance, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        if (!plugin.getCoreAPI().getBank().exists(name)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found.");
        }
        boolean has = plugin.getCoreAPI().getBank().getBalance(name) >= amount;
        return new EconomyResponse(amount, plugin.getCoreAPI().getBank().getBalance(name),
                has ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, null);
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        com.akito_sekuna.core.api.BankResult result = plugin.getCoreAPI().getBank().withdraw(name, amount);
        double balance = plugin.getCoreAPI().getBank().getBalance(name);
        if (result == com.akito_sekuna.core.api.BankResult.SUCCESS) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, result.name());
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        com.akito_sekuna.core.api.BankResult result = plugin.getCoreAPI().getBank().deposit(name, amount);
        double balance = plugin.getCoreAPI().getBank().getBalance(name);
        if (result == com.akito_sekuna.core.api.BankResult.SUCCESS) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, result.name());
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        // AC banks are not player-owned; always return success if the bank exists
        if (plugin.getCoreAPI().getBank().exists(name)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found.");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return isBankOwner(name, player);
    }

    @Override
    public List<String> getBanks() {
        return List.of(); // AC doesn't expose a bank list via API yet
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return true; // AC auto-creates on first access
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return true;
    }

    // --- Deprecated string overloads (Vault legacy) ---

    @Override public boolean hasAccount(String playerName) { return false; }
    @Override public boolean hasAccount(String playerName, String worldName) { return false; }
    @Override public double getBalance(String playerName) { return 0; }
    @Override public double getBalance(String playerName, String world) { return 0; }
    @Override public boolean has(String playerName, double amount) { return false; }
    @Override public boolean has(String playerName, String worldName, double amount) { return false; }
    @Override public EconomyResponse withdrawPlayer(String playerName, double amount) { return notSupported(); }
    @Override public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) { return notSupported(); }
    @Override public EconomyResponse depositPlayer(String playerName, double amount) { return notSupported(); }
    @Override public EconomyResponse depositPlayer(String playerName, String worldName, double amount) { return notSupported(); }
    @Override public EconomyResponse createBank(String name, String player) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return notSupported(); }
    @Override public boolean createPlayerAccount(String playerName) { return false; }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return false; }

    private EconomyResponse notSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "String-based player lookups are not supported. Use OfflinePlayer.");
    }
}
