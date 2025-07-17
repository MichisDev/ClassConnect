package com.example.agendadigital

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.agendadigital.adapters.RolDropdownAdapter
import com.example.agendadigital.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Pantalla de inicio de sesión principal para todos los roles.
 * Los usuarios pueden autenticarse con correo y contraseña, y seleccionar su rol para registrarse si no tienen cuenta.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val REQUEST_CODE_NOTIFICATIONS = 1001
    }

    override fun onStart() {
        super.onStart()
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener
            val userRef = FirebaseFirestore.getInstance().collection("usuarios").document(userId)

            userRef.get().addOnSuccessListener { document ->
                val existingToken = document.getString("fcmToken")
                if (existingToken != token) {
                    userRef.update("fcmToken", token)
                        .addOnSuccessListener { Log.d("FCM", "Token actualizado") }
                        .addOnFailureListener { e -> Log.e("FCM", "Error token", e) }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        }

        // Opciones de registro (menú desplegable)
        val roles = listOf("Profesor", "Padre", "Alumno")
        val adapter = RolDropdownAdapter(this, roles)
        binding.autoCompleteRol.setAdapter(adapter)
        binding.autoCompleteRol.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.autoCompleteRol.showDropDown()
        }
        binding.autoCompleteRol.setOnClickListener {
            binding.autoCompleteRol.showDropDown()
        }
        binding.autoCompleteRol.setOnItemClickListener { parent, _, position, _ ->
            val selectedRol = parent.getItemAtPosition(position) as String
            val iconRes = when (selectedRol.lowercase()) {
                "profesor" -> R.drawable.ic_profesor
                "padre" -> R.drawable.ic_familia
                "alumno" -> R.drawable.ic_alumno
                else -> R.drawable.ic_agenda
            }

            // Establecer icono manualmente como drawableStart
            binding.autoCompleteRol.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)
            binding.autoCompleteRol.compoundDrawablePadding = 16
        }



        binding.btnRegistrarse.setOnClickListener {
            animarBoton(it)
            when (binding.autoCompleteRol.text.toString()) {
                "Profesor" -> startActivity(Intent(this, RegisterProfesorActivity::class.java))
                "Padre" -> startActivity(Intent(this, ParentRegisterActivity::class.java))
                "Alumno" -> startActivity(Intent(this, RegisterAlumnoActivity::class.java))
                else -> Toast.makeText(this, "Selecciona un rol válido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            animarBoton(it)
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(this, "Bienvenido ${user?.email}", Toast.LENGTH_SHORT)
                                .show()

                            val uid = user?.uid ?: return@addOnCompleteListener
                            guardarTokenFCM(uid)

                            FirebaseFirestore.getInstance().collection("usuarios")
                                .document(uid).get()
                                .addOnSuccessListener { document ->
                                    when (document.getString("rol")) {
                                        "profesor" -> startActivity(
                                            Intent(
                                                this,
                                                MenuProfesorActivity::class.java
                                            )
                                        )

                                        "padre" -> {
                                            FirebaseMessaging.getInstance()
                                                .subscribeToTopic("eventos")
                                            startActivity(
                                                Intent(
                                                    this,
                                                    MenuPadreActivity::class.java
                                                )
                                            )
                                        }

                                        "alumno" -> startActivity(
                                            Intent(
                                                this,
                                                MenuAlumnoActivity::class.java
                                            )
                                        )

                                        else -> Toast.makeText(
                                            this,
                                            "Rol no reconocido",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    finish()
                                }
                        } else {
                            Log.e("LOGIN_ERROR", "Error en login", task.exception)
                            Toast.makeText(this, "Error en el login", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarTokenFCM(userId: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val userRef = FirebaseFirestore.getInstance().collection("usuarios").document(userId)
            userRef.get().addOnSuccessListener { document ->
                val existingToken = document.getString("fcmToken")
                if (existingToken != token) {
                    userRef.update("fcmToken", token)
                        .addOnSuccessListener { Log.d("FCM", "Token actualizado $userId") }
                        .addOnFailureListener { e -> Log.e("FCM", "Error token", e) }
                }
            }
        }
    }

    private fun animarBoton(boton: View) {
        val animacion = AnimationUtils.loadAnimation(this, R.anim.button_click_scale)
        boton.startAnimation(animacion)
    }
}
