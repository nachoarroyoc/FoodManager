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

public class supermercadosActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermercados);

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

    public void mostrarListaSupermercados(){

        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListaSupermercados);
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
                            generarCeldaSupermercado(datasnapshot.getKey(), datasnapshot.getChildrenCount(), false);
                        }
                        else{
                            if(datasnapshot.child("creador").getValue()==preferences.getString("identificador", null)){
                                generarCeldaSupermercado(datasnapshot.getKey(), datasnapshot.getChildrenCount()-2, true);
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
                Toast.makeText(supermercadosActivity.this, "Error al pintar los supermercados", LENGTH_SHORT).show();
            }

        });
    }

    public void mostrarListaSupermercados(View v){

        Intent mostrarHome =  new Intent(this, homeActivity.class);//args(origen, destino)
        startActivity(mostrarHome);


    }



    public void generarCeldaSupermercado(String nombreSupermercado, long numProductos, boolean flag) throws IOException {
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListaSupermercados);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1100, 250);
        params.setMargins(0, 0, 0, 10);

        LinearLayout celdaAlimento = new LinearLayout(this);
        celdaAlimento.setOrientation(LinearLayout.HORIZONTAL);
        celdaAlimento.setLayoutParams(params);
        celdaAlimento.setBackgroundResource(R.drawable.celda_lista_alimentos);


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
                    Picasso.with(supermercadosActivity.this).load(imageUrl).into(foodImage);

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

        LinearLayout div2e = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2e = new LinearLayout.LayoutParams(100, 100);
        div2e.setOrientation(LinearLayout.HORIZONTAL);
        div2e.setGravity(Gravity.START);
        div2e.setLayoutParams(paramsDiv2e);
        ImageView anadirProducto = new ImageView(this);
        LinearLayout.LayoutParams paramsAnadirProducto = new LinearLayout.LayoutParams(80, 80);
        paramsAnadirProducto.setMargins(0,10,0,0);
        anadirProducto.setLayoutParams(paramsAnadirProducto);
        anadirProducto.setImageResource(R.mipmap.anadir);
        div2e.addView(anadirProducto);
        //div2c.addView(nombreSupermercadoTextView);
        div2e.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("supermercadoAnadirProducto", nombreSupermercado);
                editor.commit();
                Intent mostrarAnadirProducto = new Intent(supermercadosActivity.this, anadirProductoActivity.class);//args(origen, destino)
                startActivity(mostrarAnadirProducto);

            }
        });
        div2d.addView(div2e);




        if(flag) {
            LinearLayout div2f = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2f = new LinearLayout.LayoutParams(100, 100);
            div2f.setOrientation(LinearLayout.HORIZONTAL);
            div2f.setGravity(Gravity.START);
            div2f.setLayoutParams(paramsDiv2f);
            ImageView eliminarSupermercado = new ImageView(this);
            LinearLayout.LayoutParams paramseliminarSupermercado = new LinearLayout.LayoutParams(80, 80);
            paramseliminarSupermercado.setMargins(0,10,0,0);
            eliminarSupermercado.setLayoutParams(paramseliminarSupermercado);
            eliminarSupermercado.setImageResource(R.mipmap.eliminar);
            div2f.addView(eliminarSupermercado);
            //div2f.setBackgroundColor(Color.GREEN);
            //div2c.addView(nombreSupermercadoTextView);
            div2d.addView(div2f);
        }
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

        celdaAlimento.addView(div1);
        celdaAlimento.addView(div2);
        layout.addView(celdaAlimento);

        DatabaseReference usrRef;

        usrRef = FirebaseDatabase.getInstance().getReference("BD");



    }

    public void anadirSupermercado(View v){

        Intent anadirSupermercado =  new Intent(this, anadirSupermercadoActivity.class);//args(origen, destino)
        startActivity(anadirSupermercado);

    }

}