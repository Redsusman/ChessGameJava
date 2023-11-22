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
           Board board = new Board("table", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR ", true);
            Thread aiThread = new Thread(() -> {
                Engine engine = new Engine();
                    Pair<Move, Double, ?> pair = engine.minimaxSingleForLoop(board, 2, Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY, false);
                    System.out.println(Arrays.toString(board.returnIndexes(pair.getT().getInitialSquare(), board)) + " " + Arrays.toString(board.returnIndexes(pair.getT().getTargetSquare(), board)));
            });
            aiThread.start();
            
         
        });
    }
}
