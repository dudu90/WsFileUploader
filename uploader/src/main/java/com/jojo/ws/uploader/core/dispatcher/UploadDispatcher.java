package com.jojo.ws.uploader.core.dispatcher;


import androidx.annotation.Nullable;

import com.jojo.ws.uploader.UploadCall;
import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.end.EndCause;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Policy on when async requests are executed.
 *
 * <p>Each dispatcher uses an {@link ExecutorService} to run calls internally. If you supply your
 * own executor, it should be able to run {@linkplain #getMaxRequests the configured maximum} number
 * of calls concurrently.
 */
public final class UploadDispatcher {
    private int maxRequests = 5;
    private @Nullable
    Runnable idleCallback;

    /**
     * Executes calls. Created lazily.
     */
    private @Nullable
    ExecutorService executorService;

    /**
     * Ready async calls in the order they'll be run.
     */
    private final Deque<UploadCall.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    /**
     * Running asynchronous calls. Includes canceled calls that haven't finished yet.
     */
    private final Deque<UploadCall.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    /**
     * Running synchronous calls. Includes canceled calls that haven't finished yet.
     */
    private final Deque<UploadCall> runningSyncCalls = new ArrayDeque<>();

    public UploadDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public UploadDispatcher() {
    }

    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }

    /**
     * Set the maximum number of requests to execute concurrently. Above this requests queue in
     * memory, waiting for the running calls to complete.
     *
     * <p>If more than {@code maxRequests} requests are in flight when this is invoked, those requests
     * will remain in flight.
     */
    public synchronized void setMaxRequests(int maxRequests) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }
        this.maxRequests = maxRequests;
        promoteCalls();
    }

    public synchronized int getMaxRequests() {
        return maxRequests;
    }


    public synchronized void setIdleCallback(@Nullable Runnable idleCallback) {
        this.idleCallback = idleCallback;
    }

    public synchronized void enqueue(UploadCall.AsyncCall call) {
        for (UploadCall.AsyncCall asyncCall : readyAsyncCalls) {
            if (asyncCall.get().uploadTask().getId() == call.get().uploadTask().getId()) {
                WsFileUploader.with().handlerDispatcher().postMain(() -> call.get().uploaderCallback().onEnd(call.get().uploadTask(), EndCause.RUNNING, null));
                return;
            }
        }
        for (UploadCall.AsyncCall asyncCall : runningAsyncCalls) {
            if (asyncCall.get().uploadTask().getId() == call.get().uploadTask().getId()) {
                WsFileUploader.with().handlerDispatcher().postMain(() -> call.get().uploaderCallback().onEnd(call.get().uploadTask(), EndCause.RUNNING, null));
                return;
            }
        }

        if (runningAsyncCalls.size() < maxRequests) {
            runningAsyncCalls.add(call);
            executorService().execute(call);
        } else {
            readyAsyncCalls.add(call);
        }
    }


    public synchronized void cancel(UploadTask uploadTask) {
        cancel(uploadTask.getId());
    }

    public synchronized void cancel(int id) {
        Iterator<UploadCall.AsyncCall> readyCalls = readyAsyncCalls.iterator();
        while (readyCalls.hasNext()) {
            UploadCall.AsyncCall asyncCall = readyCalls.next();
            if (asyncCall.get().uploadTask().getId() == id) {
                readyCalls.remove();
            }
        }
        Iterator<UploadCall.AsyncCall> runningCallsIterator = runningAsyncCalls.iterator();
        while (runningCallsIterator.hasNext()) {
            UploadCall.AsyncCall asyncCall = runningCallsIterator.next();
            if (asyncCall.get().uploadTask().getId() == id) {
                asyncCall.get().cancel();
                runningCallsIterator.remove();
            }
        }
        promoteCalls();
    }


    public synchronized void cancelAll() {
        for (UploadCall.AsyncCall call : readyAsyncCalls) {
            call.get().cancel();
        }

        for (UploadCall.AsyncCall call : runningAsyncCalls) {
            call.get().cancel();
        }

        for (UploadCall call : runningSyncCalls) {
            call.cancel();
        }
    }

    private void promoteCalls() {
        if (runningAsyncCalls.size() >= maxRequests) return; // Already running max capacity.
        if (readyAsyncCalls.isEmpty()) return; // No ready calls to promote.

        for (Iterator<UploadCall.AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            UploadCall.AsyncCall call = i.next();
            i.remove();
            runningAsyncCalls.add(call);
            executorService().execute(call);
            if (runningAsyncCalls.size() >= maxRequests) return; // Reached max capacity.
        }
    }

    /**
     * Used by {@code Call#execute} to signal it is in-flight.
     */
    synchronized void executed(UploadCall call) {
        runningSyncCalls.add(call);
    }

    /**
     * Used by {@code AsyncCall#run} to signal completion.
     */
    void finished(UploadCall.AsyncCall call) {
        finished(runningAsyncCalls, call, true);
    }

    /**
     * Used by {@code Call#execute} to signal completion.
     */
    void finished(UploadCall call) {
        finished(runningSyncCalls, call, false);
    }

    private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
        int runningCallsCount;
        Runnable idleCallback;
        synchronized (this) {
            if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
            if (promoteCalls) promoteCalls();
            runningCallsCount = runningCallsCount();
            idleCallback = this.idleCallback;
        }

        if (runningCallsCount == 0 && idleCallback != null) {
            idleCallback.run();
        }
    }

    /**
     * Returns a snapshot of the calls currently awaiting execution.
     */
    public synchronized List<UploadCall> queuedCalls() {
        List<UploadCall> result = new ArrayList<>();
        for (UploadCall.AsyncCall asyncCall : readyAsyncCalls) {
            result.add(asyncCall.get());
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a snapshot of the calls currently being executed.
     */
    public synchronized List<UploadCall> runningCalls() {
        List<UploadCall> result = new ArrayList<>();
        result.addAll(runningSyncCalls);
        for (UploadCall.AsyncCall asyncCall : runningAsyncCalls) {
            result.add(asyncCall.get());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized int queuedCallsCount() {
        return readyAsyncCalls.size();
    }

    public synchronized int runningCallsCount() {
        return runningAsyncCalls.size() + runningSyncCalls.size();
    }
}
