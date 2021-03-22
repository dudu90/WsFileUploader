package com.jojo.ws.uploader.core.interceptors;

import com.jojo.ws.uploader.Interceptor;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;
import com.jojo.ws.uploader.core.end.EndCause;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class BlockBuildInterceptor implements Interceptor {
    @Override
    public void intercept(Chain chain) throws IOException {
        chain.task().setUploadBatch(UUID.randomUUID().toString());
        WsFileUploader.with().handlerDispatcher().postMain(() -> chain.call().uploaderCallback().onStart(chain.task()));
        final File file = chain.task().getUploadFile();
        if (file.length() == 0) {
            WsFileUploader.with().handlerDispatcher().postMain(() -> chain.call().uploaderCallback().onEnd(chain.task(), EndCause.FILE_NULL, null));
            return;
        }
        final BreakInfo breakInfo;
        if (chain.task().getBreakInfo() == null) {
            breakInfo = WsFileUploader.with().breakStore().createAndInsert(chain.task());
        } else {
            breakInfo = chain.task().getBreakInfo();
        }
        chain.task().setBreakInfo(breakInfo);
        chain.call().setRandomAccessFile(new RandomAccessFile(file, "r"));
        if (chain.task().getBreakInfo().getBlockList().size() == 0) {
            final Block[] blocks = Block.blocks(file);
            chain.task().setBlocks(blocks);
            WsFileUploader.with().breakStore().update(breakInfo);
        }
        chain.proceed();
    }
}
