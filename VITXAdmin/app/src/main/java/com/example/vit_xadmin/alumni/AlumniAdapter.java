package com.example.vit_xadmin.alumni;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vit_xadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlumniAdapter extends RecyclerView.Adapter<AlumniAdapter.AlumniViewAdapter> {

    private Context context;
    private ArrayList<AlumniData> list;

    public AlumniAdapter(Context context, ArrayList<AlumniData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AlumniViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.alumni_feed_layout, parent, false);
        return new AlumniViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumniViewAdapter holder, int position) {
        AlumniData currentItem = list.get(position);

        //set data
        holder.alumniName.setText(currentItem.getName());
        holder.alumniCollegeInfo.setText(currentItem.getProgramme());
        holder.alumniRegistrationNo.setText(currentItem.getRegistrationNo());

        try {
            Picasso.get().load(currentItem.getImageUrl()).into(holder.alumniDisplayPicture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //handle item click
        holder.alumniLinkedinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(currentItem.getLinkedinUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });

        holder.alumniEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("mailto:"+currentItem.getEmail());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                context.startActivity(intent);
            }
        });

        holder.alumniVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.alumniVerifyBtn.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to verify user?");
                builder.setCancelable(true);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                dbReference.child(currentItem.getUid()).child("isVerified").setValue("true")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Alumni Verified", Toast.LENGTH_SHORT).show();
                                                holder.alumniVerifyBtn.setEnabled(true);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                holder.alumniVerifyBtn.setEnabled(true);
                                            }
                                        });
                            }
                        }
                );
                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                holder.alumniVerifyBtn.setEnabled(true);
                            }
                        }
                );

                AlertDialog dialog = null;
                try{
                    dialog = builder.create();
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (dialog!=null)
                    dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AlumniViewAdapter extends RecyclerView.ViewHolder {

        ImageView alumniDisplayPicture, alumniLinkedinBtn, alumniEmailBtn;
        TextView alumniName, alumniCollegeInfo, alumniRegistrationNo;
        Button alumniVerifyBtn;

        public AlumniViewAdapter(@NonNull View itemView) {
            super(itemView);
            //init views
            alumniDisplayPicture = itemView.findViewById(R.id.alumniDisplayPicture);
            alumniName = itemView.findViewById(R.id.alumniName);
            alumniRegistrationNo = itemView.findViewById(R.id.alumniRegistrationNo);
            alumniCollegeInfo = itemView.findViewById(R.id.alumniCollegeInfo);
            alumniLinkedinBtn = itemView.findViewById(R.id.alumniLinkedinBtn);
            alumniEmailBtn = itemView.findViewById(R.id.alumniEmailBtn);
            alumniVerifyBtn = itemView.findViewById(R.id.alumniVerifyBtn);
        }
    }
}
