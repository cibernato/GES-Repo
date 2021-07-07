package com.example.safecare.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.safecare.R
import com.example.safecare.services.IService2
import com.example.safecare.ui.activities.LoginActivity
import kotlinx.android.synthetic.main.fragment_home.*

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activarButton!!.setOnClickListener {

            Toast.makeText(
                context,
                "Safe riding! We track you for safety",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(context, IService2::class.java)
            activity?.startService(intent)
        }
        desactivarButton!!.setOnClickListener {

            val intent = Intent(context, IService2::class.java)
            activity?.stopService(intent)
        }
        logoutButton.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}