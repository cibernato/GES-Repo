package com.example.safecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.safecare.R
import kotlinx.android.synthetic.main.item_supervisados.view.*
import kotlin.random.Random

class SupervisadoAdapter(
    private val context: Context
) : RecyclerView.Adapter<SupervisadoAdapter.SupervisadoViewHolder>() {

    private val listaImagenesComprobante: ArrayList<String> = arrayListOf()

    inner class SupervisadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(imagen: String, position: Int) {
            itemView.nombre.text = imagen
//            itemView.age.text = Random.nextInt(0, 90).toString()
            itemView.ubication.text = "${Random.nextDouble(17.0,18.0).toString().substring(0,7)}, ${Random.nextDouble(17.0,18.0).toString().substring(0,7)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupervisadoViewHolder {
        return SupervisadoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_supervisados, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SupervisadoViewHolder, position: Int) {
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