package com.example.tfg_shg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class verUsuario extends AppCompatActivity {

    SharedPreferences preferences;
    DatabaseReference myRef;
    String identificadorActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuario);


        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        TextView nombre = findViewById(R.id.Nombre);
        TextView apellidos = findViewById(R.id.Apellidos);
        TextView mail = findViewById(R.id.EmailAddress);
        TextView identificador = findViewById(R.id.identificador);


        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);
        identificadorActual=preferences.getString("identificador", null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Usuarios");


        myRef.child(identificadorActual).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(verUsuario.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
                else {
                    HashMap a = (HashMap) task.getResult().getValue();
                    nombre.setText(a.get("Nombre").toString());
                    apellidos.setText(a.get("Apellidos").toString());
                    mail.setText(a.get("email").toString());
                    identificador.setText(a.get("Identificador").toString());
                }
            }
        });

    }

    public void returnToConfiguracion(View view){

        Intent returnToConfiguracion=new Intent(verUsuario.this, configuracionActivity.class);
        startActivity(returnToConfiguracion);
        finish();

    }
}