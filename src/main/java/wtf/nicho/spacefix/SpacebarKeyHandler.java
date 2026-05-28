package wtf.nicho.spacefix;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import wtf.nicho.spacefix.modules.StonecutterAction;

public class SpacebarKeyHandler {
    // Ticks between each visible sub-step (refill, then cut). 20 ticks = 1s. Tune to taste.
    private static final int STEP_TICKS = 2;

    private final List<SpacebarAction> actions = List.of(new StonecutterAction());
    private Screen lastScreen;

    // Active job state (a "job" is one Space press: single = one cut cycle, bulk = repeat).
    private SpacebarAction job;
    private Screen jobScreen;
    private boolean jobBulk;
    private boolean jobCutPhase; // false = prepare next stack, true = cut the prepared stack
    private int jobTick;

    @SubscribeEvent
    public void onScreenRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (screen != lastScreen) {
            lastScreen = screen;
            cancelJob();
            for (SpacebarAction action : actions) {
                action.reset();
            }
        }
        for (SpacebarAction action : actions) {
            if (action.handles(screen)) {
                action.observe(screen);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (event.getKeyCode() != GLFW.GLFW_KEY_SPACE) {
            return;
        }
        Screen screen = event.getScreen();
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

        boolean shift = (event.getModifiers() & GLFW.GLFW_MOD_SHIFT) != 0;
        job = action;
        jobScreen = screen;
        jobBulk = shift;
        jobCutPhase = false;
        jobTick = STEP_TICKS; // act on the very next tick for a snappy start
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (job == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != jobScreen || !job.handles(mc.screen)) {
            cancelJob(); // screen closed/changed → stop (interruptible)
            return;
        }
        if (++jobTick <= STEP_TICKS) {
            return;
        }
        jobTick = 0;

        if (!jobCutPhase) {
            if (job.prepareCut(mc, jobScreen)) {
                jobCutPhase = true;
            } else {
                cancelJob(); // nothing left to cut
            }
        } else {
            boolean progress = job.executeCut(mc, jobScreen);
            if (progress) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_TAKE_RESULT, 1.0F));
            }
            if (jobBulk && progress) {
                jobCutPhase = false; // loop to the next stack
            } else {
                cancelJob();
            }
        }
    }

    private void cancelJob() {
        job = null;
        jobScreen = null;
        jobCutPhase = false;
    }
}
