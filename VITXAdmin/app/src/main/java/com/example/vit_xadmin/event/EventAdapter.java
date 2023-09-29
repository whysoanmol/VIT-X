package com.example.vit_xadmin.event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewAdapter> {

    private Context context;
    private ArrayList<EventData> list;

    public EventAdapter(Context context, ArrayList<EventData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EventViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.events_feed_layout, parent, false);
        return new EventViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewAdapter holder, int position) {
        EventData currentItem = list.get(position);

        holder.deleteEventDescription.setText(currentItem.getDescription());

        try {
            Picasso.get().load(currentItem.getImage()).into(holder.deleteEventImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.deleteEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.deleteEventBtn.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete the event?");
                builder.setCancelable(true);
                builder.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("Events");
                                StorageReference sReference = FirebaseStorage.getInstance().getReference().child("Events");
                                String image = currentItem.getImage().substring(78, 118);

                                dbReference.child(currentItem.getKey()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                try {
                                                    sReference.child(image).delete();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show();
                                                holder.deleteEventBtn.setEnabled(true);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                holder.deleteEventBtn.setEnabled(true);
                                            }
                                        });
//                                notifyItemRemoved(position);
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        }
                );
                builder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                holder.deleteEventBtn.setEnabled(true);
                            }
                        }
                );

                AlertDialog dialog = null;
                try {
                    dialog = builder.create();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(dialog!=null)
                    dialog.show();
            }
        });
        holder.eventRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getLink()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventViewAdapter extends RecyclerView.ViewHolder {

        private ImageView deleteEventImageView;
        private TextView deleteEventDescription;
        private Button deleteEventBtn, eventRegisterBtn;

        public EventViewAdapter(@NonNull View itemView) {
            super(itemView);
            deleteEventImageView = itemView.findViewById(R.id.deleteEventImageView);
            deleteEventDescription = itemView.findViewById(R.id.deleteEventDescription);
            deleteEventBtn = itemView.findViewById(R.id.deleteEventBtn);
            eventRegisterBtn = itemView.findViewById(R.id.eventRegisterBtn);
        }
    }
}
