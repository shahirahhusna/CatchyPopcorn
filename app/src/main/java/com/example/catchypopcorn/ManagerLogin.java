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

public class ManagerLogin extends AppCompatActivity {

    Button loginBtn;
    TextInputEditText editemail, editpassword;
    TextView crew, register;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.managerlogin);

        loginBtn = findViewById(R.id.loginBtn);
        editemail = findViewById(R.id.manageremail);
        editpassword = findViewById(R.id.managerpassword);
        crew = findViewById(R.id.crewPage);
        register = findViewById(R.id.managerReg);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerLogin.this, ManagerRegister.class);
                startActivity(intent);
                finish();
            }
        });

        crew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerLogin.this, CrewLogin.class);
                startActivity(intent);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manageremail = editemail.getText().toString().trim();
                String managerpassword = editpassword.getText().toString().trim();

                if (TextUtils.isEmpty(manageremail)) {
                    Toast.makeText(ManagerLogin.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(managerpassword)) {
                    Toast.makeText(ManagerLogin.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(manageremail, managerpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                            if ("manager".equals(role)) {
                                                Toast.makeText(ManagerLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ManagerLogin.this, ManagerDashboard.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(ManagerLogin.this, "Crew cannot log in through manager login", Toast.LENGTH_SHORT).show();
                                                firebaseAuth.signOut();
                                            }
                                        } else {
                                            Toast.makeText(ManagerLogin.this, "User role not found", Toast.LENGTH_SHORT).show();
                                            firebaseAuth.signOut();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ManagerLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ManagerLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
