package Items;

import Logics.Human;
import Logics.Ia;
import Logics.Player;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class Board extends JPanel implements MouseListener {
    private final int BLACK = 1,WHITE = 2;
    private final int size = 720;
    private final int tileSize = size/8;
    private int lastRow, lastCol,tries;
    private BufferedImage crownImage = null;
    private Player currentPlayer, nextPlayer, winner;
    private Stack<int[]> moveList = new Stack<>();
    private int[][] nextMove = new int[8][8];
    private int[][] checks;
    private boolean selected = false;
    private boolean isOver = false;
    private boolean isGame;
    private int whiteChecks,whiteQueens;
    private int blackChecks,blackQueens;

    /**Private Functions*/
    private Board(int[][] ch){
        checks = new int[8][8];
        whiteChecks = 0;
        blackChecks = 0;
        blackQueens = 0;
        whiteQueens = 0;
        for(int x=0;x<8;x++){
            for (int y = 0; y<8 ; y++){
                if(x == 0 && ch[x][y] == BLACK) blackQueens++;
                else if (x ==7 && ch[x][y] == WHITE) whiteQueens++;
                else if (ch[x][y] == BLACK) blackChecks++;
                else if(ch[x][y] == WHITE) whiteChecks++;
                checks[x][y] = ch[x][y];
            }
        }
        isGame = false;
    }
    //Configure the window display
    private void window(Board board){
        JFrame frame = new JFrame();
        frame.setSize(size, size);
        frame.setIconImage(crownImage);
        frame.setLocationRelativeTo(null);
        frame.pack();
        Insets insets = frame.getInsets();
        int frameLeftBorder = insets.left;
        int frameRightBorder = insets.right;
        int frameTopBorder = insets.top;
        int frameBottomBorder = insets.bottom;
        frame.setPreferredSize(new Dimension(size + frameLeftBorder + frameRightBorder, size + frameBottomBorder + frameTopBorder));
        frame.setMaximumSize(new Dimension(size + frameLeftBorder + frameRightBorder, size + frameBottomBorder + frameTopBorder));
        frame.setMinimumSize(new Dimension(size + frameLeftBorder + frameRightBorder, size + frameBottomBorder + frameTopBorder));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(this);
        frame.requestFocus();
        frame.setVisible(true);
        frame.add(board);
    }
    // initialize the board with the starting checks position
    private void initializeBoard(){
        checks =new int[][]{{0,2,0,2,0,2,0,2},
                            {2,0,2,0,2,0,2,0},
                            {0,2,0,2,0,2,0,2},
                            {0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0},
                            {1,0,1,0,1,0,1,0},
                            {0,1,0,1,0,1,0,1},
                            {1,0,1,0,1,0,1,0}};
    }
    // Draw a check inside the Board Display
    private void drawPiece(int row, int col, Graphics g, Color color){
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(color);
        g.fillOval((col*tileSize)+2, (row*tileSize)+2, tileSize-4, tileSize-4);
    }
    // Return the player with @Param: colorPlayer
    private Player getPlayer(int colorPlayer) {
        if (currentPlayer.getColor() == colorPlayer) return currentPlayer;
        return nextPlayer;
    }
    // Display a Message of the winner (or Draw) over the Board display
    private void gameOverDisplay(Graphics g) {
        Font small = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metric = getFontMetrics(small);
        g.setColor(Color.blue);
        g.setFont(small);
        if(winner == null) g.drawString("DRAW!", (size - metric.stringWidth("DRAW!")) / 2, size / 2);
        else g.drawString(winner.getPlayerName()+" WIN!", (size - metric.stringWidth(winner.getPlayerName()+" WIN!")) / 2, size / 2);
    }
    // Check if the (row,col) position is a King
    private void checkKing(int row, int col) {
        if (checks[row][col]== BLACK && row == 0) {
            if(isGame) currentPlayer.addQueen();
            else blackQueens ++;
        }
        else if (checks[row][col]== WHITE && row == 7) {
            if(isGame) currentPlayer.addQueen();
            else whiteQueens ++;
        }
    }
    // Delete a check if is jumped by a enemy check
    private void removePiece(int row, int col, int storedRow, int storedCol){
        int pieceRow = -1;
        int pieceCol = -1;
        if(col > storedCol && row > storedRow){
            pieceRow = row-1;
            pieceCol = col-1;
        }
        if(col > storedCol && row < storedRow){
            pieceRow = row+1;
            pieceCol = col-1;
        }
        if(col < storedCol && row > storedRow){
            pieceRow = row-1;
            pieceCol = col+1;
        }
        if(col < storedCol && row < storedRow){
            pieceRow = row+1;
            pieceCol = col+1;
        }
        checks[pieceRow][pieceCol] = 0;
        if(isGame) nextPlayer.subCheck();
    }
    //Clear the parameters of the turn and update the board display (repaint)
    private void resetTurn(){
        lastRow = 0;
        lastCol = 0;
        selected = false;
        nextMove = new int[8][8];
        moveList = new Stack<>();
        repaint();
        System.out.println(toString());
    }
    //Swap the player, if the player is a Ia, it also manage the move of this player. (Ia vs Ia makes this function recursive until the end of the game)
    private void swapPlayer(){
        resetTurn();
        tries = 0;
        Player temp = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = temp;
        if(!gameOver()){
            if(currentPlayer.getClass() == Ia.class) {
                int[] result = currentPlayer.estimateNextMove(this.getClone());
                if (result[0] == 0 && result[1] == 0 && result[2] == 0 && result[3] == 0) currentPlayer.setCanMove(false);
                else {
                    currentPlayer.setCanMove(true);
                    int row = result[0];
                    int col = result[1];
                    int nextRow = result[2];
                    int nextCol = result[3];
                    makeMove(row, col, nextRow, nextCol);
                }
                swapPlayer();
            }
        }
        else {
            getWinner();
            if(winner == null) System.out.println("DRAW!");
            else {
                System.out.println(winner.getPlayerName() + " WIN!");
                if (winner.getClass() == Ia.class)
                    System.out.println("Max time used to move: " + winner.getMaxTime() + " seconds");
            }
            isOver = true;
            }

    }
    // Return the available Movements for the check in (row,col)
    private void getAvailablePlays(int row, int col) {
        if (currentPlayer.getColor() == checks[row][col]) {
            moveList = getMoveOptions(currentPlayer.getColor(),row, col);
            if(moveList.size()==0){
                tries++;
                if(tries>=5) {
                    currentPlayer.setCanMove(false);
                    swapPlayer();
                }
            }
            else {
                for (int[] move : moveList) {
                    if (move != null) nextMove[move[0]][move[1]] = 1;
                }
                selected = true;
            }
        }
        repaint();
    }
    // return true if the movement is inside the board
    private boolean isLegalPos(int row, int col){
        return !(row < 0 || row > 7 || col < 0 || col > 7);
    }
    // Calculate the winner of the game and set the variable Winner with the player with more points
    private void getWinner() {
        int nWhite = 0, nBlack = 0;
        if(nextPlayer.getChecks() == 0 && nextPlayer.getQueens() == 0){
            winner = currentPlayer;
        }
        else if(currentPlayer.getQueens() == nextPlayer.getQueens()){
            for (int i = 1; i<8; i++){
                for (int x = 0; x<8; x++) {
                    if (checks[i][x] == 1) nBlack++;
                    if (checks[7 - i][x] == 2) nWhite++;
                }
                if (nWhite>nBlack) winner =  getPlayer(WHITE);
                else if(nBlack>nWhite)winner = getPlayer(BLACK);
                nWhite = 0;
                nBlack = 0;
            }
        }
        else if (currentPlayer.getQueens() > nextPlayer.getQueens()){ winner = currentPlayer;}
        else{winner = nextPlayer; }
    }
    // Return the movement options (jump or not)
    private int[] getMove(int color, int row, int col,int nextRow,int nextCol) {
        int enemyColor;
        if (color == 1) {enemyColor = 2;}
        else {enemyColor = 1;}

        if (isLegalPos(nextRow, nextCol)) {
            if (checks[nextRow][nextCol] == 0) {
                nextMove[nextRow][nextCol] =1;
                return new int[]{nextRow, nextCol};
            }
            if (checks[nextRow][nextCol]== enemyColor && isLegalPos(nextRow+(nextRow-row),nextCol+(nextCol-col))){
                if(checks[nextRow+(nextRow-row)][nextCol+(nextCol-col)]==0){
                    nextMove[nextRow+(nextRow-row)][nextCol+(nextCol-col)] =1;
                    return new int[]{nextRow+(nextRow-row),nextCol+(nextCol-col)};
                }
            }
        }
        return null;
    }

    /** Public functions**/
    //Board constructor for estimate a game with Algoritms inside Ia class (Clone)
    public Board(Player p1, Player p2) {
        try {
            crownImage = ImageIO.read(new File("crown.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        checks = new int[8][8];
        currentPlayer = p2;
        nextPlayer = p1;
        whiteQueens = 0;
        whiteChecks = 12;
        blackQueens = 0;
        blackChecks = 12;
        winner = null;
        isGame = true;
        window(this);
        initializeBoard();
        swapPlayer();
        repaint();

    }
    Board getClone() { return new Board(checks); }

    //return the checks positions (board)
    public int[][] getChecks(){return checks;}

    //Update the Board display
    public void paint(Graphics g){
        super.paintComponent(g);
        for(int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if((row % 2 == 0 && col % 2 == 0) || (row % 2 != 0 && col % 2 != 0)){
                    g.setColor(Color.gray.brighter()); //pale yellow
                    g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
                else{
                    g.setColor(Color.gray.darker()); //dark brown
                    g.fillRect(col * tileSize, row  * tileSize, tileSize, tileSize);
                    if ((currentPlayer.getColor()==checks[row][col])) {
                        g.setColor(Color.darkGray.darker());
                        g.fillRect(col * tileSize, row  * tileSize, tileSize, tileSize);
                    }
                    if (this.nextMove[row][col] == 1) {
                        g.setColor(Color.getHSBColor(98,70,43));
                        g.fillRect(col * tileSize, row  * tileSize, tileSize, tileSize);
                    }
                    if (this.checks[row][col] != 0) {
                        if (this.checks[row][col] == WHITE) {
                            drawPiece(row, col, g, Color.yellow.brighter());
                            if (row == 7)
                                g.drawImage(crownImage, (col * tileSize)+6, (row  * tileSize) + 6, tileSize - 12, tileSize - 12, null);
                        }
                        else{
                            drawPiece(row, col, g, Color.black);
                            if (row == 0)
                                g.drawImage(crownImage, (col * tileSize)+6, (row  * tileSize) + 6, tileSize - 12, tileSize - 12, null);
                        }
                    }
                }
            }
        }
        if(isOver && isGame){
            gameOverDisplay(g);
        }
    }

    // Return true if the game is over. It can be for actual game display or for estimation inside the Ia algoritm
    public boolean gameOver(){
        if(isGame) return (currentPlayer.getChecks() == 0 || nextPlayer.getChecks() == 0 || currentPlayer.getQueens() == 4 || nextPlayer.getQueens() == 4 || (currentPlayer.cantMove() && nextPlayer.cantMove()));
        return getNumChecks(WHITE) == 0 || getNumChecks(BLACK) == 0 || getNumQueens(WHITE) == 4 || getNumQueens(BLACK) == 4;
    }
    // Capture the mouse click for Player turn and manage the movement selected
    public void mousePressed(java.awt.event.MouseEvent evt) {
        int col,row;
        if(!gameOver()){
            if(currentPlayer.getClass()== Human.class) {
                col = (evt.getX() - 8) / tileSize;
                row = (evt.getY() - 30) / tileSize;
                System.out.println(col + " " + row);
                if (!selected && checks[row][col] != 0 && (currentPlayer.getColor() == checks[row][col])) {
                    lastCol = col;
                    lastRow = row;
                    getAvailablePlays(row, col);
                } else if (selected && nextMove[row][col] == 1) {
                    makeMove(lastRow, lastCol, row, col);
                    resetTurn();
                    swapPlayer();
                } else resetTurn();
            }
        }
    }
    // Makes a move from (row,col) to (nextRow,nextCol)
    void makeMove(int row, int col, int nextRow, int nextCol) {
        int temp = checks[row][col];
        checks[nextRow][nextCol] = temp;
        checks[row][col] = 0;
        if (Math.abs(nextRow - row) == 2){
            removePiece(nextRow, nextCol,row, col);
        }
        checkKing(nextRow,nextCol);
    }
    // return a list of the movement options inside the actual board
    Stack<int[]> getMoveOptions(int color, int row, int col) {
        int[] result;
        Stack<int[]> moves = new Stack<>();
        if (color == BLACK) {
            result=getMove(color,row,col,row-1,col-1);
            if(result !=null)moves.push(result);
            result=getMove(color,row,col,row-1,col+1);
            if(result !=null)moves.push(result);
        }
        if (color == WHITE) {
            result=getMove(color,row,col,row+1,col-1);
            if(result !=null)moves.push(result);
            result=getMove(color,row,col,row+1,col+1);
            if(result !=null)moves.push(result);
        }
        return moves;
    }
    //Override functions (Unused but needed by mousePressed function)
    @Override
    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    //toString function. It display the board and time if is Ia
    public String toString() {
        StringBuilder result= new StringBuilder();
        for(int x=0;x<8;x++){
            for(int y=0;y<8;y++){
                result.append(checks[x][y]).append(",");
            }
            result.append("\n");
        }
        return result.toString();
    }

    // Return a rate of the position of the checks with the @Param: color
    public double getCenterRate(int color) {
        double rate = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if(checks[x][y] == color){
                    if(y == 3 || y == 4) rate += 6;
                    else if (y == 2 || y == 5) rate += 3;
                    else if( y == 1 || y == 6) rate +=1;
                }
            }
        }
        return rate/(getNumChecks(color)+getNumQueens(color));
    }
    // Return the number of checks with @Param:color
    public int getNumChecks(int color){
        if(color == 1) return blackChecks;
        else return whiteChecks;
    }
    // Return the number of Queens with @Param:color
    public int getNumQueens(int color) {
        if(color == 1) return blackQueens;
        else return whiteQueens;
    }
}

