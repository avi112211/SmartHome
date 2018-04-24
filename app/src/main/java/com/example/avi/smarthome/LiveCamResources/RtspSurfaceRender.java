package com.example.avi.smarthome.LiveCamResources;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.inuker.library.RGBProgram;
import com.inuker.library.encoder.BaseMovieEncoder;
import com.inuker.library.encoder.MovieEncoder1;
import com.inuker.library.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class RtspSurfaceRender implements GLSurfaceView.Renderer, RtspHelper.RtspCallback {

    private ByteBuffer mBuffer;
    private GLSurfaceView mGLSurfaceView;
    private RGBProgram mProgram;
    private String mRtspUrl;
    private BaseMovieEncoder mVideoEncoder;
    private Context context;

    public RtspSurfaceRender(GLSurfaceView glSurfaceView, Context context) {
        mGLSurfaceView = glSurfaceView;
        this.context = context;
    }

    public void setRtspUrl(String url) {
        mRtspUrl = url;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.v(String.format("onSurfaceChanged: width = %d, height = %d", width, height));
        mProgram = new RGBProgram(mGLSurfaceView.getContext(), width, height);
        mBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
        mVideoEncoder = new MovieEncoder1(mGLSurfaceView.getContext(), width, height);
        RtspHelper.getInstance(this.context).createPlayer(mRtspUrl, width, height, this);
    }

    public void onSurfaceDestoryed() {
        RtspHelper.getInstance(this.context).releasePlayer();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(1f, 1f, 1f, 1f);

        mProgram.useProgram();

        synchronized (mBuffer) {
            mProgram.setUniforms(mBuffer.array());
        }

        mProgram.draw();
    }

    @Override
    public void onPreviewFrame(final ByteBuffer buffer, int width, int height) {
        synchronized (mBuffer) {
            mBuffer.rewind();

            buffer.rewind();
            mBuffer.put(buffer);
        }

        mGLSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                mVideoEncoder.frameAvailable(buffer.array(), System.nanoTime());
            }
        });

        mGLSurfaceView.requestRender();
    }
}
