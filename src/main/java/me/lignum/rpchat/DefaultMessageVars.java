package me.lignum.rpchat;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

class DefaultMessageVars {
    public static void init(RPChat c) {
        c.registerMessageVar("user", (p, r, m) -> Text.of(p.getName()));
        c.registerMessageVar("message", (p, r, m) -> m);
        c.registerMessageVar("world", (p, r, m) -> Text.of(p.getWorld().getName()));
        c.registerMessageVar("distance", (p, r, m) -> Text.of(String.valueOf(Utils.distanceBetween(p, r))));
        c.registerMessageVar("biome", (p, r, m) -> Text.of(p.getLocation().getBiome().getName()));

        if (Sponge.getPluginManager().isLoaded("nations")) {
            c.registerMessageVar("nation", (p, r, m) -> {
                Nation nation = DataHandler.getNationOfPlayer(p.getUniqueId());

                if (nation != null) {
                    return Text.of(nation.getName());
                }

                return Text.of("true");
            });

            c.registerMessageVar("nation_inside", (p, r, m) -> {
                Location<World> loc = p.getLocation();
                Nation nation = DataHandler.getNation(loc);

                if (nation != null) {
                    return Text.of(nation.getName());
                }

                return Text.of("true");
            });
        }
    }
}
