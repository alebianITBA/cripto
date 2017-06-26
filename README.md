# Build

You need to have installed [gradle](https://gradle.org/).

To build the executable jar file, run:

```
gradle shadow
```
It'll generate a jar file inside `build/libs` called `secret-image-sharing-scheme-1.0-SNAPSHOT-all.jar`.

Move it to the location of your choice and then run it with the needed arguments..

# Arguments:

## Required:

Either -d or –r
-k <number>
-secret <path>

## Optional:
-n <number>
-dir <path>
-wh

## Meaning of arguments:
 * -d: A secret image will be distributed into other images.
 * –r: A secret image will be recovered from other images.
 * -secret <path>: <path> belongs to secret image of type .bmp. If -d was chosen, this file must exist. If -r was chosen, this will be the output file.
 * -k <number>: Number of the minimum amount of shadows for a scheme (k, n).
 * -n <number>: Number of the total amount of shadows for a scheme (k, n). Only seteable if -d was chosen. If not set, it will be considered to be the amount of images under the corresponding directory.
 * -dir <path>: Path containing the images to use as shadows. Defaults to the working directory.
 * -wh: If set, it will hide the width and height of the secret image in the horizontal and vertical resolution bytes of the .bmp file. If k != 8, this will option will be true even if not set. If k == 8 and this is not set, then all the carrier images MUST have the same dimension as the secret image.

## Examples:
 Hides the image "key.bmp", with a scheme (2, 4) looking for the carrier images under directory "carriers"
 >:$ –d –secret key.bmp –k 2 –n 4 –dir carriers

 Hides the image "key.bmp", with a scheme (3, n) looking for the carrier images under the working directory, where n is the amount of .bmp files in the working directory.
 >:$ –d –secret key.bmp –k 3

 Retrieves the secret image "key.bmp" with a scheme (2, 4) using as carrier images those under directory "carriers".
 >:$ –r –secret key.bmp –k 2 –n 4 –dir carriers

 Retrieves the secret image "key.bmp", with a scheme (3, n) using as carrier images those .bmp files under the working directory, where n is the amount of .bmp files in the working directory.
 >:$ –r –secret key.bmp –k 3

 Hides the image "key.bmp", with a scheme (8, 8) looking for the carrier images under directory "carriers", saving the width and height of the secret image on the horizontal and vertical resolution bytes of the carrier images.
 >:$ –d –secret key.bmp –k 8 –n 8 –dir carriers -wh

 Retrieves the image "key.bmp", with a scheme (8, 8) using as the carrier images under directory "carriers", retrieving the width and height of the secret image from the horizontal and vertical resolution bytes of the carrier images.
 >:$ –r –secret key.bmp –k 8 –n 8 –dir carriers -wh