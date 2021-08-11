package com.example.safecare.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.safecare.R
import com.example.safecare.StaticObjects
import com.example.safecare.ui.activities.SafeCareActivity
import com.example.safecare.validateInputField
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences

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
                saveLoggedUserSharedPreferences()
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

    private fun saveLoggedUserSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val usuario = usuarioTextInputLayout.editText?.text.toString()
        val password = passwordTextInputLayout.editText?.text.toString()
        val usuarioDb =
            StaticObjects.usuarios.first { x -> x.nombre == usuario && x.password == password }
        sharedPreferences.edit().putString("usuario", gson.toJson(usuarioDb)).apply()

    }

    private fun isUserRegistered(): Boolean {
        val usuario = usuarioTextInputLayout.editText?.text.toString()
        val password = passwordTextInputLayout.editText?.text.toString()
        return !StaticObjects.usuarios.none { x -> x.nombre == usuario && x.password == password }
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

}