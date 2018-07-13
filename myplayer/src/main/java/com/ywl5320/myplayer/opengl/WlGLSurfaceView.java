package com.ywl5320.myplayer.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

public class WlGLSurfaceView extends GLSurfaceView{

    private WlRender wlRender;

    public WlGLSurfaceView(Context context) {
        this(context, null);
    }

    public WlGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        wlRender = new WlRender(context);
        wlRender.fps_start();
        setRenderer(wlRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        wlRender.setOnRenderListener(new WlRender.OnRenderListener() {
            @Override
            public void onRender() {
                requestRender();
            }
        });
    }

    public void setYUVData(int width, int height, byte[] y, byte[] u, byte[] v)
    {
        if(wlRender != null)
        {
            wlRender.setYUVRenderData(width, height, y, u, v);
            requestRender();
        }
    }

    public WlRender getWlRender() {
        return wlRender;
    }
}
