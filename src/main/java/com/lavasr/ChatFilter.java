package com.lavasr;

import com.lavasr.Commands.FilterCommand;
import com.lavasr.Events.onChat;
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
            event.registrar().register("filter", "Manage the chat filter", new FilterCommand());
        });
        getServer().getPluginManager().registerEvents(new onChat(), this);
    }
}
