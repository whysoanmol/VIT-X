package com.example.vit_xadmin.alumni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.vit_xadmin.LoginActivity;
import com.example.vit_xadmin.R;
import com.example.vit_xadmin.event.AddEventActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VerifyAlumniActivity extends AppCompatActivity {

    private RecyclerView verifyAlumniRecyclerView;
    private LinearProgressIndicator lpi;
    private ArrayList<AlumniData> list;
    private AlumniAdapter adapter;

    private DatabaseReference dbReference;

    private MaterialToolbar topAppBar;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_alumni);

        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getString("isLogin", "false").equals("false")){
            openLoginActivity();
        }

        topAppBar = findViewById(R.id.topAppBar);
        verifyAlumniRecyclerView = findViewById(R.id.verifyAlumniRecyclerView);
        lpi = findViewById(R.id.verifyAlumniProgressBar);

        setSupportActionBar(topAppBar);

        lpi.setVisibility(View.VISIBLE);

        dbReference = FirebaseDatabase.getInstance().getReference();

        //set recycler view
        verifyAlumniRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        verifyAlumniRecyclerView.setHasFixedSize(true);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUnverifiedAlumni();
    }

    private void getUnverifiedAlumni() {
        Query query = dbReference.child("Users").orderByChild("isVerified").equalTo("false");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AlumniData data = dataSnapshot.getValue(AlumniData.class);
                    list.add(0, data);
                }

                adapter = new AlumniAdapter(VerifyAlumniActivity.this, list);
                adapter.notifyDataSetChanged();

                lpi.setVisibility(View.GONE);
                verifyAlumniRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lpi.setVisibility(View.VISIBLE);
                Toast.makeText(VerifyAlumniActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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

    private void openLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}