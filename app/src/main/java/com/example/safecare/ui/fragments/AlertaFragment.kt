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
import com.example.safecare.StaticObjects
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


        super.onViewCreated(view, savedInstanceState)
        val adapter = AltertasAdapter(requireContext())
        val layoutManagerAdapter = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        alertasList.layoutManager = layoutManagerAdapter
        adapter.setIncidencias(StaticObjects.alertas)
        alertasList.adapter = adapter
        Log.e("asd", "${alertasList.adapter?.itemCount}")
//        super.onViewCreated(view, savedInstanceState)
    }

}