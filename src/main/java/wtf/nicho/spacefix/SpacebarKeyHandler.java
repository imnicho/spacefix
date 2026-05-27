package wtf.nicho.spacefix;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import wtf.nicho.spacefix.modules.StonecutterAction;

public class SpacebarKeyHandler {
    private static final int BULK_CAP = 4096;

    private final List<SpacebarAction> actions = List.of(new StonecutterAction());
    private Screen lastScreen;

    @SubscribeEvent
    public void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        Screen screen = event.getScreen();

        if (screen != lastScreen) {
            lastScreen = screen;
            for (SpacebarAction action : actions) {
                action.reset();
            }
        }

        if (event.getKeyCode() != GLFW.GLFW_KEY_SPACE) {
            return;
        }

        SpacebarAction action = null;
        for (SpacebarAction candidate : actions) {
            if (candidate.handles(screen)) {
                action = candidate;
                break;
            }
        }
        if (action == null) {
            return;
        }

        event.setCanceled(true);

        Minecraft mc = Minecraft.getInstance();
        boolean shift = (event.getModifiers() & GLFW.GLFW_MOD_SHIFT) != 0;
        if (shift) {
            int n = 0;
            while (action.doOne(mc, screen)) {
                if (++n >= BULK_CAP) {
                    break;
                }
            }
        } else {
            action.doOne(mc, screen);
        }
    }
}
