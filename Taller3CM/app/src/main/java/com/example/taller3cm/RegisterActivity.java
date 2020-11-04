package com.example.taller3cm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {


    Button btnGaleria, btnCamara;
    ImageView imgFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnGaleria = findViewById(R.id.btnGallery);
        btnCamara = findViewById(R.id.btnCamera);
        imgFoto = findViewById(R.id.imgPhoto);


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

    private void addPhoto()
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

    private void takePicture()
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsManager.READ_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhoto();
                } else {
                    Toast toast = Toast. makeText(getApplicationContext(),"Permiso Denegado",Toast. LENGTH_SHORT);
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
                    Toast toast = Toast. makeText(getApplicationContext(),"Permiso Denegado",Toast. LENGTH_SHORT);
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
                    imgFoto.setImageBitmap(imageBitmap);
                    imgFoto.requestLayout();
                    break;
                case PermissionsManager.IMAGE_PICKER_REQUEST:
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = this.getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
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