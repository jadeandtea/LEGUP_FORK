package puzzles.sudoku;

import model.gameboard.Board;
import model.Puzzle;
import model.gameboard.ElementData;
import model.gameboard.GridCell;
import ui.Selection;

import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Sudoku extends Puzzle
{

    /**
     * Sudoku Constructor
     */
    public Sudoku()
    {
        super();
    }

    /**
     * Initializes the game board
     */
    @Override
    public void initializeBoard()
    {
        currentBoard = new SudokuBoard();
        ArrayList<ElementData> data = new ArrayList<>();
        for(int i = 0; i < 81; i++)
        {
            data.add(new GridCell(0, new Point(i / 9, i % 9))
            {
                @Override
                public GridCell copy()
                {
                    return null;
                }
            });
        }
    }

    /**
     * Generates a random puzzle based on the difficulty
     *
     * @param difficulty level of difficulty (1-10)
     *
     * @return board of the random puzzle
     */
    @Override
    public Board generatePuzzle(int difficulty)
    {
        return null;
    }

    /**
     * Determines if the puzzle was solves correctly
     *
     * @return true if the board was solved correctly, false otherwise
     */
    @Override
    public boolean isPuzzleComplete()
    {
        return false;
    }

    /**
     * Determines if the current board is a valid state
     *
     * @param board board to check for validity
     *
     * @return true if board is valid, false otherwise
     */
    @Override
    public boolean isValidBoardState(Board board)
    {
        return false;
    }

    /**
     * Callback for when the board data changes
     *
     * @param board the board that has changed
     */
    @Override
    public void onBoardChange(Board board)
    {

    }

    /**
     * Callback for when the tree selection changes
     *
     * @param newSelection
     */
    @Override
    public void onTreeSelectionChange(ArrayList<Selection> newSelection)
    {

    }

    /**
     * Imports the board using the file stream
     *
     * @param fileStream
     *
     * @return
     */
    @Override
    public Board importPuzzle(FileInputStream fileStream)
    {
        return null;
    }
}