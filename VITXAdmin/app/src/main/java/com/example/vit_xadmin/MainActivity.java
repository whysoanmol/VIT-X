package com.example.vit_xadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.vit_xadmin.alumni.VerifyAlumniActivity;
import com.example.vit_xadmin.event.AddEventActivity;
import com.example.vit_xadmin.event.DeleteEventActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    CardView addEvent, deleteEvent, verifyUser;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getString("isLogin", "false").equals("false")){
            openLoginActivity();
        }

        addEvent = findViewById(R.id.uploadEvent);
        deleteEvent = findViewById(R.id.deleteEvent);
        verifyUser = findViewById(R.id.verifyUser);
        topAppBar = findViewById(R.id.topAppBar);

        setSupportActionBar(topAppBar);
        
        addEvent.setOnClickListener(this);
        deleteEvent.setOnClickListener(this);
        verifyUser.setOnClickListener(this);


    }

    private void openLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.uploadEvent:
                intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivity(intent);
                break;

            case R.id.deleteEvent:
                intent = new Intent(MainActivity.this, DeleteEventActivity.class);
                startActivity(intent);
                break;

            case R.id.verifyUser:
                intent = new Intent(MainActivity.this, VerifyAlumniActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logoutBtn){
            editor.putString("isLogin", "false");
            editor.commit();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            openLoginActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}