package com.brunooscarmike.controledegastos

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.brunooscarmike.controledegastos.databinding.ActivityMainBinding
import com.brunooscarmike.controledegastos.databinding.ActivityResumoBinding

class ResumoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityResumoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val quantidade = intent.getIntExtra("quantidade", 0)

        with(binding) {

            val prefs = getSharedPreferences("meu_app", MODE_PRIVATE)

            btnSalvar.setOnClickListener {
                val usuario = editUsuario.text.toString().trim()

                if(usuario.isBlank()){
                    Toast.makeText(this@ResumoActivity, "Digite um nome!", Toast.LENGTH_SHORT).show()
                }else{
                    prefs.edit().putString("usuario", usuario).apply()
                    textView3.text = "Usuário salvo: $usuario"
                    editUsuario.setText("")
                }
            }

            // prefs.edit().putInt("quantidade", quantidade).apply()
            // prefs.edit().putString("usuario", novoUsuario).apply()

            val quantidadeSalva = prefs.getInt("quantidade", 0)
            val usuarioSalvo = prefs.getString("usuario", "Visitante")

            editUsuario.setText(usuarioSalvo)

            textView3.text = "Quantidade de gastos: $quantidadeSalva\nUsuário salvo: $usuarioSalvo"

            btnVoltar.setOnClickListener {
                finish()
            }

            btnRemoverUsuario.setOnClickListener {

                if(prefs.contains("usuario")){
                    prefs.edit().remove("usuario").apply()
                    textView3.text = "Nome apagado!"
                    editUsuario.setText("")
                }else{
                    Toast.makeText(this@ResumoActivity,
                        "O nome já não existe.",
                        Toast.LENGTH_SHORT)
                        .show()
                }



            }

        }

    }

    override fun onStart(){
        super.onStart()
        Log.d("CICLO", "ResumoActivity: onStart")
    }

}