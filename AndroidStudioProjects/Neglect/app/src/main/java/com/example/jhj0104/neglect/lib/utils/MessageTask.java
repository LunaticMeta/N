package com.example.jhj0104.neglect.lib.utils;

/**
 * Created by jhj0104 on 2017-01-06.
 */

/*
 * libcommon
 * utility/helper classes for myself
 *
 * Copyright (c) 2014-2016 saki t_saki@serenegiant.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

import android.util.Log;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class MessageTask implements Runnable {
    private static final String TAG = MessageTask.class.getSimpleName();
    protected static final int REQUEST_TASK_NON = 0;
    protected static final int REQUEST_TASK_RUN = -1;
    protected static final int REQUEST_TASK_RUN_AND_WAIT = -2;
    protected static final int REQUEST_TASK_START = -8;
    protected static final int REQUEST_TASK_QUIT = -9;
    private final Object mSync = new Object();
    private final int mMaxRequest;
    private final LinkedBlockingQueue<MessageTask.Request> mRequestPool;
    private final LinkedBlockingDeque<MessageTask.Request> mRequestQueue;
    private boolean mIsRunning;
    private boolean mFinished;
    private Thread mWorkerThread;

    public MessageTask() {
        this.mMaxRequest = -1;
        this.mRequestPool = new LinkedBlockingQueue();
        this.mRequestQueue = new LinkedBlockingDeque();
    }

    public MessageTask(int init_num) {
        this.mMaxRequest = -1;
        this.mRequestPool = new LinkedBlockingQueue();
        this.mRequestQueue = new LinkedBlockingDeque();

        for(int i = 0; i < init_num && this.mRequestPool.offer(new MessageTask.Request()); ++i) {
            ;
        }

    }

    public MessageTask(int max_request, int init_num) {
        this.mMaxRequest = max_request;
        this.mRequestPool = new LinkedBlockingQueue(max_request);
        this.mRequestQueue = new LinkedBlockingDeque(max_request);

        for(int i = 0; i < init_num && this.mRequestPool.offer(new MessageTask.Request()); ++i) {
            ;
        }

    }

    protected void init(int arg1, int arg2, Object obj) {
        this.mFinished = false;
        this.offer(-8, arg1, arg2, obj);
    }

    protected abstract void onInit(int var1, int var2, Object var3);

    protected abstract void onStart();

    protected void onBeforeStop() {
    }

    protected abstract void onStop();

    protected abstract void onRelease();

    protected boolean onError(Exception e) {
        return true;
    }

    protected abstract Object processRequest(int var1, int var2, int var3, Object var4) throws MessageTask.TaskBreak;

    protected MessageTask.Request takeRequest() throws InterruptedException {
        return (MessageTask.Request)this.mRequestQueue.take();
    }

    public boolean waitReady() {
        Object var1 = this.mSync;
        synchronized(this.mSync) {
            while(!this.mIsRunning && !this.mFinished) {
                try {
                    this.mSync.wait(500L);
                } catch (InterruptedException var4) {
                    break;
                }
            }

            return this.mIsRunning;
        }
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    public void run() {
        MessageTask.Request request = null;
        this.mIsRunning = true;

        try {
            request = (MessageTask.Request)this.mRequestQueue.take();
        } catch (InterruptedException var12) {
            this.mIsRunning = false;
            this.mFinished = true;
        }

        Object e = this.mSync;
        synchronized(this.mSync) {
            if(this.mIsRunning) {
                this.mWorkerThread = Thread.currentThread();

                try {
                    this.onInit(request.arg1, request.arg2, request.obj);
                } catch (Exception var11) {
                    Log.w(TAG, var11);
                    this.mIsRunning = false;
                    this.mFinished = true;
                }

                this.mSync.notifyAll();
            }
        }

        if(this.mIsRunning) {
            try {
                this.onStart();
            } catch (Exception var19) {
                if(this.callOnError(var19)) {
                    this.mIsRunning = false;
                    this.mFinished = true;
                }
            }
        }

        label119:
        while(this.mIsRunning) {
            try {
                request = this.takeRequest();
                switch(request.request) {
                    case -9:
                        break label119;
                    case -8:
                    case -7:
                    case -6:
                    case -5:
                    case -4:
                    case -3:
                    default:
                        try {
                            this.processRequest(request.request, request.arg1, request.arg2, request.obj);
                        } catch (MessageTask.TaskBreak var13) {
                            break label119;
                        } catch (Exception var14) {
                            if(this.callOnError(var14)) {
                                break label119;
                            }
                        }
                        break;
                    case -2:
                        try {
                            request.setResult(this.processRequest(request.request_for_result, request.arg1, request.arg2, request.obj));
                        } catch (MessageTask.TaskBreak var15) {
                            request.setResult((Object)null);
                            break label119;
                        } catch (Exception var16) {
                            request.setResult((Object)null);
                            if(this.callOnError(var16)) {
                                break label119;
                            }
                        }
                        break;
                    case -1:
                        if(request.obj instanceof Runnable) {
                            try {
                                ((Runnable)request.obj).run();
                            } catch (Exception var17) {
                                if(this.callOnError(var17)) {
                                    break label119;
                                }
                            }
                        }
                    case 0:
                }

                request.request = request.request_for_result = 0;
                this.mRequestPool.offer(request);
            } catch (InterruptedException var18) {
                break;
            }
        }

        e = this.mSync;
        synchronized(this.mSync) {
            this.mWorkerThread = null;
            this.mIsRunning = false;
            this.mFinished = true;
        }

        try {
            this.onBeforeStop();
            this.onStop();
        } catch (Exception var9) {
            this.callOnError(var9);
        }

        try {
            this.onRelease();
        } catch (Exception var8) {
            this.callOnError(var8);
        }

        e = this.mSync;
        synchronized(this.mSync) {
            this.mSync.notifyAll();
        }
    }

    protected boolean callOnError(Exception e) {
        try {
            return this.onError(e);
        } catch (Exception var3) {
            return true;
        }
    }

    protected MessageTask.Request obtain(int request, int arg1, int arg2, Object obj) {
        MessageTask.Request req = (MessageTask.Request)this.mRequestPool.poll();
        if(req != null) {
            req.request = request;
            req.arg1 = arg1;
            req.arg2 = arg2;
            req.obj = obj;
        } else {
            req = new MessageTask.Request(request, arg1, arg2, obj);
        }

        return req;
    }

    public boolean offer(int request, int arg1, int arg2, Object obj) {
        return !this.mFinished && this.mRequestQueue.offer(this.obtain(request, arg1, arg2, obj));
    }

    public boolean offer(int request, int arg1, Object obj) {
        return !this.mFinished && this.mRequestQueue.offer(this.obtain(request, arg1, 0, obj));
    }

    public boolean offer(int request, int arg1, int arg2) {
        return !this.mFinished && this.mRequestQueue.offer(this.obtain(request, arg1, arg2, (Object)null));
    }

    public boolean offer(int request, int arg1) {
        return this.mRequestQueue.offer(this.obtain(request, arg1, 0, (Object)null));
    }

    public boolean offer(int request) {
        return !this.mFinished && this.mRequestQueue.offer(this.obtain(request, 0, 0, (Object)null));
    }

    public boolean offer(int request, Object obj) {
        return !this.mFinished && this.mRequestQueue.offer(this.obtain(request, 0, 0, obj));
    }

    public boolean offerFirst(int request, int arg1, int arg2, Object obj) {
        return !this.mFinished && this.mRequestQueue.offerFirst(this.obtain(request, arg1, arg2, obj));
    }

    public Object offerAndWait(int request, int arg1, int arg2, Object obj) {
        if(!this.mFinished && request > 0) {
            MessageTask.Request req = this.obtain(-2, arg1, arg2, obj);
            synchronized(req) {
                req.request_for_result = request;
                req.result = null;
                this.mRequestQueue.offer(req);

                while(this.mIsRunning && req.request_for_result != 0) {
                    try {
                        req.wait(100L);
                    } catch (InterruptedException var9) {
                        break;
                    }
                }
            }

            return req.result;
        } else {
            return null;
        }
    }

    public boolean queueEvent(Runnable task) {
        return !this.mFinished && task != null && this.offer(-1, task);
    }

    public void removeRequest(MessageTask.Request request) {
        Iterator var2 = this.mRequestQueue.iterator();

        while(var2.hasNext()) {
            MessageTask.Request req = (MessageTask.Request)var2.next();
            if(req.equals(request)) {
                this.mRequestQueue.remove(req);
                this.mRequestPool.offer(req);
            }
        }

    }

    public void removeRequest(int request) {
        Iterator var2 = this.mRequestQueue.iterator();

        while(var2.hasNext()) {
            MessageTask.Request req = (MessageTask.Request)var2.next();
            if(req.request == request) {
                this.mRequestQueue.remove(req);
                this.mRequestPool.offer(req);
            }
        }

    }

    public void release() {
        this.release(false);
    }

    public void release(boolean interrupt) {
        this.mRequestQueue.clear();
        Object var2 = this.mSync;
        synchronized(this.mSync) {
            if(this.mIsRunning) {
                this.mIsRunning = false;
                this.mRequestQueue.offerFirst(this.obtain(-9, 0, 0, (Object)null));
                if(interrupt && this.mWorkerThread != null) {
                    this.mWorkerThread.interrupt();
                }

                try {
                    this.mSync.wait();
                } catch (InterruptedException var5) {
                    ;
                }
            }

        }
    }

    public void releaseSelf() {
        this.mRequestQueue.clear();
        Object var1 = this.mSync;
        synchronized(this.mSync) {
            if(this.mIsRunning) {
                this.mIsRunning = false;
                this.mRequestQueue.offerFirst(this.obtain(-9, 0, 0, (Object)null));
            }

        }
    }

    public void userBreak() throws MessageTask.TaskBreak {
        throw new MessageTask.TaskBreak();
    }

    protected static final class Request {
        int request;
        int arg1;
        int arg2;
        Object obj;
        int request_for_result;
        Object result;

        private Request() {
            this.request = this.request_for_result = 0;
        }

        public Request(int _request, int _arg1, int _arg2, Object _obj) {
            this.request = _request;
            this.arg1 = _arg1;
            this.arg2 = _arg2;
            this.obj = _obj;
            this.request_for_result = 0;
        }

        public void setResult(Object result) {
            synchronized(this) {
                this.result = result;
                this.request = this.request_for_result = 0;
                this.notifyAll();
            }
        }

        public boolean equals(Object o) {
            return o instanceof MessageTask.Request?this.request == ((MessageTask.Request)o).request && this.request_for_result == ((MessageTask.Request)o).request_for_result && this.arg1 == ((MessageTask.Request)o).arg1 && this.arg2 == ((MessageTask.Request)o).arg2 && this.obj == ((MessageTask.Request)o).obj:super.equals(o);
        }
    }

    public static class TaskBreak extends RuntimeException {
        public TaskBreak() {
        }
    }
}
