package com.example.tfg_shg;
import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class compraActivity extends AppCompatActivity {

    EditText nombreProducto;
    DatabaseReference myRef;
    ArrayList listaProductosCompra = new ArrayList();
    HashMap asociacionProductoViewCantidad = new HashMap<>();
    HashMap asociacionProductoCantidad = new HashMap<>();
    SharedPreferences preferences;
    ImageView accept;
    private DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
        pgsBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);
        ref = FirebaseDatabase.getInstance().getReference("Usuarios");



        nombreProducto=findViewById(R.id.nombreProducto);
        accept=findViewById(R.id.accept);
        accept.bringToFront();
        accept.setVisibility(View.GONE);




    }

    public void comprobarYGuardarLista(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(compraActivity.this);
        builder.setTitle("Guardar");
        builder.setMessage("Se guardará esta lista de la compra");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(listaProductosCompra.toArray().length>0){
                    String nombreSuper=preferences.getString("supermercadoMostrarProductos", null);
                    Integer valueId = generateUniqueId();
                    Date myDate = new Date();
                    for(int i=0; i<listaProductosCompra.toArray().length; i++){
                        ref.child(preferences.getString("identificador", null)).child("listasCompra").child(valueId.toString()).child("Supermercado").child(nombreSuper).child(listaProductosCompra.toArray()[i].toString()).setValue(asociacionProductoCantidad.get(listaProductosCompra.toArray()[i].toString()));
                        ref.child(preferences.getString("identificador", null)).child("listasCompra").child(valueId.toString()).child("Fecha").setValue(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(myDate));
                        ref.child(preferences.getString("identificador", null)).child("listasCompra").child(valueId.toString()).child("Realizada").setValue("NO");
                    }
                }else{
                    Toast.makeText(compraActivity.this, "Debes elegir al menos un producto", LENGTH_SHORT).show();

                }


            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void clearLayout(View view){
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListaAlimentos);
        nombreProducto.setText("");
        layout.removeAllViews();
    }

    public void showList(View view){
        ImageView image = (ImageView) findViewById(R.id.search);
        //image.setBackgroundColor(Color.parseColor("#fef5ab"));
        //view.setBackgroundColor(Color.parseColor("#f5eca4"));
        accept.setVisibility(View.VISIBLE);
        mostrarListaProductos(view, false);

    }

    public void searchProduct(View view){
        ImageView image = (ImageView) findViewById(R.id.carrito);
        //image.setBackgroundColor(Color.parseColor("#fef5ab"));
        mostrarListaProductos(view, true);
        accept.setVisibility(View.GONE);
        //view.setBackgroundColor(Color.parseColor("#f5eca4"));

    }

    public void mostrarListaProductos(View view, Boolean flag){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListaAlimentos);
        layout.removeAllViews();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String nombreSupermercadoMostrarProducto=preferences.getString("supermercadoMostrarProductos", null);
        DatabaseReference myRef = database.getReference("BD");
        Query q= myRef.child(nombreSupermercadoMostrarProducto);

        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
        pgsBar.setVisibility(View.VISIBLE);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot datasnapshot: snapshot.getChildren()){

                    if(flag){
                        try{
                            if(datasnapshot.child("product_name").getValue().toString().toLowerCase().contains(nombreProducto.getText().toString().toLowerCase())) {
                                accesoBBDD(datasnapshot, flag);
                            }
                        }catch(NullPointerException e){

                        }

                    }else{
                        if(listaProductosCompra.contains(datasnapshot.getKey())){
                            accesoBBDD(datasnapshot, flag);
                        }
                    }


                }
                pgsBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(compraActivity.this, "Error al pintar los productos", LENGTH_SHORT).show();
            }

        });
    }

    public void accesoBBDD(DataSnapshot datasnapshot, Boolean flag){
            String nombreAlimento, ckal100g, grasas, grasasSaturadas, proteinas, sal, azucar, hidratos, sodio, imageURL;

                try {
                    nombreAlimento=datasnapshot.child("product_name").getValue().toString();
                }catch(NullPointerException e){
                    nombreAlimento="Producto sin nombre";
                }

                try {
                    ckal100g=datasnapshot.child("energy-kj_100g").getValue().toString();
                }catch(NullPointerException e){
                    ckal100g="No info";
                }

                try {
                    grasas=datasnapshot.child("fat_100g").getValue().toString();
                }catch(NullPointerException e){
                    grasas="No info";
                }

                try {
                    grasasSaturadas=datasnapshot.child("saturated-fat_100g").getValue().toString();
                }catch(NullPointerException e){
                    grasasSaturadas="No info";
                }

                try {
                    proteinas=datasnapshot.child("proteins_100g").getValue().toString();
                }catch(NullPointerException e){
                    proteinas="No info";
                }

                try {
                    sal=datasnapshot.child("salt_100g").getValue().toString();
                }catch(NullPointerException e){
                    sal="No info";
                }

                try {
                    azucar=datasnapshot.child("sugars_100g").getValue().toString();
                }catch(NullPointerException e){
                    azucar="No info";
                }

                try {
                    hidratos=datasnapshot.child("carbohydrates_100g").getValue().toString();
                }catch(NullPointerException e){
                    hidratos="No info";
                }

                try {
                    sodio=datasnapshot.child("sodium_100g").getValue().toString();
                }catch(NullPointerException e){
                    sodio="No info";
                }

                try {
                    imageURL=datasnapshot.child("image_small_url").getValue().toString();
                }catch(NullPointerException e){
                    imageURL="No info";
                }

                if(nombreAlimento.equals("")){
                    nombreAlimento="Producto sin nombre";
                }
                if(ckal100g.equals("")){
                    ckal100g="No info";
                }
                if(grasas.equals("")){
                    grasas="No info";
                }
                if(grasasSaturadas.equals("")){
                    grasasSaturadas="No info";
                }
                if(proteinas.equals("")){
                    proteinas="No info";
                }
                if(sal.equals("")){
                    sal="No info";
                }
                if(azucar.equals("")){
                    azucar="No info";
                }
                if(hidratos.equals("")){
                    hidratos="No info";
                }
                if(sodio.equals("")){
                    sodio="No info";
                }
                if(imageURL.equals("")){
                    imageURL="No info";
                }

            System.out.println("Los ids de los productos: "+ datasnapshot.child("product_name").getValue());
            try {
                generarCeldaAlimento(nombreAlimento, ckal100g, grasas, grasasSaturadas, proteinas, sal, azucar, hidratos, sodio, imageURL, datasnapshot.getKey(), flag);//el flag es para el paso de lista a carrito
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void generarCeldaAlimento(String nombreAlimento, String ckal100g, String grasas, String grasasSaturadas, String proteinas, String sal, String azucar, String hidratos, String sodio, String imageURL, String key, Boolean flag) throws IOException {
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollLayoutListaAlimentos);


        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(1100, 350);
        params.setMargins(0, 0, 0, 10);

        LinearLayout celdaAlimento = new LinearLayout(this);
        celdaAlimento.setOrientation(LinearLayout.HORIZONTAL);
        celdaAlimento.setLayoutParams(params);
        celdaAlimento.setBackgroundResource(R.drawable.celda_lista_alimentos);

        LinearLayout div1 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv1= new LinearLayout.LayoutParams(400, 350);
        div1.setOrientation(LinearLayout.HORIZONTAL);
        div1.setGravity(Gravity.CENTER);
        div1.setLayoutParams(paramsDiv1);
        //div1.setBackgroundColor(Color.RED);

        ImageView foodImage = new ImageView(this);

        Picasso.with(compraActivity.this).load(imageURL).resize(300, 300).centerCrop().into(foodImage);
        div1.addView(foodImage);

        LinearLayout div2 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2= new LinearLayout.LayoutParams(700, 350);
        div2.setOrientation(LinearLayout.VERTICAL);
        div2.setGravity(Gravity.START);
        div2.setLayoutParams(paramsDiv2);

        TextView nombreAlimentoTextView = new TextView(this);
        nombreAlimentoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
        nombreAlimentoTextView.setText(nombreAlimento);
        nombreAlimentoTextView.setPadding(20, 20, 40, 20);
        nombreAlimentoTextView.setTypeface(null, Typeface.BOLD);
        //nombreAlimentoTextView.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout div2a = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv2a= new LinearLayout.LayoutParams(700, 120);
        div2a.setOrientation(LinearLayout.VERTICAL);
        div2a.setGravity(Gravity.START);
        div2a.setLayoutParams(paramsDiv2a);
        //div2a.setBackgroundColor(Color.GREEN);
        div2a.addView(nombreAlimentoTextView);
        div2.addView(div2a);

        if(flag) {
            LinearLayout div2b = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2b = new LinearLayout.LayoutParams(700, 200);
            div2b.setOrientation(LinearLayout.VERTICAL);
            div2b.setGravity(Gravity.START);
            div2b.setLayoutParams(paramsDiv2b);
            //div2b.setBackgroundColor(Color.BLUE);
            div2.addView(div2b);

            LinearLayout div2b1 = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv21 = new LinearLayout.LayoutParams(700, 50);
            div2b1.setOrientation(LinearLayout.HORIZONTAL);
            div2b1.setGravity(Gravity.START);
            div2b1.setLayoutParams(paramsDiv21);
            //div2b1.setBackgroundColor(Color.CYAN);
            div2b.addView(div2b1);

            TextView cKalTextView = new TextView(this);
            cKalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (ckal100g == "No info") {
                cKalTextView.setText("Energía: " + ckal100g);
            } else {
                cKalTextView.setText("Energía/100g: " + ckal100g + "ckal");
            }
            cKalTextView.setTypeface(null, Typeface.BOLD);
            cKalTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b1a = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv21a = new LinearLayout.LayoutParams(350, 50);
            //div2b1a.setOrientation(LinearLayout.VERTICAL);
            div2b1a.setGravity(Gravity.START);
            div2b1a.setLayoutParams(paramsDiv21a);
            //div2b1a.setBackgroundColor(Color.YELLOW);
            div2b1a.addView(cKalTextView);
            div2b1.addView(div2b1a);

            TextView proteinasTextView = new TextView(this);
            proteinasTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (proteinas == "No info") {
                proteinasTextView.setText("Proteínas: " + proteinas);
            } else {
                proteinasTextView.setText("Proteínas: " + proteinas + " g");
            }
            proteinasTextView.setTypeface(null, Typeface.BOLD);
            proteinasTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b1b = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv21b = new LinearLayout.LayoutParams(350, 50);
            //div2b1b.setOrientation(LinearLayout.VERTICAL);
            div2b1b.setGravity(Gravity.START);
            div2b1b.setLayoutParams(paramsDiv21b);
            //div2b1b.setBackgroundColor(Color.MAGENTA);
            div2b1b.addView(proteinasTextView);
            div2b1.addView(div2b1b);

            LinearLayout div2b2 = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv22 = new LinearLayout.LayoutParams(700, 50);
            div2b2.setOrientation(LinearLayout.HORIZONTAL);
            div2b2.setGravity(Gravity.START);
            div2b2.setLayoutParams(paramsDiv22);
            //div2b2.setBackgroundColor(Color.BLACK);
            div2b.addView(div2b2);

            TextView grasasTextView = new TextView(this);
            grasasTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (grasas == "No info") {
                grasasTextView.setText("Grasas: " + grasas);
            } else {
                grasasTextView.setText("Grasas/100g: " + grasas + "g");
            }
            grasasTextView.setTypeface(null, Typeface.BOLD);
            grasasTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b2a = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2b2a = new LinearLayout.LayoutParams(350, 50);
            //div2b2a.setOrientation(LinearLayout.VERTICAL);
            div2b2a.setGravity(Gravity.START);
            div2b2a.setLayoutParams(paramsDiv21a);
            //div2b2a.setBackgroundColor(Color.RED);
            div2b2a.addView(grasasTextView);
            div2b2.addView(div2b2a);

            TextView grasasSaturadasTextView = new TextView(this);
            grasasSaturadasTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (grasasSaturadas == "No info") {
                grasasSaturadasTextView.setText("Saturadas: " + grasasSaturadas);
            } else {
                grasasSaturadasTextView.setText("Saturadas: " + grasasSaturadas + " g");
            }
            grasasSaturadasTextView.setTypeface(null, Typeface.BOLD);
            grasasSaturadasTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b2b = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2b2b = new LinearLayout.LayoutParams(350, 50);
            //div2b2b.setOrientation(LinearLayout.VERTICAL);
            div2b2b.setGravity(Gravity.START);
            div2b2b.setLayoutParams(paramsDiv2b2b);
            //div2b2b.setBackgroundColor(Color.GREEN);
            div2b2b.addView(grasasSaturadasTextView);
            div2b2.addView(div2b2b);

            LinearLayout div2b3 = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv23 = new LinearLayout.LayoutParams(700, 50);
            div2b3.setOrientation(LinearLayout.HORIZONTAL);
            div2b3.setGravity(Gravity.START);
            div2b3.setLayoutParams(paramsDiv23);
            //div2b3.setBackgroundColor(Color.GRAY);
            div2b.addView(div2b3);

            TextView hidratosTextView = new TextView(this);
            hidratosTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (hidratos == "No info") {
                hidratosTextView.setText("Hidratos: " + hidratos);
            } else {
                hidratosTextView.setText("Hidratos/100g: " + hidratos + " g");
            }
            hidratosTextView.setTypeface(null, Typeface.BOLD);
            hidratosTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b3a = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2b3a = new LinearLayout.LayoutParams(350, 50);
            //div2b2a.setOrientation(LinearLayout.VERTICAL);
            div2b3a.setGravity(Gravity.START);
            div2b3a.setLayoutParams(paramsDiv2b3a);
            //div2b3a.setBackgroundColor(Color.YELLOW);
            div2b3a.addView(hidratosTextView);
            div2b3.addView(div2b3a);

            TextView azucarTextView = new TextView(this);
            azucarTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (azucar == "No info") {
                azucarTextView.setText("Azucar: " + azucar);
            } else {
                azucarTextView.setText("Azucar/100g: " + azucar + " g");
            }
            azucarTextView.setTypeface(null, Typeface.BOLD);
            azucarTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b3b = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2b3b = new LinearLayout.LayoutParams(350, 50);
            //div2b2b.setOrientation(LinearLayout.VERTICAL);
            div2b3b.setGravity(Gravity.START);
            div2b3b.setLayoutParams(paramsDiv2b3b);
            //div2b3b.setBackgroundColor(Color.CYAN);
            div2b3b.addView(azucarTextView);
            div2b3.addView(div2b3b);

            LinearLayout div2b4 = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv24 = new LinearLayout.LayoutParams(700, 50);
            div2b4.setOrientation(LinearLayout.HORIZONTAL);
            div2b4.setGravity(Gravity.START);
            div2b4.setLayoutParams(paramsDiv24);
            //div2b4.setBackgroundColor(Color.MAGENTA);
            div2b.addView(div2b4);

            TextView salTextView = new TextView(this);
            salTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (sal == "No info") {
                salTextView.setText("Sal: " + sal);
            } else {
                salTextView.setText("Sal/100g: " + sal + " g");
            }
            salTextView.setTypeface(null, Typeface.BOLD);
            salTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b4a = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2b4a = new LinearLayout.LayoutParams(350, 50);
            //div2b4a.setOrientation(LinearLayout.VERTICAL);
            div2b4a.setGravity(Gravity.START);
            div2b4a.setLayoutParams(paramsDiv2b4a);
            //div2b4a.setBackgroundColor(Color.GREEN);
            div2b4a.addView(salTextView);
            div2b4.addView(div2b4a);

            TextView sodioTextView = new TextView(this);
            sodioTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            if (sodio == "No info") {
                sodioTextView.setText("Sodio: " + sodio);
            } else {
                sodioTextView.setText("Sodio/100g: " + sodio + " g");
            }
            sodioTextView.setTypeface(null, Typeface.BOLD);
            sodioTextView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout div2b4b = new LinearLayout(this);
            LinearLayout.LayoutParams paramsDiv2b4b = new LinearLayout.LayoutParams(350, 50);
            //div2b2b.setOrientation(LinearLayout.VERTICAL);
            div2b4b.setGravity(Gravity.START);
            div2b4b.setLayoutParams(paramsDiv2b4b);
            //div2b4b.setBackgroundColor(Color.BLUE);
            div2b4b.addView(sodioTextView);
            div2b4.addView(div2b4b);
        }else{

            LinearLayout layoutCantidades = new LinearLayout(this);
            LinearLayout.LayoutParams paramslayoutCantidades = new LinearLayout.LayoutParams(600, 200);
            layoutCantidades.setGravity(Gravity.START);
            layoutCantidades.setLayoutParams(paramslayoutCantidades);
            //layoutCantidades.setBackgroundColor(Color.BLUE);
            div2.addView(layoutCantidades);

            LinearLayout decrementar = new LinearLayout(this);
            LinearLayout.LayoutParams paramsdecrementar = new LinearLayout.LayoutParams(200, 200);
            decrementar.setGravity(Gravity.START);
            LinearLayout.LayoutParams paramsdecrementarBoton = new LinearLayout.LayoutParams(125, 125);
            decrementar.setLayoutParams(paramsdecrementar);
            paramsdecrementar.gravity=Gravity.CENTER;
            //decrementar.setBackgroundColor(Color.RED);
            Button botonDecrementar = new Button(this);
            botonDecrementar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    TextView cantidadDecrementar = findViewById(Integer.parseInt(asociacionProductoViewCantidad.get(key).toString()));
                    int valorAntiguo=Integer.parseInt(cantidadDecrementar.getText().toString());
                    int valorNuevo=valorAntiguo-1;
                    if(valorNuevo>0){
                        cantidadDecrementar.setText(String.valueOf(valorNuevo));
                        asociacionProductoCantidad.remove(key);
                        asociacionProductoCantidad.put(key, valorNuevo);
                    }

                }
            });
            //botonDecrementar.setBackgroundColor(Color.parseColor("#ff8fa2"));
            botonDecrementar.setLayoutParams(paramsdecrementarBoton);
            botonDecrementar.setText("-1");
            botonDecrementar.setTextSize(15);
            paramsdecrementarBoton.leftMargin=80;
            paramsdecrementarBoton.topMargin=35;
            decrementar.addView(botonDecrementar);
            layoutCantidades.addView(decrementar);

            LinearLayout cantidad = new LinearLayout(this);
            LinearLayout.LayoutParams paramscantidad = new LinearLayout.LayoutParams(200, 200);
            cantidad.setGravity(Gravity.START);
            cantidad.setLayoutParams(paramscantidad);
            TextView cantidadString = new TextView(this);
            LinearLayout.LayoutParams paramscantidadString = new LinearLayout.LayoutParams(125, 125);
            cantidadString.setLayoutParams(paramscantidadString);
            try{
                cantidadString.setText(asociacionProductoCantidad.get(key).toString());
            }catch(Exception e){
                cantidadString.setText("1");
                asociacionProductoCantidad.put(key, 1);
            }
            paramscantidadString.leftMargin=70;
            paramscantidadString.topMargin=20;
            cantidadString.setTextSize(40);
            int id=generateUniqueId();
            cantidadString.setId(id);
            asociacionProductoViewCantidad.put(key, id);
            cantidad.addView(cantidadString);



            //cantidad.setBackgroundColor(Color.GREEN);
            layoutCantidades.addView(cantidad);

            LinearLayout incrementar = new LinearLayout(this);
            LinearLayout.LayoutParams paramsincrementar = new LinearLayout.LayoutParams(200, 200);
            incrementar.setGravity(Gravity.START);
            incrementar.setLayoutParams(paramsincrementar);
            LinearLayout.LayoutParams paramsincrementarBoton = new LinearLayout.LayoutParams(125, 125);
            decrementar.setLayoutParams(paramsdecrementar);
            paramsdecrementar.gravity=Gravity.CENTER;
            Button botonIncrementar = new Button(this);
            botonIncrementar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    TextView cantidadIncrementar = findViewById(Integer.parseInt(asociacionProductoViewCantidad.get(key).toString()));
                    int valorAntiguo=Integer.parseInt(cantidadIncrementar.getText().toString());
                    int valorNuevo=valorAntiguo+1;
                    cantidadIncrementar.setText(String.valueOf(valorNuevo));
                    asociacionProductoCantidad.remove(key);
                    asociacionProductoCantidad.put(key, valorNuevo);

                }
            });
            botonIncrementar.setLayoutParams(paramsincrementarBoton);
            botonIncrementar.setText("+1");
            botonIncrementar.setTextSize(15);
            //botonIncrementar.setBackgroundColor(Color.parseColor("#b8ffbd"));
            paramsincrementarBoton.rightMargin=80;
            paramsincrementarBoton.topMargin=35;
            incrementar.addView(botonIncrementar);
            layoutCantidades.addView(incrementar);
        }

        /*LinearLayout div3 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv3= new LinearLayout.LayoutParams(700, 200);
        div3.setOrientation(LinearLayout.VERTICAL);

        div3.setLayoutParams(paramsDiv3);
        div3.setBackgroundColor(Color.BLUE);*/



        celdaAlimento.addView(div1);
        celdaAlimento.addView(div2);
        celdaAlimento.setBackgroundColor(Color.parseColor("#ffffff"));
        if(listaProductosCompra.contains(key) && flag){
            celdaAlimento.setBackgroundColor(Color.parseColor("#ccffcf"));
        }
        celdaAlimento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(((ColorDrawable) v.getBackground()).getColor()==-1){
                    if(flag){
                        v.setBackgroundColor(Color.parseColor("#ccffcf"));
                        if(!listaProductosCompra.contains(key)){
                            listaProductosCompra.add(key);
                        }
                    }else{
                        v.setBackgroundColor(Color.parseColor("#ff8fa2"));
                        listaProductosCompra.remove(key);
                    }
                }else{
                    v.setBackgroundColor(Color.parseColor("#ffffff"));
                    if(flag){
                        listaProductosCompra.remove(key);
                    }else{
                        listaProductosCompra.add(key);
                    }

                }
            }
        });
        layout.addView(celdaAlimento);


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