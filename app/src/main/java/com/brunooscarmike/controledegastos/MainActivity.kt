package com.brunooscarmike.controledegastos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.brunooscarmike.controledegastos.databinding.ActivityMainBinding
import java.util.Locale
import org.json.JSONObject
import org.json.JSONArray


data class Gasto(
    var descricao: String,
    var valor: Double
)


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    var gastos = mutableListOf<Gasto>()

    private val prefs by lazy {
        getSharedPreferences("meu_app", MODE_PRIVATE)
    }

    private fun salvarGastos() {
        val jsonArray = JSONArray()

        gastos.forEach { gasto ->
            val jsonObject = JSONObject()
            jsonObject.put("descricao", gasto.descricao)
            jsonObject.put("valor", gasto.valor)
            jsonArray.put(jsonObject)
        }

        val json = jsonArray.toString()
        prefs.edit().putString("gastos", json).apply()
    }

    private lateinit var adapter: GastoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("meu_app", MODE_PRIVATE)
        val json = prefs.getString("gastos", "[]") ?: "[]"
        val jsonArray = JSONArray(json)
        jsonArray.length()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val descricao = jsonObject.getString("descricao")
            val valor = jsonObject.getDouble("valor")

            val gasto = Gasto(descricao, valor)
            gastos.add(gasto)
        }

        binding.recyclerGastos.layoutManager = LinearLayoutManager(this)
        adapter = GastoAdapter(gastos)
        binding.recyclerGastos.adapter = adapter

        with(binding) {

            btnAdicionar.setOnClickListener {

                val descricao = editDescricao.text.toString().trim()
                val valorTexto = editValor.text.toString().trim()

                if (descricao.isBlank() || valorTexto.isBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Preencha os campos obrigatórios.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    val valor = valorTexto.toDoubleOrNull()

                    if (valor == null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Digite um valor númerico válido.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {

                        val jaExiste = gastos.any {
                            it.descricao.equals(descricao, ignoreCase = true)
                        }

                        if (jaExiste) {
                            Toast.makeText(
                                this@MainActivity,
                                "Já existe um gasto com essa descrição.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val gasto = Gasto(descricao, valor)

                            gastos.add(gasto)
                            adapter.notifyDataSetChanged()
                            salvarGastos()

                            editDescricao.setText("")
                            editValor.setText("")
                        }


                    }


                }

            }

            btnLimpar.setOnClickListener {

                gastos.clear()
                adapter.notifyDataSetChanged()

                salvarGastos()

            }

            btnRemover.setOnClickListener {

                val removDesc = editRemoverDesc.text.toString()

                val removido = gastos.removeIf {
                    it.descricao == removDesc
                }

                adapter.notifyDataSetChanged()



                if (removido) {
                    salvarGastos()

                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Descrição não encontrada.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            btnTotal.setOnClickListener {

                var total = 0.0

                gastos.forEach { valor ->

                    total = total + valor.valor
                }

                Toast.makeText(
                    this@MainActivity,
                    "Total: R$: %.2f".format(Locale.forLanguageTag("pt-BR"), total),
                    Toast.LENGTH_SHORT
                ).show()

            }

            btnMaiorGasto.setOnClickListener {

                var maiorGasto: Gasto? = null

                gastos.forEach { maior ->

                    if (maior.valor > maiorGasto?.valor ?: 0.0) {
                        maiorGasto = maior
                    }
                }

                Toast.makeText(
                    this@MainActivity,
                    "O maior gasto é: " + "${maiorGasto?.descricao} " + "R$: %.2f".format(
                        Locale.forLanguageTag("pt-BR"), (maiorGasto?.valor)
                    ),
                    Toast.LENGTH_SHORT
                ).show()

            }

            btnMedia.setOnClickListener {

                if (gastos.isNotEmpty()) {
                    var total = 0.0

                    gastos.forEach { i ->
                        total = total + i.valor
                    }

                    var media = total / gastos.size

                    Toast.makeText(
                        this@MainActivity,
                        "Média dos gastos: R$ %.2f".format(
                            Locale.forLanguageTag("pt-BR"),
                            (media)
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Nenhum gasto cadastrado.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnAlterarGasto.setOnClickListener {

                val descAtual = editRemoverDesc.text.toString()
                val novoValor = editNovoValor.text.toString()

                if (novoValor.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Digite o novo valor.", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    val valor = novoValor.toDoubleOrNull()

                    if (valor == null) {
                        Toast.makeText(
                            this@MainActivity,
                            "O valor é inválido.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        var encontrado = false

                        gastos.forEach { gasto ->
                            if (gasto.descricao == descAtual) {
                                gasto.valor = valor
                                encontrado = true
                            }
                        }

                        if (!encontrado) {
                            Toast.makeText(
                                this@MainActivity,
                                "Descrição não encontrada",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            adapter.notifyDataSetChanged()
                            salvarGastos()
                        }

                    }

                }

                btnResumo.setOnClickListener {

                    val intent = Intent(
                        this@MainActivity, ResumoActivity::class.java
                    )

                    intent.putExtra("quantidade", gastos.size)

                    startActivity(intent)
                }
            }

        }
    }

    override fun onStart(){
        super.onStart()
        Log.d("CICLO", "MainActivity: onStart")
    }

    override fun onPause(){
        super.onPause()
        Log.d("CICLO", "MainActivity: onPause")
    }

    override fun onStop(){
        super.onStop()
        Log.d("CICLO", "MainActivity: onStop")
    }

    override fun onResume(){
        super.onResume()
        Log.d("CICLO", "MainActivity: onResume")
    }

}
