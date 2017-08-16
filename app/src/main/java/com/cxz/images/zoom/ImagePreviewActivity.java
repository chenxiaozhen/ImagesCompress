package com.cxz.images.zoom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.cxz.images.zoom.ZoomImageView;

import java.io.File;

public class ImagePreviewActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ZoomImageView(this);
        setContentView(imageView);
//        setContentView(R.layout.activity_image_preview);
//        imageView = (ImageView) findViewById(R.id.imageview);

        String path = getIntent().getStringExtra("path");
        Log.e("TAG","path---->::"+path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
    }
}
