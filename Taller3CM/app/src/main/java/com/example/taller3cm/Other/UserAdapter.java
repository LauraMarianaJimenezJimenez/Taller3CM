package com.example.taller3cm.Other;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.taller3cm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<Usuario> {

    private final Context context;
    private final ArrayList<Usuario> values;
    public final String IMAGE = "images/";
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public UserAdapter(Context context, ArrayList<Usuario> values) {
        super(context, R.layout.user_adapter, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.user_adapter, parent, false);
        TextView txtName = rowView.findViewById(R.id.txtName);
        TextView txtLastName = rowView.findViewById(R.id.txtLastName);
        ImageView imgUser = rowView.findViewById(R.id.imgUser);
        Button btnPosition = rowView.findViewById(R.id.btnUbicacion);

        txtName.setText(this.values.get(position).getNombre());
        txtLastName.setText(this.values.get(position).getApellido());
        try {
            downloadFile(this.values.get(position).getId(), imgUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Ver Ubicación", Toast.LENGTH_LONG).show();

            }
        });
        return rowView;

    }

    private void downloadFile(String id, final ImageView imgUser) throws IOException {
        final File localFile = File.createTempFile("images", "png");
        StorageReference imageRef = mStorageRef.child(IMAGE + id + "/profile.png");
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        imgUser.setImageURI(Uri.fromFile(localFile));
                        Log.i("FBApp", "succesfully downloaded");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

}