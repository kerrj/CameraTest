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

public class MainActivity extends AppCompatActivity implements FTCCamera.AllocationListener
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
    private FTCCamera ftcCamera;
    private FTCVuforia ftcVuforia;
    MySurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView=(MySurfaceView)findViewById(R.id.surfaceView);
        activity=this;
        mRS=RenderScript.create(this);
        colorsplit=new ScriptC_colorsplit(mRS);
        allocationIn=Allocation.createTyped(mRS, Type.createXY(mRS, Element.RGBA_8888(mRS), WIDTH,HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT|Allocation.USAGE_IO_INPUT);
        allocationOut=Allocation.createTyped(mRS, Type.createXY(mRS, Element.RGBA_8888(mRS), WIDTH, HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT);
        ftcCamera=new FTCCamera(this);
        ftcCamera.startCamera();

//        ftcVuforia=new FTCVuforia(this);
//        ftcVuforia.addTrackables("FTC_2016-17.xml");
//        ftcVuforia.initVuforia();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ftcCamera!=null) ftcCamera.stopCamera();
        if (ftcVuforia!=null) ftcVuforia.pauseVuforia();
    }


    public Bitmap cvtAlloc2Bitmap(Allocation a){
        Bitmap b=Bitmap.createBitmap(WIDTH,HEIGHT, Bitmap.Config.ARGB_8888);
        a.copyTo(b);
        return b;
    }
    public Allocation cvtBitmap2Alloc(Bitmap b){
        Allocation a=Allocation.createFromBitmap(mRS,b);
        return a;
    }
    public Mat cvtBitmap2Mat(Bitmap b){
        Mat m=new Mat();
        Utils.bitmapToMat(b,m);
        return m;
    }
    public Bitmap cvtMat2Bitmap(Mat m){
        Bitmap b=Bitmap.createBitmap(m.width(),m.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m,b);
        return b;
    }
    public Mat cvtAlloc2Mat(Allocation a){
        Bitmap b=cvtAlloc2Bitmap(a);
        Mat m=cvtBitmap2Mat(b);
        return m;
    }
    public Allocation cvtMat2Alloc(Mat m){
        Bitmap b=cvtMat2Bitmap(m);
        Allocation a=cvtBitmap2Alloc(b);
        return a;
    }


    ScriptC_colorsplit colorsplit;
    @Override
    public void onAllocationAvailable(Allocation inAlloc, Allocation outAlloc) {
        Bitmap b=cvtAlloc2Bitmap(inAlloc);
        surfaceView.updateBitmap(b);
    }
}
