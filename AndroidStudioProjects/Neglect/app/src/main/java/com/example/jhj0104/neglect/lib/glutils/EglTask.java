package com.example.jhj0104.neglect.lib.glutils;

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

import com.example.jhj0104.neglect.lib.glutils.EGLBase.IConfig;
import com.example.jhj0104.neglect.lib.utils.MessageTask;

public abstract class EglTask extends MessageTask {
    public static final int EGL_FLAG_DEPTH_BUFFER = 1;
    public static final int EGL_FLAG_RECORDABLE = 2;
    public static final int EGL_FLAG_STENCIL_1BIT = 4;
    public static final int EGL_FLAG_STENCIL_8BIT = 32;
    private EGLBase mEgl = null;
    private EGLBase.IEglSurface mEglHolder;

    public EglTask(EGLBase.IContext sharedContext, int flags) {
        this.init(flags, 3, sharedContext);
    }

    public EglTask(int maxClientVersion, EGLBase.IContext sharedContext, int flags) {
        this.init(flags, maxClientVersion, sharedContext);
    }

    protected void onInit(int flags, int maxClientVersion, Object sharedContext) {
        if(sharedContext == null || sharedContext instanceof EGLBase.IContext) {
            int stencilBits = (flags & 4) == 4?1:((flags & 32) == 32?8:0);
            this.mEgl = EGLBase.createFrom(maxClientVersion, (EGLBase.IContext)sharedContext, (flags & 1) == 1, stencilBits, (flags & 2) == 2);
        }

        if(this.mEgl == null) {
            this.callOnError(new RuntimeException("failed to create EglCore"));
            this.releaseSelf();
        } else {
            this.mEglHolder = this.mEgl.createOffscreen(1, 1);
            this.mEglHolder.makeCurrent();
        }

    }

    protected Request takeRequest() throws InterruptedException {
        Request result = super.takeRequest();
        this.mEglHolder.makeCurrent();
        return result;
    }

    protected void onBeforeStop() {
        this.mEglHolder.makeCurrent();
    }

    protected void onRelease() {
        this.mEglHolder.release();
        this.mEgl.release();
    }

    protected EGLBase getEgl() {
        return this.mEgl;
    }

    protected EGLBase.IContext getEGLContext() {
        return this.mEgl.getContext();
    }

    protected IConfig getConfig() {
        return this.mEgl.getConfig();
    }

    protected EGLBase.IContext getContext() {
        return this.mEgl != null?this.mEgl.getContext():null;
    }

    protected void makeCurrent() {
        this.mEglHolder.makeCurrent();
    }

    protected boolean isGLES3() {
        return this.mEgl != null && this.mEgl.getGlVersion() > 2;
    }
}
