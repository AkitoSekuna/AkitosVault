package com.akito_sekuna.vault;

import com.akito_sekuna.core.AkitosAddon;
import com.akito_sekuna.core.ReloadReason;
import com.akito_sekuna.core.api.ICoreAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements AkitosAddon {

    private static Main instance;
    private ICoreAPI coreAPI;
    private AkitosEconomyProvider economyProvider;

    public static Main getInstance() {
        return instance;
    }

    public ICoreAPI getCoreAPI() {
        return coreAPI;
    }

    // --- AkitosAddon ---

    @Override
    public String getAddonName() {
        return "AkitosVault";
    }

    @Override
    public String getAddonVersion() {
        return getPluginMeta().getVersion();
    }

    @Override
    public void onCoreReady(ICoreAPI api) {
        this.coreAPI = api;
    }

    @Override
    public void onCoreReload(ICoreAPI newApi, ReloadReason reason) {
        this.coreAPI = newApi;
    }

    @Override
    public void onCoreShutdown() {
        // nothing to release
    }

    // --- Lifecycle ---

    @Override
    public void onEnable() {
        instance = this;

        com.akito_sekuna.core.Main.registerAddon(this);

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found -- AkitosVault will not register an economy provider.");
            getLogger().warning("Install Vault if you need EssentialsX/LuckPerms economy integration.");
            return;
        }

        economyProvider = new AkitosEconomyProvider(this);
        getServer().getServicesManager().register(
                Economy.class,
                economyProvider,
                this,
                ServicePriority.Highest
        );

        getLogger().info("AkitosVault v" + getPluginMeta().getVersion() + " enabled -- economy registered with Vault.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AkitosVault disabled.");
    }
}
