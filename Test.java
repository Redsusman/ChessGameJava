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
            Board board = new Board("board", "rnbq1rk1/ppp1ppbp/3p1np1/8/2PPP3/2N2N2/PP2BPPP/R1BQK2R", true);
            Thread aiThread = new Thread(() -> {
                Engine engine = new Engine();
                    Pair<JButton, JButton, Double> pair = engine.minimax(board, 4, Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY, true);
                    Piece.finalMoveGeneration(pair.getT(), pair.getE(), board);
                    System.out.println("initialSquare: " + Arrays.toString(board.returnIndexes(pair.getT(), board)) + " targetSquare: " + Arrays.toString(board.returnIndexes(pair.getE(),board)));
            });
            aiThread.start();
            
         
        });
    }
}
