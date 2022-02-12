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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class listaComprasActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);

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


        mostrarListasDeLaCompra();
    }

    public void mostrarListasDeLaCompra(){

        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListasCompra);
        layout.removeAllViews();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Usuarios").child(preferences.getString("identificador", null)).child("listasCompra");
        Query q= myRef;

        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBarListas);
        pgsBar.setVisibility(View.VISIBLE);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot datasnapshot: snapshot.getChildren()){

                    String fecha=datasnapshot.child("Fecha").getValue().toString();
                    String realizada=datasnapshot.child("Realizada").getValue().toString();
                    String superString= "";
                    int cantidadProductosLista=0;
                    HashMap hashmap= (HashMap)datasnapshot.child("Supermercado").getValue();
                    for (Object clave:hashmap.keySet()) {
                        HashMap supermercado = (HashMap) hashmap.get(clave);
                        superString = clave.toString();
                        cantidadProductosLista=0;
                        for(Object producto: supermercado.keySet()){
                            //pintarProducto(producto, supermercado.get(producto));//idProducto, cantidad
                            cantidadProductosLista++;
                        }
                    }

                    mostrarListas(fecha, superString, datasnapshot.getKey(), cantidadProductosLista, realizada);

                }
                pgsBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(listaComprasActivity.this, "Error al pintar los supermercados", LENGTH_SHORT).show();
            }

        });
    }

    public void mostrarListas(String fecha, String supermercado, String idCompra, int cantidadProductosLista, String realizada){
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListasCompra);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1100, 250);
        params.setMargins(0, 0, 0, 10);

        LinearLayout celdaListaCompra = new LinearLayout(this);
        celdaListaCompra.setOrientation(LinearLayout.HORIZONTAL);
        celdaListaCompra.setLayoutParams(params);
        celdaListaCompra.setBackgroundResource(R.drawable.celda_lista_alimentos);
        celdaListaCompra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent listasDesglosadas =  new Intent(listaComprasActivity.this, listasDesglosadas.class);//args(origen, destino)
                listasDesglosadas.putExtra("idCompra", idCompra);
                listasDesglosadas.putExtra("superString", supermercado);
                startActivity(listasDesglosadas);
                finish();

            }
        });


        LinearLayout div1 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv1 = new LinearLayout.LayoutParams(300, 250);
        div1.setOrientation(LinearLayout.HORIZONTAL);
        div1.setGravity(Gravity.CENTER);
        div1.setLayoutParams(paramsDiv1);
        ImageView imagen = new ImageView(this);
        imagen.setImageResource(R.mipmap.listacompracelda);
        imagen.setPadding(20, 20, 40, 20);
        div1.addView(imagen);



        LinearLayout div2 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2 = new LinearLayout.LayoutParams(800, 250);
        div2.setOrientation(LinearLayout.VERTICAL);
        div2.setGravity(Gravity.START);
        div2.setLayoutParams(paramsDiv2);

        TextView nombreSupermercadoTextView = new TextView(this);
        nombreSupermercadoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
        nombreSupermercadoTextView.setText(fecha);
        nombreSupermercadoTextView.setPadding(40, 20, 40, 20);
        nombreSupermercadoTextView.setTypeface(null, Typeface.BOLD);
        //nombreSupermercadoTextView.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout div2a = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2a = new LinearLayout.LayoutParams(800, 120);
        div2a.setOrientation(LinearLayout.HORIZONTAL);
        div2a.setGravity(Gravity.START);
        div2a.setLayoutParams(paramsDiv2a);
        //div2a.setBackgroundColor(Color.GREEN);
        //div2a.addView(nombreSupermercadoTextView);
        div2.addView(div2a);

        LinearLayout div2c = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2c = new LinearLayout.LayoutParams(575, 120);
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
        if(realizada.equals("NO")){
            anadirProducto.setImageResource(R.mipmap.clock);
            div2e.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(listaComprasActivity.this);
                    alert.setTitle("¿Cuánto te ha costado la compra?");
                    final EditText input = new EditText(listaComprasActivity.this);
                    alert.setView(input);
                    alert.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for OK button here
                            marcarListaCompraComprada(idCompra);

                        }
                    });
                    alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for CANCEL button here, or leave in blank
                        }
                    });
                    alert.show();

                }
            });
        }else{
            anadirProducto.setImageResource(R.mipmap.realizada);
        }
        div2e.addView(anadirProducto);

        //div2c.addView(nombreSupermercadoTextView);

        div2d.addView(div2e);

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
        div2f.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(listaComprasActivity.this);
                builder.setTitle("Eliminar Lista Compra");
                builder.setMessage("Se borrarán todos los datos");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
        });
        div2d.addView(div2f);

        div2a.addView(div2d);

        TextView cantidadProductosTextView = new TextView(this);
        cantidadProductosTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        cantidadProductosTextView.setText(supermercado+": "+cantidadProductosLista+" productos");
        cantidadProductosTextView.setPadding(20, 10, 40, 20);
        cantidadProductosTextView.setTypeface(null, Typeface.BOLD);
        LinearLayout div2b = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2b = new LinearLayout.LayoutParams(700, 100);
        div2b.setOrientation(LinearLayout.VERTICAL);
        div2b.setGravity(Gravity.START);
        div2b.setLayoutParams(paramsDiv2b);
        div2b.addView(cantidadProductosTextView);
        div2.addView(div2b);

        celdaListaCompra.addView(div1);
        celdaListaCompra.addView(div2);
        layout.addView(celdaListaCompra);


    }

    public void marcarListaCompraComprada(String idCompra){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Usuarios").child(preferences.getString("identificador", null)).child("listasCompra").child(idCompra).child("Realizada").setValue("SI");
        mostrarListasDeLaCompra();
    }


    public void mostrarHome(View v){
        Intent mostrarHome =  new Intent(this, homeActivity.class);//args(origen, destino)
        startActivity(mostrarHome);
        finish();
    }
}