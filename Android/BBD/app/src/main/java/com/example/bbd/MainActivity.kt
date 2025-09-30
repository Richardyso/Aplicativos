package com.example.bbd

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private lateinit var scoreTextView: TextView
    private lateinit var bestScoreTextView: TextView
    private var score = 0
    private var bestScore = 0
    private val grid = Array(5) { Array(5) { 0 } }
    private var startX = 0f
    private var startY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        scoreTextView = findViewById(R.id.scoreTextView)
        bestScoreTextView = findViewById(R.id.bestScoreTextView)

        initializeGrid()
        updateUI()

        gridLayout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val endY = event.y
                    val dx = endX - startX
                    val dy = endY - startY

                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (dx > 0) moveRight() else moveLeft()
                    } else {
                        if (dy > 0) moveDown() else moveUp()
                    }

                    updateUI()
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeGrid() {
        repeat(2) { addNewDot() }
    }

    private fun addNewDot() {
        val emptySpots = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                if (grid[i][j] == 0) emptySpots.add(Pair(i, j))
            }
        }
        if (emptySpots.isNotEmpty()) {
            val spot = emptySpots[Random.nextInt(emptySpots.size)]
            grid[spot.first][spot.second] = when (Random.nextInt(100)) {
                in 0..69 -> 1 // 70% chance de ser preto (nível 1)
                in 70..89 -> 2 // 20% chance de ser vermelho (nível 2)
                else -> 3 // 10% chance de ser azul (nível 3)
            }
        }
    }

    private fun updateUI() {
        gridLayout.removeAllViews()
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val dotView = layoutInflater.inflate(R.layout.dot_view, gridLayout, false)
                dotView.setBackgroundResource(getDotColor(grid[i][j]))
                gridLayout.addView(dotView)
            }
        }
        scoreTextView.text = "Score: $score"
        bestScoreTextView.text = "Best: $bestScore"
    }

    private fun getDotColor(value: Int): Int {
        return when (value) {
            0 -> R.color.empty
            1 -> R.color.black
            2 -> R.color.red
            3 -> R.color.blue
            else -> R.color.purple
        }
    }

    private fun moveLeft() {
        var moved = false
        for (i in 0 until 5) {
            val row = grid[i].toMutableList()
            var j = 0
            while (j < row.size - 1) {
                if (row[j] != 0 && row[j] == row[j + 1]) {
                    row[j]++
                    row[j + 1] = 0
                    score += when (row[j]) {
                        2 -> 1 // Dois pretos mesclados (nível 1 -> 2)
                        3 -> 2 // Dois vermelhos mesclados (nível 2 -> 3)
                        4 -> 3 // Dois azuis mesclados (nível 3 -> 4)
                        else -> row[j] - 1 // Para níveis superiores
                    }
                    moved = true
                }
                j++
            }
            row.removeAll { it == 0 }
            while (row.size < 5) row.add(0)
            grid[i] = row.toTypedArray()
        }
        if (moved) {
            addNewDot()
            updateBestScore()
        } else {
            addNewDot() // Adiciona um novo elemento mesmo se não houver movimento
        }
    }

    private fun moveRight() {
        var moved = false
        for (i in 0 until 5) {
            val row = grid[i].reversed().toMutableList()
            var j = 0
            while (j < row.size - 1) {
                if (row[j] != 0 && row[j] == row[j + 1]) {
                    row[j]++
                    row[j + 1] = 0
                    score += when (row[j]) {
                        2 -> 1 // Dois pretos mesclados (nível 1 -> 2)
                        3 -> 2 // Dois vermelhos mesclados (nível 2 -> 3)
                        4 -> 3 // Dois azuis mesclados (nível 3 -> 4)
                        else -> row[j] - 1 // Para níveis superiores
                    }
                    moved = true
                }
                j++
            }
            row.removeAll { it == 0 }
            while (row.size < 5) row.add(0)
            grid[i] = row.reversed().toTypedArray()
        }
        if (moved) {
            addNewDot()
            updateBestScore()
        } else {
            addNewDot()
        }
    }

    private fun moveUp() {
        var moved = false
        for (j in 0 until 5) {
            val column = (0 until 5).map { grid[it][j] }.toMutableList()
            var i = 0
            while (i < column.size - 1) {
                if (column[i] != 0 && column[i] == column[i + 1]) {
                    column[i]++
                    column[i + 1] = 0
                    score += when (column[i]) {
                        2 -> 1 // Dois pretos mesclados (nível 1 -> 2)
                        3 -> 2 // Dois vermelhos mesclados (nível 2 -> 3)
                        4 -> 3 // Dois azuis mesclados (nível 3 -> 4)
                        else -> column[i] - 1 // Para níveis superiores
                    }
                    moved = true
                }
                i++
            }
            column.removeAll { it == 0 }
            while (column.size < 5) column.add(0)
            for (i in 0 until 5) {
                grid[i][j] = column[i]
            }
        }
        if (moved) {
            addNewDot()
            updateBestScore()
        } else {
            addNewDot()
        }
    }

    private fun moveDown() {
        var moved = false
        for (j in 0 until 5) {
            val column = (0 until 5).map { grid[it][j] }.reversed().toMutableList()
            var i = 0
            while (i < column.size - 1) {
                if (column[i] != 0 && column[i] == column[i + 1]) {
                    column[i]++
                    column[i + 1] = 0
                    score += when (column[i]) {
                        2 -> 1 // Dois pretos mesclados (nível 1 -> 2)
                        3 -> 2 // Dois vermelhos mesclados (nível 2 -> 3)
                        4 -> 3 // Dois azuis mesclados (nível 3 -> 4)
                        else -> column[i] - 1 // Para níveis superiores
                    }
                    moved = true
                }
                i++
            }
            column.removeAll { it == 0 }
            while (column.size < 5) column.add(0)
            val reversedColumn = column.reversed()
            for (i in 0 until 5) {
                grid[i][j] = reversedColumn[i]
            }
        }
        if (moved) {
            addNewDot()
            updateBestScore()
        } else {
            addNewDot()
        }
    }

    private fun updateBestScore() {
        if (score > bestScore) {
            bestScore = score
        }
    }
}