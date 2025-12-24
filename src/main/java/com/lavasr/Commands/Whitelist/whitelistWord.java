package com.lavasr.Commands.Whitelist;

import com.lavasr.Utils.Filter;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

import static com.lavasr.ChatFilter.plugin;
import static com.lavasr.Utils.Utils.Colorize;

public class whitelistWord implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CommandSender sender = commandSourceStack.getSender();
        if (strings.length < 1) {
            sender.sendMessage(Colorize("<usage>Usage: /whitelistword <word>"));
            return;
        }
        String word = String.join(" ", strings).toLowerCase();
        Filter.addWhitelistedWord(word);
        Filter.saveWordLists(plugin);
        sender.sendMessage(Colorize("<prefix>The word '<accent>" + word + "<main>' has been added to the whitelist."));
    }
    @Override
    public String permission(){return "filter.add";}
}
