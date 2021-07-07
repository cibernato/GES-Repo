package com.example.safecare.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safecare.R
import com.example.safecare.adapters.AltertasAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AlertaFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fechas = arrayListOf<String>()
        var today: LocalDate = LocalDate.now()
        fechas.add(today.format(DateTimeFormatter.ofPattern("dd-MMM-yy")))

        for (i in 0..10) {
            today = today.plusDays(i.toLong())
            fechas.add(today.format(DateTimeFormatter.ofPattern("dd-MMM-yy")))
        }


        super.onViewCreated(view, savedInstanceState)
        val adapter = AltertasAdapter(requireContext())
        val layoutManagerAdapter = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        alertasList.layoutManager = layoutManagerAdapter
        adapter.setIncidencias(fechas)
        alertasList.adapter = adapter
        alertasList.adapter!!.notifyDataSetChanged()
        Log.e("asd", "${alertasList.adapter?.itemCount}")
//        super.onViewCreated(view, savedInstanceState)
    }

}