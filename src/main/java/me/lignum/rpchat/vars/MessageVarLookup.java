package me.lignum.rpchat.vars;

import lombok.RequiredArgsConstructor;
import me.lignum.rpchat.RPChat;
import org.apache.commons.lang3.text.StrLookup;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class MessageVarLookup extends StrLookup<String> {
    private final Player player;
    private final Player recipient;
    private final Text rawMessage;

    private static final Pattern CONTROL_PATTERN = Pattern.compile("^(.+):(.*)$");

    @Override
    public String lookup(String key) {
        Matcher matcher = CONTROL_PATTERN.matcher(key);

        if (matcher.matches() && matcher.groupCount() > 1) {
            String control = matcher.group(1);
            String param = matcher.group(2);

            switch (control.toLowerCase()) {
                case "option": {
                    String[] sides = param.split("\\|", -1);

                    Optional<String> value = player.getOption(sides[0]);

                    if (value.isPresent()) {
                        return value.get();
                    } else if (sides.length > 1) {
                        return sides[1];
                    }

                    break;
                }

                case "permission":
                    return String.valueOf(player.hasPermission(param));

                case "if":
                case "ifn": {
                    String[] sides = param.split("\\|", -1);

                    if (sides.length > 2) {
                        String cond = sides[0];
                        String ifTrue = sides[1];
                        String ifFalse = sides[2];

                        boolean c = cond.equalsIgnoreCase("true");

                        if (control.equalsIgnoreCase("ifn"))
                            c = !c;

                        return c ? ifTrue : ifFalse;
                    }

                    break;
                }
            }
        }

        Optional<MessageVar> optMsgVar = RPChat.get().getMessageVars().stream()
            .filter(m -> m.getFirst().equalsIgnoreCase(key))
            .map(Tuple::getSecond)
            .findFirst();

        if (optMsgVar.isPresent()) {
            MessageVar msgVar = optMsgVar.get();
            Text resolved = msgVar.getText(player, recipient, rawMessage);
            return TextSerializers.FORMATTING_CODE.serialize(resolved);
        }

        return "";
    }
}
