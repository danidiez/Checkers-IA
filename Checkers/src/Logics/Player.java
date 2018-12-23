package Logics;
import Items.*;

public abstract class Player {
    int checks;
    int color;
    int queens;
    double maxTime = 0;
    private boolean move = true;
    String playerName;

    /** Return the name of the player */
    public String getPlayerName(){return playerName;}
    /** return true if the player hasn't more checks*/
    public int getChecks(){return checks;}
    /** return the color of player*/
    public int getColor(){return color;}
    /** sub one check to this player */
    public void subCheck(){ checks --;}
    /** add one queen to this player */
    public void addQueen(){ queens ++; checks --; }
    /** The player win if he obtain 4 queens  */
    public int getQueens() { return queens; }
    /** Return the next move of the IA (Abstract definition)*/
    public abstract int[] estimateNextMove(Board board);
    /** Return if the player cant Move*/
    public boolean cantMove(){ return !move; }
    /** Move Setter*/
    public void setCanMove(boolean bol){ move = bol; }
    /** Return the max time spend to move*/
    public Double getMaxTime() {return maxTime;}
}
