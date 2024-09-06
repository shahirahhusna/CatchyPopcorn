package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddSupplier extends AppCompatActivity {

    EditText name, address, contact;
    Button submit;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_supplier);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                        Intent intent = new Intent(AddSupplier.this, CrewDashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(AddSupplier.this, ManagerDashboard.class);
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

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        contact = findViewById(R.id.contact);
        submit = findViewById(R.id.submitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierName = name.getText().toString();
                String supplierAddress = address.getText().toString();
                String supplierContact = contact.getText().toString();

                // Create a new supplier object
                Map<String, Object> supplier = new HashMap<>();
                supplier.put("name", supplierName);
                supplier.put("address", supplierAddress);
                supplier.put("contact", supplierContact);

                // Add supplier data to Firestore
                db.collection("suppliers")  // Replace "suppliers" with your desired collection name
                        .add(supplier)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "Supplier added with ID: " + documentReference.getId());
                                Intent intent = new Intent(AddSupplier.this, Supplier.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding supplier", e);
                            }
                        });
            }
        });


    }
}