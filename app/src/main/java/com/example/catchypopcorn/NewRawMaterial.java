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

public class NewRawMaterial extends AppCompatActivity {

    Button scan , newMaterial;
    EditText quantityEditText;
    TextView material;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_rawmaterial);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRawMaterial.this, ManagerDashboard.class);
                startActivity(intent);
                finish();
            }
        });

        scan = findViewById(R.id.newMaterialScan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(NewRawMaterial.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan QR Code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });
        newMaterial = findViewById(R.id.newMaterialBtn);
        quantityEditText = findViewById(R.id.quantityEditText);
        material = findViewById(R.id.newMaterialText);

        newMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String materialName = material.getText().toString();
                String quantityString = quantityEditText.getText().toString();

                if (materialName.isEmpty() || quantityString.isEmpty()) {
                    Toast.makeText(NewRawMaterial.this, "Please enter material name and quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                double quantity;
                try {
                    quantity = Double.parseDouble(quantityString);
                } catch (NumberFormatException e) {
                    Toast.makeText(NewRawMaterial.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                String field = "gram"; // Default field for weight-based materials
                if (materialName.equals("Oil")) {
                    field = "millilitre"; // Use "L" for liquid materials like oil
                }

                // Create a new product object
                Map<String, Object> productData = new HashMap<>();
                productData.put("materialName", materialName);
                productData.put(field, quantity);

                db.collection("rawMaterial")
                        .add(productData)
                        .addOnSuccessListener(documentReference -> {
                            // Product added successfully
                            Toast.makeText(NewRawMaterial.this, "Raw Material added successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewRawMaterial.this, Inventory.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Error occurred while adding the product
                            Toast.makeText(NewRawMaterial.this, "Error adding raw material: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                material.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}