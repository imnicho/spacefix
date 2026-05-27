# SpaceFix

Brings the villager trade screen's Space-to-restock quick-craft to other crafting screens.

In vanilla Minecraft, the villager trading screen lets you tap **Space** to restock the input
slots from your inventory, so you can bulk-trade without dragging items around. Several other
crafting screens lack any equivalent. SpaceFix fixes that, starting with the **stonecutter**.

## What it does

With a recipe selected in the stonecutter:

- **Space** — cut one full input stack. If the input slot is empty or partial, it's topped up to
  a full stack from your inventory (same material) first, then the whole stack is cut.
- **Shift + Space** — bulk mode. Repeatedly refill the input, re-select the recipe, and cut until
  no matching material is left in your inventory or your inventory is full.

No keybind to configure — it mirrors villager trading and uses Space directly.

## Client-side only

SpaceFix runs entirely on the client. It works on any server with only your client having the mod
installed — no server-side install, no datapack. This is the same category of behavior as the
villager spacebar trick: everything happens through the normal slot-interaction packets a vanilla
client already sends, so the server needs no awareness of the mod.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.231 or newer
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
`SpacebarAction` interface — `handles(screen)`, `doOne(...)`, and `reset()` — and is registered in
a single list in `SpacebarKeyHandler`. Adding support for a new screen is a matter of writing one
class and registering it; no changes to the dispatch logic.

The stonecutter is the only module shipped today. The loom, smithing table, and the crafting-table
recipe book are natural next fits and are planned.

## License

MIT — see [LICENSE](LICENSE).
