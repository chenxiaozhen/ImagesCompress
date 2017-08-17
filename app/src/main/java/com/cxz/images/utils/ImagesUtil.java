package com.cxz.images.utils;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by chenxz on 2017/8/16.
 */

public class ImagesUtil {

    /**
     * 旋转
     *
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     * @param angle 旋转的角度
     */
    public static void transformed(String srcPath, String destPath, int angle){
        if (TextUtils.isEmpty(srcPath))
            return;
        destPath = setDefined(srcPath,destPath);
        try {
            // 为了防止图片过大获取Bitmap对象导致的OOM
            Bitmap bitmap = CompressUtil.adjustBitmap(srcPath);
            // 旋转图片
            Bitmap bmp = BitmapUtil.rotateBitmap(bitmap, angle);
            // 将图片保存到目标路径
            BitmapUtil.saveBitmapToFile(bmp, destPath);
            if (!bitmap.isRecycled()){
                bitmap.recycle();
            }
            if (!bmp.isRecycled()){
                bmp.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩
     *
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     */
    public static void compress(String srcPath, String destPath){
        if (TextUtils.isEmpty(srcPath))
            return;
        destPath = setDefined(srcPath, destPath);
        File destFile = new File(destPath);
        try {
            CompressUtil.doCompress(srcPath,destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     */
    public static void scaled(String srcPath, String destPath,int newWidth,int newHeight){
        if (TextUtils.isEmpty(srcPath))
            return;
        destPath = setDefined(srcPath, destPath);
        try {
            // 为了防止图片过大获取Bitmap对象导致的OOM
            Bitmap bitmap = CompressUtil.adjustBitmap(srcPath);
            Bitmap bmp = BitmapUtil.scaleBitmap(bitmap, newWidth, newHeight);
            BitmapUtil.saveBitmapToFile(bmp, destPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     * @param ratio 比例
     */
    public static void scaled(String srcPath, String destPath,float ratio){
        if (TextUtils.isEmpty(srcPath))
            return;
        destPath = setDefined(srcPath, destPath);
        try {
            // 为了防止图片过大获取Bitmap对象导致的OOM
            Bitmap bitmap = CompressUtil.adjustBitmap(srcPath);
            Bitmap bmp = BitmapUtil.scaleBitmap(bitmap, ratio);
            BitmapUtil.saveBitmapToFile(bmp, destPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static String setDefined(String srcPath, String destPath) {
        if (TextUtils.isEmpty(destPath)){
            destPath = srcPath;
        }
        int index = destPath.lastIndexOf("/");
        if (index > 0){
            File tempFile = new File(destPath.substring(0,index));
            if (!tempFile.exists()){
                tempFile.mkdirs();
            }
        }
        return destPath;
    }

}
