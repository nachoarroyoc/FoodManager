package com.example.tfg_shg;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class seleccionarSupermercado extends AppCompatActivity {

    SharedPreferences preferences;
    HashMap asociacionKeySupermercado = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_supermercado);
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


        mostrarListaSupermercados();

    }

    public void mostrarHome(View v){
        Intent mostrarListaSupermercados =  new Intent(this, homeActivity.class);//args(origen, destino)
        startActivity(mostrarListaSupermercados);
        finish();
    }

    public void mostrarListaSupermercados(){

        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListaSupermercadosCompra);
        layout.removeAllViews();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BD");
        Query q= myRef;

        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBarS);
        pgsBar.setVisibility(View.VISIBLE);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    try {
                        if(datasnapshot.child("creador").getValue()==null){
                            generarCeldaSupermercadoCompra(datasnapshot.getKey(), datasnapshot.getChildrenCount(), false);//el flag es para saber si no tiene creador el supermercado
                        }
                        else{
                            if(datasnapshot.child("creador").getValue().equals(preferences.getString("identificador", null))) {
                                generarCeldaSupermercadoCompra(datasnapshot.getKey(), datasnapshot.getChildrenCount() - 2, true);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                pgsBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(seleccionarSupermercado.this, "Error al pintar los supermercados", LENGTH_SHORT).show();
            }

        });
    }




    public void generarCeldaSupermercadoCompra(String nombreSupermercado, long numProductos, boolean flag) throws IOException {
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListaSupermercadosCompra);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1100, 250);
        params.setMargins(0, 0, 0, 10);

        LinearLayout celdaSupermercado = new LinearLayout(this);
        celdaSupermercado.setOrientation(LinearLayout.HORIZONTAL);
        celdaSupermercado.setLayoutParams(params);
        celdaSupermercado.setBackgroundResource(R.drawable.celda_lista_alimentos);


        LinearLayout div1 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv1 = new LinearLayout.LayoutParams(400, 250);
        div1.setOrientation(LinearLayout.HORIZONTAL);
        div1.setGravity(Gravity.CENTER);
        div1.setLayoutParams(paramsDiv1);
        //div1.setBackgroundColor(Color.RED);

        ImageView foodImage = new ImageView(this);

        //Picasso.with(compraActivity.this).load(mercado).resize(300, 300).centerCrop().into(foodImage);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BD");

        if(flag){
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String imageUrl = dataSnapshot.child(nombreSupermercado).child("urlImage").getValue().toString();
                    Picasso.with(seleccionarSupermercado.this).load(imageUrl).into(foodImage);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            if(nombreSupermercado.equals("Mercadona")){
                foodImage.setImageResource(R.mipmap.mercadona);
            }
            else if(nombreSupermercado.equals("Carrefour")){
                foodImage.setImageResource(R.mipmap.carrefour);
            }
        }

        div1.addView(foodImage);


        LinearLayout div2 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2 = new LinearLayout.LayoutParams(700, 250);
        div2.setOrientation(LinearLayout.VERTICAL);
        div2.setGravity(Gravity.START);
        div2.setLayoutParams(paramsDiv2);

        TextView nombreSupermercadoTextView = new TextView(this);
        nombreSupermercadoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
        nombreSupermercadoTextView.setText(nombreSupermercado);
        nombreSupermercadoTextView.setPadding(20, 20, 40, 20);
        nombreSupermercadoTextView.setTypeface(null, Typeface.BOLD);
        //nombreSupermercadoTextView.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout div2a = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2a = new LinearLayout.LayoutParams(700, 120);
        div2a.setOrientation(LinearLayout.HORIZONTAL);
        div2a.setGravity(Gravity.START);
        div2a.setLayoutParams(paramsDiv2a);
        //div2a.setBackgroundColor(Color.GREEN);
        //div2a.addView(nombreSupermercadoTextView);
        div2.addView(div2a);

        LinearLayout div2c = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2c = new LinearLayout.LayoutParams(475, 120);
        //div2c.setOrientation(LinearLayout.HORIZONTAL);
        div2c.setGravity(Gravity.START);
        div2c.setLayoutParams(paramsDiv2c);
        //div2c.setBackgroundColor(Color.GREEN);
        div2c.addView(nombreSupermercadoTextView);
        div2a.addView(div2c);

        LinearLayout div2d = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2d = new LinearLayout.LayoutParams(200, 100);
        div2d.setOrientation(LinearLayout.HORIZONTAL);
        div2d.setGravity(Gravity.START);
        div2d.setLayoutParams(paramsDiv2c);
        //div2d.setBackgroundColor(Color.BLUE);
        //div2c.addView(nombreSupermercadoTextView);

        div2a.addView(div2d);

        TextView cantidadProductosTextView = new TextView(this);
        cantidadProductosTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        cantidadProductosTextView.setText("Total productos: "+numProductos);
        cantidadProductosTextView.setPadding(20, 10, 40, 20);
        cantidadProductosTextView.setTypeface(null, Typeface.BOLD);
        LinearLayout div2b = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2b = new LinearLayout.LayoutParams(700, 100);
        div2b.setOrientation(LinearLayout.VERTICAL);
        div2b.setGravity(Gravity.START);
        div2b.setLayoutParams(paramsDiv2b);
        div2b.addView(cantidadProductosTextView);
        div2.addView(div2b);

        celdaSupermercado.addView(div1);
        celdaSupermercado.addView(div2);



        celdaSupermercado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nombre=nombreSupermercado;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("supermercadoMostrarProductos", nombreSupermercado);
                editor.commit();
                Intent compraActivity = new Intent(seleccionarSupermercado.this, compraActivity.class);
                compraActivity.putExtra("supermercadoMostrarProductos", nombreSupermercado);
                startActivity(compraActivity);

            }
        });

        int id=generateUniqueId();
        div2c.setId(id);
        asociacionKeySupermercado.put(nombreSupermercado, id);


        layout.addView(celdaSupermercado);

        DatabaseReference usrRef;

        usrRef = FirebaseDatabase.getInstance().getReference("BD");



    }

    public static int generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }

}