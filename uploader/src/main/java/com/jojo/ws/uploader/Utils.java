package com.jojo.ws.uploader;

import android.content.Context;

import com.jojo.ws.uploader.core.breakstore.BreakStore;
import com.jojo.ws.uploader.core.breakstore.BreakStoreOnCache;
import com.jojo.ws.uploader.core.connection.UploadConnection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Utils {
    public static BreakStore createBreakStoreOnDisk(final Context context) {
        final String breakStoreOnDiskClassName = "com.jojo.ws.uploader.cache.BreakStoreOnDisk";
        try {
            final Constructor constructor = Class.forName(breakStoreOnDiskClassName)
                    .getDeclaredConstructor(Context.class);
            return (BreakStore) constructor.newInstance(context);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        }
        return new BreakStoreOnCache(null);
    }

    public static UploadConnection.Factory createDefaultConnectionFactory() {
        final String uploadConnectionFactoryClassName = "com.jojo.wsuploader.okhttp.UploadOkhttpConnection$Factory";
        try {
            final Constructor constructor = Class.forName(uploadConnectionFactoryClassName)
                    .getDeclaredConstructor();
            return (UploadConnection.Factory) constructor.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        }
        return null;
    }
}
