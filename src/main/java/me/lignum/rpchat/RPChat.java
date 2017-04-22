package me.lignum.rpchat;

import lombok.Getter;
import me.lignum.rpchat.commands.MuteCommand;
import me.lignum.rpchat.config.Config;
import me.lignum.rpchat.listeners.ChatListener;
import me.lignum.rpchat.vars.MessageVar;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.util.Tuple;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@Plugin(id = "rpchat", name = "RPChat", version = "1.0.0", dependencies = {
    @Dependency(id = "nations", optional = true)
})
public class RPChat {
    private static RPChat instance;

    public static RPChat get() {
        return instance;
    }

    @Inject
    @Getter private Logger logger;
    @Getter private Config config;

    @Getter private List<Tuple<String, MessageVar>> messageVars = new LinkedList<>();
    @Getter private List<Player> mutedPlayers = new LinkedList<>();

    public void registerMessageVar(String name, MessageVar mvar) {
        messageVars.add(Tuple.of(name, mvar));
    }

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path configDir;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        Sponge.getEventManager().registerListeners(this, new ChatListener());
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        File configDirFile = configDir.toFile();
        File configFile = new File(configDirFile, "rpchat.cfg");

        if (!configDirFile.exists() && !configDirFile.mkdirs()) {
            logger.severe("Failed to create config directory!!");
            return;
        }

        config = new Config(configFile);

        DefaultMessageVars.init(this);

        Sponge.getCommandManager().register(this, MuteCommand.MUTE_SPEC, "mute");
        Sponge.getCommandManager().register(this, MuteCommand.UNMUTE_SPEC, "unmute");
    }
}
