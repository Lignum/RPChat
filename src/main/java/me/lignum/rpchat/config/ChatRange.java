package me.lignum.rpchat.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigSerializable
public class ChatRange {
    public ChatRange() {
        this.enabled = false;
        this.radius = 80;
    }

    @Setting(comment = "Should chat be ranged? If false, everyone can hear every message.")
    private final boolean enabled;

    @Setting(comment = "The range, in blocks, in which a chat message may be heard.")
    private final int radius;
}
