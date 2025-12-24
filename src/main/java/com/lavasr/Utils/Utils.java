package com.lavasr.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Utils {
    private static final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
    public static final MiniMessage miniMessage = MiniMessage.builder().tags(TagResolver.resolver(
            StandardTags.defaults(),
            Placeholder.parsed("prefix", "<dark_red>[LAVASR] <gray>"),
            Placeholder.parsed("main", "<gray>"),
            Placeholder.parsed("accent", "<red>"),
            Placeholder.parsed("error", "<dark_red>"),
            Placeholder.parsed("usage", "<yellow>"),
            Placeholder.parsed("denied", "<red>"),
            Placeholder.parsed("other", "<white>"),
            Placeholder.parsed("server", "lavasr")
    )).build();
    public static Component Colorize(String input) {
        String mmFormatted = convertLegacyToMiniMessage(input);
        return miniMessage.deserialize(mmFormatted);
    }
    public static String convertLegacyToMiniMessage(String legacyText) {
        return legacyText
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");
    }
    public static String stripAllColors(String input) {
        if(input == null) return null;
        Component component = miniMessage.deserialize(input);
        return serializer.serialize(component);
    }
    public static String stripAllColors(Component component) {
        if(component == null) return null;
        return serializer.serialize(component);
    }
}
