package com.nash.tictactoe

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout

enum class PlayerType(val label: String) {
  X("❌"),
  O("⚫")
}

enum class CellState {
  NORMAL,
  SELECTED,
  WINNING,
  DISABLED
}

class MainActivity : AppCompatActivity() {
  private lateinit var playerTurnTextView: TextView
  private lateinit var gridLayout: GridLayout
  private lateinit var boardCells: Array<TextView>
  private var currentPlayer = PlayerType.X
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

  private fun initializeBoard() {
    for (i in 0 until gridLayout.childCount) {
      val cellTextView = gridLayout.getChildAt(i) as TextView

      cellTextView.setOnClickListener { onCellClicked(it as TextView) }
      setCellState(cellTextView, CellState.NORMAL)

      this.boardCells[i] = cellTextView
    }
  }

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

    initializeBoard()
    replacePlayerTurnText("Player Turn: ${currentPlayer.label}")
  }

  private fun switchPlayers() {
    this.currentPlayer =
      if (currentPlayer == PlayerType.X) PlayerType.O else PlayerType.X

    replacePlayerTurnText("Player Turn: ${currentPlayer.label}")
  }

  private fun onCellClicked(cellTextView: TextView) {
    if (!gameActive) return

    setCellState(cellTextView, CellState.SELECTED)
    val clickedIndex = cellTextView.tag.toString().toInt()

    if (boardState[clickedIndex].isEmpty()) {
      boardState[clickedIndex] = currentPlayer.label
      cellTextView.text = currentPlayer.label

      val winningCombination = checkWin()

      if (winningCombination != null) {
        handleWin(winningCombination)
        replacePlayerTurnText("Player ${currentPlayer.label} Wins!")

        gameActive = false
      } else if (checkDraw()) {
        replacePlayerTurnText("It's a Draw!")

        gameActive = false
      } else {
        switchPlayers()
      }
    }
  }

  private fun handleWin(winningCombination: List<Int>) {
    for (index in winningCombination) setCellState(boardCells[index], CellState.WINNING)

    for ((_, cell) in boardCells.withIndex().filter { it.index !in winningCombination })
      setCellState(cell, CellState.DISABLED)
  }

  private fun checkWin() = winningCombinations
    .firstOrNull { (a, b, c) ->
      boardState[a].takeIf { it.isNotEmpty() }?.let { it == boardState[b] && it == boardState[c] }
        ?: false
    }

  private fun checkDraw() = boardState.all { it.isNotEmpty() }

  private fun replacePlayerTurnText(updatedPlayerTurnText: String) {
    playerTurnTextView.text = updatedPlayerTurnText
  }

  private fun setCellState(cellTextView: TextView, cellState: CellState) {
    cellTextView.background = GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      cornerRadius = 16f
      setStroke(6, Color.BLACK)

      when (cellState) {
        CellState.NORMAL -> setColor(Color.TRANSPARENT)

        CellState.SELECTED -> {
          cellTextView.isEnabled = false

          setColor(Color.LTGRAY)
        }

        CellState.WINNING -> setColor(Color.parseColor("#FF4CAF50"))

        CellState.DISABLED -> setColor(Color.parseColor("#6E7786E6"))
      }
    }
  }
}


