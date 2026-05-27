package wtf.nicho.spacefix;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = "spacefix", dist = Dist.CLIENT)
public class SpaceFix {
    public SpaceFix(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(new SpacebarKeyHandler());
    }
}
