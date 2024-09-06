package com.example.catchypopcorn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.collection.BuildConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GenerateQR extends AppCompatActivity {

    EditText input;
    Button generate, share;
    ImageView qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.generate_qr);

        input = findViewById(R.id.generateEdit);
        generate = findViewById(R.id.generateBtn);
        share = findViewById(R.id.shareBtn);
        qr = findViewById(R.id.qrImage);
        
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = input.getText().toString().trim();
                if(TextUtils.isEmpty(text)){
                    Toast.makeText(GenerateQR.this, "Enter text", Toast.LENGTH_SHORT).show();
                } else {
                    generateQRCode(text);
                }
            }
        });
        
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
            }
        });

    }

    private void generateQRCode(String text) {

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            qr.setImageBitmap(bitmap);
            share.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(GenerateQR.this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }

    }

    private void shareQRCode() {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) qr.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // Create the cache directory if it doesn't exist
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // Overwrite existing file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File imagePath = new File(getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, "com.example.catchypopcorn", newFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));

    }

}