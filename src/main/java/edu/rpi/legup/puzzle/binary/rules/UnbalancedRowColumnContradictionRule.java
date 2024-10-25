package edu.rpi.legup.puzzle.binary.rules;

import edu.rpi.legup.model.gameboard.Board;
import edu.rpi.legup.model.gameboard.PuzzleElement;
import edu.rpi.legup.model.rules.ContradictionRule;
import edu.rpi.legup.puzzle.binary.BinaryBoard;
import edu.rpi.legup.puzzle.binary.BinaryCell;
import edu.rpi.legup.puzzle.binary.BinaryType;
import java.util.Set;

public class UnbalancedRowColumnContradictionRule extends ContradictionRule {

    private final String NO_CONTRADICTION_MESSAGE =
            "Does not contain a contradiction at this index";
    private final String INVALID_USE_MESSAGE = "Row or column must have a value in each cell";

    public UnbalancedRowColumnContradictionRule() {
        super(
                "BINA-CONT-0002",
                "Unbalanced Row/Column",
                "Each row or column must contain an equal number of zeros and ones",
                "edu/rpi/legup/images/binary/rules/UnbalancedRowColumnContradictionRule.png");
    }

    /**
     * Checks whether the transition has a contradiction at the specific puzzleElement index using
     * this rule
     *
     * @param board board to check contradiction
     * @param puzzleElement equivalent puzzleElement
     * @return null if the transition contains a contradiction at the specified puzzleElement,
     *     otherwise error message
     */
    @Override
    public String checkContradictionAt(Board board, PuzzleElement puzzleElement) {
        BinaryBoard binaryBoard = (BinaryBoard) board;

        BinaryCell cell = (BinaryCell) binaryBoard.getPuzzleElement(puzzleElement);
        Set<BinaryCell> row = binaryBoard.getRowCells(cell.getLocation().y);

        int size = row.size();
        int rowNumZeros = 0;
        int rowNumOnes = 0;

        for (BinaryCell item : row) {
            if (item.getType() == BinaryType.ZERO) {
                rowNumZeros++;
            } else if (item.getType() == BinaryType.ONE) {
                rowNumOnes++;
            }
        }

        // if there are too many zeros or ones in this row
        if (rowNumZeros > size / 2 || rowNumOnes > size / 2) {
            return null;
        }

        Set<BinaryCell> col = binaryBoard.getColCells(cell.getLocation().x);

        size = col.size();
        int colNumZeros = 0;
        int colNumOnes = 0;

        for (BinaryCell item : col) {
            if (item.getType() == BinaryType.ZERO) {
                colNumZeros++;
            } else if (item.getType() == BinaryType.ONE) {
                colNumOnes++;
            }
        }

        // if there are too many zeros or ones in this column
        if (colNumZeros > size / 2 || colNumOnes > size / 2) {
            return null;
        }

        return super.getNoContradictionMessage() + ": " + this.NO_CONTRADICTION_MESSAGE;
    }
}
