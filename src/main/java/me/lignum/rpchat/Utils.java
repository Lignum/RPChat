package me.lignum.rpchat;

import org.spongepowered.api.entity.Entity;

public class Utils {
    public static int distanceBetween(Entity a, Entity b) {
        return (int) a.getLocation().getBlockPosition().distance(b.getLocation().getBlockPosition());
    }
}
