package com.example.soundwaves;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextInputEditText registerUsername, registerEmail, registerPassword, registerConfPassword;
    ConstraintLayout progressBarContainer;
    Button createAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBarContainer = findViewById(R.id.registerProgressBarContainer);
        registerUsername = findViewById(R.id.registerUsernameInput);
        registerEmail = findViewById(R.id.registerEmailInput);
        registerPassword = findViewById(R.id.registerPasswordInput);
        registerConfPassword = findViewById(R.id.registerPasswordConfirmInput);
        createAccount = findViewById(R.id.registerCreateAccountButton);

        createAccount.setOnClickListener(this::registerAccount);
    }

    public void registerAccount(View view){
        String username, email, password, confirmPassword;
        username = registerUsername.getText().toString();
        email = registerEmail.getText().toString();
        password = registerPassword.getText().toString();
        confirmPassword = registerConfPassword.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Enter your Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this,"Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        } else if (!TextUtils.equals(confirmPassword,password)) {
            Toast.makeText(this,"Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBarContainer.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("email", email);
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("documentTag", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("documentTag", "Error writing document", e);
                                        }
                                    });
                            Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                            login.putExtra("email",email);
                            startActivity(login);
                            finish();
                        } else {
                            progressBarContainer.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this,"Failed to Create Account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}