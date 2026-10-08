# Ender Tweeks (Fabric, Minecraft 1.21.11)

Press **Right Ctrl** in game to open the menu.

* Left-click a module = turn on/off
* Right-click a module = settings + keybind (press the key, Esc = none)
* **Friends** tab: add players by username. Middle-click a player in the world to add/remove them.
  Trigger Bot and Shield Breaker ignore friends. Tracers draw friends in green.

## Build the .jar

You need JDK 21 or newer and an internet connection.

    ./gradlew build          (Windows: gradlew.bat build)

The mod jar is `build/libs/ender-tweeks-1.0.0.jar` (ignore the `-sources` jar).
Put it in `.minecraft/mods` together with **Fabric Loader 0.19.5+** and **Fabric API 0.141.6+1.21.11**.

No JDK? Upload this folder to a GitHub repo - the included workflow (`.github/workflows/build.yml`)
builds it for you and you download the jar from the run's "Artifacts".

## Modules

| Module | How it works |
|---|---|
| Flight | Jump = up, Sneak = down. Right-click: Speed |
| Shield Breaker | Sword ready + target raises shield -> swaps to axe and hits |
| Trigger Bot | Auto-hits what you aim at (sword/axe, cooldown ready). Option: Hit mobs |
| Tracers | Red line to players, green to friends |
| Name Tags | Big tags with armor, hands, durability |
| Fast Break | Haste III |
| Anchor Helper | **Keybind**: place anchor, 1 glowstone, totem, detonate (needs those in hotbar/offhand) |
| Safe Anchor | **Keybind**: place + charge anchor, glowstone on ground toward you, totem, detonate (needs 2 glowstone) |
| Web Drainer | Places a cobweb in nearby water (hotbar needs cobwebs) |
| Web Placer | Cobweb at your feet |
| Block ESP | Settings: Block (e.g. `diamond_ore`), Range, Tracers |
| Xray | Setting: Block (comma separated, e.g. `diamond_ore,ancient_debris`), Fullbright |
| Storage ESP | Chests, barrels, hoppers, spawners, chest minecarts + brown lines |
| Logouts | Blue box + line where a player logged out |
| Hover Totem | Hover a totem in any inventory with empty offhand -> goes to offhand |

Block names match by id substring, so `diamond_ore` also matches `deepslate_diamond_ore`.

## Notes
* Anchors only explode outside the Nether. Totems need to be in the hotbar (or offhand).
* ESP/tracers/name tags are drawn as a 2D overlay, so they show through walls.
* Xray uses mixins on block face rendering; rendering mods like Sodium may bypass it.
* Most multiplayer servers ban these features. Use on singleplayer / servers that allow it.
