package me.lignum.rpchat.vars;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@FunctionalInterface
public interface MessageVar {
    Text getText(Player player, Player recipient, Text message);
}
