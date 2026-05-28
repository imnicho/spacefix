package wtf.nicho.spacefix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public interface SpacebarAction {
    boolean handles(Screen screen);

    /** Top up the input and (re)select the recipe. Returns true when ready to cut. */
    boolean prepareCut(Minecraft mc, Screen screen);

    /** Cut one prepared stack. Returns true if it made progress (something was cut). */
    boolean executeCut(Minecraft mc, Screen screen);

    default void observe(Screen screen) {
    }

    default void reset() {
    }
}
