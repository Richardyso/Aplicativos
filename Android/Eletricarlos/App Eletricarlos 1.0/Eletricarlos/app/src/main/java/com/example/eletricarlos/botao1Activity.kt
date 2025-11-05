package com.example.eletricarlos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Botao1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.botao1)

        val mpButton = findViewById<Button>(R.id.mp)
        val mcButton = findViewById<Button>(R.id.mc)
        val obsButton = findViewById<Button>(R.id.obs)

        mpButton.setOnClickListener {
            val intent = Intent(this, MpActivity::class.java)
            intent.putExtra("buttonId", "botao1")
            startActivity(intent)
        }

        mcButton.setOnClickListener {
            val intent = Intent(this, McActivity::class.java)
            intent.putExtra("buttonId", "botao1")
            startActivity(intent)
        }

        obsButton.setOnClickListener {
            val intent = Intent(this, ObsActivity::class.java)
            intent.putExtra("buttonId", "botao1")
            startActivity(intent)
        }
    }
}

