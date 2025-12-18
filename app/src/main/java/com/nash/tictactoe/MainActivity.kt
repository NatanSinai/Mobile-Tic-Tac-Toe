package com.nash.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout

enum class PlayerType(val symbol: String) {
  X("❌"),
  O("⭕");
}

class MainActivity : AppCompatActivity() {
  private lateinit var playerTurnTextView: TextView
  private lateinit var gridLayout: GridLayout
  private lateinit var boardCells: Array<TextView>
  private var currentPlayer = PlayerType.X.symbol
  private var boardState = Array(9) { "" }
  private var gameActive = true
  private val winningCombinations = listOf(
    listOf(0, 1, 2),
    listOf(3, 4, 5),
    listOf(6, 7, 8),
    listOf(0, 3, 6),
    listOf(1, 4, 7),
    listOf(2, 5, 8),
    listOf(0, 4, 8),
    listOf(2, 4, 6)
  )

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
    boardCells = Array(gridLayout.childCount) { TextView(this) }

    for (i in 0 until gridLayout.childCount) {
      val cell = gridLayout.getChildAt(i) as TextView

      cell.setOnClickListener { onCellClicked(it) }
      this.boardCells[i] = cell
    }

    this.replacePlayerTurnText("Player Turn: $currentPlayer")
  }

  private fun switchPlayers() {
    this.currentPlayer =
      if (this.currentPlayer == PlayerType.X.symbol) PlayerType.O.symbol else PlayerType.X.symbol

    this.replacePlayerTurnText("Player Turn: $currentPlayer")
  }

  private fun disableCell(cell: TextView) {
    cell.isEnabled = false
    cell.alpha = 0.9f
  }

  private fun onCellClicked(view: View) {
    if (!gameActive) return

    val cell = view as TextView
    this.disableCell(cell)
    
    val clickedIndex = cell.tag.toString().toInt()

    if (boardState[clickedIndex].isEmpty()) {
      boardState[clickedIndex] = this.currentPlayer
      cell.text = this.currentPlayer

      if (checkWin()) {
        this.replacePlayerTurnText("Player $currentPlayer Wins!")

        gameActive = false
      } else if (checkDraw()) {
        this.replacePlayerTurnText("It's a Draw!")

        gameActive = false
      } else {
        switchPlayers()
      }
    }
  }

  private fun checkWin() = winningCombinations.any { (a, b, c) ->
    boardState[a].isNotEmpty() && boardState[a] == boardState[b] && boardState[a] == boardState[c]
  }

  private fun checkDraw() = boardState.all { it.isNotEmpty() }

  private fun replacePlayerTurnText(updatedPlayerTurnText: String) {
    playerTurnTextView.text = updatedPlayerTurnText
  }
}