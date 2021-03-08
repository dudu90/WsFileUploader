package com.jojo.ws.uploader.core.interceptors;

import android.util.Log;

import androidx.annotation.NonNull;

import com.jojo.ws.uploader.BlockTask;
import com.jojo.ws.uploader.Interceptor;
import com.jojo.ws.uploader.core.breakstore.Block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockUploadInterceptor implements Interceptor {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                final Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }


    @Override
    public void intercept(Chain chain) throws IOException {
        final List<Block> blocks = chain.task().getBreakInfo().getBlockList();
        List<Future<Block>> futures = new ArrayList<>();
        int index = 0;
        for (Block block : blocks) {
            futures.add(submit(new BlockTask(block.getIndex(), chain, block)));
        }
        List<Block> resultBlock = new ArrayList<>();
        for (Future<Block> blockFuture : futures) {
            try {
                Block bl = blockFuture.get();
                if (bl != null) {
                    resultBlock.add(bl);
                }
            } catch (InterruptedException | ExecutionException e) {
            }
        }
        chain.task().getBreakInfo().setBlocks(resultBlock);
        chain.proceed();
    }

    private Future<Block> submit(BlockTask blockTask) {
        return EXECUTOR.submit(blockTask);
    }

}
