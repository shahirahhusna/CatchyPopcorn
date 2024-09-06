package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class NewProduct extends AppCompatActivity {

    Button scan, newProduct;
    EditText quantityEditText;
    TextView product;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_product);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewProduct.this, ManagerDashboard.class);
                startActivity(intent);
                finish();
            }
        });

        scan = findViewById(R.id.newProductScan);
        quantityEditText = findViewById(R.id.quantityEditText);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(NewProduct.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan QR Code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });

        product = findViewById(R.id.newProductText);
        newProduct = findViewById(R.id.newProductBtn);

        newProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = product.getText().toString();
                int quantity = Integer.parseInt(quantityEditText.getText().toString());

                // Create a new product object
                Map<String, Object> productData = new HashMap<>();
                productData.put("productName", productName);
                productData.put("quantity", quantity);

                db.collection("products")
                        .add(productData)
                        .addOnSuccessListener(documentReference -> {
                            // Product added successfully
                            Toast.makeText(NewProduct.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewProduct.this, StockProduct.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Error occurred while adding the product
                            Toast.makeText(NewProduct.this, "Error adding product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String contents = intentResult.getContents();
            if (contents != null) {
                product.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}