package com.example.taller3cm.Other;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.taller3cm.Activities.UserMapActivity;
import com.example.taller3cm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class NotificationService extends JobIntentService {
    private static final int JOB_ID = 12;
    public static final String USERS = "users/";
    public static final String CHANNEL_ID = "M&L App";
    int notificationId = 0;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Usuario currentUser;
    ArrayList<Usuario> usersSnapshot = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        loadCurrentUser(user);
        Log.i("ENTRE","Al servicio");
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotificationService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        myRef = database.getReference(USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot single: dataSnapshot.getChildren()){
                    Usuario usuario = single.getValue(Usuario.class);
                    usuario.setId(single.getKey());
                    boolean changeAux = validateChanges(usuario);
                    if(mAuth.getCurrentUser() != null
                            && changeAux
                            && !usuario.getId().equals(currentUser.getId())){
                        Log.i("MI ID", "Mi id es " + currentUser.getId() );
                        buildAndShowNotification("Usuario Disponible!", usuario.getNombre().concat(" ").concat(usuario.getApellido())
                                + " ahora está DISPONIBLE.", usuario.getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("DB Realtime", "Error");
            }
        });
    }

    private void buildAndShowNotification(String title, String message, String userKey){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(this, UserMapActivity.class);
        intent.putExtra("idSeguir", userKey);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public void loadCurrentUser(FirebaseUser user){
        if(user != null){
            myRef = database.getReference(USERS + user.getUid());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(Usuario.class);
                    currentUser.setId(dataSnapshot.getKey());
                    makeSnapshot();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i("DB Realtime", "Error");
                }
            });
        }
    }

    public void makeSnapshot(){
        myRef = database.getReference(USERS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot single: dataSnapshot.getChildren()){
                    Usuario usuario = single.getValue(Usuario.class);
                    usuario.setId(single.getKey());
                    usersSnapshot.add(usuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("DB Realtime", "Error");
            }
        });
    }

    private boolean validateChanges(Usuario userChanged){
        boolean validChange = false;
        int position = -1;
        for(int i = 0; i < usersSnapshot.size(); i++){
            if(usersSnapshot.get(i).getId().equals(userChanged.getId())){
                if(!usersSnapshot.get(i).isDisponible() && userChanged.isDisponible()){
                    validChange = true;
                    position = i;
                    break;
                }
            }
        }
        if (position != -1){
            usersSnapshot.remove(position);
            usersSnapshot.add(userChanged);
        }
        return validChange;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
