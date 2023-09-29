package com.example.vit_xadmin.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vit_xadmin.LoginActivity;
import com.example.vit_xadmin.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteEventActivity extends AppCompatActivity {

    private RecyclerView deleteEventRecyclerView;
    private ArrayList<EventData> list;
    private EventAdapter adapter;

    private DatabaseReference dbReference;

    private MaterialToolbar topAppBar;
    private LinearProgressIndicator lpi;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_event);

        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getString("isLogin", "false").equals("false")){
            openLoginActivity();
        }

        topAppBar = findViewById(R.id.topAppBar);
        deleteEventRecyclerView = findViewById(R.id.deleteEventRecyclerView);
        lpi = findViewById(R.id.deleteEventProgressBar);

        setSupportActionBar(topAppBar);

        lpi.setVisibility(View.VISIBLE);

        dbReference = FirebaseDatabase.getInstance().getReference().child("Events");

        deleteEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deleteEventRecyclerView.setHasFixedSize(true);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getEvent();
    }

    private void getEvent() {
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    EventData data = dataSnapshot.getValue(EventData.class);
                    list.add(data);
                }
                adapter = new EventAdapter(DeleteEventActivity.this, list);
                adapter.notifyDataSetChanged();

                lpi.setVisibility(View.INVISIBLE);
                deleteEventRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lpi.setVisibility(View.INVISIBLE);
                Toast.makeText(DeleteEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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