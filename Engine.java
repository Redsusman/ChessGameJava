import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.SwingWorker;

public class Engine {
    public Engine() {
    }

    public Pair<JButton, JButton, Double> minimax(Board board, int depth, double alpha, double beta,
            boolean maximizingPlayer) {
        if (depth == 0) {
            return new Pair<>(null, null, evaluate(board));
        }
        List<JButton> pieces = board.getPieces(board);
        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            JButton bestMove = null;
            JButton correspondingPiece = null;
            for (var piece : pieces) {
                List<JButton> moves = Piece.generateMoves(piece, board);
                for (var move : moves) {
                    board.storedMoves.push(piece);
                    board.storedMoves.push(move);
                    Piece.finalMoveGeneration(piece, move, board);
                    double v = minimax(board, depth - 1, alpha, beta, false).d;
                    // maxEval = Math.max(maxEval, v);
                    if (v > maxEval) {
                        maxEval = v;
                        correspondingPiece = piece;
                        bestMove = move;
                    }
                    Piece.unmakeMove(board);
                    alpha = Math.max(alpha, v);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

            return new Pair<>(correspondingPiece, bestMove, maxEval);
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            JButton bestMove = null;
            JButton correspondingPiece = null;
            for (var piece : pieces) {
                List<JButton> moves = Piece.generateMoves(piece, board);
                for (var move : moves) {
                    board.storedMoves.push(piece);
                    board.storedMoves.push(move);
                    Piece.finalMoveGeneration(piece, move, board);
                    double v = minimax(board, depth - 1, alpha, beta, true).d;
                    // minEval = Math.min(minEval, v);
                    if (v < minEval) {
                        minEval = v;
                        bestMove = move;
                        correspondingPiece = piece;
                    }
                    Piece.unmakeMove(board);
                    beta = Math.min(beta, v);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return new Pair<>(correspondingPiece, bestMove, minEval);
        }
    }

    public Pair<Move, Double, ?> minimaxSingleForLoop(Board board, int depth, double alpha, double beta,
            boolean maximizingPlayer) {
        List<Move> moves = Piece.getMoves(board);

        if (depth == 0) {
            return new Pair<>(null, evaluate(board), null);
        }
        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            Move bestMove = null;

            for (var move : moves) {
                board.storedMoves.push(move.getInitialSquare());
                board.storedMoves.push(move.getTargetSquare());
                Piece.move(move, board);
                double v = minimaxSingleForLoop(board, depth - 1, alpha, beta, false).e;
                if (v > maxEval) {
                    maxEval = v;
                    bestMove = move;
                }
                Piece.unmakeMove(board);
                alpha = Math.max(alpha, v);
                if (beta <= alpha) {
                    break;
                }
            }
            return new Pair<>(bestMove, maxEval, null);
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            Move bestMove = null;
            for (var move : moves) {
                board.storedMoves.push(move.getInitialSquare());
                board.storedMoves.push(move.getTargetSquare());
                Piece.move(move, board);
                double v = minimaxSingleForLoop(board, depth - 1, alpha, beta, true).e;
                if (v < minEval) {
                    minEval = v;
                    bestMove = move;
                }
                Piece.unmakeMove(board);
                beta = Math.min(beta, v);
                if (beta <= alpha) {
                    break;
                }
            }

            return new Pair<>(bestMove, minEval, null);
        }
    }

    public List<Move> moveOrderMoves(List<Move> moves, Board board) {
        TreeMap<Integer, Move> sorter = new TreeMap<>(Comparator.reverseOrder());
            int i = 0;
            for (var move : moves) {
                if (Piece.isCapture(move.getInitialSquare(), move.getTargetSquare(), board)) {
                    int value = board.getFlagIndivSquare(move.getTargetSquare()).value;
                    sorter.put(value, move);
                } else {
                    sorter.put(i--, move);
                }
            }
        List<Move> moveOrderedList = new LinkedList<>(sorter.values());
        return moveOrderedList;
    }

    public void randomMoves(Board board) {
        List<JButton> blackPieces = board.getPieceSquares(Color.BLACK, board);
        List<JButton> blackMoves = Piece.getAllMoves(board, Color.BLACK);
        JButton kingSquare = board.getKingSquare(Color.BLACK, board);
        List<JButton> kingMoves = Piece.generateMoves(kingSquare, board);
        while (board.moveCounter % 2 != 0) {
            if (!Piece.isCheck(Color.BLACK, board)) {
                Random random = new Random();
                int randomPieceIndex = random.nextInt(0, blackPieces.size() - 1);
                int randomMoveIndex = random.nextInt(0, blackMoves.size() - 1);
                Piece.finalMoveGeneration(blackPieces.get(randomPieceIndex), blackMoves.get(randomMoveIndex), board);
            } else {
                Random random = new Random();
                int randomMoveIndex = random.nextInt(0, kingMoves.size() - 1);
                Piece.finalMoveGeneration(kingSquare, blackMoves.get(randomMoveIndex), board);
            }
        }
    }

    public double evaluate(Board board) {
        double whiteVal = 0;
        double blackVal = 0;
        List<JButton> whitePieces = board.getPieceSquares(Color.WHITE, board);
        List<JButton> blackPieces = board.getPieceSquares(Color.BLACK, board);

        if (Piece.WHITE_MATERIAL > Piece.BLACK_MATERIAL) {
            whiteVal += Math.abs(Piece.WHITE_MATERIAL - Piece.BLACK_MATERIAL);
        } else {
            blackVal -= Math.abs(Piece.BLACK_MATERIAL - Piece.WHITE_MATERIAL);
        }
        // encourage pieces to control center.
        for (int i = 2; i <= 5; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.squares[i][j].getComponentCount() != 0 && i == 4 && i == 5) {
                    whiteVal += 0.1;
                } else if (board.squares[i][j].getComponentCount() != 0 && i == 3 && i == 2) {
                    blackVal -= 0.1;
                }

            }
        }
        // generally, pieces with more moves have more space than a close position, this
        // should benefit the winning side.
        for (var whitePiece : whitePieces) {
            List<JButton> moves = Piece.generateMoves(whitePiece, board);
            for (var move : moves) {
                whiteVal += 0.05;
            }
        }
        for (var blackPiece : blackPieces) {
            List<JButton> moves = Piece.generateMoves(blackPiece, board);
            for (var move : moves) {
                blackVal -= 0.05;
            }
        }
        double whiteTransformedEval = whiteVal / 2;
        double blackTransformedEval = blackVal / 2;
        return whiteTransformedEval + blackTransformedEval;
    }

    public int getNumPositions(Board board, int depth, boolean isMaximizingPlayer) {
        if (depth == 0) {
            return 1;
        }
        int numPositions = 0;
        List<Move> moves = Piece.getMoves(board);
        for (var move : moves) {

            Piece.move(move, board);
            board.storedMoves.push(move.getInitialSquare());
            board.storedMoves.push(move.getTargetSquare());
            Piece.unmakeMove(board);
            numPositions += getNumPositions(board, depth - 1, isMaximizingPlayer);

        }

        if (depth == 0) {
            return 1;
        }

        return numPositions;
    }

    public double evaluateTwice(Board board) {
        double whiteVal = 0;
        double blackVal = 0;
        if (Piece.WHITE_MATERIAL > Piece.BLACK_MATERIAL) {
            whiteVal += Math.abs(Piece.WHITE_MATERIAL - Piece.BLACK_MATERIAL);
        } else {
            blackVal -= Math.abs(Piece.BLACK_MATERIAL - Piece.WHITE_MATERIAL);
        }

        return (whiteVal / 2) + (blackVal / 2);
    }

}
