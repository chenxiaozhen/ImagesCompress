package com.cxz.images;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cxz.images.compress.Luban;
import com.cxz.images.compress.OnCompressListener;
import com.cxz.images.zoom.ImagePreviewActivity;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Luban";

    private TextView fileSize;
    private TextView imageSize;
    private TextView thumbFileSize;
    private TextView thumbImageSize;
    private ImageView image;
    private ImageView image2;

    private String path;
    private String path2;
    private String srcPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        fileSize = (TextView) findViewById(R.id.file_size);
        imageSize = (TextView) findViewById(R.id.image_size);
        thumbFileSize = (TextView) findViewById(R.id.thumb_file_size);
        thumbImageSize = (TextView) findViewById(R.id.thumb_image_size);
        image = (ImageView) findViewById(R.id.image);
        image2 = (ImageView) findViewById(R.id.image2);

        Button fab = (Button) findViewById(R.id.fab);

        Button fab2 = (Button) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(5)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(MainActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
//                image.setImageBitmap(rotatingImage(bitmap));
                transformed(srcPath);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ImagePreviewActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ImagePreviewActivity.class);
                intent.putExtra("path",path2);
                startActivity(intent);
            }
        });
    }

    int angle = 90;
    public Bitmap rotatingImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                angle = 90;
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                angle = 180;
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                angle = 270;
//                break;
//        }
        matrix.setRotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap != null){
            bitmap.recycle();
        }
        return bmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                File imgFile = new File(photos.get(0));
                fileSize.setText(imgFile.length() / 1024 + "k");
                imageSize.setText(computeSize(imgFile)[0] + "*" + computeSize(imgFile)[1]);

                srcPath = imgFile.getAbsolutePath();

                compress(imgFile.getAbsolutePath());



//                transformed(imgFile.getAbsolutePath());
//                scaled(imgFile.getAbsolutePath());

                for (String photo : photos) {
                    compressWithLs(new File(photo));
                }
            }
        }
    }

    private void transformed(final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ss = Environment.getExternalStorageDirectory()+"/222.jpg";
                ImageApi.transformed(path,ss);
            }
        }).start();
    }

    private void scaled(final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ss2 = Environment.getExternalStorageDirectory()+"/333.jpg";
                ImageApi.scaled(path,ss2,0.5f);
            }
        }).start();
    }

    /**
     *
     * @param path
     */
    private void compress(String path){
        final String destPath = Environment.getExternalStorageDirectory()+"/111.jpg";
        ImageApi.compress(path,destPath);
        File desFile = new File(destPath);
        image2.setImageBitmap(BitmapFactory.decodeFile(desFile.getAbsolutePath()));
        thumbFileSize.append(desFile.length() / 1024 + "k");
        thumbImageSize.append(computeSize(desFile)[0] + "*" + computeSize(desFile)[1]);
        path2 = desFile.getAbsolutePath();

//        File desFile = new File(Environment.getExternalStorageDirectory(),"111.jpg");
//        try {
//            BitmapCompressHelper.doCompress(path,desFile);
//            image2.setImageBitmap(BitmapFactory.decodeFile(desFile.getAbsolutePath()));
//            thumbFileSize.append(desFile.length() / 1024 + "k");
//            thumbImageSize.append(computeSize(desFile)[0] + "*" + computeSize(desFile)[1]);
//            path2 = desFile.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Luban
     *
     * 压缩单张图片 Listener 方式
     */
    private void compressWithLs(File file) {
        Luban.with(this)
                .load(file)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity.this, "I'm start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("path", file.getAbsolutePath());
                        path = file.getAbsolutePath();

                        //Glide.with(MainActivity.this).load(file).into(image);
                        Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
                        image.setImageBitmap(b);

                        thumbFileSize.append("\nLu:"+file.length() / 1024 + "k");
                        thumbImageSize.append("\nLu:"+computeSize(file)[0] + "*" + computeSize(file)[1]);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

    private void compressWithRx(File file) {
//        Flowable.just(file)
//                .observeOn(Schedulers.io())
//                .map(new Function<File, File>() {
//                    @Override public File apply(@NonNull File file) throws Exception {
//                        return Luban.with(MainActivity.this).load(file).get();
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<File>() {
//                    @Override public void accept(@NonNull File file) throws Exception {
//                        Log.d(TAG, file.getAbsolutePath());
//
//                        Glide.with(MainActivity.this).load(file).into(image);
//
//                        thumbFileSize.setText(file.length() / 1024 + "k");
//                        thumbImageSize.setText(computeSize(file)[0] + "*" + computeSize(file)[1]);
//                    }
//                });
    }

    private int[] computeSize(File srcImg) {
        int[] size = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(srcImg.getAbsolutePath(), options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;

        return size;
    }
}
