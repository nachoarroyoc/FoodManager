package com.example.tfg_shg;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class listasDesglosadas extends AppCompatActivity {

    SharedPreferences preferences;
    ArrayList listaPkProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas_desglosadas);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();





        final String idCompra = getIntent().getStringExtra("idCompra");
        final String superName = getIntent().getStringExtra("superString");

        preferences=getSharedPreferences("Preferences", MODE_PRIVATE);


        cargarProductos(idCompra, superName);
    }

    public void cargarProductos(String idCompra, String superName){

        LinearLayout layout = (LinearLayout) findViewById(R.id.LayoutScrollAlimentosListaCompraDesglosada);
        layout.removeAllViews();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Usuarios").child(preferences.getString("identificador", null)).child("listasCompra").child(idCompra).child("Supermercado").child(superName);
        Query q= myRef;
        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
        pgsBar.setVisibility(View.VISIBLE);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot datasnapshot: snapshot.getChildren()){

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("BD").child(superName).child(datasnapshot.getKey());
                    myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {

                            }
                            else {
                                HashMap listaProductos = (HashMap) task.getResult().getValue();
                                String nombreAlimento="", ckal100g="", grasas="", grasasSaturadas="", proteinas="", sal="", azucar="", hidratos="", sodio="", imageURL="";
                                for (Object clave:listaProductos.keySet()) {

                                    if(clave.toString().equals("product_name")){
                                        try {
                                            nombreAlimento=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            nombreAlimento="Producto sin nombre";
                                        }
                                    }
                                    if(clave.toString().equals("energy-kj_100g")){
                                        try {
                                            ckal100g=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            ckal100g="No info";
                                        }
                                    }
                                    if(clave.toString().equals("fat_100g")){
                                        try {
                                            grasas=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            grasas="No info";
                                        }
                                    }
                                    if(clave.toString().equals("saturated-fat_100g")){
                                        try {
                                            grasasSaturadas=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            grasasSaturadas="No info";
                                        }
                                    }
                                    if(clave.toString().equals("proteins_100g")){
                                        try {
                                            proteinas=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            proteinas="No info";
                                        }
                                    }
                                    if(clave.toString().equals("salt_100g")){
                                        try {
                                            sal=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            sal="No info";
                                        }
                                    }
                                    if(clave.toString().equals("sugars_100g")){
                                        try {
                                            azucar=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            azucar="No info";
                                        }
                                    }
                                    if(clave.toString().equals("carbohydrates_100g")){
                                        try {
                                            hidratos=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            hidratos="No info";
                                        }
                                    }
                                    if(clave.toString().equals("sodium_100g")){
                                        try {
                                            sodio=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            sodio="No info";
                                        }
                                    }
                                    if(clave.toString().equals("image_small_url")){
                                        try {
                                            imageURL=listaProductos.get(clave).toString();
                                        }catch(NullPointerException e){
                                            imageURL="No info";
                                        }
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
                                }

                                try {
                                    generarCeldaAlimento(nombreAlimento, ckal100g, grasas, grasasSaturadas, proteinas, sal, azucar, hidratos, sodio, imageURL, datasnapshot.getKey(), true);//el flag es para el paso de lista a carrito
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }
                pgsBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(listasDesglosadas.this, "Error al pintar los productos", LENGTH_SHORT).show();
            }

        });



    }

    public void generarCeldaAlimento(String nombreAlimento, String ckal100g, String grasas, String grasasSaturadas, String proteinas, String sal, String azucar, String hidratos, String sodio, String imageURL, String key, Boolean flag) throws IOException {
        LinearLayout layout = (LinearLayout) findViewById(R.id.LayoutScrollAlimentosListaCompraDesglosada);


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

        Picasso.with(listasDesglosadas.this).load(imageURL).resize(300, 300).centerCrop().into(foodImage);
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
        }else {
        }

        /*LinearLayout div3 = new LinearLayout(this);
        LinearLayout.LayoutParams paramsDiv3= new LinearLayout.LayoutParams(700, 200);
        div3.setOrientation(LinearLayout.VERTICAL);

        div3.setLayoutParams(paramsDiv3);
        div3.setBackgroundColor(Color.BLUE);*/



        celdaAlimento.addView(div1);
        celdaAlimento.addView(div2);
        celdaAlimento.setBackgroundColor(Color.parseColor("#ffffff"));
        /*if(listaProductosCompra.contains(key) && flag){
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
        });*/
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


    public void returnToListaListas(View v){
        Intent returnToListaListas =  new Intent(this, listaComprasActivity.class);//args(origen, destino)
        startActivity(returnToListaListas);
        finish();
    }
}