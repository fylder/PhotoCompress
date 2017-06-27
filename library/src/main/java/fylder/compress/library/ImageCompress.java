package fylder.compress.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by fylder on 2017/6/27.
 */
public class ImageCompress {

    public static Observable<File> compress(Context context, File oldFile) {
        return compress(context, 1080, 1080, 80, oldFile, "temp.jpg");
    }

    public static Observable<File> compress(Context context, File oldFile, String outFileName) {
        return compress(context, 1080, 1080, 80, oldFile, outFileName);
    }

    public static Observable<File> compress(Context context, int maxWidth, int maxHeight, File oldFile, String outFileName) {
        return compress(context, maxWidth, maxHeight, 80, oldFile, outFileName);
    }

    public static Observable<File> compress(final Context context, final int maxWidth, final int maxHeight, final int quality, final File oldFile, final String outFileName) {
        Observable<File> observable = Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                // 你也可以自定义压缩
                if (oldFile == null) {
                    emitter.onError(new Throwable("file null"));
                } else {
                    File newFile = new CompressHelper.Builder(context)
                            .setMaxWidth(maxWidth)      // 默认最大宽度为720
                            .setMaxHeight(maxHeight)    // 默认最大高度为960
                            .setQuality(quality)        // 默认压缩质量为80
                            .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                            .setFileName(outFileName)   // 设置你的文件名
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .build()
                            .compressToFile(oldFile);
                    emitter.onNext(newFile);
                }
            }
        });

        return observable;
    }

    public static Observable<File> compressLuban(final Context context, final File oldFile) {
        Observable<File> observable = Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(final ObservableEmitter<File> emitter) throws Exception {
                if (oldFile == null) {
                    emitter.onError(new Throwable("file null"));
                } else {
                    // 你也可以自定义压缩
                    Luban.with(context)
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
                                    emitter.onNext(file);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    // TODO 当压缩过程出现问题时调用
                                    emitter.onError(e);
                                }
                            }).launch();    //启动压缩
                }
            }
        });

        return observable;
    }

}
