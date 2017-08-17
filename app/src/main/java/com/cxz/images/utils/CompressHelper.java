package com.cxz.images.utils;
/*
 * Copyright (C) 2016 即时通讯网(52im.net) The RainbowChat Project.
 * All rights reserved.
 *
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 *
 * ClientCoreSDK.java at 2016-2-20 11:25:50, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

/**
 * 一个使用Android平台原生API实现的图片大小压缩、质量压缩的实用工具类。
 * <p>
 * 本类中的图片大小压缩(利用Android平台解析图片对象的最佳实践：即通过
 * 设置inSimpleSize值实现)、质量压缩参数阀值都是以微信为参考进行大量采
 * 样对比后得出的数值，仅供参考。
 * <p>
 * 特别说明：本类中对图片大小的压缩方法可防止普通方法读取超高分辨率相机
 * 拍出的大尺寸图片时导致APP出现OOM而崩溃。
 *
 * @author Jack Jiang (http://www.52im.net), 2013-10
 * @version 1.0
 */
public class CompressHelper
{
    private static String TAG = CompressHelper.class.getSimpleName();

    /** 压缩质量：发送前要压缩的图片质量（0~100值） */
    // 在魅族2上，微信先将图片缩小至75%后，再压缩质量（从400K左右到35K左右），经测试估计是质量75哦
    // 调整COMPRESS_QUALITY值可压缩图像大小，最大100. meizu2上100时400K左右，75时35K左右，再低则
    // 图像的大小变化不太明显了（20为13K左右）
    public static final int COMPRESS_QUALITY = 75;
    /**
     * 此项将用于计算BitmapFactory.Opts的inSimpleSize值，目的是保
     * 证加载到内存中的图片不至于过大，此值将会与requestHeight一同计算出最终的inSimpleSize
     * ，从而使得加载到内存中的Bitmap不至于过大而导致OOM.
     */
    public static final int mRequestWidth = 648;//720 * 0.9 = 648
    /**
     * 此项将用于计算BitmapFactory.Opts的inSimpleSize值，目的是保
     * 证加载到内存中的图片不至于过大，此值将会与requestWidth一同计算出最终的inSimpleSize
     * ，从而使得加载到内存中的Bitmap不至于过大而导致OOM.
     */
    public static final int mRequestHeight = 864;//960 * 0.9 = 864


    /**
     * 压缩尺寸，防止超高分辨率相机拍出的大尺寸图片导致APP出现OOM而崩溃
     *
     * @param imageFilePath
     * @return Bitmap
     */
    public static Bitmap adjustBitmap(String imageFilePath) throws Exception {
        return loadLocalBitmap(imageFilePath,
                computeSampleSize2(imageFilePath, mRequestWidth, mRequestHeight));
    }

    /**
     * 图片裁剪、压缩实现方法。
     *
     * @param imageFilePath 原始（未裁剪尺寸、未压缩质量前）图片的保存路径
     * @param savedPath 压缩处理完成后的图片将要保存的路径
     * @param savedPath 获取质量压缩后将要保存到的路径（目前的实现即是覆盖原始图片）
     * @exception Exception 处理过程中发生任何问题都将抛出异常
     */
    public static void doCompress(String imageFilePath, File savedPath) throws Exception
    {
        Bitmap decreasedBm = null;

        // 【【第一步：压缩尺寸，防止超高分辨率相机拍出的大尺寸图片导致APP出现OOM而崩溃】】
        decreasedBm = loadLocalBitmap(imageFilePath
                // 调整inSimpleSize值，确保在用户载入巨大尺寸时不致于OOM!
                , computeSampleSize2(imageFilePath, mRequestWidth, mRequestHeight));

        // 【【第二步：降低图片质量（从而减小文件大小以便节省网络传输数据量）】】

        //旋转图片
        decreasedBm = rotatingBitmap(imageFilePath,decreasedBm);
        try
        {
            if(savedPath != null)
            {
                boolean compressOk = saveBitmapToFile(
                        decreasedBm, COMPRESS_QUALITY, savedPath);
                if(compressOk)
                    Log.d(TAG, "【SendPic】质量压缩完成，压缩质量为："+COMPRESS_QUALITY+", 临时文件保存路径是："+savedPath);
                else
                    Log.w(TAG, "【SendPic】质量压缩失败！！！压缩质量为："+COMPRESS_QUALITY+", 将要保存路径是："+savedPath);
            }
            else
                Log.e(TAG, "【SendPic】质量压缩时，压缩完成后将要保存的路径居然是null ？！savedPath="+savedPath);
        }
        catch (Exception e)
        {
            Log.e(TAG, "【SendPic】降低图片质量的过程中出错了！", e);
        }
    }

    /**
     * 旋转图片，如果原图片不正，则旋转
     *
     * @param imageFilePath
     * @param bitmap
     * @return
     * @throws Exception
     */
    private static Bitmap rotatingBitmap(String imageFilePath, Bitmap bitmap) throws Exception {
        ExifInterface srcExif  = new ExifInterface(imageFilePath);
        if (srcExif == null)
            return bitmap;

        Matrix matrix = new Matrix();
        int angle = 0;
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
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 读取本地图片。
     * <p>
     * 根据Android平台解析图片对象的最佳实践：设置opts参数可解决超大图片导致内存OOM问题.
     * <b>特别说明</b>：如果设置了opts参数，则图片文件被加载到内存中时就已经是对尺寸进行裁剪后的结果了。
     *
     * @param localUrl 本地图片文件物理地址
     * @param opts null-ok; Options that control downsampling and
     * whether the image should be completely decoded, or just is size returned.
     * @return Bitmap 成功读取则返回图片对象，否则返回null
     * @exception Exception 当图片文件解码成图象对象失败时将抛出异常
     */
    private static Bitmap loadLocalBitmap(String localUrl, BitmapFactory.Options opts) throws Exception
    {
        try
        {
            return BitmapFactory.decodeFile(localUrl, opts);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * 保存（并提供压缩图像质量的能力）Bitmap到SDCard文件中.
     *
     * <p>很多情况下需要将Bitmap保存成文件，但高质量的图片文件大小会很大，
     * 本方法的目的就是在减小图片质量的情况下达到降低文件大小的目的.
     *
     * @param bp 原图
     * @param qaulity 图像质量，0~100值，据根经验：小于75后，压缩大小就不明显了
     * @param outputDestFile 压缩完成后输出的文件路径
     * @return true表示压缩成功，否则表示失败
     * @throws Exception 过程中出现任何异常都将抛出
     */
    private static boolean saveBitmapToFile(Bitmap bp, int qaulity, File outputDestFile) throws Exception
    {
        if(bp == null || outputDestFile == null)
            return false;
        try
        {
            FileOutputStream outputStream = new FileOutputStream(outputDestFile); // 文件输出流

            bp.compress(Bitmap.CompressFormat.JPEG
                    // # 据测试，微信将640*640的图片裁剪、压缩后的大小约为34K左右，经测试是质量75哦
                    // # 调整此值可压缩图像大小，经测试，再小于75后，压缩大小就不明显了
                    // # 经Jack Jiang在Galaxy sIII上测试：原拍照裁剪完成的60K左右的头像按75压缩后大小约为34K左右，
                    //   从高清图片中选取的裁剪完成时的200K左右按75压缩后大小依然约为34K左右，所以75的压缩比率在头
                    //   像质量和文件大小上应是一个较好的平衡点
                    , qaulity
                    , outputStream);// 将图片压缩到流中

            outputStream.flush(); // 输出
            outputStream.close(); // 关闭输出流
            return true;
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    //======================================================================== 计算inSampleSize【算法2】 START
    //********************************** 此算法由国人编写，简单直接，推荐使用
    //http://blog.csdn.net/soldierguard/article/details/9369461
    private static BitmapFactory.Options computeSampleSize2(String filePath,
                                                            int reqWidth, int reqHeight)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try
        {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = computeSampleSize2(opts, reqWidth, reqHeight);
        }
        catch (Exception e)
        {
            Log.e("computeSampleSize", "计算图片1的inSampleSize时出错.", e.getCause());
        }
        finally
        {
            opts.inJustDecodeBounds = false;
        }

        Log.d("computeSampleSize", ">> inSampleSize算法[2]计算完成，计算结果是【"+opts.inSampleSize+"】，reqWidth="+
                reqWidth+", reqHeight="+reqHeight+", filePath="+filePath);

        return opts;
    }
    private static int computeSampleSize2(BitmapFactory.Options options,
                                          int reqWidth, int reqHeight)
    {
        // 计算原始图像的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;

        Log.d("computeSampleSize", ">> inSampleSize算法[2]计算中，[原始options.outWidth="+options.outWidth
                +", o原始ptions.outHeight="+options.outHeight
                +"]，目标reqWidth="+reqWidth+", 目标reqHeight="+reqHeight+", options="+options);

        int inSampleSize = 1;

        //判定，当原始图像的高和宽大于所需高度和宽度时
        if (height > reqHeight || width > reqWidth)
        {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            //算出长宽比后去比例小的作为inSamplesize，保障最后imageview的dimension比request的大
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            //计算原始图片总像素
            final float totalPixels = width * height;
            // Anything more than 2x the requested pixels we'll sample down further
            //所需总像素*2,长和宽的根号2倍
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            //如果遇到很长，或者是很宽的图片时，这个算法比较有用
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
            {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
    //======================================================================== 计算inSampleSize【算法2】 END
}
