package com.jojo.ws.uploader.core.interceptors;

import com.jojo.ws.uploader.Interceptor;
import com.jojo.ws.uploader.core.breakstore.Block;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BlockBuildInterceptor implements Interceptor {
    @Override
    public void intercept(Chain chain) throws IOException {
        chain.task().setUploadBatch(UUID.randomUUID().toString());
        final File file = chain.task().getUploadFile();
        final Block[] blocks = Block.blocks(file);
        chain.task().setBlocks(blocks);
        chain.proceed();
    }
}
