package com.example.catchypopcorn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Supplier extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Map<String, Object>> supplierList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FloatingActionButton fab;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supplier);

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
                        Intent intent = new Intent(Supplier.this, CrewDashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(Supplier.this, ManagerDashboard.class);
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

        fab = findViewById(R.id.addSupplier);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Supplier.this, AddSupplier.class);
                startActivity(intent);
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Set layout manager for RecyclerView

        supplierList = new ArrayList<>();  // Initialize supplier list

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get supplier data from Firestore
        fetchSupplierData();
    }

    private void fetchSupplierData() {
        db.collection("suppliers")  // Replace "suppliers" with your collection name
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Map<String, Object> supplier = document.getData();
                                supplierList.add(supplier);
                            }
                            // Set adapter for RecyclerView after fetching data
                            recyclerView.setAdapter(new SupplierAdapter(supplierList));
                        } else {
                            Log.d("TAG", "No suppliers found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error fetching suppliers", e);
                    }
                });
    }
}
