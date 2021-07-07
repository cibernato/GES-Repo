package com.example.safecare.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.safecare.R
import com.example.safecare.ui.activities.SafeCareActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usuarioTextInputLayout.editText?.addTextChangedListener(
            createTextWatcher(
                usuarioTextInputLayout
            )
        )
        passwordTextInputLayout.editText?.addTextChangedListener(
            createTextWatcher(
                passwordTextInputLayout
            )
        )

        ingresarButton.setOnClickListener {
            if (!isValidFields()) return@setOnClickListener
            if (!isUserRegistered()) {
                Toast.makeText(context, "Usuario o contraseña incorrecta", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                val intent = Intent(context, SafeCareActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }

        }
        registrarButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_registroFragment)
        }


    }

    private fun isUserRegistered(): Boolean {
        return true
    }

    private fun createTextWatcher(inputLayout: TextInputLayout) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputLayout.isErrorEnabled = false
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun isValidFields(): Boolean {
        return validateInputField(usuarioTextInputLayout, "Usuario") && validateInputField(
            passwordTextInputLayout, "Contraseña"
        )
    }

    private fun validateInputField(inputLayout: TextInputLayout, field: String): Boolean {
        if (inputLayout.editText?.text.toString().isEmpty()) {
            inputLayout.error = "$field no puede estar vacio"
            inputLayout.isErrorEnabled = true
            return false
        }
        return true
    }


}