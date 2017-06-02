package com.example.justin.cameratest;

import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.ScriptGroup;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.util.Log;

import com.justin.opencvcamera.ScriptC_canny;
import com.justin.opencvcamera.ScriptC_grey;
import com.justin.opencvcamera.ScriptC_hysteresis;
import com.justin.opencvcamera.ScriptC_sobel;

import static com.example.justin.cameratest.MainActivity.mRS;

/**
 * Created by justin on 6/1/17.
 */

public class Canny {

    private ScriptC_canny canny;
    private ScriptC_hysteresis hysteresis;
    private ScriptIntrinsicBlur blur;
    private ScriptC_sobel sobel;
    private ScriptC_grey grey;
    private ScriptGroup scriptGroup;
    public Allocation mAllocationOut;

    public Canny(int WIDTH,int HEIGHT,int BLUR_RADIUS,short UPPER,short LOWER){
        //UPPER=35, LOWER=20,BLUR_RDIUS=3 seem to be good params
        mAllocationOut=Allocation.createTyped(mRS,Type.createXY(mRS, Element.RGBA_8888(mRS), WIDTH, HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_GRAPHICS_TEXTURE |  Allocation.USAGE_SCRIPT);
        canny=new ScriptC_canny(mRS);
        blur=ScriptIntrinsicBlur.create(mRS,Element.U8(mRS));
        hysteresis=new ScriptC_hysteresis(mRS);
        sobel=new ScriptC_sobel(mRS);
        grey=new ScriptC_grey(mRS);

        canny.set_height(HEIGHT);
        canny.set_width(WIDTH);
        canny.set_UPPER(UPPER);
        canny.set_LOWER(LOWER);
        hysteresis.set_height(HEIGHT);
        hysteresis.set_width(WIDTH);
        hysteresis.set_gradientThreshold(.0f);
        blur.setRadius(BLUR_RADIUS);
        sobel.set_width((long) WIDTH);
        sobel.set_height((long) HEIGHT);
        //setup scriptgroup flow chain
        ScriptGroup.Builder mScriptGroupBuilderCanny =new ScriptGroup.Builder(mRS);
        mScriptGroupBuilderCanny.addKernel(grey.getKernelID_split());
        mScriptGroupBuilderCanny.addKernel(blur.getKernelID());
        mScriptGroupBuilderCanny.addKernel(sobel.getKernelID_sobel());
        mScriptGroupBuilderCanny.addKernel(canny.getKernelID_suppress());
        mScriptGroupBuilderCanny.addKernel(hysteresis.getKernelID_hysteresis());
        Type u8= Type.createXY(mRS, Element.U8(mRS),WIDTH,HEIGHT);
        mScriptGroupBuilderCanny.addConnection(u8,
                grey.getKernelID_split(), blur.getFieldID_Input());
        mScriptGroupBuilderCanny.addConnection(u8,
                blur.getKernelID(), sobel.getFieldID_inAllocation());
        mScriptGroupBuilderCanny.addConnection(Type.createXY(mRS, Element.F32_2(mRS), WIDTH, HEIGHT),
                sobel.getKernelID_sobel(), canny.getFieldID_inAllocation());
        mScriptGroupBuilderCanny.addConnection(Type.createXY(mRS, Element.F32_2(mRS), WIDTH, HEIGHT),
                canny.getKernelID_suppress(), hysteresis.getFieldID_inAllocation());
        scriptGroup = mScriptGroupBuilderCanny.create();
    }

    public Allocation execute(Allocation in){
        scriptGroup.setInput(grey.getKernelID_split(), in);
        scriptGroup.setOutput(hysteresis.getKernelID_hysteresis(), mAllocationOut);
        scriptGroup.execute();
        return mAllocationOut;
    }
}
