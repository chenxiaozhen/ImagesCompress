package com.cxz.images.utils;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by chenxz on 2017/8/17.
 *
 * 获取图片的信息
 */

public class ImageInfoUtil {

    private ExifInterface srcExif;

    public ImageInfoUtil(String srcPath) {
        try {
            srcExif = new ExifInterface(srcPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取创建时间
     *
     * @return
     */
    public String getDateTimeOriginal(){
        return srcExif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
    }

    /**
     * 获取修改时间
     *
     * @return
     */
    public String getDateTime(){
        return srcExif.getAttribute(ExifInterface.TAG_DATETIME);
    }

    /**
     * 获取文件大小
     *
     * @return
     */
    public String getSize(){
        return srcExif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
    }

    /**
     * 获取主图像的X方向像素
     *
     * @return
     */
    public String getPixelXDimension(){
        return srcExif.getAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION);
    }

    /**
     * 获取主图像的Y方向像素
     *
     * @return
     */
    public String getPixelYDimension(){
        return srcExif.getAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION);
    }


    /**
     * 获取图片的方向
     *
     * @return
     */
    public String getOrientation(){
        return srcExif.getAttribute(ExifInterface.TAG_ORIENTATION);
    }

    /**
     * 设置图像方向
     * @param orientation 图像方向,取值范围（1~8）
     */
    public void setOrientation(String orientation){
        try {
            srcExif.setAttribute(ExifInterface.TAG_ORIENTATION,orientation);
            srcExif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取备注
     *
     * @return
     */
    public String getUserComment(){
        return srcExif.getAttribute(ExifInterface.TAG_USER_COMMENT);
    }

    /**
     * 设置图像方向
     * @param comment
     */
    public void setUserComment(String comment){
        try {
            srcExif.setAttribute(ExifInterface.TAG_USER_COMMENT,comment);
            srcExif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取GPS纬度
     *
     * @return
     */
    public String getGpsLatitude(){
        return srcExif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
    }

    /**
     * 设置GPS纬度
     * @param latitude
     */
    public void setGpsLatitude(String latitude){
        try {
            srcExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,latitude);
            srcExif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取GPS经度
     *
     * @return
     */
    public String getGpsLongitude(){
        return srcExif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
    }

    /**
     * 设置GPS纬度
     * @param longitude
     */
    public void setGpsLongitude(String longitude){
        try {
            srcExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,longitude);
            srcExif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
