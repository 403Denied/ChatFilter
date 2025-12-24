package com.lavasr.Commands.Whitelist;

import com.lavasr.Utils.Filter;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.lavasr.ChatFilter.plugin;
import static com.lavasr.Utils.Utils.Colorize;

public class unwhitelistWord implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CommandSender sender = commandSourceStack.getSender();
        if (strings.length < 1) {
            sender.sendMessage(Colorize("<usage>Usage: /unwhitelistword <word>"));
            return;
        }
        String word = String.join(" ", strings).toLowerCase();
        Filter.removeWhitelistedWord(word);
        Filter.saveWordLists(plugin);
        sender.sendMessage(Colorize("<prefix>The word '<accent>" + word + "<main>' has been removed from the whitelist."));
    }
    @Override
    public String permission(){return "filter.remove";}
    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length == 0) {
            return Filter.getWhitelistedWords();
        }

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Filter.getWhitelistedWords().stream()
                    .filter(word -> word.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
