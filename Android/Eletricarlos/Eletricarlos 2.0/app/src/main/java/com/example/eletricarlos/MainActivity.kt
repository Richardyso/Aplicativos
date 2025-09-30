package com.example.eletricarlos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botao1 = findViewById<Button>(R.id.botao1)
        val botao2 = findViewById<Button>(R.id.botao2)
        val botao3 = findViewById<Button>(R.id.botao3)
        val botao4 = findViewById<Button>(R.id.botao4)

        botao1.setOnClickListener {
            startActivity(Intent(this, Botao1Activity::class.java))
        }

        botao2.setOnClickListener {
            startActivity(Intent(this, Botao2Activity::class.java))
        }

        botao3.setOnClickListener {
            startActivity(Intent(this, Botao3Activity::class.java))
        }

        botao4.setOnClickListener {
            startActivity(Intent(this, Botao4Activity::class.java))
        }
    }
}