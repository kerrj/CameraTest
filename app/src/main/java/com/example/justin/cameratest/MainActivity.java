package com.example.justin.cameratest;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.qualcomm.ftcrobotcontroller.ScriptC_colorsplit;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity
{
    static  RenderScript mRS;
    public static int WIDTH=1280,HEIGHT=720;

    public static RenderScript getmRS() {
        return mRS;
    }

    public static Allocation getAllocationIn() {
        return allocationIn;
    }

    public static Allocation getAllocationOut() {
        return allocationOut;
    }

    public static MainActivity getActivity() {
        return activity;
    }

    static Allocation allocationIn,allocationOut;
    static MainActivity activity;
    MySurfaceView surfaceView;
    Sandbox s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView=(MySurfaceView)findViewById(R.id.surfaceView);
        activity=this;
        mRS=RenderScript.create(this);
        allocationIn=Allocation.createTyped(mRS, Type.createXY(mRS, Element.RGBA_8888(mRS), WIDTH,HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT|Allocation.USAGE_IO_INPUT);
        allocationOut=Allocation.createTyped(mRS, Type.createXY(mRS, Element.RGBA_8888(mRS), WIDTH, HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT);
        s=new Sandbox(surfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        s.pause();
    }


}
