package com.nash.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {
  private lateinit var playerTurnTextView: TextView
  private lateinit var gridLayout: GridLayout
  private var currentPlayer = "X"
  private var boardState = Array(9) { "" }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContentView(R.layout.activity_main)

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constrainLayout)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }


    playerTurnTextView = findViewById(R.id.playerTurnTextView)
    gridLayout = findViewById(R.id.gridLayout)
  }

  private fun switchPlayers() {
    this.currentPlayer = if (this.currentPlayer == "X") "O" else "X"
  }

  fun showPlayerType(view: View) {
    val clickedPositionTextView = view as TextView
    val clickedIndex = clickedPositionTextView.tag.toString().toInt()

    if (boardState[clickedIndex].isEmpty()) {
      boardState[clickedIndex] = this.currentPlayer
      clickedPositionTextView.text = this.currentPlayer

      this.switchPlayers()
    }
  }
}