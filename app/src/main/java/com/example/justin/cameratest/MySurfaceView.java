package com.example.justin.cameratest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by justin on 5/30/17.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    Bitmap bitmap= Bitmap.createBitmap(MainActivity.WIDTH,MainActivity.HEIGHT, Bitmap.Config.ARGB_8888);
    Point[] points;
    UpdateThread thread;

    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }


    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);

    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getHolder().addCallback(this);

    }

    //this is called when the application has opened, and the surface has been created
    //so we start all our graphics tools at this stage
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread=new UpdateThread(holder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //this method runs when the surface is destroyed, so we kill the graphics thread
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.kill();
    }


    //these methods are for passing graphics information from Sandbox to be drawn here
    public void updateBitmap(Bitmap b){
        bitmap=b;
    }
    public void updatePoints(Point[] p){
        points=p;
    }

    //this is the thread which handles drawing bitmaps and graphics
    public class UpdateThread extends Thread{
        boolean running=true;
        SurfaceHolder holder;
        Canvas canvas;
        public UpdateThread(SurfaceHolder holder){
            this.holder=holder;
        }
        public void kill(){
            running=false;
        }

        public void run(){
            while(running){
                try {
                    //the canvas object is what we draw on to display on the screen
                    canvas = holder.lockCanvas();
                    Paint paint = new Paint();
                    paint.setStrokeWidth(50);
                    paint.setColor(Color.GREEN);
                    //the input image is rotated 90 deg by default, so rotate back
                    canvas.rotate(90);
                    //draw the latest bitmap
                    canvas.drawBitmap(bitmap, 0,-720, paint);
                    //draw the list of points
                    if(points!=null){
                        for(Point p:points) {
                            canvas.drawPoint(p.x, p.y-720, paint);
                        }
                    }
                    //add any additional draw methdos here, and add -720 to the y coord because of the rotation
                    holder.unlockCanvasAndPost(canvas);
                }catch(Exception e) {//kill all exceptions xd
                    e.printStackTrace();
                }
            }
        }
    }

}
