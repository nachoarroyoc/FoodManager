package com.example.tfg_shg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import id.zelory.compressor.Compressor;

public class anadirSupermercadoActivity extends AppCompatActivity {

    Bitmap thumb_bitmap=null;
    ProgressDialog cargando;
    StorageReference storageReference;
    private DatabaseReference usrRef;
    ImageView imagenSupermercado;
    TextView nombreSupermercado;
    SharedPreferences preferences;
    Uri downloaduri = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_supermercado);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        usrRef = FirebaseDatabase.getInstance().getReference("BD");
        imagenSupermercado=findViewById(R.id.imagenSupermercado);
        nombreSupermercado=findViewById(R.id.editTextNombreSupermercado);
        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);
        cargando = new ProgressDialog(anadirSupermercadoActivity.this);
        storageReference= FirebaseStorage.getInstance().getReference("BD");

    }

    public void anadirImagenSupermercado(View v){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                elegirFoto();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                elegirFoto();
            }
        } else {
            return;
        }
    }

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    public void elegirFoto() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();


                CropImage.activity(selectedImage).setGuidelines(CropImageView.Guidelines.ON).setRequestedSize(480, 480)
                        .setAspectRatio(1,1).start(this);


            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (data != null) {
                    System.out.println("Entra aquiiiiii");
                    Uri resultUri = result.getUri();
                    File url = new File(resultUri.getPath());
                    //  Picasso.with(this).load(url).into(fotoPerfil);

                    //comprimir imagenra
                    try {
                        thumb_bitmap = new Compressor(this).setMaxWidth(480).setMaxHeight(480).setQuality(90).compressToBitmap(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                    //fin compresor



                    cargando.setTitle("Cargando imagen...");
                    cargando.setMessage("Espere por favor...");
                    cargando.show();



                    StorageReference ref = storageReference.child(String.valueOf(generateUniqueId()));
                    UploadTask uploadTask = ref.putBytes(thumb_byte);

                    //subir image a storage

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful())
                            {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            downloaduri = task.getResult();
                            Picasso.with(anadirSupermercadoActivity.this).load(downloaduri).into(imagenSupermercado);
                            cargando.dismiss();
                            Toast.makeText(anadirSupermercadoActivity.this, "Imagen establecida correctamente", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public static int generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }

    public void guardarSupermercado(View v){

        if(nombreSupermercado.getText().length()>0){
            usrRef.child(nombreSupermercado.getText().toString()).child("urlImage").setValue(downloaduri.toString());
            usrRef.child(nombreSupermercado.getText().toString()).child("creador").setValue(preferences.getString("identificador", null));



            Intent mostrarListaSupermercados =  new Intent(this, supermercadosActivity.class);//args(origen, destino)
            startActivity(mostrarListaSupermercados);
            finish();


        }
        else{
                Toast.makeText(anadirSupermercadoActivity.this, "Introduce el nombre del supermercado", Toast.LENGTH_SHORT).show();
        }
    }

}