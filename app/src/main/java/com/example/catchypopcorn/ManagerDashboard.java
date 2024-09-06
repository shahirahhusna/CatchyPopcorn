package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ManagerDashboard extends AppCompatActivity {

    CardView newProduct, newMaterial, stockProduct, scanQR, calculateOrder, inventory, supplier, report;
    TextView managerName;
    ImageView back;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.managerdashboard);

        back = findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(ManagerDashboard.this, ManagerLogin.class);
                startActivity(intent);
                finish();
            }
        });

        managerName = findViewById(R.id.managerName);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            // Retrieve and set the display name
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                managerName.setText(displayName);
            } else {
                managerName.setText("No display name set");
            }
        } else {
            // If no user is logged in, redirect to login screen or show a message
            Toast.makeText(ManagerDashboard.this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
        }

        newMaterial = findViewById(R.id.newmaterialCard);
        newMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, NewRawMaterial.class);
                startActivity(intent);
                finish();
            }
        });

        newProduct = findViewById(R.id.newproductCard);
        newProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, NewProduct.class);
                startActivity(intent);
                finish();
            }
        });

        stockProduct = findViewById(R.id.stockProductCard);
        stockProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, StockProduct.class);
                startActivity(intent);
                finish();
            }
        });

        scanQR = findViewById(R.id.scanqrCard);
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, ManagerScanQR.class);
                startActivity(intent);
                finish();
            }
        });

        calculateOrder = findViewById(R.id.orderCard);
        calculateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, CalculateOrder.class);
                startActivity(intent);
                finish();
            }
        });

        inventory = findViewById(R.id.inventoryCard);
        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, Inventory.class);
                startActivity(intent);
                finish();
            }
        });

        supplier = findViewById(R.id.supplierListCard);
        supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, Supplier.class);
                startActivity(intent);
                finish();
            }
        });

        report = findViewById(R.id.reportCard);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerDashboard.this, SalesReport.class);
                startActivity(intent);
                finish();
            }
        });

    }
}