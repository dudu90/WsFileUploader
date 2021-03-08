package com.jojo.wsuploader.okhttp;

import android.util.Log;

import com.jojo.ws.uploader.core.connection.UploadConnection;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class UploadOkhttpConnection implements UploadConnection, UploadConnection.Connected {
    final OkHttpClient client;
    private final Request.Builder requestBuilder;
    private Request request;
    Response response;

    UploadOkhttpConnection(OkHttpClient client, String url) {
        this(client, new Request.Builder().url(url));
    }

    UploadOkhttpConnection(OkHttpClient client, Request.Builder requestBuilder) {
        this.client = client;
        this.requestBuilder = requestBuilder;
    }

    @Override
    public void addHeader(String name, String value) {
        this.requestBuilder.addHeader(name, value);
    }

    @Override
    public Connected excuted(byte[] data) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream; charset=utf-8"), data);
        request = requestBuilder.post(body).build();
        response = client.newCall(request).execute();
        return this;
    }

    @Override
    public Connected excuted(String json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("json : application/json"), json);
        request = requestBuilder.post(body).build();
        response = client.newCall(request).execute();
        return this;
    }

    @Override
    public Connected postExcuted(String json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), json);
        requestBuilder.post(body);
        setRequestMethod("POST");
        request = requestBuilder.build();
        response = client.newCall(request).execute();
        return this;
    }

    @Override
    public Connected postExcuted(byte[] data) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), data);
        requestBuilder.post(body);
        request = requestBuilder.build();
        response = client.newCall(request).execute();
        return this;
    }

    @Override
    public Connected postText(String text) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain; charset=UTF-8"), text);
        requestBuilder.post(body);
        request = requestBuilder.build();
        response = client.newCall(request).execute();
        return this;
    }

    @Override
    public boolean setRequestMethod(String method) throws ProtocolException {
        this.requestBuilder.method(method, null);
        return true;
    }

    @Override
    public Connected execute() throws IOException {
        request = requestBuilder.build();
        response = client.newCall(request).execute();
        return this;
    }

    @Override
    public void release() {
        request = null;
        if (response != null) response.close();
        response = null;
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        if (request != null) {
            return request.headers().toMultimap();
        } else {
            return requestBuilder.build().headers().toMultimap();
        }
    }

    @Override
    public String getRequestProperty(String key) {
        if (request != null) {
            return request.header(key);
        } else {
            return requestBuilder.build().header(key);
        }
    }

    @Override
    public int getResponseCode() throws IOException {
        if (response == null) throw new IOException("Please invoke execute first!");
        return response.code();
    }

    @Override
    public String getResponseString() throws IOException {
        if (response == null) throw new IOException("Please invoke execute first!");
        final ResponseBody body = response.body();
        if (body == null) throw new IOException("no body found on response!");
        Log.d("getResponseString--->", body.toString());
        return body.string();
    }

    @Override
    public Map<String, List<String>> getResponseHeaderFields() {
        return response == null ? null : response.headers().toMultimap();
    }

    @Override
    public String getResponseHeaderField(String name) {
        return response == null ? null : response.header(name);
    }

    @Override
    public String getRedirectLocation() {
        return null;
    }

    public static class Factory implements UploadConnection.Factory {
        private OkHttpClient.Builder clientBuilder;
        private volatile OkHttpClient client;

        Factory() {
            builder();
        }

        public Factory setBuilder(OkHttpClient.Builder builder) {
            this.clientBuilder = builder;
            return this;
        }

        public OkHttpClient.Builder builder() {
            if (clientBuilder == null) {
                HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(message -> Log.d("HttpLogInfo", message));
                logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                clientBuilder = new OkHttpClient.Builder().addInterceptor(logInterceptor);
            }
            return clientBuilder;
        }

        @Override
        public UploadConnection create(String url) throws IOException {
            if (client == null) {
                synchronized (Factory.class) {
                    if (client == null) {
                        client = clientBuilder != null ? clientBuilder.build() : new OkHttpClient();
                        clientBuilder = null;
                    }
                }
            }
            return new UploadOkhttpConnection(client, url);
        }
    }
}
