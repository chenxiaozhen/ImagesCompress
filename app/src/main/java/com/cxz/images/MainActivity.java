package com.cxz.images;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.cxz.images.utils.BitmapWaterMarkHelper;
import com.cxz.images.utils.ImageUtil;
import com.cxz.images.zoom.ImagePreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    //声明一个线程池
    private static ExecutorService mExecutor = Executors.newFixedThreadPool(10);

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
        Button fab3 = (Button) findViewById(R.id.fab3);

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
                transformed(srcPath);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scaled(srcPath);

                // TODO 添加文字、图片水印测试代码
                String destPath = Environment.getExternalStorageDirectory() + "/444.jpg";
                BitmapWaterMarkHelper.setDrawTextFont("",true,true,true);
                Bitmap bmp = BitmapWaterMarkHelper.drawTextMark(srcPath,"我是水印",100,100,200,200,"#ff0000",50,destPath);
//                Bitmap bmp = BitmapWaterMarkHelper.drawImageMark(srcPath,srcPath,200,200,200,200,0.9f,destPath);
                image.setImageBitmap(bmp);

                // TODO 获取图片信息测试代码
//                ImageInfoHelper info = new ImageInfoHelper(srcPath);
//                StringBuilder s = new StringBuilder();
//                s.append(info.getDateTime()+",");
//                s.append(info.getOrientation()+",");
//                info.setUserComment("111111111");
//                s.append(info.getUserComment()+",");
//                Log.e("TAG","cxz----------------->"+s);

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImagePreviewActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImagePreviewActivity.class);
                intent.putExtra("path", path2);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                final File imgFile = new File(photos.get(0));
                fileSize.setText(imgFile.length() / 1024 + "k");
                imageSize.setText(computeSize(imgFile)[0] + "*" + computeSize(imgFile)[1]);

                srcPath = imgFile.getAbsolutePath();

                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        compress(imgFile.getAbsolutePath());

//                for (String photo : photos) {
//                    compressWithLs(new File(photo));
//                }
                    }
                });
            }
        }
    }

    private void transformed(final String path) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String ss = Environment.getExternalStorageDirectory() + "/222.jpg";
                ImageUtil.transformed(path, ss);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        image.setImageBitmap(BitmapFactory.decodeFile(ss));
                    }
                });
            }
        });

    }

    private void scaled(final String path) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String ss2 = Environment.getExternalStorageDirectory() + "/333.jpg";
                ImageUtil.scaled(path, ss2, 0.5f);
            }
        });
    }

    /**
     * @param path
     */
    private void compress(String path) {
        final String destPath = Environment.getExternalStorageDirectory() + "/111.jpg";
        ImageUtil.compress(path, destPath);
        final File desFile = new File(destPath);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image2.setImageBitmap(BitmapFactory.decodeFile(desFile.getAbsolutePath()));
                thumbFileSize.append(desFile.length() / 1024 + "k");
                thumbImageSize.append(computeSize(desFile)[0] + "*" + computeSize(desFile)[1]);
                path2 = desFile.getAbsolutePath();
            }
        });
    }

    /**
     * Luban
     * <p>
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

                        thumbFileSize.append("\nLu:" + file.length() / 1024 + "k");
                        thumbImageSize.append("\nLu:" + computeSize(file)[0] + "*" + computeSize(file)[1]);
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
