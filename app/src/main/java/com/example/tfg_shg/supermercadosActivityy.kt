package com.example.tfg_shg

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Build
import android.view.WindowManager
import android.content.pm.ActivityInfo
import android.content.Intent
import kotlin.Throws
import android.view.Gravity
import com.squareup.picasso.Picasso
import android.util.TypedValue
import android.graphics.Typeface
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import java.io.IOException

class supermercadosActivityy : AppCompatActivity() {
    var preferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supermercados)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val actionBar = supportActionBar
        actionBar!!.hide()
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE)
        mostrarListaSupermercados()
    }

    fun mostrarListaSupermercados() {
        val layout = findViewById<View>(R.id.scrollLayoutListaSupermercados) as LinearLayout
        layout.removeAllViews()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("BD")
        val q: Query = myRef
        val pgsBar = findViewById<View>(R.id.pBarS) as ProgressBar
        pgsBar.visibility = View.VISIBLE
        q.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (datasnapshot in snapshot.children) {
                    try {
                        if (datasnapshot.child("creador").value == null) {
                            generarCeldaSupermercado(datasnapshot.key, datasnapshot.childrenCount, false)
                        } else {
                            if (datasnapshot.child("creador").value == preferences!!.getString("identificador", null)) {
                                generarCeldaSupermercado(datasnapshot.key, datasnapshot.childrenCount - 2, true)
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                pgsBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@supermercadosActivityy, "Error al pintar los supermercados", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun mostrarListaSupermercados(v: View?) {
        val mostrarHome = Intent(this, homeActivity::class.java) //args(origen, destino)
        startActivity(mostrarHome)
    }

    @Throws(IOException::class)
    fun generarCeldaSupermercado(nombreSupermercado: String?, numProductos: Long, flag: Boolean) {
        val layout = findViewById<View>(R.id.scrollLayoutListaSupermercados) as LinearLayout
        val params = LinearLayout.LayoutParams(1100, 250)
        params.setMargins(0, 0, 0, 10)
        val celdaAlimento = LinearLayout(this)
        celdaAlimento.orientation = LinearLayout.HORIZONTAL
        celdaAlimento.layoutParams = params
        celdaAlimento.setBackgroundResource(R.drawable.celda_lista_alimentos)
        val div1 = LinearLayout(this)
        val paramsDiv1 = LinearLayout.LayoutParams(400, 250)
        div1.orientation = LinearLayout.HORIZONTAL
        div1.gravity = Gravity.CENTER
        div1.layoutParams = paramsDiv1
        //div1.setBackgroundColor(Color.RED);
        val foodImage = ImageView(this)

        //Picasso.with(compraActivity.this).load(mercado).resize(300, 300).centerCrop().into(foodImage);
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("BD")
        if (flag) {
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val imageUrl = dataSnapshot.child(nombreSupermercado!!).child("urlImage").value.toString()
                    Picasso.with(this@supermercadosActivityy).load(imageUrl).into(foodImage)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        } else {
            if (nombreSupermercado == "Mercadona") {
                foodImage.setImageResource(R.mipmap.mercadona)
            } else if (nombreSupermercado == "Carrefour") {
                foodImage.setImageResource(R.mipmap.carrefour)
            }
        }
        div1.addView(foodImage)
        val div2 = LinearLayout(this)
        val paramsDiv2 = LinearLayout.LayoutParams(700, 250)
        div2.orientation = LinearLayout.VERTICAL
        div2.gravity = Gravity.START
        div2.layoutParams = paramsDiv2
        val nombreSupermercadoTextView = TextView(this)
        nombreSupermercadoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        nombreSupermercadoTextView.text = nombreSupermercado
        nombreSupermercadoTextView.setPadding(20, 20, 40, 20)
        nombreSupermercadoTextView.setTypeface(null, Typeface.BOLD)
        //nombreSupermercadoTextView.setGravity(Gravity.CENTER_VERTICAL);
        val div2a = LinearLayout(this)
        val paramsDiv2a = LinearLayout.LayoutParams(700, 120)
        div2a.orientation = LinearLayout.HORIZONTAL
        div2a.gravity = Gravity.START
        div2a.layoutParams = paramsDiv2a
        //div2a.setBackgroundColor(Color.GREEN);
        //div2a.addView(nombreSupermercadoTextView);
        div2.addView(div2a)
        val div2c = LinearLayout(this)
        val paramsDiv2c = LinearLayout.LayoutParams(475, 120)
        //div2c.setOrientation(LinearLayout.HORIZONTAL);
        div2c.gravity = Gravity.START
        div2c.layoutParams = paramsDiv2c
        //div2c.setBackgroundColor(Color.GREEN);
        div2c.addView(nombreSupermercadoTextView)
        div2a.addView(div2c)
        val div2d = LinearLayout(this)
        val paramsDiv2d = LinearLayout.LayoutParams(200, 100)
        div2d.orientation = LinearLayout.HORIZONTAL
        div2d.gravity = Gravity.START
        div2d.layoutParams = paramsDiv2c
        //div2d.setBackgroundColor(Color.BLUE);
        //div2c.addView(nombreSupermercadoTextView);
        val div2e = LinearLayout(this)
        val paramsDiv2e = LinearLayout.LayoutParams(100, 100)
        div2e.orientation = LinearLayout.HORIZONTAL
        div2e.gravity = Gravity.START
        div2e.layoutParams = paramsDiv2e
        val anadirProducto = ImageView(this)
        val paramsAnadirProducto = LinearLayout.LayoutParams(80, 80)
        paramsAnadirProducto.setMargins(0, 10, 0, 0)
        anadirProducto.layoutParams = paramsAnadirProducto
        anadirProducto.setImageResource(R.mipmap.anadir)
        div2e.addView(anadirProducto)
        //div2c.addView(nombreSupermercadoTextView);
        div2e.setOnClickListener {
            val editor = preferences!!.edit()
            editor.putString("supermercadoAnadirProducto", nombreSupermercado)
            editor.commit()
            val mostrarAnadirProducto = Intent(this@supermercadosActivityy, anadirProductoActivity::class.java) //args(origen, destino)
            startActivity(mostrarAnadirProducto)
        }
        div2d.addView(div2e)
        if (flag) {
            val div2f = LinearLayout(this)
            val paramsDiv2f = LinearLayout.LayoutParams(100, 100)
            div2f.orientation = LinearLayout.HORIZONTAL
            div2f.gravity = Gravity.START
            div2f.layoutParams = paramsDiv2f
            val eliminarSupermercado = ImageView(this)
            val paramseliminarSupermercado = LinearLayout.LayoutParams(80, 80)
            paramseliminarSupermercado.setMargins(0, 10, 0, 0)
            eliminarSupermercado.layoutParams = paramseliminarSupermercado
            eliminarSupermercado.setImageResource(R.mipmap.eliminar)
            div2f.addView(eliminarSupermercado)
            //div2f.setBackgroundColor(Color.GREEN);
            //div2c.addView(nombreSupermercadoTextView);
            div2d.addView(div2f)
        }
        div2a.addView(div2d)
        val cantidadProductosTextView = TextView(this)
        cantidadProductosTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        cantidadProductosTextView.text = "Total productos: $numProductos"
        cantidadProductosTextView.setPadding(20, 10, 40, 20)
        cantidadProductosTextView.setTypeface(null, Typeface.BOLD)
        val div2b = LinearLayout(this)
        val paramsDiv2b = LinearLayout.LayoutParams(700, 100)
        div2b.orientation = LinearLayout.VERTICAL
        div2b.gravity = Gravity.START
        div2b.layoutParams = paramsDiv2b
        div2b.addView(cantidadProductosTextView)
        div2.addView(div2b)
        celdaAlimento.addView(div1)
        celdaAlimento.addView(div2)
        layout.addView(celdaAlimento)
        val usrRef: DatabaseReference
        usrRef = FirebaseDatabase.getInstance().getReference("BD")
    }

    fun anadirSupermercado(v: View?) {
        val anadirSupermercado = Intent(this, anadirSupermercadoActivity::class.java) //args(origen, destino)
        startActivity(anadirSupermercado)
    }
}