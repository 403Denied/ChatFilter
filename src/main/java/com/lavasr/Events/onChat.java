package com.lavasr.Events;

import com.lavasr.Utils.Filter;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.lavasr.ChatFilter.plugin;
import static com.lavasr.Utils.Utils.Colorize;

public class onChat implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChatHigh(AsyncChatEvent e){
        String rawChatMessage = LegacyComponentSerializer.legacySection().serialize(e.message());
        Component highlight = Filter.getBlockedHighlightMessage(rawChatMessage);
        if (highlight != null) {
            Filter.BlacklistEntry matched = Filter.getMatchedEntry(rawChatMessage);
            if (matched != null && matched.autoBan()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    String command = "minecraft:ban " + e.getPlayer().getName() + " Auto-banned for using blacklisted word: " + matched.word();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                });
            }
            e.setCancelled(true);
            Bukkit.broadcast(Colorize("<main><bold>(Filtered) <reset><accent>" + e.getPlayer().getName() + "<main>: ").append(highlight), "filter.notify");
            e.getPlayer().sendMessage(Colorize("<prefix>Your message was blocked due to the following characters: ").append(highlight));
        }
    }
}
