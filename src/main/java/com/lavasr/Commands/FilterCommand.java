package com.lavasr.Commands;

import com.lavasr.Utils.Filter;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.lavasr.ChatFilter.plugin;
import static com.lavasr.Utils.Utils.Colorize;

public class FilterCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();

        if (args.length == 0) {
            sender.sendMessage(Colorize("<usage>Usage: /filter <whitelist|blacklist|reload|check>"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "whitelist" -> handleWhitelist(stack, shift(args));
            case "blacklist" -> handleBlacklist(stack, shift(args));
            case "reload" -> {
                if (!sender.hasPermission("filter.reload")) return;
                plugin.reloadConfig();
                Filter.init(plugin);
                sender.sendMessage(Colorize("<prefix>Filter configuration reloaded successfully."));
            }
            case "check" -> handleCheck(stack, shift(args));
            default -> sender.sendMessage(Colorize("<usage>Unknown subcommand."));
        }
    }

    @Override
    public String permission() {
        return "filter.use";
    }

    private void handleWhitelist(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();

        if (args.length == 0) {
            sender.sendMessage(Colorize("<usage>Usage: /filter whitelist <add|remove|list>"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (!sender.hasPermission("filter.add")) return;
                if (args.length < 2) {
                    sender.sendMessage(Colorize("<usage>Usage: /filter whitelist add <word>"));
                    return;
                }
                String word = joinGreedy(args);
                Filter.addWhitelistedWord(word);
                Filter.saveWordLists(plugin);
                sender.sendMessage(Colorize("<prefix>The word '<accent>" + word + "<main>' has been added to the whitelist."));
            }

            case "remove" -> {
                if (!sender.hasPermission("filter.remove")) return;
                if (args.length < 2) {
                    sender.sendMessage(Colorize("<usage>Usage: /filter whitelist remove <word>"));
                    return;
                }
                String word = joinGreedy(args);
                Filter.removeWhitelistedWord(word);
                Filter.saveWordLists(plugin);
                sender.sendMessage(Colorize("<prefix>The word '<accent>" + word + "<main>' has been removed from the whitelist."));
            }

            case "list" -> {
                if (!sender.hasPermission("filter.list")) return;
                sender.sendMessage(Filter.listWhitelistedWords());
            }
        }
    }

    private void handleBlacklist(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();

        if (args.length == 0) {
            sender.sendMessage(Colorize("<usage>Usage: /filter blacklist <add|remove|list>"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> {
                if (!sender.hasPermission("filter.list")) return;
                sender.sendMessage(Filter.listBlacklistedWords());
            }

            case "remove" -> {
                if (!sender.hasPermission("filter.remove")) return;
                if (args.length < 2) {
                    sender.sendMessage(Colorize("<usage>Usage: /filter blacklist remove <word>"));
                    return;
                }
                String word = joinGreedy(args);
                boolean removed = Filter.getBlacklistedEntries()
                        .removeIf(entry -> entry.word().equals(word));

                if (removed) {
                    Filter.saveWordLists(plugin);
                    sender.sendMessage(Colorize("<prefix>The word '<accent>" + word + "<main>' has been removed."));
                } else {
                    sender.sendMessage(Colorize("<prefix>The word '<accent>" + word + "<main>' was not found."));
                }
            }

            case "add" -> {
                if (!sender.hasPermission("filter.add")) return;

                ParsedBlacklist parsed = parseBlacklistAdd(args);
                if (parsed == null) {
                    sender.sendMessage(Colorize("<usage>Usage: /filter blacklist add <word> <exact:true|false> <autoban:true|false>"));
                    return;
                }

                Filter.addBlacklistedWord(parsed.word, parsed.exact, parsed.autoBan);
                Filter.saveWordLists(plugin);

                sender.sendMessage(Colorize(
                        "<prefix>The word '<accent>" + parsed.word + "<main>' has been added as "
                                + (parsed.exact ? "<accent>exact" : "<accent>fuzzy")
                                + "<main>, autoban: <accent>" + parsed.autoBan + "<main>."
                ));
            }
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        if (args.length == 0) {
            return List.of("whitelist", "blacklist", "reload", "check");
        }

        if (args.length == 1) {
            return List.of("whitelist", "blacklist", "reload", "check").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        String root = args[0].toLowerCase();

        if (args.length == 2 && root.equals("whitelist")) {
            return List.of("add", "remove", "list");
        }

        if (args.length == 2 && root.equals("blacklist")) {
            return List.of("add", "remove", "list");
        }

        if (args.length >= 3 && root.equals("whitelist") && args[1].equalsIgnoreCase("remove")) {
            String input = args[args.length - 1].toLowerCase();
            return Filter.getWhitelistedWords().stream()
                    .filter(word -> word.startsWith(input))
                    .toList();
        }

        if (args.length >= 3 && root.equals("blacklist") && args[1].equalsIgnoreCase("remove")) {
            String input = args[args.length - 1].toLowerCase();
            return Filter.getBlacklistedEntries().stream()
                    .map(Filter.BlacklistEntry::word)
                    .filter(word -> word.startsWith(input))
                    .toList();
        }
        if (root.equals("blacklist") && args.length >= 4 && args[1].equalsIgnoreCase("add")) {

            String last = args[args.length - 1].toLowerCase();

            return List.of("true", "false").stream()
                    .filter(b -> b.startsWith(last))
                    .toList();
        }

        return List.of();
    }

    private void handleCheck(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();

        if (!sender.hasPermission("filter.check")) return;

        if (args.length == 0) {
            sender.sendMessage(Colorize("<usage>Usage: /filter check <message>"));
            return;
        }

        String message = String.join(" ", args);

        Component highlight = Filter.getBlockedHighlightMessage(message);
        Filter.BlacklistEntry matched = Filter.getMatchedEntry(message);

        if (highlight == null || matched == null) {
            sender.sendMessage(Colorize("<prefix>Message is allowed."));
            return;
        }

        sender.sendMessage(Colorize("<prefix><error>Message would be filtered."));
        sender.sendMessage(Colorize("<main>Matched word: <accent>" + matched.word()));
        sender.sendMessage(Colorize("<main>Exact match: <accent>" + matched.exact()));
        sender.sendMessage(Colorize("<main>Autoban: <accent>" + matched.autoBan()));
        sender.sendMessage(Colorize("<main>Blocked portion: ").append(highlight));
    }

    private static String[] shift(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private static String joinGreedy(String[] args) {
        return String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
    }

    private ParsedBlacklist parseBlacklistAdd(String[] args) {
        if (args.length < 4) return null;

        int boolStart = args.length - 2;
        String exactRaw = args[boolStart];
        String autoRaw = args[boolStart + 1];

        if (!isBoolean(exactRaw) || !isBoolean(autoRaw)) return null;

        String word = String.join(" ", Arrays.copyOfRange(args, 1, boolStart)).toLowerCase();
        return new ParsedBlacklist(word, Boolean.parseBoolean(exactRaw), Boolean.parseBoolean(autoRaw));
    }

    private boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    private record ParsedBlacklist(String word, boolean exact, boolean autoBan) {}
}

