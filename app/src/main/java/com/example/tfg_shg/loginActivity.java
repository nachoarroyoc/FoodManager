package com.example.tfg_shg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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


public class loginActivity extends AppCompatActivity {

    EditText editTextEmailAddress, editTextPassword;

    SharedPreferences preferences;

    private DatabaseReference usrRef;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usrRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        editTextEmailAddress=findViewById(R.id.editTextEmailAddress);
        editTextPassword=findViewById(R.id.editTextPassword);


        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);

        if(preferences.getString("identificador", null)!=null)
        {
            Intent home = new Intent(loginActivity.this, homeActivity.class);
            home.putExtra("identificador", preferences.getString("identificador", null));
            startActivity(home);
            finish();
        }

    }

    public void loadRegistrarActivity(View v){

        Intent registerActivity =  new Intent(this, registroActivity.class);//args(origen, destino)
        startActivity(registerActivity);

    }



    public void logearUsuario(View view){

        if(editTextEmailAddress.getText().length()>0 && editTextPassword.getText().length()>0) {

            String email = editTextEmailAddress.getText().toString();
            String password = editTextPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Te has logeado correctamente", Toast.LENGTH_SHORT).show();

                        Query q = usrRef.orderByChild("email").equalTo(editTextEmailAddress.getText().toString());
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                                    Intent home = new Intent(loginActivity.this, homeActivity.class);
                                    home.putExtra("identificador", datasnapshot.getKey());
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("usrMail", editTextEmailAddress.getText().toString());
                                    editor.putString("identificador", datasnapshot.getKey());
                                    editor.commit();
                                    startActivity(home);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error con el login", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Toast.makeText(getApplicationContext(), "Credenciales incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(loginActivity.this, "Introduce todos los campos", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}