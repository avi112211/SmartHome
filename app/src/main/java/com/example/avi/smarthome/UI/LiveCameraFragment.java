package com.example.avi.smarthome.UI;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avi.smarthome.LiveCamResources.RtspSurfaceRender;
import com.example.avi.smarthome.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveCameraFragment extends Fragment {

    public static final String URL = "rtsp://192.168.1.8:554/unicast";

    private GLSurfaceView mSurfaceView;
    private RtspSurfaceRender mRender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_live_camera, container, false);

        mSurfaceView = v.findViewById(R.id.surface);
        mSurfaceView.setEGLContextClientVersion(3);

        mRender = new RtspSurfaceRender(mSurfaceView, getActivity().getApplicationContext());
        mRender.setRtspUrl(URL);

        mSurfaceView.setRenderer(mRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    public void onDestroy() {
        mRender.onSurfaceDestoryed();
        super.onDestroy();
    }
}
