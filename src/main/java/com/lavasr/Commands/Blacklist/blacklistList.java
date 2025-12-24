package com.lavasr.Commands.Blacklist;

import com.lavasr.Utils.Filter;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class blacklistList implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CommandSender sender = commandSourceStack.getSender();
        sender.sendMessage(Filter.listBlacklistedWords());
    }
    @Override
    public String permission() {return "filter.list";}
}
