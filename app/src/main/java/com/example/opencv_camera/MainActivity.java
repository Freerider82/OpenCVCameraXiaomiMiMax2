package com.example.opencv_camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    final String DIR_BAMBU_SCREEN = "/Images_Bambu";
    private static final int PERMISSION_REQUEST_CAMERA = 83854;

    private ImageView preview;

    private SeekBar seekBarWidth,seekBarHeight,seekBarSigmaX;
    private TextView textViewWidth,textViewHeight,textViewSigmaX;
    private double  width,height,sigmaX;

    private Mat hierarchy,mRgba;
    private List<MatOfPoint> contours ;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    YUVtoRGB translator = new YUVtoRGB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug();

        seekBarWidth = (SeekBar)findViewById(R.id.seekBarWidth);
        seekBarHeight = (SeekBar)findViewById(R.id.seekBarHeight);
        seekBarSigmaX = (SeekBar)findViewById(R.id.seekBarSigmaX);
        textViewWidth = findViewById(R.id.textViewWidth);
        textViewHeight = findViewById(R.id.textViewHeight);
        textViewSigmaX = findViewById(R.id.textViewSigmaX);

        preview = findViewById(R.id.preview);

//Спрашиваем пользователя на разрешение работы камеры
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            //initializeCamera();
            try {
                testPicture();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void testPicture() throws IOException {

        try {

            File sdPath = Environment.getExternalStorageDirectory();
            // добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/" + "Images_Bambu");

            File sdFile = new File(sdPath, "one_pixel.jpg");

            Bitmap bitmapImageFile = BitmapFactory.decodeFile(sdFile.getPath());


            int color = bitmapImageFile.getPixel(0,0);

            //preview.setImageBitmap(bitmapImageFile);
            //preview.buildDrawingCache();
            /*
            BitmapDrawable drawable = (BitmapDrawable) preview.getDrawable();
            Bitmap btmap = drawable.getBitmap();
            */

            Mat matTest = new Mat();

            Utils.bitmapToMat(bitmapImageFile,matTest);


            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", matTest, matOfByte);

            byte[] byteArray = matOfByte.toArray();

            Mat A = matTest.clone();
            Mat C = A.clone();
            A.convertTo(A, CvType.CV_64FC3); // New line added.
            int size = (int) (A.total() * A.channels());
            double[] temp = new double[size]; // use double[] instead of byte[]
            A.get(0, 0, temp);
       //     for (int i = 0; i < size; i++)
       //         temp[i] = (temp[i] / 2);  // no more casting required.
       //     C.put(0, 0, temp);
            int x =0;

        } catch (OutOfMemoryError e) {

        }

    }

    //Проверяем получили разрешение от пользователя на работу камеры
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           // initializeCamera();
            try {
                testPicture();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void convertToGray(Bitmap bitmap){
        int size = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < size; i++) {
            int color = pixels[i];
            int r = color >> 16 & 0xff;
            int g = color >> 8 & 0xff;
            int b = color & 0xff;
            int gray = (r + g + b) / 3;
            pixels[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());
    }

    private void initializeCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    //Preview preview = new Preview.Builder().build();

                    //ImageCapture imageCapture = new ImageCapture.Builder().build();

                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(1024, 768))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(MainActivity.this),
                            new ImageAnalysis.Analyzer() {
                                @Override
                                public void analyze(@NonNull ImageProxy image) {
                                    @SuppressLint("UnsafeOptInUsageError") Image img = image.getImage();
                                    Bitmap bitmap = translator.translateYUV(img, MainActivity.this);
                                    //convertToGray(bitmap);
                                    Mat mat = new Mat();
                                    mRgba = mat;
                                    Utils.bitmapToMat(bitmap,mat);

                                    //findContours(Mat image, List<MatOfPoint> contours, Mat hierarchy, int mode, int method, Point offset)
                                    //Imgproc.findContours();
                                    Mat mat1 = new Mat();
                                    org.opencv.core.Size s = mat.size();

                                    if(seekBarWidth.getProgress()%2 != 0){
                                        width = (double) seekBarWidth.getProgress();
                                        textViewWidth.setText(String.valueOf(width));
                                    }
                                    if(seekBarHeight.getProgress()%2 != 0){
                                        height = (double) seekBarHeight.getProgress();
                                        textViewHeight.setText(String.valueOf(height));
                                    }
                                    if(seekBarSigmaX.getProgress()%2 != 0){
                                        sigmaX = (double) seekBarSigmaX.getProgress();
                                        textViewSigmaX.setText(String.valueOf(sigmaX));
                                    }

                                    //Imgproc.GaussianBlur(mat,mat1, new org.opencv.core.Size(width, width), sigmaX);
                                    //Imgproc.cvtColor(mat1,mat,Imgproc.COLOR_RGB2GRAY);
                                    Imgproc.Canny(mat,mat1,80,100);

                                    contours = new ArrayList<MatOfPoint>();
                                    hierarchy = new Mat();

                                    Imgproc.findContours(mat1, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));
                                    hierarchy.release();

                                    for(int contourIdx = 0; contourIdx < contours.size(); contourIdx++){

                                        MatOfPoint2f approxCurve = new MatOfPoint2f();
                                        MatOfPoint2f countour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());

                                        double approxDistance = Imgproc.arcLength(countour2f,true) * 0.01;
                                        Imgproc.approxPolyDP(countour2f, approxCurve, approxDistance ,true);

                                        MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                                        Rect rect = Imgproc.boundingRect(points);

                                        double height = rect.height;
                                        double width = rect.width;

                                        if(height > 300 && width >300){
                                            Imgproc.rectangle(mRgba, new Point(rect.x ,rect.y),
                                                    new Point(rect.x + rect.width, rect.y + rect.height) , new Scalar(0,255,0,0) , 3);
                                            Imgproc.putText(mRgba, "These objects are > 300", rect.tl(), 0,2,
                                                    new Scalar (0,255,255), 4);
                                        }


                                    }

                                    Utils.matToBitmap(mRgba,bitmap);



                                    preview.setRotation(image.getImageInfo().getRotationDegrees());
                                    preview.setImageBitmap(bitmap);
                                    image.close();
                                }
                            });

                    cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, imageAnalysis);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void onClickEditActivity(View view) {


    }
}