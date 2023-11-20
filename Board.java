
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.NodeList;

public class Board {

    public JButton[][] squares;
    public int[] squareIndex;
    public ArrayList<JButton> clickedMoves;
    public LinkedList<JButton> storedMoves;
    public LinkedList<Pair<Integer, Integer, Piece.PieceType>> capturedPieces;
    public boolean isBoardFlipped;
    public String name;
    public String fen;
    public boolean setVisible;
    public int moveCounter;
    public Engine engine;

    // public Board(String fen) {
    // this.fen = fen;
    // }

    public Board(String name, String fen, boolean setVisible) {
        this.fen = fen;
        this.name = name;
        this.setVisible = setVisible;
        this.squares = new JButton[8][8];
        this.squareIndex = new int[1];
        this.clickedMoves = new ArrayList<>();
        this.isBoardFlipped = false;
        this.storedMoves = new LinkedList<>();
        this.capturedPieces = new LinkedList<>();
        this.moveCounter = 0;
        this.engine = new Engine();
        init(fen, setVisible, this, name, engine);
    }

    private static void init(String fen, boolean setVisible, Board board, String name, Engine engine) {
        JFrame frame = new JFrame(name);
        GridLayout layout = new GridLayout(8, 8);
        JPanel panel = new JPanel(layout);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int storedRow = row;
                final int storedColumn = col;
                board.squares[row][col] = new JButton();
                Color color = (row + col) % 2 == 0 ? Color.white : Color.DARK_GRAY;
                board.squares[row][col].setBackground(color);
                board.squares[row][col].addActionListener(arg0 -> {
                    board.clickedMoves.add(board.squares[storedRow][storedColumn]);
                    board.storedMoves.push(board.squares[storedRow][storedColumn]);
                    if (board.clickedMoves.get(0).getComponentCount() != 0) {
                        if (board.clickedMoves.size() - 1 >= 1) {
                            Piece.castle(board.clickedMoves.get(0), board.clickedMoves.get(1), board); 
                            Piece.orderedMoves(board.clickedMoves.get(0), board.clickedMoves.get(1), board);                 
                            board.clickedMoves.clear();
                            // System.out.println(Arrays.toString(board.capturedPieces.keySet().iterator().next()));

                        }
                    } else {
                        board.clickedMoves.clear();
                    }

                });
                panel.add(board.squares[row][col]);

            }
        }

        frame.setSize(750, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(setVisible);
        board.loadFromFen(fen, board);

    }

    public int[] returnIndex(int row, int col) {
        int[] ret = { row, col };
        return ret;
    }

    public void loadPieceToBoardd(Piece.PieceType type, int rank, int file, Board board) {
        Image scaledImage = type.pieceImage.getImage().getScaledInstance(70,
                70, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        JLabel label = new JLabel(icon);
        board.squares[file][rank].add(label);
        board.squares[file][rank].putClientProperty("pieceType", type);
    }

    public void loadPieceToBoard(Piece.PieceType type, int rank, int file, Board board) {
        Image scaledImage = type.pieceImage.getImage().getScaledInstance(70,
                70, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        JLabel label = new JLabel(icon);
        board.squares[file - 1][rank - 1].add(label);
        board.squares[file - 1][rank - 1].putClientProperty("pieceType", type);
    }

    public void loadFromFen(String fen, Board board) {
        String[] ranks = fen.split("/");
        int col = 0;
        int spaces = 0;
        Map<Character, Piece.PieceType> fenTable = new HashMap<>() {
            {
                put('p', Piece.PieceType.BLACK_PAWN);
                put('n', Piece.PieceType.BLACK_KNIGHT);
                put('b', Piece.PieceType.BLACK_BISHOP);
                put('r', Piece.PieceType.BLACK_ROOK);
                put('q', Piece.PieceType.BLACK_QUEEN);
                put('k', Piece.PieceType.BLACK_KING);
                put('P', Piece.PieceType.WHITE_PAWN);
                put('N', Piece.PieceType.WHITE_KNIGHT);
                put('B', Piece.PieceType.WHITE_BISHOP);
                put('R', Piece.PieceType.WHITE_ROOK);
                put('Q', Piece.PieceType.WHITE_QUEEN);
                put('K', Piece.PieceType.WHITE_KING);
            }
        };
        for (var rank : ranks) {
            col++;
            spaces = 0;
            for (char square : rank.toCharArray()) {
                String value = String.valueOf(square);
                if (Character.isDigit(square)) {
                    spaces += Integer.parseInt(value);
                } else {
                    spaces++;
                }
                if (Character.isLetter(square)) {
                    loadPieceToBoard(fenTable.get(square), spaces, col, board);
                } else {
                    continue;
                }
            }
        }
    }

    public Piece.PieceType getFlag(JButton[][] squares) {
        // this code determines the type of piece a square has, looping all 64 squares.
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j].getComponentCount() != 0 && squares[i][j].getComponent(0) instanceof JLabel) {
                    JLabel label = (JLabel) squares[i][j].getComponent(0);
                    Icon icon = label.getIcon();
                    for (Piece.PieceType type : Piece.PieceType.values()) {
                        if (type.pieceImage == icon) {
                            return type;
                        }
                    }
                }
            }
        }

        throw new IllegalStateException("unknown");
    }

    public Piece.PieceType getFlagIndivSquare(JButton square) {
        return (Piece.PieceType) square.getClientProperty("pieceType");
    }

    public int[] returnIndexes(JButton clickedButton, Board board) {
        int[] indexes = new int[2];
        for (int i = 0; i < board.squares.length; i++) {
            for (int j = 0; j < board.squares[i].length; j++) {
                if (board.squares[i][j] == clickedButton) {
                    indexes[0] = i;
                    indexes[1] = j;
                    return indexes;
                }
            }
        }
        return indexes;
    }

    public String reverseFen(String fen) {
        ArrayList<String> list = new ArrayList<>(List.of(fen.split("/")));
        Collections.reverse(list);
        list.forEach(rank -> {
            ArrayList<Character> charList = new ArrayList<>();
            for (var character : rank.toCharArray()) {
                charList.add(character);
            }
            Collections.reverse(charList);
            String joined = charList.stream().map(String::valueOf).collect(Collectors.joining());
            list.set(list.indexOf(rank), joined);
        });
        isBoardFlipped = true;
        return String.join("/", list);
    }

    public List<JButton> getPieceSquares(Color color, Board board) {
        List<JButton> ret = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.squares[i][j].getComponentCount() != 0) {
                    if (getFlagIndivSquare(board.squares[i][j]).color == color) {
                        ret.add(board.squares[i][j]);
                    }
                }
            }
        }
        return ret;
    }

    public JButton getKingSquare(Color color, Board board) {
        JButton ret = null;
        List<JButton> coloredPieces = color == Color.WHITE ? getPieceSquares(Color.WHITE, board)
                : getPieceSquares(Color.BLACK, board);

        for (JButton piece : coloredPieces) {
            if (getFlagIndivSquare(piece).flag == "k") {
                ret = piece;
            }
        }
        return ret;

    }

    public int getMaterialCount(Color color) {
        int ret = color == Color.WHITE ? Piece.WHITE_MATERIAL : Piece.BLACK_MATERIAL;
        return ret;
    }

    public List<JButton> getPieces(Board board) {
        List<JButton> ret = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.squares[i][j].getComponentCount() != 0) {
                    ret.add(board.squares[i][j]);
                }
            }
        }
        return ret;
    }

    public String generateFenFromPosition(Board board) {
        StringBuilder fenBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int ranks = 0;
            for (int j = 0; j < 8; j++) {
                if (board.squares[i][j].getComponentCount() == 0) {
                    ranks++;
                } else {
                    if (ranks > 0) {
                        fenBuilder.append(ranks);
                        ranks = 0;
                    }
                    String character = getFlagIndivSquare(board.squares[i][j]).color == Color.WHITE
                            ? getFlagIndivSquare(board.squares[i][j]).flag.toUpperCase()
                            : getFlagIndivSquare(board.squares[i][j]).flag;

                    fenBuilder.append(character);
                }
            }
            if (ranks > 0) {
                fenBuilder.append(ranks);
            }
            if (i < 7) {
                fenBuilder.append("/");
            }
        }

        return fenBuilder.toString();
    }

    public List<JButton> alternatingPieceList(Board board) {
        List<JButton> ret = new ArrayList<>();
        List<JButton> whitePieces = board.getPieceSquares(Color.WHITE, board);
        List<JButton> blackPieces = board.getPieceSquares(Color.BLACK, board);

        for (int i = 0, j = 0; i < whitePieces.size() && j < blackPieces.size(); i++, j++) {
            ret.add(whitePieces.get(i));
            ret.add(blackPieces.get(j));
        }
        return ret;
    }

}
