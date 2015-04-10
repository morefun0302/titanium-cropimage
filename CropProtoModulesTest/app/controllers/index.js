var cropModule = require("com.example.crop");

var configuration = {
    overwrite       : false,
    renamePrefix    : "IWouldRatherPutThisPrefix_",
    outputX         : 400
};

if (cropModule.configure(configuration)) 
    Ti.API.info("Module configured successfully");


var cropImage = function (showCameraEvent) {
    if (showCameraEvent.mediaType == Ti.Media.MEDIA_TYPE_PHOTO) {
        cropModule.cropImage({
            imagePath: showCameraEvent.media.getNativePath(),
            success: function(result) {
                Ti.API.info(result.imagePath);
                $.imageView.image = result.imagePath;
            },
            error: function(error) {
                alert(error.message);
            }
        });
    } else {
        alert("Invalid media type");
    }
};

function selectImage() {
    Titanium.Media.showCamera({
        success: cropImage,
        cancel: function(){},
        error: function(error) {
            alert("An error occured : " + error.code);
        },
    });
}

$.index.open();
