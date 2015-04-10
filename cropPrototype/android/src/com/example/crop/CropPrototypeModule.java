/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.example.crop;

import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;

import com.android.camera.CropImageIntentBuilder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.nio.file.Files;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.ClassCastException;
import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

@Kroll.module(name="CropPrototype", id="com.example.crop")
public class CropPrototypeModule extends KrollModule
{
	// Standard Debugging variables
    private static final String LCAT = "CropPrototypeModule"; 
    private static final boolean DBG = TiConfig.LOGD;

    /* Keys Constants Values */
    @Kroll.constant public static final String imagePath    = "imagePath";
    @Kroll.constant public static final String onSuccess    = "success";
    @Kroll.constant public static final String onError      = "error";
    @Kroll.constant public static final String message      = "message";

    /* Options */
    private boolean overwrite       = false;
    private int     borderColor     = 0xFF03A9F4;
    private String  renamePrefix    = "_cropped"; 
    private boolean quietMode       = true;

    /** Default constructor */
    public CropPrototypeModule() { super(); }

    /**
     * Configure the module before using. 
     * */
    @Kroll.method public boolean configure(KrollDict options)
    {
        try {
            for (String k : options.keySet()) {
                this.getClass()
                    .getDeclaredMethod("set"+ k.substring(0,1).toUpperCase() + k.substring(1), Object.class)
                    .invoke(this, options.get(k));
            }
        } catch (NoSuchMethodException e) {
            logError("Invalid option key; Please check the supplied options");
            return false;
        } catch (IllegalAccessException e) {
            logError("Invalid option key; Please check the supplied options");
            return false;
        } catch (InvocationTargetException e) {
            /* Come from a ClassCastException in the setters */
            logError("Invalid option type; Please check the supplied options");
            return false;
        }
        return true;
    }

    /** 
     * Use to crop an image selected.  
     * Returns are processed via callbacks that handle a single object
     * @param options Handle all options and data supplied to the function.
     *      Expected options : 
     *          imagePath   - str       : Path to the image to crop
     *          success     - function  : Success callback
     *          error       - function  : Error callback
     * */
    @Kroll.method public void cropImage(KrollDict options) 
    {
        /* Check if the path is there */
        if (quietMode && !(options.containsKey(imagePath))) {
            logError("Path to the source image is missing");
            return;
        }
        String srcFileUrl = options.get(imagePath).toString();

        /* Be sure that callbacks exist and are valid KrollFunction */
        if (quietMode && !(options.containsKey(onSuccess) && options.containsKey(onError))) {
            logError("Callbacks are missing");
            return;
        }

        KrollFunction success, error; 
        try { 
            success = (KrollFunction) options.get(onSuccess);
            error = (KrollFunction) options.get(onError);
        } catch (ClassCastException e) {
            if(!quietMode) throw e;
            logError("Callbacks are invalid. Please specify valid JavaSript functions");
            return;
        }
        CropResultHandler cropResultHandler = new CropResultHandler(success, error);

        try {
            if(DBG) Log.d(LCAT, "Starting cropImage");

            /* Define the output URI*/ 
            Uri srcFileUri = Uri.parse(srcFileUrl);
            Uri destFileUri = srcFileUri;
            if (!overwrite) {
                String filename = ( new File(srcFileUri.toString()) ).getName();
                destFileUri = Uri.parse(srcFileUrl.replaceFirst(filename, renamePrefix + filename));
            }
            
            if(DBG) {
                Log.d(LCAT, "Source file : " + srcFileUri.toString());
                Log.d(LCAT, "Destination file : " + destFileUri.toString());
            }

            /* Initialize the intent for the crop activity */
            CropImageIntentBuilder intentBuilder = new CropImageIntentBuilder(200, 200, destFileUri); 
            intentBuilder.setOutlineColor(borderColor);
            intentBuilder.setSourceImage(srcFileUri);

            /* Get the current activity and call the intent */
            Activity mainActivity = TiApplication.getAppCurrentActivity(); 
            TiActivitySupport mainActivitySupport = (TiActivitySupport) mainActivity;
            mainActivitySupport.launchActivityForResult(
                intentBuilder.getIntent(mainActivity),
                mainActivitySupport.getUniqueResultCode(), 
                cropResultHandler); 

        } catch (Exception e) {
            cropResultHandler.handleError(e);
        }
    }

    protected class CropResultHandler implements TiActivityResultHandler
    {
        private KrollFunction successCallback, errorCallback;
        private KrollDict callbackArgs; 

        public CropResultHandler(KrollFunction successCallback, KrollFunction errorCallback)
        {
            /* Declare all callbacks for further purpose */
            this.successCallback  = successCallback;
            this.errorCallback    = errorCallback;
            callbackArgs = new KrollDict(); 
        }

        public void handleError(Exception e)
        {
            if(DBG) Log.d(LCAT, "An error has occured");
            callbackArgs.put(message, e.getMessage());
            errorCallback.callAsync((KrollObject) errorCallback, callbackArgs);
        }
   
        public void onError(Activity activity, int requestCode, Exception e) 
        {
          handleError(e);
        }

        public void onResult(Activity activity, int requestCode, int resultCode, Intent data)
        {
            if (resultCode == Activity.RESULT_CANCELED) {
                handleError(new Exception("Crop has been aborted by the User"));  
                return;
            }

            callbackArgs.put(imagePath, data.getAction()); 
            successCallback.callAsync((KrollObject) successCallback, callbackArgs); 
        }
    }

    /* Log an error into Titanium's console */
    private void logError (String errorMsg) 
    {
      Log.e(LCAT, "\n/!\\ ------------------------ /!\\\n" + errorMsg);
    }

    /* Configuration Setters */
    /** overwrite option setter, expecting a boolean argument; 
     * if True, the source image will be overwritten with the cropped one. 
     * @param value The value to set; expecting a boolean.
     * */
    @Kroll.setProperty public void setOverwrite(Object value) {
        overwrite = ((Boolean) value).booleanValue();
    }

    /** border-color option setter, expecting an integer. 
     * Set the color of the rectangle used during the crop, in hexadecimal with alpha levels. 
     * Exemple : 0xFF03A9F4 
     * @param value The value to set; expecting an hexidecimal integer value.
     * */
    @Kroll.setProperty public void setBorderColor(Object value) {
        borderColor = ((Integer) value).intValue();
    }

    /** renamePrefix option setter, expecting a String.
     * In case when overwritting is set to false, used to rename the input 
     * @param value The value to set; expecting a String value.
     * */
    @Kroll.setProperty public void setRenamePrefix(Object value) {
        renamePrefix = value.toString();
    }

    /** quietMode option setter, expecting a boolean argument; 
     * if True, errors that can't be passed through the error callback might be caught 
     * and handled via console error messages, rather than making the app crash 
     * @param value The value to set; expecting a boolean value.
     * */
    @Kroll.setProperty public void setQuietMode(Object value) {
        quietMode = ((Boolean) value).booleanValue();
    }

}
