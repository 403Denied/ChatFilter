package com.lavasr.Utils;

import com.lavasr.ChatFilter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lavasr.Utils.Utils.Colorize;

public class Filter {

    private static final List<BlacklistEntry> blacklistedEntries = new ArrayList<>();
    private static final @NotNull List<String> whitelistedWords = new ArrayList<>();

    private static final Map<Character, String> replacements = Map.ofEntries(
            Map.entry('a', "[a4@9аạąäàáą]"),
            Map.entry('b', "[b8]"),
            Map.entry('c', "[cсƈċ]"),
            Map.entry('d', "[dԁɗ]"),
            Map.entry('e', "[e3еẹėéè]"),
            Map.entry('g', "[g96ġ/]"),
            Map.entry('h', "[hһһ]"),
            Map.entry('i', "[i1!|¡7іíï/]"),
            Map.entry('j', "[jјʝ]"),
            Map.entry('k', "[kκќ]"),
            Map.entry('l', "[l1!|iӏḷ]"),
            Map.entry('m', "[mn]"),
            Map.entry('n', "[nmո]"),
            Map.entry('o', "[o0оοօȯọỏơóòö]"),
            Map.entry('p', "[pрƿ]"),
            Map.entry('q', "[q9զ]"),
            Map.entry('s', "[s$5ʂ]"),
            Map.entry('t', "[t7+]"),
            Map.entry('u', "[uυüúùս]"),
            Map.entry('v', "[vυⅴνѵ]"),
            Map.entry('x', "[xхҳ]"),
            Map.entry('y', "[yуүÿý]"),
            Map.entry('z', "[z2ʐż]"),
            Map.entry('.', "[.,]")
    );


    public static void init(ChatFilter plugin) {
        blacklistedEntries.clear();
        List<Map<?, ?>> list = plugin.getConfig().getMapList("blacklisted-words");
        for (Map<?, ?> map : list) {
            String word = (String) map.get("word");
            boolean exact = Boolean.TRUE.equals(map.get("exact"));
            boolean autoBan = Boolean.TRUE.equals(map.get("autoban"));

            if (word != null) {
                blacklistedEntries.add(new BlacklistEntry(word, exact, autoBan));
            }

        }

        whitelistedWords.clear();
        whitelistedWords.addAll(plugin.getConfig().getStringList("whitelisted-words"));
    }

    public static void saveWordLists(ChatFilter plugin) {
        List<Map<String, Object>> toSave = new ArrayList<>();
        for (BlacklistEntry entry : blacklistedEntries) {
            Map<String, Object> map = new HashMap<>();
            map.put("word", entry.word());
            map.put("exact", entry.exact());
            map.put("autoban", entry.autoBan());
            toSave.add(map);
        }
        plugin.getConfig().set("blacklisted-words", toSave);
        plugin.getConfig().set("whitelisted-words", whitelistedWords);
        plugin.saveConfig();
    }

    public static Component getBlockedHighlightMessage(String msg) {
        String lower = msg.toLowerCase().replaceAll("\\s+", "");

        for (BlacklistEntry entry : blacklistedEntries) {
            boolean whitelisted = whitelistedWords.stream()
                    .anyMatch(w -> lower.contains(w.replaceAll("\\s+", "")));
            if (whitelisted) continue;

            String regex = entry.exact
                    ? "\\b" + wordToRegex(entry.word) + "\\b"
                    : wordToRegex(entry.word);

            Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(msg.toLowerCase());
            if (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();

                String before = msg.substring(0, start);
                String matched = msg.substring(start, end);

                int trailingStart = matched.length();
                while (trailingStart > 0 && Character.isWhitespace(matched.charAt(trailingStart - 1))) {
                    trailingStart--;
                }

                String target = matched.substring(0, trailingStart);
                String trailingSpaces = matched.substring(trailingStart);
                String after = msg.substring(end);

                return Colorize("<accent>" + before + "<other>&n" + target + "<reset><accent>" + trailingSpaces + after);
            }
        }

        return null;
    }
    public static BlacklistEntry getMatchedEntry(String msg) {
        String lower = msg.toLowerCase().replaceAll("\\s+", "");

        for (BlacklistEntry entry : blacklistedEntries) {

            boolean whitelisted = whitelistedWords.stream()
                    .anyMatch(w -> lower.contains(w.replaceAll("\\s+", "")));
            if (whitelisted) continue;

            String regex = entry.exact()
                    ? "\\b" + wordToRegex(entry.word()) + "\\b"
                    : wordToRegex(entry.word());

            Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(msg.toLowerCase());
            if (matcher.find()) {
                return entry;
            }
        }
        return null;
    }


    public static void addBlacklistedWord(String word, boolean exact, boolean autoBan) {
        word = word.toLowerCase();
        whitelistedWords.remove(word);
        for (BlacklistEntry entry : blacklistedEntries) {
            if (entry.word().equals(word)) return;
        }
        blacklistedEntries.add(new BlacklistEntry(word, exact, autoBan));
    }

    public static void addWhitelistedWord(String word) {
        word = word.toLowerCase();
        String finalWord = word;
        blacklistedEntries.removeIf(entry -> entry.word.equals(finalWord));
        if (!whitelistedWords.contains(word)) {
            whitelistedWords.add(word);
        }
    }

    public static void removeWhitelistedWord(String word) {
        whitelistedWords.remove(word.toLowerCase());
    }

    public static Component listBlacklistedWords() {
        if (blacklistedEntries.isEmpty()) {
            return Colorize("<prefix>No blacklisted words found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (BlacklistEntry entry : blacklistedEntries) {
                sb.append("<accent>");
                if (entry.autoBan()) {
                    sb.append("<other>&n").append(entry.word()).append("<reset><accent>");
                } else {
                    sb.append(entry.word());
                }
                sb.append(entry.exact() ? " (exact)" : " (fuzzy)")
                        .append("<main>, ");
            }
            String result = sb.substring(0, sb.length() - 2);
            return Colorize("<prefix>Blacklisted words: " + result);
        }
    }

    public static Collection<BlacklistEntry> getBlacklistedEntries() {
        return blacklistedEntries;
    }

    public static Collection<String> getWhitelistedWords() {
        return whitelistedWords;
    }

    public static Component listWhitelistedWords() {
        if (whitelistedWords.isEmpty()) {
            return Colorize("<prefix>No whitelisted words found.");
        } else {
            String words = String.join(", ", "<accent>" + String.join("<main>, <accent>", whitelistedWords));
            return Colorize("<prefix>Whitelisted words: " + words);
        }
    }

    public static String wordToRegex(String word) {
        StringBuilder regex = new StringBuilder();
        for (char c : word.toCharArray()) {
            if (replacements.containsKey(c)) {
                regex.append(replacements.get(c));
            } else {
                regex.append(Pattern.quote(String.valueOf(c)));
            }
            regex.append("[\\s.\\-]*");
        }
        return regex.toString();
    }

    public record BlacklistEntry(String word, boolean exact, boolean autoBan) {
        public BlacklistEntry(String word, boolean exact, boolean autoBan) {
            this.word = word.toLowerCase();
            this.exact = exact;
            this.autoBan = autoBan;
        }
    }

}
