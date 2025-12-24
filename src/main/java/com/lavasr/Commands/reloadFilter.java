package com.lavasr.Commands;

import com.lavasr.Utils.Filter;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

import static com.lavasr.ChatFilter.plugin;
import static com.lavasr.Utils.Utils.Colorize;

public class reloadFilter implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CommandSender sender = commandSourceStack.getSender();
        plugin.reloadConfig();
        Filter.init(plugin);
        sender.sendMessage(Colorize("<prefix>Filter configuration reloaded successfully."));
    }
    @Override
    public String permission() {return "filter.reload";}
}
