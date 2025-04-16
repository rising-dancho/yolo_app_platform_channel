package com.example.flutter_yolo_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;

public class YoloDetector {
    private Module module;
    final Context context;

    public YoloDetector(Context context) {
        this.context = context;
        try {
            module = Module.load(assetFilePath(context, "best.ptl"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String detect(byte[] imageBytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, false);

        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resizedBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
        );

        Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
        float[] scores = outputTensor.getDataAsFloatArray();

        // Simple thresholding for demonstration
        float threshold = 0.5f;
        int count = 0;
        for (float score : scores) {
            if (score > threshold) count++;
        }

        return "Detected " + count + " objects.";
    }

    private static String assetFilePath(Context context, String assetName) throws IOException {
        java.io.File file = new java.io.File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (java.io.InputStream is = context.getAssets().open(assetName);
            java.io.FileOutputStream os = new java.io.FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        }
        return file.getAbsolutePath();
    }
}
