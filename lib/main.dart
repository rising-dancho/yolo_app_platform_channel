// import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:io';
import 'package:image_picker/image_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'YOLOv8 Flutter Demo',
      home: YoloHomePage(),
    );
  }
}

class YoloHomePage extends StatefulWidget {
  @override
  _YoloHomePageState createState() => _YoloHomePageState();
}

class _YoloHomePageState extends State<YoloHomePage> {
  static const platform = MethodChannel('yolo_channel');
  String _result = 'No detection yet.';
  File? _image;

  Future<void> _pickAndDetectImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);
    if (pickedFile == null) return;

    final bytes = await pickedFile.readAsBytes();
    final result = await platform.invokeMethod('detect', bytes);
    setState(() {
      _image = File(pickedFile.path);
      _result = result;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('YOLOv8 Flutter Demo'),
      ),
      body: Center(
        child: Column(
          children: [
            ElevatedButton(
              onPressed: _pickAndDetectImage,
              child: Text('Pick Image and Detect'),
            ),
            if (_image != null) Image.file(_image!),
            SizedBox(height: 20),
            Text(_result),
          ],
        ),
      ),
    );
  }
}
