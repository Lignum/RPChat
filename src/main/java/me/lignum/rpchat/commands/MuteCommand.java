package me.lignum.rpchat.commands;

import lombok.val;
import me.lignum.rpchat.RPChat;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class MuteCommand implements CommandExecutor {
    public static final CommandSpec MUTE_SPEC = CommandSpec.builder()
        .description(Text.of("Mutes a player."))
        .permission("rpchat.cmds.mute")
        .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
        .executor(new MuteCommand(true))
        .build();

    public static final CommandSpec UNMUTE_SPEC = CommandSpec.builder()
        .description(Text.of("Unmutes a player that has been muted with /mute."))
        .permission("rpchat.cmds.unmute")
        .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
        .executor(new MuteCommand(false))
        .build();

    private final boolean mute;

    private MuteCommand(boolean mute) {
        this.mute = mute;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> optPlayer = args.getOne("player");

        if (!optPlayer.isPresent()) {
            src.sendMessage(Text.of(TextColors.RED, "No such player!"));
            return CommandResult.empty();
        }

        Player player = optPlayer.get();

        val mutedPlayers = RPChat.get().getMutedPlayers();

        if (mute) {
            if (mutedPlayers.contains(player)) {
                src.sendMessage(Text.of(TextColors.RED, "This player is already muted! Use /unmute to unmute them."));
                return CommandResult.empty();
            }

            mutedPlayers.add(player);

            src.sendMessage(Text.of(
                TextColors.GREEN, "Player ", TextColors.YELLOW, player.getName(), TextColors.GREEN, " has been muted."
            ));
        } else {
            if (!mutedPlayers.contains(player)) {
                src.sendMessage(Text.of(TextColors.RED, "This player has not been muted!"));
                return CommandResult.empty();
            }

            mutedPlayers.remove(player);

            src.sendMessage(Text.of(
                TextColors.GREEN, "Player ", TextColors.YELLOW, player.getName(), TextColors.GREEN, " has been unmuted."
            ));
        }
        return CommandResult.success();
    }
}
