package com.jojo.ws.uploader.core.interceptors;


import com.jojo.ws.uploader.BlockTask;
import com.jojo.ws.uploader.Interceptor;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.breakstore.Block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BlockUploadInterceptor implements Interceptor {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);


    @Override
    public void intercept(Chain chain) throws IOException {
        final List<Block> blocks = chain.task().getBreakInfo().getBlockList();
        List<Future<Block>> futures = new ArrayList<>();
        int index = 0;
        for (Block block : blocks) {
            futures.add(submit(new BlockTask(block.getIndex(), chain, block)));
        }
        for (Future<Block> blockFuture : futures) {
            try {
                Block bl = blockFuture.get();
                for (int i = 0; i < chain.task().getBreakInfo().getBlockList().size(); i++) {
                    if (bl != null && bl.getIndex() == chain.task().getBreakInfo().getBlockList().get(i).getIndex()) {
                        chain.task().getBreakInfo().getBlockList().get(i).setUploadSuccess(bl.isUploadSuccess());
                    }
                }
                WsFileUploader.with().handlerDispatcher().postMain(() -> chain.call().uploaderCallback().onBlockUploaded(chain.task().getBreakInfo().getBlockList()));
            } catch (InterruptedException | ExecutionException e) {
            }
        }
        chain.proceed();
    }

    private Future<Block> submit(BlockTask blockTask) {
        return EXECUTOR.submit(blockTask);
    }

}
