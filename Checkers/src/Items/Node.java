package Items;

import java.util.Stack;

public class Node {
    private int row;
    private int col;
    private int nextRow;
    private int nextCol;
    private Double value;
    private Board board;
    private Node topNode;
    private Stack<Node> childs = new Stack<>();


    private Node(int row, int col, int nextRow, int nextCol, Board board) {
        this.col = col;
        this.row = row;
        this.nextCol = nextCol;
        this.nextRow = nextRow;
        this.board = board;
    }
    public Node(Double resultVal, Node node) {
        this.value = resultVal;
        topNode = node;
    }
    public Node(Board board) {
        this.board = board;
    }

    /** Return the Top Node (the result)*/
    public Node getTopNode(){return topNode; }
    /** Return the Heuristic value of this Node*/
    public Double getValue(){return value; }
    /** Return the Board of this Node*/
    public Board getBoard(){return this.board;}
    /** Return the actual column (movement)*/
    public int getCol() {return col; }
    /** Return the row column (movement)*/
    public int getRow() {return row; }
    /** Return the column to move(movement)*/
    public int getNextCol() {return nextCol; }
    /** Return the row to move(movement)*/
    public int getNextRow() {return nextRow; }
    /** return the childs of this node (movements)*/
    public Stack<Node> getChilds(int color, boolean max) {
        int[][] checks = board.getChecks();
        Stack<int[]> moveOptions;
        if (!max){
            if(color == 1) color = 2;
            else color = 1;
        }
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (checks[row][col] == color) {
                    moveOptions = this.board.getMoveOptions(color,row, col);
                    if (!moveOptions.isEmpty()) {
                        for (int[] move : moveOptions) {
                            {
                                Board nextMove = this.board.getClone();
                                nextMove.makeMove(row, col, move[0], move[1]);
                                childs.push(new Node(row, col, move[0], move[1], nextMove));
                            }
                        }
                    }
                }
            }
        }
        if(childs.isEmpty())childs.push(this);
        return childs;
    }
}
