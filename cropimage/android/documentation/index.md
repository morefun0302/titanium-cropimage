# cropimage Module

## Description
This module extends Titanium native functionality on android in order to crop images easily.
It does not rely on the com.android.camera.action.CROP intent but it is rather built thanks
to *Lorenzo Villani* and his crop activity (accessible on github
https://github.com/lvillani/android-cropimage).

The module is quite simple, and act more like a prototype of what it could be.

## Accessing the cropimage Module

To access this module from JavaScript, you would do the following:

    var cropimage = require("proto.cropimage");

The cropimage variable is a reference to the Module object.

## Reference

### cropimage Functions
#### `cropimage.cropImage(options)`
Use to start the crop activity. Three arguments are expected through the options :

* `imagePath` {string} : The path to the source / input image to crop. By default,
the image will not be replaced.  

* `success` {function} : A callback function called when the image has been
successfully called. The function take one argument which is an object that hold
the path to the cropped output accessible via the `imagePath` key.
If the user cancel the crop activity, the path will be the input path.

* `error` {function} : A callback called when an error occurs during the activity.
The function takes one argument which is an object that hold an error message
accessible via the `message` key.

#### `cropimage.configure(options) : {boolean}`
This allow you to configure the crop activity. All options possess a default
value; Configure only what you need to. `options` should be a JavaScript object
that may contain :

* `circleCrop` {boolean} : If **true**, the cropper will be displayed as a circle
and the corresponding cropped image will also be a circle.

* `doFaceDetection` {boolean} : If **true**, the activity will try to identify
faces in the image and focus the crop selection on faces.

* `outlineColor` {string} :  Set the color of the rectangle used during the crop,
using android color format. Example : `#FF1DB7FF`

* `outputFormat` {string} : Specify the output format of the cropped image;
Expecting a string in ('JPEG', 'PNG', 'WEBP')

* `outputQuality` {number} : Only works with JPEG output format to specify the
quality of the JPEG : 0 = lowest quality, 100 = best quality; Integer expected.

* `outputX` {number} : Specify the width in px of the expected cropped output.

* `outputY` {number} : Specify the height in px of the expected cropped output.

* `overwrite` {boolean} : If **true**, the source image will be overwritten with the
cropped one.

* `quietMode` {boolean} : If **true**, errors that can't be passed through the error
callback might be caught and handled via console error messages, rather than
making the app crash

* `renamePrefix` {string} : When overwritting is set to false, used to rename
the input.

* `scale` {boolean} : If **true**, scale down the image to fit the cropped output.

* `scaleUpIfNeeded` {boolean} : If **true**, scale up the image to fit the cropped
output size.

The function return **true** if the module has been configured successfully; **false**
otherwise.
### cropimage Properties

All options described above in the `cropimage.configure` method are also
properties of the module and can be set via : `cropimage.set{PropertyName}`,
with the first letter of the property's name capitalized.

## Usage

In a controller `myController.js` :

```javascript
  var cropModule = require("proto.cropimage");

  var configuration = {
    overwrite       : false,
    renamePrefix    : "IWouldRatherPutThisPrefix_",
    outputX         : 400
  };
  cropModule.configure(configuration)

  var cropImage = function (showCameraEvent) {
    if (showCameraEvent.mediaType == Ti.Media.MEDIA_TYPE_PHOTO) {
      cropModule.cropImage({
        imagePath: showCameraEvent.media.getNativePath(),
        success: function (result) {
          $.imageView.image = result.imagePath;
        },
        error: function (error) {
          Ti.API.error(error.message);
        }
      });
    } else {
      Ti.API.error("Invalid media type");
    }
  };

  function selectImage() {
    Titanium.Media.showCamera({
      success: cropImage,
      cancel: function () {},
      error: function (error) { Ti.API.error("An error occured : " + error.code); },
    });
  }
```

## Credits
*Matthias Benkort* <matthias.benkort@gmail.com>

*Lorenzo Villani* <lorenzo@villani.me>


## License
[![License](http://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat)](http://choosealicense.com/licenses/apache-2.0/)
