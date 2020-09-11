package dev.luaq.chatfilter.handlers;

import dev.luaq.chatfilter.ChatFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ConfigHandler {
    public static void parsePatterns() throws IOException {
        ChatFilter cf = ChatFilter.instance;
        cf.getChatPatterns().clear();

        if (!cf.getConfig().exists()) {
            return;
        }

        Files.lines(cf.getConfig().toPath()).forEach(line -> {
            try {
                Pattern pattern = Pattern.compile(line, Pattern.CASE_INSENSITIVE);
                cf.getChatPatterns().add(pattern);
            } catch (PatternSyntaxException ignored) {
            }
        });
    }

    public static void writePatterns() throws IOException {
        Files.write(ChatFilter.instance.getConfig().toPath(), getStringPatterns());
    }

    public static List<String> getStringPatterns() {
        return ChatFilter.instance.getChatPatterns()
                .stream().map(Pattern::toString)
                .collect(Collectors.toList());
    }
}
