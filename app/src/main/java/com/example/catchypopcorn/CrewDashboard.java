package com.example.catchypopcorn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CrewDashboard extends AppCompatActivity {

    CardView stockProduct, scanQR, calculateOrder ,viewInventory, supplierList;
    TextView crewName;
    ImageView back;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.crewdashboard);

        back = findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(CrewDashboard.this, CrewLogin.class);
                startActivity(intent);
                finish();
            }
        });

        stockProduct = findViewById(R.id.stockProductCard);
        scanQR = findViewById(R.id.scanqrCard);
        calculateOrder = findViewById(R.id.orderCard);
        viewInventory = findViewById(R.id.viewinventoryCard);
        supplierList = findViewById(R.id.supplierListCard);
        crewName = findViewById(R.id.crewName);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null){
            // Retrieve and set the display name
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                crewName.setText(displayName);
            } else {
                crewName.setText("No display name set");
            }
        } else {
            // If no user is logged in, redirect to login screen or show a message
            Toast.makeText(CrewDashboard.this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
        }


        stockProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrewDashboard.this, StockProduct.class));
            }
        });

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrewDashboard.this, CrewScanQR.class));
            }
        });

        calculateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrewDashboard.this, CalculateOrder.class));
            }
        });

        viewInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrewDashboard.this, Inventory.class));
            }
        });

        supplierList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrewDashboard.this, Supplier.class));
            }
        });

    }
}