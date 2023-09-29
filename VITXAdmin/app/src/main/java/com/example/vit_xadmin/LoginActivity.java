package com.example.vit_xadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText adminUsername, adminPassword;
    private Button loginBtn;
    private String username, pass;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getString("isLogin", "false").equals("true")){
            openDashboard();
        }

        adminUsername = findViewById(R.id.adminUsername);
        adminPassword = findViewById(R.id.adminPassword);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        username = adminUsername.getText().toString();
        pass = adminPassword.getText().toString();

        if(username.isEmpty()){
            adminUsername.setError("Required");
            adminUsername.requestFocus();
        } else if(pass.isEmpty()){
            adminPassword.setError("Required");
            adminPassword.requestFocus();
        } else if(username.equals("admin") && pass.equals("123456")){
            editor.putString("isLogin", "true");
            editor.commit();
            openDashboard();
        }else {
            Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDashboard() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}