package com.example.safecare.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.safecare.R
import com.example.safecare.ui.activities.SafeCareActivity
import kotlinx.android.synthetic.main.fragment_registro.*

class RegistroFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supervisadoChip.setOnCheckedChangeListener { buttonView, isChecked ->
            supervisorChip.isChecked = false
            buttonView.isChecked = isChecked
        }
        supervisorChip.setOnCheckedChangeListener { buttonView, isChecked ->
            supervisadoChip.isChecked = false
            buttonView.isChecked = isChecked
        }
        finishRegisterButton.setOnClickListener {
            val intent = Intent(context, SafeCareActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
        cancelarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}