package com.example.justin.cameratest;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.util.Log;

import com.qualcomm.ftcrobotcontroller.ScriptC_colorsplit;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.justin.cameratest.MainActivity.HEIGHT;
import static com.example.justin.cameratest.MainActivity.WIDTH;

/**
 * Created by justin on 5/31/17.
 */

public class Sandbox implements FTCCamera.AllocationListener {
    /*Imagine this class is your opmode, I've separated it from MainActivity just for illustrating how the allocations are created
    * in FTCRobotControllerActivity, not in your opmode. */
    FTCCamera camera;
    FTCVuforia vuforia;
    MySurfaceView surfaceView;
    VuforiaThread thread;
    Canny canny=new Canny(WIDTH,HEIGHT,3,(short)35,(short)20);


    //This code would run in init() during an opmode
    public Sandbox(MySurfaceView s){
        surfaceView=s;

//        camera=new FTCCamera(this);
//        camera.startCamera();

        thread=new VuforiaThread();
        vuforia=new FTCVuforia(MainActivity.getActivity());
        vuforia.addTrackables("FTC_2016-17.xml");
        vuforia.initVuforia();
        thread.start();
    }


    //this code runs as soon as the camera stream is available after you call camera.startCamera()
    @Override
    public void onAllocationAvailable(Allocation inAlloc, Allocation outAlloc) {
        //use inAlloc as the camera frame, use outAlloc as the target for any Renderscript processing
        //do some processing with RS/OpenCV then pass the final bitmap for display
        Bitmap b=cvtAlloc2Bitmap(inAlloc);
        surfaceView.updateBitmap(b);
    }


    //some convenient conversion functions
    public Bitmap cvtAlloc2Bitmap(Allocation a){
        Bitmap b=Bitmap.createBitmap(WIDTH,HEIGHT, Bitmap.Config.ARGB_8888);
        a.copyTo(b);
        return b;
    }
    public Allocation cvtBitmap2Alloc(Bitmap b){
        Allocation a=Allocation.createFromBitmap(MainActivity.getmRS(),b);
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

    //let's say we want to grab an image while Vuforia is operating, in which case you can't use FTCCamera
    public void processVuforiaFrame(){
        Bitmap frame=vuforia.getLastBitmap();
        //do some processing
        surfaceView.updateBitmap(frame);
    }


    //This is a thread which mimics what an opmode run() would do for using vuforia
    private class VuforiaThread extends Thread{
        boolean running=true;
        public void kill(){
            running=false;
        }

        public void run(){
            while(running){
                try {
                    processVuforiaFrame();
                    //draw a dot in the center of the target, whose coordinates come from FTCVuforia.Vuforia_onUpdate
                    if(vuforia.getVuforiaData().containsKey("Wheels")) {
                        //retrieve the data for the wheels image target
                        double[] data = vuforia.getVuforiaData().get("Wheels");

                        //make a Point object from the returned data
                        android.graphics.Point middle = new android.graphics.Point((int) data[7], (int) data[8]);
                        android.graphics.Point[] points = new android.graphics.Point[]{middle};

                        //pass the point to the surfaceview to draw
                        surfaceView.updatePoints(points);
                    }
                    //sleeping prevents the thread from idling and wasting processing
                    Thread.sleep(1);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    //kill running camera streams when the app closes
    //this would run during opmode stop()
    public void pause()  {
        if(thread!=null)thread.kill();
        if(camera!=null) camera.stopCamera();
        if (vuforia!=null) try {
            vuforia.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
