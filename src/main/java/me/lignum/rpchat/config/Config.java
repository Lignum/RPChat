package me.lignum.rpchat.config;

import com.google.common.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.Getter;
import me.lignum.rpchat.RPChat;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Getter
public class Config {
    private String chatFormat = "&7${user}&8:&r ${message}";
    private ChatRange chatRange;

    @Getter(AccessLevel.NONE)
    private final ConfigurationLoader<CommentedConfigurationNode> loader;

    @Getter(AccessLevel.NONE)
    private ConfigurationNode rootNode;

    private boolean valid = true;

    public Config(File configFile) {
        loader = HoconConfigurationLoader.builder()
            .setFile(configFile)
            .build();

        try {
            rootNode = loader.load();
        } catch (IOException e) {
            valid = false;
            RPChat.get().getLogger().log(Level.SEVERE, "Failed to load config file!!", e);
            return;
        }

        try {
            if (rootNode.getNode("chat", "format").isVirtual()) {
                rootNode.getNode("chat", "format").setValue(chatFormat);
                rootNode.getNode("chat", "range").setValue(TypeToken.of(ChatRange.class), new ChatRange());

                try {
                    loader.save(rootNode);
                } catch (IOException e) {
                    RPChat.get().getLogger().log(Level.SEVERE, "Failed to create default config file!!", e);
                    return;
                }
            }

            chatFormat = rootNode.getNode("chat", "format").getString(chatFormat);
            chatRange = rootNode.getNode("chat", "range").getValue(TypeToken.of(ChatRange.class), new ChatRange());
        } catch (ObjectMappingException e) {
            RPChat.get().getLogger().log(Level.SEVERE, "Failed to load config file!!", e);
        }
    }
}
