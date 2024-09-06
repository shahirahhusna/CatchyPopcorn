package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class CalculateOrder extends AppCompatActivity {

    private EditText quantityEditText;
    private RadioButton bucketRadioButton, cupRadioButton, packetRadioButton;
    private TextView oilTextView, sugarTextView, seedTextView;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calculate_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Set up back button listener
        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(v -> firestore.collection("Users").document(currentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    if ("crew".equals(role)) {
                        Intent intent = new Intent(CalculateOrder.this, CrewDashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(CalculateOrder.this, ManagerDashboard.class);
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

        quantityEditText = findViewById(R.id.quantity);
        bucketRadioButton = findViewById(R.id.bucketradio);
        cupRadioButton = findViewById(R.id.cupradio);
        packetRadioButton = findViewById(R.id.packetradio);
        oilTextView = findViewById(R.id.oil);
        sugarTextView = findViewById(R.id.sugar);
        seedTextView = findViewById(R.id.seed);

        bucketRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> calculate());
        cupRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> calculate());
        packetRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> calculate());

        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculate();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void calculate() {
        if (quantityEditText.getText().toString().isEmpty()) {
            oilTextView.setText("");
            sugarTextView.setText("");
            seedTextView.setText("");
            return;
        }

        double quantity = Double.parseDouble(quantityEditText.getText().toString());
        double seed = 0, oil = 0, sugar = 0;

        if (bucketRadioButton.isChecked()) {
            seed = 5.95 * quantity;
            oil = 1.99 * quantity;
            sugar = 3.96 * quantity;
        } else if (cupRadioButton.isChecked()) {
            seed = 1.88 * quantity;
            oil = 0.63 * quantity;
            sugar = 1.25 * quantity;
        } else if (packetRadioButton.isChecked()) {
            seed = 5.64 * quantity;
            oil = 1.89 * quantity;
            sugar = 3.75 * quantity;
        }

        seedTextView.setText(String.format("%.2f grams", seed));
        oilTextView.setText(String.format("%.2f ml", oil));
        sugarTextView.setText(String.format("%.2f grams", sugar));
    }

}