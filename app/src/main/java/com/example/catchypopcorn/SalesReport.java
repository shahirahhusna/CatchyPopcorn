package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SalesReport extends AppCompatActivity {

    private EditText week1Sales, week2Sales, week3Sales, week4Sales;
    private Button submitWeek1SalesButton, submitWeek2SalesButton, submitWeek3SalesButton, submitWeek4SalesButton, viewReportButton;
    private TextView reportText;
    private Spinner updateMonthSpinner, viewMonthSpinner;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_report);

        week1Sales = findViewById(R.id.week1_sales);
        week2Sales = findViewById(R.id.week2_sales);
        week3Sales = findViewById(R.id.week3_sales);
        week4Sales = findViewById(R.id.week4_sales);
        submitWeek1SalesButton = findViewById(R.id.submit_week1_sales_button);
        submitWeek2SalesButton = findViewById(R.id.submit_week2_sales_button);
        submitWeek3SalesButton = findViewById(R.id.submit_week3_sales_button);
        submitWeek4SalesButton = findViewById(R.id.submit_week4_sales_button);
        viewReportButton = findViewById(R.id.view_report_button);
        reportText = findViewById(R.id.report_text);
        updateMonthSpinner = findViewById(R.id.update_month_spinner);
        viewMonthSpinner = findViewById(R.id.view_month_spinner);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();

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
                        Intent intent = new Intent(SalesReport.this, CrewDashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SalesReport.this, ManagerDashboard.class);
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

        setupSpinners();

        submitWeek1SalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSales("week1", week1Sales, getSelectedMonth(updateMonthSpinner));
            }
        });

        submitWeek2SalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSales("week2", week2Sales, getSelectedMonth(updateMonthSpinner));
            }
        });

        submitWeek3SalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSales("week3", week3Sales, getSelectedMonth(updateMonthSpinner));
            }
        });

        submitWeek4SalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSales("week4", week4Sales, getSelectedMonth(updateMonthSpinner));
            }
        });

        viewReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMonthlyReport(getSelectedMonth(viewMonthSpinner));
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateMonthSpinner.setAdapter(adapter);
        viewMonthSpinner.setAdapter(adapter);

        // Set the current month as the default selected month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        updateMonthSpinner.setSelection(currentMonth);
        viewMonthSpinner.setSelection(currentMonth);
    }

    private String getSelectedMonth(Spinner monthSpinner) {
        return monthSpinner.getSelectedItem().toString();
    }

    private void submitSales(final String week, EditText salesEditText, final String month) {
        String sales = salesEditText.getText().toString().trim();
        if (TextUtils.isEmpty(sales)) {
            salesEditText.setError("Enter sales amount");
            return;
        }

        final int salesAmount = Integer.parseInt(sales);
        final DocumentReference docRef = firestore.collection("sales").document(currentUser)
                .collection(month).document("weekly");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, update it
                        docRef.update(week, salesAmount).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SalesReport.this, "Sales updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SalesReport.this, "Error updating sales: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Document does not exist, create it
                        Map<String, Object> data = new HashMap<>();
                        data.put(week, salesAmount);
                        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SalesReport.this, "Sales added successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SalesReport.this, "Error adding sales: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(SalesReport.this, "Failed to check document existence: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void viewMonthlyReport(String month) {
        DocumentReference reportRef = firestore.collection("sales")
                .document(currentUser)
                .collection(month)
                .document("weekly");

        reportRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        StringBuilder report = new StringBuilder();
                        for (int i = 1; i <= 4; i++) {
                            String week = "week" + i;
                            if (documentSnapshot.contains(week)) {
                                report.append("Week ").append(i).append(": ").append(documentSnapshot.getLong(week)).append("\n");
                            } else {
                                report.append("Week ").append(i).append(": No data\n");
                            }
                        }
                        reportText.setText(report.toString());
                    } else {
                        reportText.setText("No sales report available for " + month);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SalesReport.this, "Failed to retrieve report: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
