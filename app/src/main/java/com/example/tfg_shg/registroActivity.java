package com.example.tfg_shg;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class registroActivity extends AppCompatActivity {

    private DatabaseReference usrRef;
    EditText textMail, textPassword, textIdentificador, editTextName, editTextSurName;
    TextView botonRegistro;
    SharedPreferences preferences;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);
        usrRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        textMail=findViewById(R.id.editTextEmailAddress);
        textIdentificador=findViewById(R.id.editTextidentificador);
        textPassword=(EditText)findViewById(R.id.editTextPass);
        editTextName=findViewById(R.id.editTextNombre);
        editTextSurName=findViewById(R.id.editTextApellidos);
    }

    public void checkIfUserExist(View v){
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Usuarios");
        Query q= myRef.orderByChild("Identificador").equalTo(textIdentificador.getText().toString());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int contador=0;
                String mail="null";
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    contador++;
                }
                if(contador>0)
                {
                    Toast.makeText(registroActivity.this, "Este identificador esta en uso", LENGTH_SHORT).show();
                }
                else
                {
                    registrarUsuario();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
}

    public void registrarUsuario(){

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        if(textMail.getText().length()>0 && textPassword.getText().length()>5 && textIdentificador.getText().length()>0 && editTextName.getText().toString().length()>0 && editTextSurName.getText().toString().length()>0)
        {
            String email = textMail.getText().toString().toLowerCase();
            final String pass = textPassword.getText().toString();
            String user = FirebaseAuth.getInstance().getUid();

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        anadirUsuarioBD(textIdentificador.getText().toString(), textMail.getText().toString().toLowerCase(), editTextName.getText().toString(), editTextSurName.getText().toString());
                        Intent home = new Intent(registroActivity.this, homeActivity.class);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("identificador", textIdentificador.getText().toString());
                        editor.commit();
                        home.putExtra("identificador", textIdentificador.getText().toString());
                        Toast.makeText(getApplicationContext(), "Te has registrado correctamente", Toast.LENGTH_SHORT).show();
                        startActivity(home);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ha habido un error al registrate, intentalo de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            if(textPassword.getText().length()<=5){
                Toast.makeText(registroActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(registroActivity.this, "Introduce todos los campos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void anadirUsuarioBD(String identificador, String email, String name, String surName){

        Map<String, Object> usrData=new HashMap<>();
        usrData.put("Identificador", identificador);
        usrData.put("email", email);
        usrData.put("Nombre", name);
        usrData.put("Apellidos", surName);
        usrRef.child(identificador).setValue(usrData);
    }

}