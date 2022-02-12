package com.example.tfg_shg;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class configuracionActivity extends AppCompatActivity {

    private DatabaseReference usrRef;
    FirebaseAuth mAuth;
    SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        usrRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        mAuth = FirebaseAuth.getInstance();
        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);


        String identificador=getIntent().getStringExtra("identificador");

        usrRef = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    public void informacionUsuario(View view){

        Intent informacionUsuario=new Intent(configuracionActivity.this, verUsuario.class);
        startActivity(informacionUsuario);
        finish();

    }

    public void editarUsuario(View view){

        Intent editarUsuario=new Intent(configuracionActivity.this, editarUsuario.class);
        startActivity(editarUsuario);
        finish();

    }

    public void returnToHome(View view){
        Intent returnToHome=new Intent(configuracionActivity.this, homeActivity.class);
        startActivity(returnToHome);
        finish();
    }

    public void cerrarSesion(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(configuracionActivity.this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estas seguro que deseas cerrar sesión?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("usrMail", null);
                editor.putString("identificador", null);
                editor.commit();
                Toast.makeText(configuracionActivity.this, "Se ha cerrado sesion correctamente", Toast.LENGTH_SHORT).show();
                Intent volver=new Intent(configuracionActivity.this, loginActivity.class);
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

    public void eliminarCuenta(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(configuracionActivity.this);
        builder.setTitle("Eliminar cuenta");
        builder.setMessage("¿Estas seguro que deseas eliminar esta cuenta? Perderás todos los datos");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String identificador=preferences.getString("identificador", null);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("usrMail", null);
                editor.putString("identificador", null);
                editor.commit();


                FirebaseAuth.getInstance().getCurrentUser().delete();
                usrRef.child(identificador).removeValue();
                Toast.makeText(configuracionActivity.this, "Se ha eliminado tu cuenta correctamente", Toast.LENGTH_SHORT).show();
                Intent volver=new Intent(configuracionActivity.this, loginActivity.class);
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
}