var myModule = require("com.example.crop");

function selectImage() {
    Titanium.Media.showCamera({
        success: function(event) {
            if (event.mediaType == Ti.Media.MEDIA_TYPE_PHOTO) {
                myModule.cropImage(
                    event.media.getNativePath(),
                    false, //Overwrite the previous file
                    {
                        success: function(result) {
                            $.imageView.image = result.imagePath;
                        },
                        error: function(error) {
                            alert(error.message);
                        }
                    });
            } else {
                alert("Invalid media type");
            }
        },

        cancel: function(){},

        error: function(error) {
            alert("An error occured : " + error.code);
        },
    });
}

$.index.open();
