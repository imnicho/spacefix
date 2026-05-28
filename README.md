# SpaceFix

Brings the villager trade screen's Space-to-restock quick-craft to other crafting screens.

In vanilla Minecraft, the villager trading screen lets you tap **Space** to restock the input
slots from your inventory, so you can bulk-trade without dragging items around. Several other
crafting screens lack any equivalent. SpaceFix fixes that, starting with the **stonecutter**.

## What it does

Pick a recipe in the stonecutter once — just click it as you normally would. SpaceFix remembers
that choice (per material), so you don't have to re-select it every time the input runs dry. Then:

- **Space** — cut one full input stack. If the input slot is empty or partial, it's topped up to
  a full stack from your inventory (same material) first, the recipe is re-selected, then the
  whole stack is cut. You see the input fill, then cut a moment later, with a stonecutter sound.
- **Shift + Space** — bulk mode. It refills the input, re-selects the recipe, and cuts, over and
  over, until no matching material is left in your inventory or your inventory is full.

Cutting plays out one stack at a time rather than vanishing in a single frame: each refill→cut
cycle is spaced a few ticks apart and a stonecutter sound plays on every cut, so a big bulk run
looks and sounds like work being done.

No keybind to configure — it mirrors villager trading and uses Space directly. (Just like a
villager trade, you select what you want once; Space repeats it.)

## Client-side only

SpaceFix runs entirely on the client. It works on any server with only your client having the mod
installed — no server-side install, no datapack. This is the same category of behavior as the
villager spacebar trick: everything happens through the normal slot-interaction packets a vanilla
client already sends, so the server needs no awareness of the mod.

## Requirements

- Minecraft 1.21.1
- NeoForge for Minecraft 1.21.1 (any 21.1.x)
- Java 21 (only needed to build from source)

## Building

Uses the Gradle wrapper — no system Gradle required.

```bash
./gradlew build
```

The jar is written to `build/libs/spacefix-<version>.jar`. Drop it into your `mods/` folder.

To launch a development client for testing:

```bash
./gradlew runClient
```

## Extending it

The mod is built around a small registry of per-screen handlers. Each screen plugs in through the
`SpacebarAction` interface — `handles(screen)`, `prepareCut(...)`, `executeCut(...)`, and
`reset()` — and is registered in a single list in `SpacebarKeyHandler`. Adding support for a new
screen is a matter of writing one class and registering it; no changes to the dispatch logic.

The stonecutter is the only module shipped today. The loom, smithing table, and the crafting-table
recipe book are natural next fits and are planned.

## License

MIT — see [LICENSE](LICENSE).
