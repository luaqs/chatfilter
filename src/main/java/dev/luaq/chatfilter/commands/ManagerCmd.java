package dev.luaq.chatfilter.commands;

import dev.luaq.chatfilter.ChatFilter;
import dev.luaq.chatfilter.handlers.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ManagerCmd implements ICommand {
    @Override
    public String getCommandName() {
        return "chatfilter";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chatfilter <'list' | 'add' | 'remove'> [id: int | regex: String]";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("cf", "cfilter");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            msg(String.format("Usage: %s", getCommandUsage(sender)));
            return;
        }

        ChatFilter cf = ChatFilter.instance;
        switch (args[0].toLowerCase()) {
            case "list":
                int id = 0;
                for (String pattern : ConfigHandler.getStringPatterns()) {
                    msg(String.format("[%d] %s", id, pattern));
                    ++id;
                }
                return;

            case "add":
                if (args.length < 2) {
                    return;
                }

                try {
                    String[] shift = Arrays.copyOfRange(args, 1, args.length);
                    Pattern compiled = Pattern.compile(String.join(" ", shift));
                    cf.getChatPatterns().add(compiled);

                    msg("Added the regex successfully.");
                } catch (PatternSyntaxException | IndexOutOfBoundsException e) {
                    msg("Invalid regex. " + e.getMessage());
                }

                break;

            case "remove":
                if (args.length < 2) {
                    return;
                }

                try {
                    int pos = Integer.parseInt(args[1]);
                    cf.getChatPatterns().remove(pos);

                    msg("Removed the regex successfully.");
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    msg("Not a valid ID. Do /cf list to see the valid ones.");
                }

                break;

            default:
                break;
        }

        try {
            ConfigHandler.writePatterns();
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private void msg(String msg) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(msg));
    }
}
