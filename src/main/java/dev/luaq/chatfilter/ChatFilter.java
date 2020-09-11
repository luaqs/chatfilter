package dev.luaq.chatfilter;

import dev.luaq.chatfilter.commands.ManagerCmd;
import dev.luaq.chatfilter.handlers.ConfigHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Mod(modid = "chatfilter", version = "1.0")
public class ChatFilter {
    @Mod.Instance
    public static ChatFilter instance;

    private List<Pattern> chatPatterns = new ArrayList<>();
    private File config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        config = event.getSuggestedConfigurationFile();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        ConfigHandler.parsePatterns();

        System.out.println("Loaded the stupid mod");

        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ManagerCmd());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        String unformattedMessage = event.message.getUnformattedText().replaceAll("[&§][0-9A-FM-RKa-fm-ok]", "");
        if (ChatFilter.instance.getChatPatterns()
                .stream().noneMatch(pattern -> pattern.matcher(unformattedMessage).find())) {
            return;
        }

        event.setCanceled(true);
    }

    public File getConfig() {
        return config;
    }

    public List<Pattern> getChatPatterns() {
        return chatPatterns;
    }
}
