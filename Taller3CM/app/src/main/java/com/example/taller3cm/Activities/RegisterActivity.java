package com.example.taller3cm.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taller3cm.Other.PermissionsManager;
import com.example.taller3cm.R;
import com.example.taller3cm.Other.Usuario;
import com.example.taller3cm.Other.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    public final String TAG = "Taller Autentiación";
    public final String IMAGE = "images/";
    public static final String USERS = "users/";
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    Button btnGaleria, btnCamara, btnRegister;
    ImageView imgFoto;
    EditText edtEmail, edtPassword, edtNombre, edtApellido, edtDocumento, edtLatitud, edtLongitud;
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USERS);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnGaleria = findViewById(R.id.btnGallery);
        btnCamara = findViewById(R.id.btnCamera);
        btnRegister = findViewById(R.id.btnRegistrar);

        imgFoto = findViewById(R.id.imgPhoto);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPass);
        edtNombre = findViewById(R.id.edtName);
        edtApellido = findViewById(R.id.edtLastName);
        edtDocumento = findViewById(R.id.edtDocumento);
        //edtLatitud = findViewById(R.id.edtLatitud);
        //edtLongitud = findViewById(R.id.edtLongitud);



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
                }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signUp() {



        String email = this.edtEmail.getText().toString();
        String password = this.edtPassword.getText().toString();

        boolean validEmail = Utils.validateEmail(email);
        boolean validPass = Utils.validatePassword(password);


        if (!validEmail) {
            if (email.isEmpty()) {
                edtEmail.setError("Requerido");
            } else {
                edtEmail.setError("E-mail no válido");
            }
        } else edtEmail.setError(null);
        if (!validPass) {
            if (password.isEmpty()) {
                edtPassword.setError("Requerido");
            }
            else{
                edtPassword.setError(("Error"));
            }

        }


        if (validEmail && validPass) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser mUser) {
        if (mUser != null) {
            String nombre = this.edtNombre.getText().toString();
            String apellido = this.edtApellido.getText().toString();
            String documento = this.edtDocumento.getText().toString();
            String latitud = this.edtLatitud.getText().toString();
            String longitud = this.edtLongitud.getText().toString();
            boolean disponible = false;
            boolean validFields = true;

            if(edtNombre.getText().toString().isEmpty()) {
                validFields = false;
                edtNombre.setError("Requerido");
            }
            if(edtApellido.getText().toString().isEmpty()) {
                validFields = false;
                edtApellido.setError("Requerido");
            }
            if(edtEmail.getText().toString().isEmpty()) {
                validFields = false;
                edtEmail.setError("Requerido");
            }
            if(edtPassword.getText().toString().isEmpty()) {
                validFields = false;
                edtPassword.setError("Requerido");
            }
            if(edtDocumento.getText().toString().isEmpty()) {
                validFields = false;
                edtDocumento.setError("Requerido");
            }
            if(edtLatitud.getText().toString().isEmpty()) {
                validFields = false;
                edtLatitud.setError("Requerido");
            }
            if(edtLongitud.getText().toString().isEmpty()) {
                validFields = false;
                edtLongitud.setError("Requerido");
            }


            if(validFields){
                Usuario user = new Usuario(nombre, apellido, documento, longitud, latitud, disponible);
                myRef = database.getReference(USERS + mUser.getUid());
                myRef.setValue(user);
                StorageReference myRefStorage = mStorageRef.child(IMAGE + mUser.getUid() + "/ profile.png");
                myRefStorage.putBytes(convertirImagen()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Intent intent = new Intent(getBaseContext(), MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

            }

        } /* else {
            Toast toast = Toast. makeText(getApplicationContext(),"No se pudo registrar",Toast. LENGTH_SHORT);
            toast. setMargin(50,50);
            toast. show();
        } */
    }

    private byte[] convertirImagen(){
        ByteArrayOutputStream arregloByte = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,arregloByte);
        return arregloByte.toByteArray();
    }

    private void addPhoto()
    {
        PermissionsManager.requestPermission((Activity)this, Manifest.permission.READ_EXTERNAL_STORAGE,
                "Para poder mostrar fotos que ya tenga guardadas", PermissionsManager.READ_STORAGE_PERMISSION);
        if(ContextCompat.checkSelfPermission((Activity)this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, PermissionsManager.IMAGE_PICKER_REQUEST);
        }
   }

    private void takePicture()
    {
        PermissionsManager.requestPermission((Activity) this, Manifest.permission.CAMERA,
                "Para poder mostrar fotos tomadas desde su cámara", PermissionsManager.CAMERA_PERMISSION );
        if (ContextCompat.checkSelfPermission(getBaseContext() , Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, PermissionsManager.REQUEST_IMAGE_CAPTURE);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionsManager.READ_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhoto();
                } else {
                    Toast toast = Toast. makeText(getApplicationContext(),"permiso denegado",Toast. LENGTH_SHORT);
                    toast. setMargin(50,50);
                    toast. show();
                }
                return;
            }
            case PermissionsManager.CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    takePicture();
                }
                else
                {

                    Toast toast = Toast. makeText(getApplicationContext(),"permiso denegado",Toast. LENGTH_SHORT);
                    toast. setMargin(50,50);
                    toast. show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PermissionsManager.REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image = imageBitmap;
                    imgFoto.setImageBitmap(imageBitmap);
                    imgFoto.requestLayout();
                    break;
                case PermissionsManager.IMAGE_PICKER_REQUEST:
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = this.getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        image = selectedImage;
                        imgFoto.setImageBitmap(selectedImage);
                        imgFoto.setScaleType(ImageView.ScaleType.FIT_XY);
                        imgFoto.requestLayout();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

}