package fylder.compress.demo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import fylder.compress.library.CompressHelper;
import fylder.compress.library.tools.FileUtil;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    @BindView(R.id.main_image_old)
    protected ImageView mImageOld;
    @BindView(R.id.main_text_old)
    protected TextView mTextOld;

    @BindView(R.id.main_image_new)
    protected ImageView mImageNew;
    @BindView(R.id.main_text_new)
    protected TextView mTextNew;

    @BindView(R.id.main_image_luban)
    protected ImageView mImageLuban;
    @BindView(R.id.main_text_luban)
    protected TextView mTextLuban;

    private File oldFile;
    private File newFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    public void compress(View view) {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        compressOne();
                        compressTwo();
                    } else {
                        // Oups permission denied
                    }
                });
    }

    /**
     * 限制尺寸大小
     */
    void compressOne() {
        // 默认的压缩方法，多张图片只需要直接加入循环即可
//        newFile = CompressHelper.getDefault(getApplicationContext()).compressToFile(oldFile);


        String yourFileName = "123.jpg";

        // 你也可以自定义压缩
        newFile = new CompressHelper.Builder(this)
                .setMaxWidth(1600)  // 默认最大宽度为720
                .setMaxHeight(1600) // 默认最大高度为960
                .setQuality(80)    // 默认压缩质量为80
                .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                .setFileName(yourFileName) // 设置你的文件名
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(oldFile);


        mImageNew.setImageBitmap(BitmapFactory.decodeFile(newFile.getAbsolutePath()));
        mTextNew.setText(String.format("Size : %s", getReadableFileSize(newFile.length())));
    }

    void compressTwo() {
        Luban.with(this)
                .load(oldFile)  //传人要压缩的图片
                .setCompressListener(new OnCompressListener() {
                    //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        mImageLuban.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        mTextLuban.setText(String.format("Size : %s", getReadableFileSize(file.length())));
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();    //启动压缩

    }

    public void takePhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                oldFile = FileUtil.getTempFile(this, data.getData());
                mImageOld.setImageBitmap(BitmapFactory.decodeFile(oldFile.getAbsolutePath()));
                mTextOld.setText(String.format("Size : %s", getReadableFileSize(oldFile.length())));
                clearImage();
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private int getRandomColor() {
        Random rand = new Random();
        return Color.argb(100, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }


    private void clearImage() {
        mImageOld.setBackgroundColor(getRandomColor());
        mImageNew.setImageDrawable(null);
        mImageNew.setBackgroundColor(getRandomColor());
        mTextNew.setText("Size : -");
    }


    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
