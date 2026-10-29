package com.brunooscarmike.controledegastos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brunooscarmike.controledegastos.databinding.ItemGastoBinding

class GastoAdapter(
    private val gastos: List<Gasto>
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    class GastoViewHolder(
        val binding: ItemGastoBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GastoViewHolder {

        val binding = ItemGastoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GastoViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: GastoViewHolder,
        position: Int
    ) {
        val gasto = gastos[position]

        holder.binding.textDescricao.text = gasto.descricao
        holder.binding.textValor.text = gasto.valor.toString()

    }

    override fun getItemCount(): Int {
        return gastos.size
    }

}