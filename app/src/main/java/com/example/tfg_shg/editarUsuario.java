package com.example.tfg_shg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class editarUsuario extends AppCompatActivity {

    SharedPreferences preferences;
    DatabaseReference myRef;
    String identificadorActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);


        identificadorActual=preferences.getString("identificador", null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Usuarios");


    }


    public void modificar(View view){

        EditText nombre=findViewById(R.id.editTextNombreEditar);
        EditText apellidos=findViewById(R.id.editTextApellidosEditar);

        AlertDialog.Builder builder = new AlertDialog.Builder(editarUsuario.this);
        builder.setTitle("Modificar datos");
        builder.setMessage("¿Estas seguro que deseas modificar los datos del usuario? (Solo se modificarán los campos que no esten vacíos)");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(nombre.getText().toString().length()>0){
                    myRef.child(identificadorActual).child("Nombre").setValue(nombre.getText().toString());
                }
                if(apellidos.getText().toString().length()>0){
                    myRef.child(identificadorActual).child("Apellidos").setValue(apellidos.getText().toString());
                }

                Toast.makeText(editarUsuario.this, "Campos modificados correctamente", Toast.LENGTH_SHORT).show();
                Intent volver=new Intent(editarUsuario.this, configuracionActivity.class);
                startActivity(volver);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(fixActivity.this, "He pulsado no", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();


    }

    public void returnToConfiguracion(View view){

        Intent editarUsuario=new Intent(editarUsuario.this, configuracionActivity.class);
        startActivity(editarUsuario);
        finish();

    }
}