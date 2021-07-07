package com.example.safecare.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safecare.R
import com.example.safecare.adapters.AltertasAdapter
import com.example.safecare.adapters.SupervisadoAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_lista.*


class ListaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManagerAdapter = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = SupervisadoAdapter(requireContext())
        listaSupervisados.layoutManager = layoutManagerAdapter
        adapter.setIncidencias(arrayListOf("Nombre 1","Nombre 2 ", "Nombre 3"))
        listaSupervisados.adapter = adapter
    }


}