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
import com.cxz.images.utils.BitmapWaterMarkUtil;
import com.cxz.images.utils.ImageInfoUtil;
import com.cxz.images.utils.ImagesUtil;
import com.cxz.images.zoom.ImagePreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity {

    private TextView fileSize;
    private TextView imageSize;
    private TextView thumbFileSize;
    private TextView thumbImageSize;
    private ImageView imageView;
    private ImageView imageView2;

    private String pathLuBan;
    private String pathPress;
    private String srcPath;

    //声明一个线程池
    private static ExecutorService mExecutor = Executors.newFixedThreadPool(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileSize = (TextView) findViewById(R.id.file_size);
        imageSize = (TextView) findViewById(R.id.image_size);
        thumbFileSize = (TextView) findViewById(R.id.thumb_file_size);
        thumbImageSize = (TextView) findViewById(R.id.thumb_image_size);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        Button btn_select = (Button) findViewById(R.id.btn_select);
        Button btn_trans = (Button) findViewById(R.id.btn_trans);
        Button btn_scaled = (Button) findViewById(R.id.btn_scaled);
        Button btn_mark = (Button) findViewById(R.id.btn_mark);
        Button btn_info = (Button) findViewById(R.id.btn_info);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(MainActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });
        btn_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transformed(srcPath);
            }
        });

        btn_scaled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaled(srcPath);
            }
        });

        btn_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageMark(srcPath);
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 获取图片信息测试代码
                ImageInfoUtil info = new ImageInfoUtil(srcPath);
                StringBuilder s = new StringBuilder();
                s.append(info.getDateTime() + ",");
                s.append(info.getOrientation() + ",");
                info.setUserComment("111111111");
                s.append(info.getUserComment());
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImagePreviewActivity.class);
                intent.putExtra("path", pathLuBan);
                startActivity(intent);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImagePreviewActivity.class);
                intent.putExtra("path", pathPress);
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
                thumbFileSize.setText("");
                thumbImageSize.setText("");

                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        compress(imgFile.getAbsolutePath());

                        compressWithLs(new File(imgFile.getAbsolutePath()));
                    }
                });
            }
        }
    }

    /**
     * 添加水印
     *
     * @param srcPath
     */
    private void imageMark(final String srcPath) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String destPath = Environment.getExternalStorageDirectory() + "/111_text_mark.jpg";
                BitmapWaterMarkUtil.setDrawTextFont("", true, true, true);
                final Bitmap bmp = BitmapWaterMarkUtil.drawTextMark(srcPath, "我是水印", 100, 100, 200, 200, "#ff0000", 50, destPath);
//                String destPath = Environment.getExternalStorageDirectory() + "/111_image_mark.jpg";
//                Bitmap bmp = BitmapWaterMarkUtil.drawImageMark(srcPath,srcPath,200,200,200,200,0.8f,destPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bmp);
                    }
                });
            }
        });
    }

    /**
     * 图片旋转
     *
     * @param path
     */
    private void transformed(final String path) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String destPath = Environment.getExternalStorageDirectory() + "/111_trans.jpg";
                ImagesUtil.transformed(path, destPath, 90);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(destPath));
                    }
                });
            }
        });
    }

    /**
     * 图片缩放
     *
     * @param path
     */
    private void scaled(final String path) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String destPath = Environment.getExternalStorageDirectory() + "/111_scaled.jpg";
                ImagesUtil.scaled(path, destPath, 0.5f);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(destPath));
                    }
                });
            }
        });
    }

    /**
     * 图片压缩
     *
     * @param path
     */
    private void compress(final String path) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String destPath = Environment.getExternalStorageDirectory() + "/111_compress.jpg";
                ImagesUtil.compress(path, destPath);
                final File desFile = new File(destPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(desFile.getAbsolutePath()));
                        thumbFileSize.append(desFile.length() / 1024 + "k");
                        thumbImageSize.append(computeSize(desFile)[0] + "*" + computeSize(desFile)[1]);
                        pathPress = desFile.getAbsolutePath();
                    }
                });
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
                        pathLuBan = file.getAbsolutePath();

                        //Glide.with(MainActivity.this).load(file).into(imageView);
                        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imageView2.setImageBitmap(bmp);

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
