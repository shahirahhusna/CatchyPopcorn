package com.example.catchypopcorn;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Inventory extends AppCompatActivity {

    TextView poppingOil, packet, seed, sugar, cup, bucket, poppingOilDate, seedDate, sugarDate, cupDate, bucketDate, packetDate;
    FirebaseAuth auth;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inventory);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Users").document(currentUser).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            if ("crew".equals(role)) {
                                Intent intent = new Intent(Inventory.this, CrewDashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(Inventory.this, ManagerDashboard.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "User role not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error getting document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        poppingOilDate = findViewById(R.id.poppingOilDate);
        seedDate = findViewById(R.id.seedDate);
        sugarDate = findViewById(R.id.sugarDate);
        cupDate = findViewById(R.id.cupDate);
        bucketDate = findViewById(R.id.bucketDate);
        packetDate = findViewById(R.id.packetDate);
        poppingOil = findViewById(R.id.poppingoil);
        packet = findViewById(R.id.packet);
        seed = findViewById(R.id.seeds);
        sugar = findViewById(R.id.sugar);
        cup = findViewById(R.id.cup);
        bucket = findViewById(R.id.bucket);

        showQuantity();

    }

    private void showQuantity() {
        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming the document ID is known or passed from another activity
        String poppingOilId = "cBraLJjQetevXbBlkMWO"; // Replace with the actual document ID
        String seedId ="pxa0wTJDEi9RwK8UDk2l";
        String sugarId ="7go60vLN8znWWi4U4Cug";
        String cupId ="htEYx8EAj9HfGWLj5UXk";
        String bucketId ="Tq6nC3BsppfjHV2o7Zxc";
        String packetId ="Hxrnlc1gSXlgocaabi20";

        // Get the reference to the document
        DocumentReference poppingOilDocRef = db.collection("rawMaterial").document(poppingOilId);
        DocumentReference seedDocRef = db.collection("rawMaterial").document(seedId);
        DocumentReference sugarDocRef = db.collection("rawMaterial").document(sugarId);
        DocumentReference cupDocRef = db.collection("products").document(cupId);
        DocumentReference bucketDocRef = db.collection("products").document(bucketId);
        DocumentReference packetDocRef = db.collection("products").document(packetId);


        // Retrieve the "popping oil" document
        poppingOilDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the quantity field from the "popping oil" document
                        Long quantity = document.getLong("millilitre");
                        String date = document.getString("date");

                        // Set the quantity in the TextView
                        poppingOil.setText(String.valueOf(quantity));
                        poppingOilDate.setText(String.valueOf(date));
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
                        // Retrieve the quantity field from the "caramel" document
                        Long quantity = document.getLong("quantity");
                        String date = document.getString("date");

                        // Set the quantity in the TextView
                        packet.setText(String.valueOf(quantity));
                        packetDate.setText(String.valueOf(date));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        seedDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the quantity field from the "caramel" document
                        Long quantity = document.getLong("gram");
                        String date = document.getString("date");

                        // Set the quantity in the TextView
                        seed.setText(String.valueOf(quantity));
                        seedDate.setText(String.valueOf(date));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        sugarDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the quantity field from the "caramel" document
                        Long quantity = document.getLong("gram");
                        String date = document.getString("date");

                        // Set the quantity in the TextView
                        sugar.setText(String.valueOf(quantity));
                        sugarDate.setText(String.valueOf(date));
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
                        // Retrieve the quantity field from the "caramel" document
                        Long quantity = document.getLong("quantity");
                        String date = document.getString("date");

                        // Set the quantity in the TextView
                        cup.setText(String.valueOf(quantity));
                        cupDate.setText(String.valueOf(date));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        bucketDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the quantity field from the "caramel" document
                        Long quantity = document.getLong("quantity");
                        String date = document.getString("date");

                        // Set the quantity in the TextView
                        bucket.setText(String.valueOf(quantity));
                        bucketDate.setText(String.valueOf(date));
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