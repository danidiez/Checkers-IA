package Logics;
import Items.*;
import java.util.Stack;

public class Ia extends Player{

    private String heuristic;
    private int maxLvl;
    private boolean max, miniMax;

    public Ia(String name,int color,String heuristic,int mLvl,int checks,int queens,boolean minimax){
        super.playerName = name;
        super.color = color;
        super.checks = checks;
        super.queens = queens;
        this.heuristic = heuristic;
        this.maxLvl = mLvl;
        this.max = true;
        this.miniMax = minimax;
    }

    /** Return the next move of the IA*/
    public int[] estimateNextMove(Board b){
        int[] result = new int[4];
        Node top = new Node(b);
        long start = System.currentTimeMillis();
        if (miniMax){
            top = miniMax(top, 0,max).getTopNode();
        }
        else top = alphaBeta(top,0, max,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY).getTopNode();
        long end = System.currentTimeMillis()-start;
        double time = (double)end / 1000.0000D;
        System.out.println("time : "+time);
        if(time > maxTime) maxTime = time;
        result[0] = top.getRow();
        result[1] = top.getCol();
        result[2] = top.getNextRow();
        result[3] = top.getNextCol();
        return result;
    }
    /** miniMax Algoritm*/
    private Node miniMax(Node node, int lvl,boolean max) {
        if (node.getBoard().gameOver()) {
            if (getWinner(node ,color)==color)
                return new Node(Double.POSITIVE_INFINITY, new Node(null));
            else
                return new Node(Double.NEGATIVE_INFINITY, new Node(null));
        }
        else if (lvl == maxLvl) {
            return new Node(h(node), new Node(null));
        }
        else {
            Double resultVal;
            Node resultMove = new Node(null);

            if (max)
                resultVal = Double.NEGATIVE_INFINITY;
            else
                resultVal = Double.POSITIVE_INFINITY;

            Stack<Node> childs =node.getChilds(color,max);
            if(childs.size()!=0) {
                while (!childs.empty()) {
                    Node n = childs.pop();
                    Node result = miniMax(n, lvl + 1, !max);
                    if (max && (result.getValue() >= resultVal)) {
                        resultVal = result.getValue();
                        resultMove = n;
                    } else if (!max && (result.getValue()<=resultVal)) {
                        resultVal = result.getValue();
                        resultMove = n;
                    }
                }
            }
            return new Node(resultVal, resultMove);
        }
     }
    /** Alpha-Beta Algoritm*/
     private Node alphaBeta(Node node,int lvl,  boolean max, double alpha, double beta) {
         if (node.getBoard().gameOver()) {
             if (getWinner(node, color) == color)
                 return new Node(Double.POSITIVE_INFINITY, new Node(null));
             else
                 return new Node(Double.NEGATIVE_INFINITY, new Node(null));
         } else if (lvl == maxLvl) {
             return new Node(h(node), new Node(null));
         }
         else {
             Node resultMove = new Node(null);
             Stack<Node> childs =node.getChilds(color,max);
             if(childs.size()!=0) {
                 while (!childs.empty() && alpha < beta) {
                     Node child = childs.pop();
                     Node result = alphaBeta(child, lvl + 1, !max,alpha,beta);
                     if (max && (result.getValue() > alpha)) {
                         alpha = result.getValue();
                         resultMove = child;
                     }
                     else if (!max && (result.getValue() < beta)) {
                         beta = result.getValue();
                         resultMove = child;
                     }
                 }
             }
             if(max) return new Node(alpha, resultMove);
             else return new Node(beta,resultMove);
        }
     }
    /** Heuristic function*/
    private Double h(Node node) {
        double h = 0.0D;
        int enemyColor;
        if(color == 1) enemyColor = 2;
        else enemyColor = 1;
        switch (heuristic) {
            case "checks+queens":
                h =(node.getBoard().getNumChecks(color)+(node.getBoard().getNumQueens(color)*3))-(node.getBoard().getNumChecks(enemyColor)+(node.getBoard().getNumQueens(enemyColor)*3));
                break;
            case "bestPosition":
                h = (node.getBoard().getCenterRate(color)+node.getBoard().getNumQueens(color)*5)-(node.getBoard().getCenterRate(enemyColor)+ (node.getBoard().getNumQueens(enemyColor)*5));
                break;
            case "optimal":
                h = ((node.getBoard().getNumChecks(color))+(node.getBoard().getNumQueens(color)*3)+(node.getBoard().getCenterRate(color)/5))-((node.getBoard().getNumChecks(enemyColor))+(node.getBoard().getNumQueens(enemyColor)*3)+(node.getBoard().getCenterRate(enemyColor)/5));
                break;
        }
        return h;
    }
    /** Return the winner inside the estimation of the algoritm*/
    private int getWinner(Node node, int color) {
        int numWhite = 0, numBlack = 0, enemyColor;
        if (color == 1) enemyColor = 2;
        else enemyColor = 1;
        int[][] checks = node.getBoard().getChecks();

        if (node.getBoard().getNumChecks(enemyColor) == 0 && node.getBoard().getNumQueens(enemyColor) == 0) {
            return color;
        } else if (node.getBoard().getNumQueens(color) == node.getBoard().getNumQueens(enemyColor)) {
            for (int x = 1; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (checks[7 - x][y] == 2) numWhite++;
                    if (checks[x][y] == 1) numBlack++;
                }
                if (numWhite > numBlack) return 2;
                else if (numBlack > numWhite) return 1;
                numWhite = 0;
                numBlack = 0;
            }
        } else if (node.getBoard().getNumQueens(color) > node.getBoard().getNumQueens(enemyColor)) {
            return color;
        } else {
            return enemyColor;
        }
        return 0;
    }
}
