#### 照片压缩

根据限制尺寸大小压缩，并调整压缩质量


##### 限制最大尺寸

```java
 ImageCompress.compress(this, oldFile)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mImageNew.setImageBitmap(BitmapFactory.decodeFile(s.getAbsolutePath()));
                    mTextNew.setText(String.format("Size : %s", getReadableFileSize(s.length())));
                }, r -> Log.w("photo", r.getMessage()));
```

##### Luban

```java
 ImageCompress.compressLuban(this, oldFile)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mImageLuban.setImageBitmap(BitmapFactory.decodeFile(s.getAbsolutePath()));
                    mTextLuban.setText(String.format("Size : %s", getReadableFileSize(s.length())));
                }, r -> Log.w("photo", r.getMessage()));
```

#

[CompressHelper](https://github.com/nanchen2251/CompressHelper)

##### 限制最大尺寸

```java
 String yourFileName = "123.jpg";

        // 你也可以自定义压缩
        newFile = new CompressHelper.Builder(this)
                .setMaxWidth(1600)  // 默认最大宽度为720
                .setMaxHeight(1600) // 默认最大高度为960
                .setQuality(80)    // 默认压缩质量为80
                .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                .setFileName(yourFileName) // 设置你的文件名
                .setDestinationDirectoryPath(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(oldFile);
```

##### Luban

```java
Luban.with(this)
    .load(File)                     //传人要压缩的图片
    .setCompressListener(new OnCompressListener() { //设置回调
        @Override
        public void onStart() {
            // TODO 压缩开始前调用，可以在方法内启动 loading UI
        }
        @Override
        public void onSuccess(File file) {
            // TODO 压缩成功后调用，返回压缩后的图片文件
        }

        @Override
        public void onError(Throwable e) {
            // TODO 当压缩过程出现问题时调用
        }
    }).launch();    //启动压缩
```