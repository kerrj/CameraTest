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

    public void updateBitmap(Bitmap b){
        bitmap=b;
    }
    public void updatePoints(Point[] p){
        points=p;
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread=new UpdateThread(holder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.kill();
    }

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
                    canvas = holder.lockCanvas();
                    Paint paint = new Paint();
                    paint.setStrokeWidth(50);
                    paint.setColor(Color.GREEN);
                    canvas.rotate(90);
                    canvas.drawBitmap(bitmap, 0,-720, paint);
                    if(points!=null){
                        for(Point p:points) {
                            canvas.drawPoint(p.x, p.y-720, paint);
                        }
                    }
                    holder.unlockCanvasAndPost(canvas);
                }catch(Exception e) {//kill all exceptions xd
                    e.printStackTrace();
                }
            }
        }
    }

}
