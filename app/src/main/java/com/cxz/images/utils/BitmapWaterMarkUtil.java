package com.cxz.images.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;

/**
 * Created by chenxz on 2017/8/17.
 */

public class BitmapWaterMarkUtil {

    private static Paint p = new Paint();

    /**
     * 设置水印文字的字体
     *
     * @param family      设置字体类型 如宋体 楷体 微软雅黑等,默认微软雅黑
     * @param isBold      是否加粗,默认false
     * @param isItalic    是否斜体,默认false
     * @param isUnderline 是否添加下划线,默认false
     */
    public static void setDrawTextFont(String family, boolean isBold, boolean isItalic, boolean isUnderline) {

        int type = Typeface.NORMAL;
        if (isItalic) {
            type = Typeface.ITALIC;
        }
        if (TextUtils.isEmpty(family)) {
            family = "微软雅黑";
        }
        Typeface font = Typeface.create(family, type);
        p.setTypeface(font);
        p.setFakeBoldText(isBold); //true为粗体，false为非粗体
        p.setUnderlineText(isUnderline); //true为下划线，false为非下划线
    }

    /**
     * 添加文字水印
     *
     * @param srcPath    源路径
     * @param text       水印文字
     * @param x          水印范围的x坐标，单位像素
     * @param y          水印范围的y坐标，单位像素
     * @param markWidth  水印的宽度
     * @param markHeight 水印的高度
     * @param color      文字颜色,格式:"#3d3d3d"
     * @param fontSize   字体大小
     * @param destPath   保存水印图片的路径
     * @return Bitmap
     */
    public static Bitmap drawTextMark(String srcPath, String text, int x, int y, int markWidth, int markHeight, String color, int fontSize, String destPath) {
        Bitmap bmp = null;
        try {
            Bitmap target = CompressUtil.adjustBitmap(srcPath);
            int w = target.getWidth();
            int h = target.getHeight();

            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);

            p.setColor(Color.parseColor(color));
            p.setTextSize(fontSize);

            p.setAntiAlias(true);// 去锯齿
            canvas.drawBitmap(target, 0, 0, p);

            canvas.drawText(text, x, y, p);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bmp != null)
            BitmapUtil.saveBitmapToFile(bmp, destPath);
        return bmp;
    }

    /**
     * 按坐标向图片文件中写入图片水印
     *
     * @param srcPath       源路径
     * @param markImagePath 水印图片路径
     * @param x             水印范围的x坐标，单位像素
     * @param y             水印范围的y坐标，单位像素
     * @param markWidth     水印的宽度
     * @param markHeight    水印的高度
     * @param transparency  水印透明度
     * @param destPath      保存水印图片的路径
     * @return Bitmap
     */
    public static Bitmap drawImageMark(String srcPath, String markImagePath, int x, int y, int markWidth, int markHeight, float transparency, String destPath) {
        Bitmap bmp = null;
        try {
            Bitmap target = CompressUtil.adjustBitmap(srcPath);
            Bitmap markBmp = CompressUtil.adjustBitmap(markImagePath);
            int w = target.getWidth();
            int h = target.getHeight();

            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);

            canvas.drawBitmap(target, 0, 0, p);

            //添加透明度
            int alpha = (int) (255 * transparency);
            p.setAlpha(alpha);

            canvas.drawBitmap(markBmp, x, y, p);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bmp != null)
            BitmapUtil.saveBitmapToFile(bmp, destPath);
        return bmp;
    }

}
