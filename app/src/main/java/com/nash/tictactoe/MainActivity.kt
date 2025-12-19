package com.nash.tictactoe

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
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

val INITIAL_BOARD_STATE = Array(9) { "" }
val INITIAL_PLAYER = PlayerType.X

class MainActivity : AppCompatActivity() {
  private lateinit var playerTurnTextView: TextView
  private lateinit var gridLayout: GridLayout
  private lateinit var boardCells: Array<TextView>
  private lateinit var restartButton: Button

  private var currentPlayer = INITIAL_PLAYER
  private var boardState = INITIAL_BOARD_STATE
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

    this.playerTurnTextView = findViewById(R.id.playerTurnTextView)
    this.gridLayout = findViewById(R.id.gridLayout)
    this.boardCells = Array(gridLayout.childCount) { TextView(this) }
    this.restartButton = findViewById(R.id.restart_button)
    this.restartButton.setOnClickListener { restartGame() }

    initializeBoard()
    replacePlayerTurnText("Player Turn: ${currentPlayer.label}")
  }

  private fun restartGame() {
    this.boardState = Array(9) { "" }
    this.gameActive = true
    this.currentPlayer = PlayerType.X
    this.restartButton.visibility = View.GONE

    replacePlayerTurnText("Player Turn: ${currentPlayer.label}")

    for (i in 0 until gridLayout.childCount) {
      val cellTextView = gridLayout.getChildAt(i) as TextView

      cellTextView.text = ""
      setCellState(cellTextView, CellState.NORMAL)
      cellTextView.isEnabled = true
    }
  }

  private fun switchPlayers() {
    this.currentPlayer = if (currentPlayer == PlayerType.X) PlayerType.O else PlayerType.X

    replacePlayerTurnText("Player Turn: ${currentPlayer.label}")
  }

  private fun handleCellClick(cellTextView: TextView, clickedIndex: Int) {
    setCellState(cellTextView, CellState.SELECTED)

    this.boardState[clickedIndex] = currentPlayer.label
    cellTextView.text = currentPlayer.label
  }

  private fun onCellClicked(cellTextView: TextView) {
    if (!gameActive) return

    val clickedIndex = cellTextView.tag.toString().toInt()

    if (boardState[clickedIndex].isNotEmpty()) return

    handleCellClick(cellTextView, clickedIndex)
    val winningCombination = checkWin()

    if (winningCombination != null) handleWin(winningCombination)
    else if (checkDraw()) handleDraw()
    else switchPlayers()
  }

  private fun changeCellsOnWin(winningCombination: List<Int>) {
    for (index in winningCombination) setCellState(boardCells[index], CellState.WINNING)

    val nonWinningCells = boardCells.withIndex().filter { it.index !in winningCombination }

    for ((_, cell) in nonWinningCells) setCellState(cell, CellState.DISABLED)
  }

  private fun handleWin(winningCombination: List<Int>) {
    changeCellsOnWin(winningCombination)
    replacePlayerTurnText("Player ${currentPlayer.label} Wins!")

    this.gameActive = false
    this.restartButton.visibility = View.VISIBLE
  }

  private fun checkWin() = winningCombinations
    .firstOrNull { (a, b, c) ->
      boardState[a].takeIf { it.isNotEmpty() }?.let { it == boardState[b] && it == boardState[c] }
        ?: false
    }

  private fun handleDraw() {
    replacePlayerTurnText("It's a Draw!")

    this.gameActive = false
    this.restartButton.visibility = View.VISIBLE
  }

  private fun checkDraw() = boardState.all { it.isNotEmpty() }

  private fun replacePlayerTurnText(updatedPlayerTurnText: String) {
    this.playerTurnTextView.text = updatedPlayerTurnText
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


