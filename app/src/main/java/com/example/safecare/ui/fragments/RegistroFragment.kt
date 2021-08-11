package com.example.safecare.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.safecare.R
import com.example.safecare.StaticObjects
import com.example.safecare.dtos.UsuarioDto
import com.example.safecare.ui.activities.SafeCareActivity
import com.example.safecare.validateInputField
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_registro.*

class RegistroFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences

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
        finishRegisterButton.setOnClickListener {
            if (validateFields()) {
                saveUser()
                val intent = Intent(context, SafeCareActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }else{
                Toast.makeText(context,"Campos Invalidos",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveUser() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val usuarioDto = UsuarioDto(
            usuarioInputLayout.editText?.text.toString(),
            correoInputLayout.editText?.text.toString(),
            "",
            numberInputLayout.editText?.text.toString(),
            passwordInputLayout.editText?.text.toString(),
            supervisorChip.isChecked
        )
        StaticObjects.usuarios.add(usuarioDto)
        val gson = Gson()
        sharedPreferences.edit().putString("usuario", gson.toJson(usuarioDto)).apply()
    }

    private fun validateFields(): Boolean {
        return validateInputField(usuarioInputLayout, "Usuario")
                && validateInputField(correoInputLayout, "Correo")
                && validateInputField(passwordInputLayout, "Contraseña")
                && validateInputField(numberInputLayout, "Telefono")
                && passwordsAreTheSame()
    }

    private fun passwordsAreTheSame(): Boolean {
        return passwordInputLayout.editText?.text.toString() == password2InputLayout.editText?.text.toString()
    }

}