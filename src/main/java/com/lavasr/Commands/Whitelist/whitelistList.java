package com.lavasr.Commands.Whitelist;

import com.lavasr.Utils.Filter;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class whitelistList implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CommandSender sender = commandSourceStack.getSender();
        sender.sendMessage(Filter.listWhitelistedWords());
    }
    @Override
    public String permission(){return "filter.list";}
}
