package com.example.catchypopcorn;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class CrewRegister extends AppCompatActivity {

    TextInputEditText editemail, editpassword;
    TextView crewlogin;
    Button registerBtn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.crew_register);

        registerBtn = findViewById(R.id.registerBtn);
        editemail = findViewById(R.id.crewemail);
        editpassword = findViewById(R.id.crewpassword);
        crewlogin = findViewById(R.id.crewlogin);
        crewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrewRegister.this, CrewLogin.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String crewemail = editemail.getText().toString().trim();
                String crewpassword = editpassword.getText().toString().trim();

                if (TextUtils.isEmpty(crewemail)) {
                    Toast.makeText(CrewRegister.this, "Fill Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(crewpassword)) {
                    Toast.makeText(CrewRegister.this, "Fill Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (crewpassword.length() < 6) {
                    Toast.makeText(CrewRegister.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(crewemail, crewpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("role", "crew");

                                    firestore.collection("Users").document(userId).set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CrewRegister.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CrewRegister.this, CrewName.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(CrewRegister.this, "Failed to store user role", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                                    handleError(errorCode);
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                }
                            }
                        });
            }
        });
    }

    private void handleError(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                Toast.makeText(CrewRegister.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(CrewRegister.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(CrewRegister.this, "Password is too weak.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(CrewRegister.this, "Signup failed.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
