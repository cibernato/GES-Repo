package com.example.safecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.safecare.R
import kotlinx.android.synthetic.main.alerta_item_layout.view.*

class AltertasAdapter(
    private val context: Context
) : RecyclerView.Adapter<AltertasAdapter.AlertaViewHolder>() {

    private val listaImagenesComprobante: ArrayList<String> = arrayListOf()

    inner class AlertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(imagen: String, position: Int) {
            itemView.fechaEvento.text = imagen
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertaViewHolder {
        return AlertaViewHolder(
            LayoutInflater.from(context).inflate(R.layout.alerta_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlertaViewHolder, position: Int) {
        holder.setData(listaImagenesComprobante[position], position)
    }

    override fun getItemCount(): Int {
        return listaImagenesComprobante.size
    }

    fun setIncidencias(incidenciasList: List<String>) {
        this.listaImagenesComprobante.clear()
        this.listaImagenesComprobante.addAll(incidenciasList)
        notifyDataSetChanged()
    }
}