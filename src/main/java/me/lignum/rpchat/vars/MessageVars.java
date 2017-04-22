package me.lignum.rpchat.vars;

import me.lignum.rpchat.RPChat;
import me.lignum.rpchat.Utils;
import org.spongepowered.api.text.Text;

public class MessageVars {


    public static final MessageVar USER     = (p, r, m) -> Text.of(p.getName());
    public static final MessageVar MESSAGE  = (p, r, m) -> m;
    public static final MessageVar WORLD    = (p, r, m) -> Text.of(p.getWorld().getName());
    public static final MessageVar DISTANCE = (p, r, m) -> Text.of(String.valueOf(Utils.distanceBetween(p, r)));

    public static void initDefaults() {
        RPChat p = RPChat.get();
        p.registerMessageVar("user", USER);
        p.registerMessageVar("message", MESSAGE);
        p.registerMessageVar("world", WORLD);
        p.registerMessageVar("distance", DISTANCE);
    }
}
