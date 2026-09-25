## The Village

### Overview

This is a simulation of a family spending a cosy day at home. What tasks they choose to do are deterministic, but can change drastically based on their initial starting context.

### Usage

This is a [Mill](https://mill-build.org) project pinned to Mill 0.11.7 (`.mill-version`); use the `./millw` wrapper (`millw.bat` on Windows). Mill 0.11 needs **JDK 21** (newer JDKs crash it).

```sh
./millw game.run        # run the game
./millw game.test       # unit tests (munit)
./millw game.compile    # compile only
./millw __.reformat     # scalafmt
```

Press **F1** in game to toggle the debug overlay: the old outline shapes plus the tile grid, drawn over the sprites.

### Rendering

The house is 17×22 tiles of 16 px, so the world is a fixed 272×352 image. It is drawn into a framebuffer, then scaled onto the screen at the largest whole-number scale that fits. The UI text is drawn at screen resolution. Draw order is floor, walls, counter, then furniture and characters sorted by y (higher y is drawn first).

Sprites come from `assets/atlas/game.atlas`. A missing region is drawn as a magenta square and logged once.

### Art pipeline

```sh
./millw game.genPlaceholders   # write solid-colour PNGs to art/placeholders/
./millw game.packAssets        # pack art/placeholders + art/export into assets/atlas/
```

Put real art (e.g. an Aseprite CLI export) in `art/export/`. A file there replaces the placeholder with the same name. The packed atlas is committed, so the game runs without packing first. Re-run `packAssets` after changing art.

Region names (one PNG per region, named `<region>.png`, or `<region>_N.png` for animation frames):

| Kind | Names | Size (px) |
| --- | --- | --- |
| Characters | `<who>_idle_{down,up,side}`, `<who>_walk_{down,up,side}_0..3`, `<who>_sit` where `<who>` is `father`, `mother`, `son` or `daughter` | 16×24 |
| Facing | `side` faces right; the game mirrors it for left | |
| Stateful objects | `fireplace_{unlit,lit}`, `stove_{idle,cooking,fire}`, `dishwasher_{idle,running,open}`, `front_door_{broken,fixed}` | footprint |
| Other furniture | `couch`, `sofachair`, `table`, `worktable`, `easel`, `fridge`, `washing_machine`, `coffee_table`, `living_room_table`, `counter_bottom`, `counter_side`, `bedroom_door` | footprint |
| House | `floor` (one tile, repeated), `wall_h`, `wall_v`, `wall_corner` | 16×16, 16×4, 4×16, 4×4 |

Any region can gain frames by adding `_0`, `_1`, … files. `SnowedInSprites.placeholders` in `game/src/main/scala/snowedin/sprites.scala` lists every region with its size. The stories that make a character sit are listed in `SnowedInSprites.storyPoses` in the same file.

To save a screenshot and exit, run `SNOWEDIN_SCREENSHOT=/tmp/shot.png SNOWEDIN_SCREENSHOT_FRAME=900 ./millw game.run`. Set `SNOWEDIN_DEBUG=1` to start with the debug overlay on.
