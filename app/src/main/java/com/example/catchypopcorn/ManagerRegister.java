package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManagerRegister extends AppCompatActivity {

    TextInputEditText editemail, editpassword;
    TextView managerlogin;

    Button registerBtn;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manager_register);

        registerBtn = findViewById(R.id.registerBtn);
        editemail = findViewById(R.id.manageremail);
        editpassword = findViewById(R.id.managerpassword);
        managerlogin = findViewById(R.id.managerlogin);
        managerlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerRegister.this, ManagerLogin.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manageremail = editemail.getText().toString().trim();
                String managerpassword = editpassword.getText().toString().trim();

                if (TextUtils.isEmpty(manageremail)) {
                    Toast.makeText(ManagerRegister.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(managerpassword)) {
                    Toast.makeText(ManagerRegister.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (managerpassword.length() < 6) {
                    Toast.makeText(ManagerRegister.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(manageremail, managerpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("role", "manager");

                                    firestore.collection("Users").document(userId).set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ManagerRegister.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(ManagerRegister.this, ManagerName.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ManagerRegister.this, "Failed to store user role", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                                    handleError(errorCode);
                                }
                            }
                        });
            }
        });
    }

    private void handleError(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                Toast.makeText(ManagerRegister.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(ManagerRegister.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(ManagerRegister.this, "Password is too weak.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(ManagerRegister.this, "Signup failed.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
