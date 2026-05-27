package wtf.nicho.spacefix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public interface SpacebarAction {
    boolean handles(Screen screen);

    boolean doOne(Minecraft mc, Screen screen);

    default void reset() {
    }
}
