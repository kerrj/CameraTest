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

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity
{
    //this has the same stuff as FTCRobotControllerActivity

    static  RenderScript mRS;
    static Allocation allocationIn,allocationOut;
    static MainActivity activity;
    //these variables need to be accessed from Sandbox (or the opmode in the robot app) so we supply
    //these static retrieval methods
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

    MySurfaceView surfaceView;
    Sandbox s;


    //this method is called when the app opens for the first time in its life-cycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView=(MySurfaceView)findViewById(R.id.surfaceView);
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, new LoaderCallbackInterface() {
//            @Override
//            public void onManagerConnected(int status) {
//
//            }
//
//            @Override
//            public void onPackageInstall(int operation, InstallCallbackInterface callback) {
//
//            }
//        });

        //initialize the variables we need for Sandbox
        activity=this;
        mRS=RenderScript.create(this, RenderScript.ContextType.DEBUG);
        allocationIn=Allocation.createTyped(mRS, Type.createXY(mRS, Element.RGBA_8888(mRS), WIDTH,HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT|Allocation.USAGE_IO_INPUT);
        allocationOut=Allocation.createTyped(mRS, Type.createXY(mRS, Element.RGBA_8888(mRS), WIDTH, HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT);
    }

    //this method is called when the app is reopened (without being killed)
    @Override
    protected void onResume() {
        super.onResume();
        s=new Sandbox(surfaceView);
    }

    //this method runs when the app is closed (without being killed)
    @Override
    protected void onPause() {
        super.onPause();
        s.pause();
    }


}
