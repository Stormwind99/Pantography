# Pantography
Minecraft plugin: Copy Map item data to other Maps including different scale Maps.

A pantograph is "is a mechanical linkage connected in a manner based on parallelograms so that the movement of one pen, in tracing an image, produces identical movements in a second pen. If a line drawing is traced by the first point, an identical, enlarged, or miniaturized copy will be drawn by a pen fixed to the other" \[[Wikipedia](https://en.wikipedia.org/wiki/Pantograph)\].

Place two Filled Maps into a crafting grid - the first will be the destination Map, and the second the source Map.  The maps can be different scales and different areas.

The resulting Filled Map will be the destination Map with any data that fits into its area from the source Map copied into any blank pixels of the destination Map - and the source Map will be left in the crafting grid unchanged.

## Example uses

Example uses:

* Make a bunch of detailed scale 0 Maps, then copy all those Maps into a scale 4 Map to make a single overview Map without duplicating exploration.
* Make and fill a scale 4 Map, decide where to explore in detail, and copy the scale 4 Map data into a scale 1 Map to start with a general picture of the area and explore to fill in detail.
* Using [Bibliocraft's Atlas](http://www.bibliocraftmod.com/wiki/atlas/), create one or more scale 1 Atlas of Maps, then copy the Map data to a set of scale 5 Maps to make an overview Atlas.

## Screenshots

![Higher scale to lower scale map copy](https://raw.githubusercontent.com/Stormwind99/Pantography/master/src/resources/screenshots/higher-to-lower-scale-map.png)

A higher scale source map has been copied into a lower scale destination map. Since the source map had less detail, the copied map pixels are large. The large map pixels will be replaced with normal map detail when the map is used in that area.

![Lower scale to higher scale map copy](https://raw.githubusercontent.com/Stormwind99/Pantography/master/src/resources/screenshots/lower-to-higher-scale-map.png)

A lower scale source map has been copied into a higher scale destination map.  The lower scale source map's data shows as an isolated rectangle in the destination map.

![Recipe](https://raw.githubusercontent.com/Stormwind99/Pantography/master/src/resources/screenshots/example-copy.png)

Example of copying filled Map data from one Map (on the right) to the other Map (on the left)