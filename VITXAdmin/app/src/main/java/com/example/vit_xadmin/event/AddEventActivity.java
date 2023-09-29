package com.example.vit_xadmin.event;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vit_xadmin.LoginActivity;
import com.example.vit_xadmin.MainActivity;
import com.example.vit_xadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

public class AddEventActivity extends AppCompatActivity {

    private CardView selectImage;
    private ImageView eventImageView;
    private EditText eventDescription;
    private EditText eventLink;
    private Button addEventBtn;

    ActivityResultLauncher<String> takeImage;
    private Bitmap bitmap;

    private DatabaseReference dbReference;
    private StorageReference sReference;
    String downloadUrl="";

    private MaterialToolbar topAppBar;
    private LinearProgressIndicator lpi;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getString("isLogin", "false").equals("false")){
            openLoginActivity();
        }

        dbReference = FirebaseDatabase.getInstance().getReference();
        sReference = FirebaseStorage.getInstance().getReference();


        topAppBar = findViewById(R.id.topAppBar);
        lpi = findViewById(R.id.progressBar);
        lpi.setVisibility(View.INVISIBLE);

        setSupportActionBar(topAppBar);

        selectImage = findViewById(R.id.selectImage);
        eventImageView = findViewById(R.id.eventImageView);
        eventDescription = findViewById(R.id.eventDescription);
        eventLink = findViewById(R.id.eventLink);
        addEventBtn = findViewById(R.id.addEventBtn);

        takeImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        eventImageView.setImageBitmap(bitmap);
                    }
                }
        );

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage.launch("image/*");
            }
        });

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap==null){
                    Toast.makeText(AddEventActivity.this, "Please add Image", Toast.LENGTH_SHORT).show();
                }
                else if(eventDescription.getText().toString().isEmpty()){
                    eventDescription.setError("Description cannot be empty");
                    eventDescription.requestFocus();
                }
                else if (!eventLink.getText().toString().isEmpty() && Patterns.WEB_URL.matcher(eventLink.getText().toString()).matches()==false){
                    Toast.makeText(AddEventActivity.this, "Please add correct URL", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadImage();
                }
            }
        });

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void uploadImage() {
        lpi.setVisibility(View.VISIBLE);
        addEventBtn.setEnabled(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImage = baos.toByteArray();

        final StorageReference filePath;
        filePath = sReference.child("Events").child(UUID.randomUUID() +".jpg");

        final UploadTask uploadTask = filePath.putBytes(finalImage);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrl = String.valueOf(uri);
                        uploadData();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddEventActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadData() {
        dbReference = dbReference.child("Events");
        final String uniqueKey = dbReference.push().getKey();

        String description = eventDescription.getText().toString();
        String link = eventLink.getText().toString();

        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MM-yy");
        String date = currDate.format(calendarForDate.getTime());

        Calendar calendarForTime = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("hh:mm a");
        String time = currTime.format(calendarForTime.getTime());

        EventData eventData = new EventData(description, link, downloadUrl, date, time, uniqueKey);

        dbReference.child(uniqueKey).setValue(eventData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                lpi.setVisibility(View.INVISIBLE);
                Toast.makeText(AddEventActivity.this, "Event Added", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddEventActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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