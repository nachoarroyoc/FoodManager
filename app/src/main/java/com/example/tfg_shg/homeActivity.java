package com.example.tfg_shg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class homeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void abrirConfiguracion(View v){

        Intent homeActivity =  new Intent(this, configuracionActivity.class);//args(origen, destino)
        startActivity(homeActivity);

    }

    public void abrirListaCompras(View v){

        Intent abrirListaCompras =  new Intent(this, listaComprasActivity.class);//args(origen, destino)
        startActivity(abrirListaCompras);

    }

    public void mostrarCompraMercadona(View v){

        Intent compraActivity =  new Intent(this, compraActivity.class);//args(origen, destino)
        startActivity(compraActivity);

    }

    public void abrirSeleccionarSupermercado(View v){

        Intent abrirSeleccionarSupermercado =  new Intent(this, seleccionarSupermercado.class);//args(origen, destino)
        startActivity(abrirSeleccionarSupermercado);

    }

    public void mostrarSupermercados(View v){
        Intent supermercadosActivity =  new Intent(this, supermercadosActivity.class);//args(origen, destino)
        startActivity(supermercadosActivity);
    }
}