# RasterPlotter

[RasterPlotter.jar](RasterPlotter.jar) - Jar download: `java -jar RasterPlotter.jar`:

```
Usage: RasterPlotter pathToImage thresholdValue
```

RasterPlotter assumes input image is a monochromatic dithered image with black and white pixels only, the output will appear negative/inverted, this is so it can be plotted by a pen plotter.

To use with [DitherKt](https://github.com/fiskurgit/DitherKt) where `jrun` is an alias to a Java JDK:

`jrun DitherCL.jar image.png 8x8Bayer | tail -1 | (read dithered; jrun RasterPlotter.jar "$dithered")`

![Sample](test_anna_8x8Bayer.png)
