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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CrewLogin extends AppCompatActivity {

    Button loginBtn;
    TextInputEditText loginemail, loginpassword;
    TextView manager, register;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.crewlogin);

        loginBtn = findViewById(R.id.loginBtn);
        loginemail = findViewById(R.id.crewemail);
        loginpassword = findViewById(R.id.crewpassword);
        manager = findViewById(R.id.managerPage);
        register = findViewById(R.id.crewReg);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrewLogin.this, CrewRegister.class);
                startActivity(intent);
                finish();
            }
        });
        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrewLogin.this, ManagerLogin.class);
                startActivity(intent);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginemail.getText().toString().trim();
                String password = loginpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(CrewLogin.this, "Fill Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(CrewLogin.this, "Fill Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            String role = task.getResult().getString("role");
                                            if ("crew".equals(role)) {
                                                Toast.makeText(CrewLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CrewLogin.this, CrewDashboard.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(CrewLogin.this, "Managers cannot log in through crew login", Toast.LENGTH_SHORT).show();
                                                firebaseAuth.signOut();
                                            }
                                        } else {
                                            Toast.makeText(CrewLogin.this, "User role not found", Toast.LENGTH_SHORT).show();
                                            firebaseAuth.signOut();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(CrewLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CrewLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
