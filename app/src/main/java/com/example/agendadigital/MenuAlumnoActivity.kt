package com.example.agendadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.agendadigital.databinding.ActivityAlumnoBinding

/**
 * Activity que representa el menú principal para usuarios con rol "alumno".
 *
 * Desde este menú, el alumno puede acceder a las siguientes secciones:
 * - Eventos escolares: ver eventos asignados a su aula.
 * - Apuntes: consultar y tomar apuntes personales.
 * - Notas: visualizar las calificaciones validadas y pendientes.
 * - Estadísticas: analizar su progreso académico mediante gráficos.
 *
 * Cada botón del menú lanza una Activity correspondiente.
 *
 * Dependencias:
 * - {@link EventosAlumnoActivity}
 * - {@link ApuntesAlumnoActivity}
 * - {@link NotasAlumnoActivity}
 * - {@link EstadisticasNotasAlumnoActivity}
 */
class MenuAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlumnoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardEventosAlumno.setOnClickListener {
            startActivity(Intent(this, EventosAlumnoActivity::class.java))
        }

        binding.cardApuntesAlumno.setOnClickListener {
            startActivity(Intent(this, ApuntesAlumnoActivity::class.java))
        }

        binding.cardNotasAlumno.setOnClickListener {
            startActivity(Intent(this, NotasAlumnoActivity::class.java))
        }

        binding.cardEstadisticasAlumno.setOnClickListener {
            startActivity(Intent(this, EstadisticasNotasAlumnoActivity::class.java))
        }
    }
}