package com.arsoft.agendate.views;

import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;

/**
 * Created by larcho on 11/11/16.
 */

public class ImageView extends View {

    private int downloadPercent = 0;
    private boolean downloading = false;
    private File imageFile = null;
    private Bitmap bitmap = null;
    private Paint bitmapPaint;
    private Thread downloadThread;

    public ImageView(Context context) {
        super(context);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (ImageView.this) {
            if(downloadPercent > 0) {
                //canvas.drawARGB(downloadPercent, 63, 98, 179);
            }
            if(bitmap != null) {
                canvas.drawBitmap(bitmap, 0,  0, bitmapPaint);
            }
        }
    }

    public void setImageURL(final String url)
    {
        if(downloadThread != null) {
            downloadThread.interrupt();
        }

        synchronized (ImageView.this) {
            downloadPercent = 0;
            bitmap = null;
        }
        invalidate();

        synchronized (ImageView.this) {
            bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(false);
            bitmapPaint.setFilterBitmap(false);
            bitmapPaint.setDither(false);

            File cacheDir = getContext().getCacheDir();
            imageFile = new File(cacheDir, md5(url));
        }
        final Handler handler = new Handler();

        if(imageFile.exists()) {
            synchronized (ImageView.this) {
                downloading = false;
            }
            createBitmap();
            return;
        }

        downloadThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    synchronized (ImageView.this) {
                        downloading = true;
                    }

                    URLConnection connection = new URL(url).openConnection();

                    int connectionTotalSize = connection.getContentLength();
                    InputStream reader = connection.getInputStream();

                    String randomName = md5(Long.toString(new Date().getTime() + new Random().nextLong(), 0));

                    File tempFile = new File(getContext().getCacheDir(), randomName);
                    FileOutputStream writer = new FileOutputStream(tempFile);

                    int read = 0;
                    int totalRead = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = reader.read(bytes)) != -1) {
                        if(Thread.interrupted()) {
                            synchronized (ImageView.this) {
                                writer.close();
                                reader.close();
                                tempFile.delete();
                            }
                            return;
                        }

                        synchronized (ImageView.this) {
                            writer.write(bytes, 0, read);
                            totalRead += read;
                            downloadPercent = (int)(((double)totalRead / (double)connectionTotalSize) * 255.0d);
                        }
                        postInvalidate();
                    }
                    writer.close();
                    reader.close();
                    tempFile.renameTo(imageFile);
                    synchronized (ImageView.this) {
                        downloading = false;
                    }
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            createBitmap();
                        }
                    });
                } catch(Exception ex) {
                    synchronized (ImageView.this) {
                        downloading = false;
                    }
                }
            }

        });

        downloadThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createBitmap();
    }

    private void createBitmap()
    {
        synchronized (ImageView.this) {
            if(downloading)
                return;

            if(bitmap != null)
                return;
        }

        if(imageFile == null)
            return;

        if(!imageFile.exists())
            return;

        if(this.getWidth() <= 0 || this.getHeight() <= 0)
            return;

        final float containerWidth = this.getWidth();
        final float containerHeight = this.getHeight();

        new Thread(new Runnable() {

            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

                float bitmapWidth = options.outWidth;
                float bitmapHeight = options.outHeight;

                float destWidth = 0;
                float destHeight = 0;

                int sampleSize = 0;

                if((containerWidth / containerHeight) > (bitmapWidth / bitmapHeight)) {
                    destWidth = (bitmapWidth / bitmapHeight) * containerHeight;
                    destHeight = containerHeight;
                    sampleSize = (int)Math.floor(bitmapWidth / containerWidth);
                } else {
                    destWidth = containerWidth;
                    destHeight = (bitmapHeight / bitmapWidth) * containerWidth;
                    sampleSize = (int)Math.floor(bitmapHeight / containerHeight);
                }
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;

                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int)destWidth, (int)destHeight, true);

                    synchronized (ImageView.this) {
                        ImageView.this.bitmap = bitmap;
                    }
                    postInvalidate();
                } catch (Exception ex) {}
            }
        }).start();
    }

    public static String md5(String input)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex){
            return input;
        }
    }
}
