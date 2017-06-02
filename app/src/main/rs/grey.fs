#pragma version(1)
#pragma rs java_package_name(com.justin.opencvcamera)
#pragma RS_FP_IMPRECISE


uchar __attribute__ ((kernel)) split(uchar4 in, uint32_t x, uint32_t y){
    uchar pixelOut=(in.r+in.b+in.g)/3;
    return pixelOut;
}