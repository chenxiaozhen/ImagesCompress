package com.cxz.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.cxz.images.compress2.BitmapCompressHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by chenxz on 2017/8/16.
 */

public class ImageApi {

    /**
     * 旋转
     *
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     */
    public static void transformed(String srcPath, String destPath){
        if (TextUtils.isEmpty(srcPath))
            return;
        destPath = setDefined(srcPath,destPath);
        try {
            ExifInterface srcExif = new ExifInterface(srcPath);
            int angle = 90;
            int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
            }
            // TODO 存在内存溢出问题，待解决
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
            Bitmap bmp = ImageUtil.rotateBitmap(bitmap, angle);
            ImageUtil.saveBitmapToFile(bmp, destPath);
            if (!bitmap.isRecycled()){
                Log.e("TAg","--------1");
                bitmap.recycle();
            }
            if (!bmp.isRecycled()){
                Log.e("TAg","--------2");
                bmp.recycle();
            }
        } catch (IOException e) {
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
            BitmapCompressHelper.doCompress(srcPath,destFile);
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
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
        Bitmap bmp = ImageUtil.scaleBitmap(bitmap,newWidth,newHeight);
        ImageUtil.saveBitmapToFile(bmp,destPath);
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
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
        Bitmap bmp = ImageUtil.scaleBitmap(bitmap,ratio);
        ImageUtil.saveBitmapToFile(bmp,destPath);
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
