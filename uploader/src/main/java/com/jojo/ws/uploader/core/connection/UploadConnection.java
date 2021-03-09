package com.jojo.ws.uploader.core.connection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public interface UploadConnection {
    int NO_RESPONSE_CODE = 0;

    void addHeader(String name, String value);


    Connected excuted(byte[] data) throws IOException;

    Connected excuted(String json) throws IOException;

    Connected postExcuted(String json) throws IOException;

    Connected postExcuted(byte[] data) throws IOException;

    Connected postExcutedWithProgress(byte[] data,ProgressListener progressListener) throws IOException;

    Connected postText(String text) throws IOException;


    /**
     * Set the method for the request, one of:
     * <UL>
     * <LI>GET
     * <LI>POST
     * <LI>HEAD
     * <LI>OPTIONS
     * <LI>PUT
     * <LI>DELETE
     * <LI>TRACE
     * </UL> are legal, subject to protocol restrictions.  The default
     * method is GET.
     *
     * @param method the HTTP method
     * @return {@code true} if set effect, otherwise {@code false}.
     * @throws ProtocolException if the method cannot be reset or if
     *                           the requested method isn't valid for HTTP.
     * @throws SecurityException if a security manager is set and the
     *                           method is "TRACE", but the "allowHttpTrace"
     *                           NetPermission is not granted.
     */
    boolean setRequestMethod(@NonNull String method) throws ProtocolException;

    /**
     * Invokes the request immediately, and blocks until the response can be processed or is in
     * error.
     */
    Connected execute() throws IOException;

    void release();

    Map<String, List<String>> getRequestProperties();

    String getRequestProperty(String key);

    interface Connected {
        int getResponseCode() throws IOException;

        String getResponseString() throws IOException;

        /**
         * Returns an unmodifiable Map of the header fields. The Map keys are Strings that represent
         * the response-header field names. Each Map value is an unmodifiable List of Strings that
         * represents the corresponding field values.
         * <p>
         * The capacity of this method is similar to the {@link URLConnection#getHeaderFields()}
         *
         * @return a Map of header fields
         */
        @Nullable
        Map<String, List<String>> getResponseHeaderFields();

        /**
         * Returns the value of the named header field, which would be the response-header field.
         * <p>
         * If called on a connection that sets the same header multiple times
         * with possibly different values, only the last value is returned.
         *
         * @param name the name of a header field.
         * @return the value of the named header field, or <code>null</code>
         * if there is no such field in the header.
         */
        @Nullable
        String getResponseHeaderField(String name);

        /**
         * Returns redirect location so that the upper layer to download target file directly and
         * there is no need to handle redirect again
         *
         * @return redirect location
         */
        String getRedirectLocation();
    }

    interface Factory {
        UploadConnection create(String url) throws IOException;
    }
}
