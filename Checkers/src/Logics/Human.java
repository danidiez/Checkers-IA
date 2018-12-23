package Logics;

import Items.Board;


public class Human extends Player{
    public Human(String name,int color){
        super.playerName = name;
        super.color = color;
        super.checks = 12;
        super.queens = 0;
    }
    @Override
    public int[] estimateNextMove(Board board) {
        return null;
    }
}
