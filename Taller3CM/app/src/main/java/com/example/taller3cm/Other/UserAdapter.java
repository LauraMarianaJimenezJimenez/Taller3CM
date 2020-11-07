package com.example.taller3cm.Other;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taller3cm.R;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<Usuario>{

    private final Context context;
    private final ArrayList<Usuario> values;

    public UserAdapter(Context context, ArrayList<Usuario> values) {
        super(context, R.layout.user_adapter,values);
        this.context = context;
        this.values = values;
    }
/*
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.user_adapter, parent, false);
        TextView txtFriendName = rowView.findViewById(R.id.txtFriendName);
        TextView txtFriendUser =  rowView.findViewById(R.id.txtFriendUser);
        ImageView imgFriend = rowView.findViewById(R.id.imgFotoAmigo);
        final ImageButton imgAdd = rowView.findViewById(R.id.imgAdd);

        txtFriendName.setText(this.values.get(position).getNombre());
        txtFriendUser.setText(this.values.get(position).getUser());
        imgFriend.setImageResource(R.drawable.ic_profilepic);
        imgAdd.setImageResource(R.drawable.ic_plus);

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Amigo agregado", Toast.LENGTH_LONG).show();
                imgAdd.setImageResource(R.drawable.ic_minus);

            }
        });
        return rowView;

    }
*/
}

