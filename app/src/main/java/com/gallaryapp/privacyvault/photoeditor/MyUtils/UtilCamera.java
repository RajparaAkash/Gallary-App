package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import kotlin.jvm.internal.DefaultConstructorMarker;

public final class UtilCamera {

    public static final Companion Companion = new Companion(null);

    public static void takeSnapShot(AppCompatActivity appCompatActivity, PreviewView previewView) {
        Companion.takeSnapShot(appCompatActivity, previewView);
    }

    public static final class Companion {
        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public void takeSnapShot(AppCompatActivity mActivity, PreviewView viewFinder) {
            startCamera(mActivity, viewFinder);
        }

        private void startCamera(final AppCompatActivity mActivity, final PreviewView viewFinder) {
            final ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(mActivity);
            cameraProviderFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        UtilCamera.Companion.start_Camera(cameraProviderFuture, mActivity, viewFinder);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, ContextCompat.getMainExecutor(mActivity));
        }


        public static void start_Camera(ListenableFuture cameraProviderFuture, AppCompatActivity mActivity, PreviewView viewFinder) throws ExecutionException, InterruptedException {
            CameraProvider v = (CameraProvider) cameraProviderFuture.get();
            ProcessCameraProvider cameraProvider = (ProcessCameraProvider) v;
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
            ImageCapture imageCapture = new ImageCapture.Builder().build();
            CameraSelector DEFAULT_FRONT_CAMERA = CameraSelector.DEFAULT_FRONT_CAMERA;
            try {
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(mActivity, DEFAULT_FRONT_CAMERA, preview, imageCapture);
                UtilCamera.Companion.takePhoto(mActivity, imageCapture);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        private void takePhoto(AppCompatActivity mActivity, ImageCapture imageCapture) {

            if (!UtilApp.secretSnapFile.exists()){
                UtilApp.secretSnapFile.mkdir();
            }
            File photoFile = new File(UtilApp.secretSnapFile, "SecretSnap_" + Calendar.getInstance().getTimeInMillis() + ".jpg");
            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(mActivity), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onError(ImageCaptureException exc) {
                }

                @Override
                public void onImageSaved(ImageCapture.OutputFileResults output) {
                    Uri savedUri = Uri.fromFile(photoFile);
                    String msg = "Photo capture succeeded: " + savedUri;
                }
            });
        }
    }
}
