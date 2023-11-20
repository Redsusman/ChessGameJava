import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Piece {

    public enum PieceType {
        BLACK_PAWN(new ImageIcon("ChessGameJava/imageFolder/pawn.png"), "p", Color.BLACK, 0, 1),
        BLACK_KNIGHT(new ImageIcon("ChessGameJava/imageFolder/knight.png"), "n", Color.BLACK, 0, 3),
        BLACK_BISHOP(new ImageIcon("ChessGameJava/imageFolder/bishop.png"), "b", Color.BLACK, 0, 3),
        BLACK_ROOK(new ImageIcon("ChessGameJava/imageFolder/rook.png"), "r", Color.BLACK, 0, 5),
        BLACK_QUEEN(new ImageIcon("ChessGameJava/imageFolder/queen.png"), "q", Color.BLACK, 0, 9),
        BLACK_KING(new ImageIcon("ChessGameJava/imageFolder/king.png"), "k", Color.BLACK, 0, 0),
        WHITE_PAWN(new ImageIcon("ChessGameJava/imageFolder/white_pawn.png"), "p", Color.WHITE, 0, 1),
        WHITE_KNIGHT(new ImageIcon("ChessGameJava/imageFolder/white_knight.png"), "n", Color.WHITE, 0, 3),
        WHITE_BISHOP(new ImageIcon("ChessGameJava/imageFolder/white_bishop.png"), "b", Color.WHITE, 0, 3),
        WHITE_ROOK(new ImageIcon("ChessGameJava/imageFolder/white_rook.png"), "r", Color.WHITE, 0, 5),
        WHITE_QUEEN(new ImageIcon("ChessGameJava/imageFolder/white_queen.png"), "q", Color.WHITE, 0, 9),
        WHITE_KING(new ImageIcon("ChessGameJava/imageFolder/white_king.png"), "k", Color.WHITE, 0, 0);

        public ImageIcon pieceImage;
        public String flag;
        public Color color;
        public int moveCounter;
        public int value;

        PieceType(ImageIcon pieceImage, String flag, Color color, int moveCounter, int value) {
            this.pieceImage = pieceImage;
            this.flag = flag;
            this.color = color;
            this.moveCounter = moveCounter;
            this.value = value;
        }
    }

    public static int WHITE_MATERIAL = 0;
    public static int BLACK_MATERIAL = 0;
    public static int moveCounter = 0;

    public static final int[][] KNIGHT_SHIFTS = { { -2, -1 }, { -2, 1 }, { -1, 2 }, { 1, 2 }, { 2, 1 }, { 2, -1 },
            { 1, -2 },
            { -1, -2 } };
    public static final int[][] BISHOP_SHIFTS = { { -1, 1 }, { -1, -1 }, { 1, -1 }, { 1, 1 } };
    public static final int[][] ROOK_SHIFTS = { { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 0 } };
    public static final int[][] QUEEN_SHIFTS = { { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 0 }, { -1, 1 }, { -1, -1 },
            { 1, -1 }, { 1, 1 } };

    public static List<int[]> storedMoves = new ArrayList<>();

    public Piece(PieceType type) {

    }

    public static void moveTo(JButton initialSquare, JButton targetSquare, Board board) {
        if (initialSquare.getComponentCount() != 0) {
            initialSquare.remove(0);
            board.loadPieceToBoardd(board.getFlagIndivSquare(initialSquare),
                    board.returnIndexes(targetSquare, board)[1],
                    board.returnIndexes(targetSquare, board)[0], board);
            board.getFlagIndivSquare(targetSquare).moveCounter++;
            initialSquare.revalidate();
            targetSquare.revalidate();
            initialSquare.repaint();
            targetSquare.repaint();
            board.moveCounter++;
        }
    }

    public static void moveToAgain(JButton initialSquare, JButton targetSquare, Board board) {
        if (isCapture(initialSquare, targetSquare, board)) {
            // try {
            // playSound("imageFolder/capture.wav");
            // } catch (LineUnavailableException | IOException |
            // UnsupportedAudioFileException e) {
            // e.printStackTrace();
            // }
            updateMaterial(board.getFlagIndivSquare(targetSquare), board.getFlagIndivSquare(initialSquare).color);
            targetSquare.remove(0);
            board.capturedPieces.push(new Pair<>(board.returnIndexes(targetSquare, board)[1],
                    board.returnIndexes(targetSquare, board)[0], board.getFlagIndivSquare(targetSquare)));
            moveTo(initialSquare, targetSquare, board);
            
        } else if (targetSquare.getComponentCount() == 0) {
            // try {
            // playSound("imageFolder/351518__mh2o__chess_move_on_alabaster.wav");
            // } catch (LineUnavailableException | IOException |
            // UnsupportedAudioFileException e) {
            // e.printStackTrace();
            // }
            moveTo(initialSquare, targetSquare, board);
        } else {
            return;
        }
    }

    public static void unmakeMove(Board board) {
        if (board.storedMoves.size() - 1 >= 1) {
            if (board.capturedPieces.isEmpty()) {
                moveToAgain(board.storedMoves.pop(), board.storedMoves.pop(), board);
            } else {
                // moveToAgain(board.storedMoves.pop(), board.storedMoves.pop(), board);
                moveTo(board.storedMoves.pop(), board.storedMoves.pop(), board);
                Pair<Integer, Integer, Piece.PieceType> capturedPieceData = board.capturedPieces.pop();
                board.loadPieceToBoardd(capturedPieceData.getD(), capturedPieceData.getT(), capturedPieceData.getE(),
                        board);
                
            }
        }
    }

    public static boolean isCapture(JButton initialSquare, JButton targetSquare, Board board) {
        if (initialSquare.getComponentCount() != 0 && targetSquare.getComponentCount() != 0) {
            boolean ret = board.getFlagIndivSquare(initialSquare).color != board.getFlagIndivSquare(targetSquare).color
                    ? true
                    : false;
            return ret;
        } else {
            return false;
        }
    }

    public static void playSound(String file)
            throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
    }

    public static List<JButton> generateMoves(JButton initialSquare, Board board) {
        List<JButton> possibleMoves = new LinkedList<>();
        String flag = board.getFlagIndivSquare(initialSquare).flag;
        Color color = board.getFlagIndivSquare(initialSquare).color;

        if (initialSquare.getComponentCount() != 0) {
            switch (flag) {
                case "p":
                    int pawnDirection = getColor(initialSquare, board) == Color.WHITE && !board.isBoardFlipped ? -1 : 1;
                    int pawnDirectionSecond = getColor(initialSquare, board) == Color.WHITE && !board.isBoardFlipped
                            ? -2
                            : 2;
                    int[][] diagonalDirection = getColor(initialSquare, board) == Color.WHITE && !board.isBoardFlipped
                            ? new int[][] { { -1, -1 }, { -1, 1 } }
                            : new int[][] { { 1, -1 }, { 1, 1 } };
                    // possibleMoves.add(
                    // squares[returnIndexes(initialSquare)[0]
                    // + pawnDirectionSecond][returnIndexes(initialSquare)[1]]);

                    for (int i = 0; i < diagonalDirection.length; i++) {
                        try {
                            if (isCapture(initialSquare,
                                    board.squares[board.returnIndexes(initialSquare, board)[0]
                                            + diagonalDirection[i][0]][board.returnIndexes(initialSquare, board)[1]
                                                    + diagonalDirection[i][1]],
                                    board)) {
                                possibleMoves.add(board.squares[board.returnIndexes(initialSquare, board)[0]
                                        + diagonalDirection[i][0]][board.returnIndexes(initialSquare, board)[1]
                                                + diagonalDirection[i][1]]);
                            } else {
                                continue;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                    if (!isCapture(initialSquare, board.squares[board.returnIndexes(initialSquare, board)[0]
                            + pawnDirection][board.returnIndexes(initialSquare, board)[1]], board)) {
                        possibleMoves.add(
                                board.squares[board.returnIndexes(initialSquare, board)[0]
                                        + pawnDirection][board.returnIndexes(initialSquare, board)[1]]);
                    }
                    try {
                        if (board.returnIndexes(initialSquare, board)[0] == 6
                                || board.returnIndexes(initialSquare, board)[0] == 1 && !isCapture(initialSquare,
                                        board.squares[board.returnIndexes(initialSquare, board)[0]
                                                + pawnDirectionSecond][board.returnIndexes(initialSquare, board)[1]],
                                        board)) {
                            if (board.squares[board.returnIndexes(initialSquare, board)[0]
                                    + pawnDirection][board.returnIndexes(initialSquare, board)[1]]
                                    .getComponentCount() != 1) {
                                possibleMoves.add(
                                        board.squares[board.returnIndexes(initialSquare, board)[0]
                                                + pawnDirectionSecond][board.returnIndexes(initialSquare, board)[1]]);
                            } else {
                                return new ArrayList<JButton>();
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        ;
                    }
                    break;
                case "n":
                    for (int i = 0; i < KNIGHT_SHIFTS.length; i++) {
                        try {
                            possibleMoves.add(board.squares[board.returnIndexes(initialSquare, board)[0]
                                    + KNIGHT_SHIFTS[i][0]][board.returnIndexes(initialSquare, board)[1]
                                            + KNIGHT_SHIFTS[i][1]]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                    break;
                case "b":
                    for (int[] direction : BISHOP_SHIFTS) {
                        int x = board.returnIndexes(initialSquare, board)[0];
                        int y = board.returnIndexes(initialSquare, board)[1];
                        for (int j = 0; j < 25; j++) {
                            x += direction[0];
                            y += direction[1];
                            try {
                                JButton diagonalSquare = board.squares[x][y];
                                if (diagonalSquare.getComponentCount() == 0) {
                                    possibleMoves.add(diagonalSquare);

                                } else if (isCapture(initialSquare, diagonalSquare, board)) {
                                    possibleMoves.add(diagonalSquare);
                                    break;
                                } else {
                                    break;
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                continue;
                            }
                        }
                    }
                    break;
                case "r":
                    for (int[] direction : ROOK_SHIFTS) {
                        int x = board.returnIndexes(initialSquare, board)[0];
                        int y = board.returnIndexes(initialSquare, board)[1];
                        for (int j = 0; j < 25; j++) {
                            x += direction[0];
                            y += direction[1];
                            try {
                                JButton orthogonalSquare = board.squares[x][y];
                                if (orthogonalSquare.getComponentCount() == 0) {
                                    possibleMoves.add(orthogonalSquare);
                                } else if (isCapture(initialSquare, orthogonalSquare, board)) {
                                    possibleMoves.add(orthogonalSquare);
                                    break;
                                } else {
                                    break;
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                continue;
                            }
                        }
                    }
                    break;
                case "q":
                    for (int[] direction : QUEEN_SHIFTS) {
                        int x = board.returnIndexes(initialSquare, board)[0];
                        int y = board.returnIndexes(initialSquare, board)[1];
                        for (int j = 0; j < 25; j++) {
                            x += direction[0];
                            y += direction[1];
                            try {
                                JButton multiDirectionalSquare = board.squares[x][y];
                                if (multiDirectionalSquare.getComponentCount() == 0) {
                                    ;
                                    possibleMoves.add(multiDirectionalSquare);
                                } else if (isCapture(initialSquare, multiDirectionalSquare, board)) {
                                    possibleMoves.add(multiDirectionalSquare);
                                    break;
                                } else {
                                    break;
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                continue;
                            }
                        }
                    }
                    break;
                case "k":
                    for (int i = 0; i < QUEEN_SHIFTS.length; i++) {
                        try {
                            possibleMoves.add(board.squares[board.returnIndexes(initialSquare, board)[0]
                                    + QUEEN_SHIFTS[i][0]][board.returnIndexes(initialSquare, board)[1]
                                            + QUEEN_SHIFTS[i][1]]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                    break;
                default:
                    break;

            }
        }
        return possibleMoves;
    }

    public static void finalMoveGeneration(JButton initialSquare, JButton targetSquare, Board board) {
        List<JButton> moves = generateMoves(initialSquare, board);
        List<JButton> enemyPieces = board.getPieceSquares(getOppositeColor(initialSquare, board), board);
        boolean moveMatch = false;
        if (board.getFlagIndivSquare(initialSquare).flag != "k") {
            if (!isCheck(board.getFlagIndivSquare(initialSquare).color, board)) {
                for (JButton move : moves) {
                    if (Arrays.equals(board.returnIndexes(move, board),
                            board.returnIndexes(targetSquare, board)) == true) {
                        moveMatch = true;
                        Piece.moveToAgain(initialSquare, targetSquare, board);
                    } else {
                    }
                }
            } else {
                List<JButton> enemyCheckMoves = getSquaresBetweenCheckAndAttacker(
                        board.getFlagIndivSquare(initialSquare).color, board);
                for (JButton move : moves) {
                    if (Arrays.equals(board.returnIndexes(move, board),
                            board.returnIndexes(targetSquare, board)) == true
                            && enemyCheckMoves.stream().anyMatch(checkSquare -> Arrays
                                    .equals(board.returnIndexes(checkSquare, board),
                                            board.returnIndexes(targetSquare, board)) == true) == true) {
                        moveMatch = true;
                        Piece.moveToAgain(initialSquare, targetSquare, board);
                    }
                }
            }
        } else {
            for (JButton piece : enemyPieces) {
                List<JButton> enemyMoves = generateMoves(piece, board);
                for (JButton enemyMove : enemyMoves) {
                    moves.removeIf(move -> Arrays.equals(board.returnIndexes(move, board),
                            board.returnIndexes(enemyMove, board)));
                }
            }
            for (JButton move : moves) {
                if (Arrays.equals(board.returnIndexes(move, board), board.returnIndexes(targetSquare, board)) == true) {
                    moveMatch = true;
                    Piece.moveToAgain(initialSquare, targetSquare, board);
                }
            }
        }
        if (moveMatch == false) {
            return;
        }

    }

    public static List<JButton> getSquaresBetweenCheckAndAttacker(Color color, Board board) {
        JButton kingSquare = board.getKingSquare(color, board);
        List<JButton> attackingPieces = getAttackingPieceGivingCheck(color, board);
        List<JButton> attackingPieceMoves = generateMoves(attackingPieces.get(0), board);
        List<JButton> ret = new ArrayList<>();
        for (int[] direction : QUEEN_SHIFTS) {
            int x = board.returnIndexes(kingSquare, board)[0];
            int y = board.returnIndexes(kingSquare, board)[1];
            for (int j = 0; j < 25; j++) {
                x += direction[0];
                y += direction[1];
                try {
                    JButton multiDirectionalSquare = board.squares[x][y];
                    if (multiDirectionalSquare.getComponentCount() == 0
                            && attackingPieceMoves.stream().anyMatch(move -> move == multiDirectionalSquare) == true) {
                        ret.add(multiDirectionalSquare);
                    } else if (isCapture(kingSquare, multiDirectionalSquare, board)) {
                        ret.add(multiDirectionalSquare);
                        break;
                    } else {
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        }
        ret.add(attackingPieces.get(0));

        return ret;
    }

    public static void castle(JButton kingSquare, JButton rookSquare, Board board) {
        boolean castled = false;
        try {
            if (board.getFlagIndivSquare(kingSquare).flag == "k" && board.getFlagIndivSquare(rookSquare).flag == "r"
                    && board.getFlagIndivSquare(kingSquare).moveCounter == 0
                    && board.getFlagIndivSquare(rookSquare).moveCounter == 0
                    && board.getFlagIndivSquare(kingSquare).color == board.getFlagIndivSquare(rookSquare).color) {
                List<JButton> rookPath = generateMoves(rookSquare, board);
                List<JButton> kingMoves = generateMoves(kingSquare, board);
                List<JButton> oppositeColorPieces = board.getPieceSquares(getOppositeColor(kingSquare, board), board);
                for (JButton attackingPiece : oppositeColorPieces) {
                    List<JButton> enemyMoves = generateMoves(attackingPiece, board);
                    if (enemyMoves.stream().anyMatch(rookPath::contains) != true
                            && kingMoves.stream().anyMatch(rookPath::contains) == true) {
                        castled = true;
                    } else {
                        castled = false;
                        break;
                    }
                }
            }
            if (castled == true) {
                switch (board.returnIndexes(rookSquare, board)[1]) {
                    case 7:
                        try {
                            playSound("imageFolder/351518__mh2o__chess_move_on_alabaster.wav");
                        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                            e.printStackTrace();
                        }
                        moveTo(kingSquare, board.squares[board.returnIndexes(kingSquare, board)[0]][board
                                .returnIndexes(kingSquare, board)[1] + 2], board);
                        moveTo(rookSquare, board.squares[board.returnIndexes(rookSquare, board)[0]][board
                                .returnIndexes(rookSquare, board)[1] - 2], board);
                        board.moveCounter = board.moveCounter - 1;
                        break;
                    default:
                        try {
                            playSound("imageFolder/351518__mh2o__chess_move_on_alabaster.wav");
                        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                            e.printStackTrace();
                        }
                        moveTo(kingSquare, board.squares[board.returnIndexes(kingSquare, board)[0]][board
                                .returnIndexes(kingSquare, board)[1] - 2], board);
                        moveTo(rookSquare, board.squares[board.returnIndexes(rookSquare, board)[0]][board
                                .returnIndexes(rookSquare, board)[1] + 3], board);
                        board.moveCounter = board.moveCounter - 1;
                        break;
                }
                if (castled == false) {
                    return;
                }
            } else {
                return;
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    public static void enPassent() {

    }

    public static boolean isCheck(Color color, Board board) {
        List<JButton> enemyPieces = board.getPieceSquares(getOppositeColorColor(color), board);
        JButton kingSquare = board.getKingSquare(color, board);
        boolean isInCheck = false;

        for (JButton enemyPiece : enemyPieces) {
            List<JButton> moves = generateMoves(enemyPiece, board);
            if (moves.stream()
                    .anyMatch(move -> Arrays.equals(board.returnIndexes(move, board),
                            board.returnIndexes(kingSquare, board)) == true)) {
                isInCheck = true;
                break;
            } else {
                isInCheck = false;
            }
        }

        return isInCheck;
    }

    public static Color getOppositeColor(JButton square, Board board) {
        if (square.getComponentCount() != 0) {
            if (board.getFlagIndivSquare(square).color == Color.WHITE) {
                return Color.BLACK;
            } else {
                return Color.WHITE;
            }
        } else {
            return null;
        }
    }

    public static List<JButton> getAttackingPieceGivingCheck(Color color, Board board) {
        JButton kingSquare = board.getKingSquare(color, board);
        List<JButton> enemyPieces = board.getPieceSquares(getOppositeColorColor(color), board);
        List<JButton> ret = new ArrayList<>();

        for (JButton piece : enemyPieces) {
            List<JButton> moves = generateMoves(piece, board);
            if (moves.stream()
                    .anyMatch(move -> Arrays.equals(board.returnIndexes(move, board),
                            board.returnIndexes(kingSquare, board)) == true)) {
                ret.add(piece);
            }
        }
        return ret;
    }

    public static Color getOppositeColorColor(Color color) {
        Color ret = color == Color.WHITE ? Color.BLACK : Color.WHITE;
        return ret;
    }

    public static Color getColor(JButton square, Board board) {
        if (square.getComponentCount() != 0) {
            return board.getFlagIndivSquare(square).color;
        } else {
            return null;
        }
    }

    public static void updateMaterial(Piece.PieceType type, Color color) {
        if (color == Color.WHITE) {
            WHITE_MATERIAL += type.value;
        } else {
            BLACK_MATERIAL += type.value;
        }
    }

    public int getMaterialCount(Color color) {
        int ret = color == Color.WHITE ? WHITE_MATERIAL : BLACK_MATERIAL;
        return ret;
    }

    public static void orderedMoves(JButton initialSquare, JButton targetSquare, Board board) {

        if (board.moveCounter % 2 == 0) {

            if (board.getFlagIndivSquare(initialSquare).color == Color.WHITE) {
                Piece.finalMoveGeneration(initialSquare, targetSquare, board);
            } else {
                return;
            }
        } else {
            if (board.getFlagIndivSquare(initialSquare).color == Color.BLACK) {
                Piece.finalMoveGeneration(initialSquare, targetSquare, board);
            } else {
                return;
            }
        }
    }

    public static void promote(Board board, JButton square) {
        if (square.getComponentCount() != 0) {
            if (board.getFlagIndivSquare(square).flag == "p" && board.returnIndexes(square, board)[0] == 7
                    || board.returnIndexes(square, board)[0] == 0) {
                Color color = board.getFlagIndivSquare(square).color;
                if (color == Color.WHITE) {
                    board.loadPieceToBoardd(Piece.PieceType.WHITE_QUEEN, board.returnIndexes(square, board)[0],
                            board.returnIndexes(square, board)[1], board);
                } else {
                    board.loadPieceToBoardd(Piece.PieceType.BLACK_QUEEN, board.returnIndexes(square, board)[0],
                            board.returnIndexes(square, board)[1], board);
                }
                square.remove(0);
            } else {
                return;
            }
        } else {
            return;
        }
    }

    public static boolean isGameOver(Board board, Color color) {
        boolean gameIsOver = false;
        // deal with draws first; stalemate.
        if (WHITE_MATERIAL >= 36 && BLACK_MATERIAL >= 36) {
            List<JButton> pieces = board.getPieces(board);
            for (JButton piece : pieces) {
                if (board.getFlagIndivSquare(piece).flag.compareTo("p") == 0) {
                    gameIsOver = false;
                    break;
                } else {
                    gameIsOver = true;
                    break;
                }
            }
        } else {
            return false;
        }

        // checkmates.
        JButton kingSquare = color == Color.WHITE ? board.getKingSquare(Color.WHITE, board)
                : board.getKingSquare(Color.BLACK, board);
        List<JButton> kingMoves = Piece.generateMoves(kingSquare, board);
        List<JButton> enemyPieces = board.getPieceSquares(Piece.getOppositeColor(kingSquare, board), board);
        if (isCheck(Piece.getColor(kingSquare, board), board) && allKingSquaresCutOff(board, color)) {
            gameIsOver = true;
        } else {
            gameIsOver = false;
        }

        return gameIsOver;
    }

    public static boolean allKingSquaresCutOff(Board board, Color color) {
        JButton kingSquare = board.getKingSquare(color, board);
        List<JButton> enemyPieces = board.getPieceSquares(getOppositeColor(kingSquare, board), board);
        List<JButton> kingMoves = generateMoves(kingSquare, board);
        if (kingMoves.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static List<JButton> getAllMoves(Board board, Color color) {
        List<JButton> pieces = board.getPieceSquares(color, board);
        List<JButton> ret = new LinkedList<>();

        for (var piece : pieces) {
            List<JButton> moves = generateMoves(piece, board);
            for (var move : moves) {
                ret.add(move);
            }
        }
        return ret;
    }

}
