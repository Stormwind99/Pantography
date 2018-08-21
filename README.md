# Pantography
Minecraft plugin: Copy map item data to other maps including different scale maps.

A pantograph is "is a mechanical linkage connected in a manner based on parallelograms so that the movement of one pen, in tracing an image, produces identical movements in a second pen. If a line drawing is traced by the first point, an identical, enlarged, or miniaturized copy will be drawn by a pen fixed to the other" \[[Wikipedia](https://en.wikipedia.org/wiki/Pantograph)\].

Two methods of use are available:

1. Place two filled maps into a crafting grid
   * The first filled map will be the target map, and the second filled map the source map.  The maps can be different scales and different areas.
   * The resulting filled map will be the target map with any data that fits into its area from the source map copied into any blank pixels of the target map - and the source map will be left in the crafting grid unchanged.
2. Build and use a pantograph.
   * Place source filled maps in the left crafting grid, and a target filled map in the target slot in middle.  If the source maps all intersect the target map, a result map will appear in the crafting result slot on the right.  Take the result map, and the target map will also clear.
   * Place source filled maps in the left crafting grid, and a target empty map in the target slot in middle.  In the crafting result slot on the right will appear a result filled map, which attempts to contain the data from all of the source filled maps (even scaling to higher scale levels if needed).


## Example uses

Example uses:

* Make a bunch of detailed scale 0 maps, then copy all those maps into a scale 4 map to make a single overview map without duplicating exploration.
* Make and fill a scale 4 map, decide where to explore in detail, and copy the scale 4 Map data into a scale 1 map to start with a general picture of the area and explore to fill in detail.
* Using [Bibliocraft's Atlas](http://www.bibliocraftmod.com/wiki/atlas/), create an atlas of scale 0 maps, then copy the map data to a set of scale 4 maps to make an overview atlas.

## Screenshots

![Higher scale to lower scale map copy](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/higher-to-lower-scale-map.png)

A higher scale source map has been copied into a lower scale destination map. Since the source map had less detail, the copied map pixels are large. The large map pixels will be replaced with normal map detail when the map is used in that area.

![Lower scale to higher scale map copy](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/lower-to-higher-scale-map.png)

A lower scale source map has been copied into a higher scale destination map.  The lower scale source map's data shows as an isolated rectangle in the destination map.

![Crafting grid recipe](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/example-copy.png)

Example of copying filled Map data from one Map (on the right) to the other Map (on the left)

![Pantograph recipe](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/pantograph_recipe.png)

Pantograph recipe to build a pantograph.

![Pantograph block](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/pantograph_block.png)

Pantograph block, with an example resulting filled map in a frame showing a scale 0 (lower left) and scale 4 (upper right) map transcribed together.

![Transcribe](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/pantograph_transcribe.png)

Example of a pantograph map transcription.

![Copy](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/pantograph_copy.png)

Example of a pantograph map copy.

![Copy result](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/pantograph_copy_result.png)

The resulting filled map from the above pantograph map copy.

![Copy fail](https://raw.githubusercontent.com/Stormwind99/Pantography/master/other/screenshots/pantograph_fail.png)

Example of an invalid attempt at a pantograph map copy - one of the source filled maps is outside the area of the target map.
