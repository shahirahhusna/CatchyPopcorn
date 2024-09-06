package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Date;
import java.text.SimpleDateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StockIn extends AppCompatActivity {

    Button scan, stockIn;
    EditText quantityEditText;
    TextView product;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.stockin);

        // Get the current user ID
        String currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Set up back button listener
        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(v -> db.collection("Users").document(currentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    if ("crew".equals(role)) {
                        Intent intent = new Intent(StockIn.this, CrewDashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(StockIn.this, ManagerDashboard.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User role not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error getting document: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        }));

        scan = findViewById(R.id.stockInScan);
        quantityEditText = findViewById(R.id.quantityEditText);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(StockIn.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan QR Code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });

        product = findViewById(R.id.stockIntext);
        stockIn = findViewById(R.id.stockInBtn);


        stockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = product.getText().toString();
                String quantityString = quantityEditText.getText().toString();

                if (productName.isEmpty() || quantityString.isEmpty()) {
                    Toast.makeText(StockIn.this, "Please enter product name and quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                double quantity;
                try {
                    quantity = Double.parseDouble(quantityString);
                } catch (NumberFormatException e) {
                    Toast.makeText(StockIn.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                String collection;
                String field;
                String nameField; // To hold the correct field for the name

                if (productName.equals("Cup") || productName.equals("Bucket") || productName.equals("Packet")) {
                    collection = "products";
                    field = "quantity";
                    nameField = "productName"; // Name field for products
                } else {
                    collection = "rawMaterial";
                    nameField = "materialName"; // Name field for raw materials
                    if (productName.equals("Popping Oil")) {
                        field = "millilitre";
                    } else if (productName.equals("Seed") || productName.equals("Sugar")) {
                        field = "gram";
                    } else {
                        field = "gram"; // Default field for other raw materials
                    }
                }

                // Log the collection, field, and nameField being used
                Log.d("StockIn", "Collection: " + collection + ", Field: " + field + ", Name Field: " + nameField);

                db.collection(collection).whereEqualTo(nameField, productName)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(StockIn.this, "Product not found", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Log the document ID being processed
                                Log.d("StockIn", "Processing document ID: " + documentSnapshot.getId());

                                double existingQuantity = documentSnapshot.getDouble(field);
                                double updatedQuantity = existingQuantity + quantity;

                                // Get the current date
                                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                                Map<String, Object> productData = new HashMap<>();
                                productData.put(field, updatedQuantity);
                                productData.put("date", currentDate); // Update the current date

                                db.collection(collection)
                                        .document(documentSnapshot.getId())
                                        .update(productData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(StockIn.this, "Product quantity updated successfully!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(StockIn.this, Inventory.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(StockIn.this, "Error updating product quantity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(StockIn.this, "Error fetching product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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