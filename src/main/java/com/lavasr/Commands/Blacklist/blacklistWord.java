package com.lavasr.Commands.Blacklist;

import com.lavasr.Utils.Filter;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

import static com.lavasr.ChatFilter.plugin;
import static com.lavasr.Utils.Utils.Colorize;

public class blacklistWord implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CommandSender sender = commandSourceStack.getSender();

        if (strings.length < 3) {
            sender.sendMessage(Colorize("<usage>Usage: /blacklistword <word> <exact:true|false> <autoban:true|false>"));
            return;
        }

        String word = strings[0].toLowerCase();

        String flagExact = strings[1].toLowerCase();
        boolean exact;
        if (flagExact.equals("true")) {
            exact = true;
        } else if (flagExact.equals("false")) {
            exact = false;
        } else {
            sender.sendMessage(Colorize("<usage>Invalid exact flag. Use true or false."));
            return;
        }
        String flagAuto = strings[2].toLowerCase();

        boolean autoBan;
        if (flagAuto.equals("true")) {
            autoBan = true;
        } else if (flagAuto.equals("false")) {
            autoBan = false;
        } else {
            sender.sendMessage(Colorize("<usage>Invalid autoban flag. Use true or false."));
            return;
        }
        Filter.addBlacklistedWord(word, exact, autoBan);
        Filter.saveWordLists(plugin);
        sender.sendMessage(Colorize("<prefix>The word '<accent>" + word + "<main>' has been added as "
                + (exact ? "<accent>exact" : "<accent>fuzzy")
                + "<main> match, autoban: <accent>" + autoBan + "<main>."));
    }

    @Override
    public String permission() {return "filter.add";}

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length == 1) {
            return List.of();
        }
        if (args.length == 2) {
            return List.of("true", "false");
        }
        if (args.length == 3) {
            return List.of("true", "false");
        }
        return List.of();
    }
}
