package com.example.catchypopcorn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ManagerName extends AppCompatActivity {

    TextInputEditText name;
    Button login;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manager_name);


        name = findViewById(R.id.managername);
        login = findViewById(R.id.loginBtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setName = name.getText().toString().trim();

                if (TextUtils.isEmpty(setName)) {
                    Toast.makeText(ManagerName.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(setName).build();

                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ManagerName.this, "Name set successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ManagerName.this, ManagerDashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ManagerName.this, "Failed to set name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }
}