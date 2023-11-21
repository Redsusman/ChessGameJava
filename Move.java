import javax.swing.JButton;

public class Move {
    private JButton initialSquare;
    private JButton targetSquare;
    public Move(JButton initialSquare, JButton targetSquare) {
        this.initialSquare = initialSquare;
        this.targetSquare = targetSquare;
    }

    public JButton getInitialSquare() {
        return initialSquare;
    }

    public JButton getTargetSquare() {
        return targetSquare;
    }
}
