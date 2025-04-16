package com.example.flutter_yolo_app;
import com.example.flutter_yolo_app.YoloDetector;

import android.os.Bundle;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "yolo_channel";
    private YoloDetector yoloDetector;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        yoloDetector = new YoloDetector(this);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("detect")) {
                                byte[] imageBytes = call.arguments();
                                String detectionResult = yoloDetector.detect(imageBytes);
                                result.success(detectionResult);
                            } else {
                                result.notImplemented();
                            }
                        }
                );
    }
}
