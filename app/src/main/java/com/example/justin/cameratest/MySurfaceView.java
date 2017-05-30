package com.example.justin.cameratest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by justin on 5/30/17.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    Bitmap bitmap= Bitmap.createBitmap(MainActivity.WIDTH,MainActivity.HEIGHT, Bitmap.Config.ARGB_8888);
    UpdateThread thread;
    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public void updateBitmap(Bitmap b){
        bitmap=b;
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
                    canvas.rotate(90);
                    canvas.drawBitmap(bitmap, 0,-720, paint);
                    holder.unlockCanvasAndPost(canvas);
                }catch(Exception e) {//kill all exceptions xd
                    e.printStackTrace();
                }
            }
        }
    }

}
