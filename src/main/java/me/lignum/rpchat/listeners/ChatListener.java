package me.lignum.rpchat.listeners;

import lombok.val;
import me.lignum.rpchat.RPChat;
import me.lignum.rpchat.Utils;
import me.lignum.rpchat.config.ChatRange;
import me.lignum.rpchat.config.Config;
import me.lignum.rpchat.vars.MessageVarLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatListener {
    private static int getPlayerChatRadius(Player player) {
        Optional<String> chatRadius = player.getOption("chatRadius");
        int globalRadius = RPChat.get().getConfig().getChatRange().getRadius();

        if (chatRadius.isPresent()) {
            try {
                return Integer.valueOf(chatRadius.get());
            } catch (NumberFormatException e) {
                return globalRadius;
            }
        }

        return globalRadius;
    }

    private static boolean canPlayerHearMessage(Player sender, Player recipient) {
        Config cfg = RPChat.get().getConfig();
        ChatRange cr = cfg.getChatRange();

        return !cr.isEnabled() || Utils.distanceBetween(sender, recipient) <= getPlayerChatRadius(recipient);
    }

    private static Text createChatMessage(Player player, Player recipient, Text rawMessage) {
        String chatFormat = RPChat.get().getConfig().getChatFormat();
        StrSubstitutor sub = new StrSubstitutor(new MessageVarLookup(player, recipient, rawMessage));
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(sub.replace(chatFormat));
    }

    @Listener
    public void onChatMessage(MessageChannelEvent.Chat event) {
        Cause cause = event.getCause();

        val optPlayer = cause.first(Player.class);

        if (optPlayer.isPresent()) {
            // We want to take over the entire chat, meaning we will
            // cancel all chat events.
            event.setMessageCancelled(true);

            Player player = optPlayer.get();
            Text rawMessage = event.getRawMessage();

            // The player is always going to hear themselves, so we'll send their own message to them
            // right away, so that we can filter them out of all further actions below.
            player.sendMessage(createChatMessage(player, player, rawMessage));

            // Find out who can hear this player.
            List<Player> recipients = Sponge.getServer().getOnlinePlayers().stream()
                .filter(p -> !p.equals(player))
                .filter(p -> canPlayerHearMessage(player, p))
                .collect(Collectors.toList());

            // Technically this is just recipients.size() - 1, but I can feel the weird edge cases in my soul.
            long playersWhoCanHear = recipients.stream()
                .filter(p -> !p.equals(player))
                .count();

            if (playersWhoCanHear <= 0) {
                player.sendMessage(Text.of(TextColors.RED, "Nobody heard you..."));
            } else {
                for (Player p : recipients) {
                    p.sendMessage(createChatMessage(player, p, rawMessage));
                }
            }
        }
    }
}
