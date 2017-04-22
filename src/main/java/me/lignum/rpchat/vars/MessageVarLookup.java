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

    private static final Pattern OPTION_PATTERN = Pattern.compile("^option:(.+)$");

    @Override
    public String lookup(String key) {
        Matcher matcher = OPTION_PATTERN.matcher(key);

        if (matcher.matches() && matcher.groupCount() > 0) {
            String option = matcher.group(1);
            String[] sides = option.split("\\|");

            Optional<String> value = player.getOption(sides[0]);

            if (value.isPresent()) {
                return value.get();
            } else if (sides.length > 1) {
                return sides[1];
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
