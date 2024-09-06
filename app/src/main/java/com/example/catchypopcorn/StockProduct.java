package com.example.catchypopcorn;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StockProduct extends AppCompatActivity {

    TextView bucket, cup, packet, bucketDate, cupDate, packetDate;
    ImageView bucketArrow, cupArrow, packetArrow;
    FirebaseAuth auth;
    EditText bucketEdit, cupEdit, packetEdit;
    FirebaseFirestore firestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.stockproduct);

        // Initialize views
        bucketDate = findViewById(R.id.bucketDate);
        cupDate = findViewById(R.id.cupDate);
        packetDate = findViewById(R.id.packetDate);
        bucket = findViewById(R.id.bucketView);
        cup = findViewById(R.id.cupView);
        packet = findViewById(R.id.packetView);
        bucketArrow = findViewById(R.id.bucketArrow);
        cupArrow = findViewById(R.id.cupArrow);
        packetArrow = findViewById(R.id.packetArrow);
        bucketEdit = findViewById(R.id.bucketEdit);
        cupEdit = findViewById(R.id.cupEdit);
        packetEdit = findViewById(R.id.packetEdit);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Get the current user ID
        String currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Set up back button listener
        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(v -> firestore.collection("Users").document(currentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    if ("crew".equals(role)) {
                        Intent intent = new Intent(StockProduct.this, CrewDashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(StockProduct.this, ManagerDashboard.class);
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

        // Set up click listeners for the arrows
        bucketArrow.setOnClickListener(v -> updateProductQuantity("Bucket", bucketEdit));
        cupArrow.setOnClickListener(v -> updateProductQuantity("Cup", cupEdit));
        packetArrow.setOnClickListener(v -> updateProductQuantity("Packet", packetEdit));

        // Show quantities
        showQuantity();
    }

    private void updateProductQuantity(String productName, EditText quantityEdit) {
        String quantityString = quantityEdit.getText().toString();
        if (quantityString.isEmpty()) {
            Toast.makeText(StockProduct.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityString);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to find the document with the specified product name
        db.collection("Products")
                .whereEqualTo("productName", productName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the document and update the quantity
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        DocumentReference productRef = document.getReference();

                        // Update the quantity and date fields
                        productRef.update("quantity", quantity, "date", currentDate)
                                .addOnSuccessListener(aVoid -> {
                                    // Quantity updated successfully
                                    Toast.makeText(StockProduct.this, "Quantity updated successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StockProduct.this, StockProduct.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while updating the quantity
                                    Toast.makeText(StockProduct.this, "Error updating quantity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // No document found with the specified product name
                        Toast.makeText(StockProduct.this, "Product not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while querying the product
                    Toast.makeText(StockProduct.this, "Error querying product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showQuantity() {
        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming the document ID is known or passed from another activity
        String bucketId = "at1DR4uqwjK1PDhm6UQ7"; // Replace with the actual document ID
        String cupId = "ITtY9zJ5S82lk4OpLJY5";
        String packetId = "zsYLoRrBzbEz7qflCo94";

        // Get the reference to the documents
        DocumentReference bucketDocRef = db.collection("Products").document(bucketId);
        DocumentReference cupDocRef = db.collection("Products").document(cupId);
        DocumentReference packetDocRef = db.collection("Products").document(packetId);

        bucketDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the quantity and date fields from the document
                        Long quantity = document.getLong("quantity");
                        String date = document.getString("date");

                        // Set the quantity and date in the TextView
                        bucket.setText(String.valueOf(quantity));
                        bucketDate.setText(date);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        cupDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the quantity and date fields from the document
                        Long quantity = document.getLong("quantity");
                        String date = document.getString("date");

                        // Set the quantity and date in the TextView
                        cup.setText(String.valueOf(quantity));
                        cupDate.setText(date);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        packetDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the quantity and date fields from the document
                        Long quantity = document.getLong("quantity");
                        String date = document.getString("date");

                        // Set the quantity and date in the TextView
                        packet.setText(String.valueOf(quantity));
                        packetDate.setText(date);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
