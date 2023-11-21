import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Test {
    public static void main(String[] args) {
        // Create and show the Swing GUI on the EDT
        SwingUtilities.invokeLater(() -> {
           Board board = new Board("table", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", true);
            Thread aiThread = new Thread(() -> {
                Engine engine = new Engine();
                    Pair<Move, Double, ?> pair = engine.minimaxSingleForLoop(board, 2, Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY, true);
                    // // Piece.finalMoveGeneration(pair.getT(), pair.getE(), board);
                    // // System.out.println("initialSquare: " + Arrays.toString(board.returnIndexes(pair.getT(), board)) + " targetSquare: " + Arrays.toString(board.returnIndexes(pair.getE(),board)));
                    // System.out.println(engine.getNumPositions(board, 2, false));
            });
            aiThread.start();
            
         
        });
    }
}
