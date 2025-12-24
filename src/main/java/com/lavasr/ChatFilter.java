package com.lavasr;

import com.lavasr.Commands.reloadFilter;
import com.lavasr.Events.onChat;
import com.lavasr.Commands.Whitelist.*;
import com.lavasr.Commands.Blacklist.*;
import com.lavasr.Utils.Filter;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatFilter extends JavaPlugin {
    public static ChatFilter plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        Filter.init(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("blacklistlist", "View the blacklisted words", new blacklistList());
            event.registrar().register("blacklistword", "Add a word to the chat filter", new blacklistWord());
            event.registrar().register("whitelistlist", "List the whitelisted words", new whitelistList());
            event.registrar().register("whitelistword", "Add a word to the whitelist", new whitelistWord());
            event.registrar().register("unblacklistword", "Remove a word from the chat filter", new unblacklistWord());
            event.registrar().register("unwhitelistword", "Remove a word from the chat filter whitelist", new unwhitelistWord());
            event.registrar().register("reloadfilter", "Reload the filter configuration file", new reloadFilter());
        });
        getServer().getPluginManager().registerEvents(new onChat(), this);
    }
}
