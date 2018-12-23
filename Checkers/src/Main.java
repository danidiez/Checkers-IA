import Items.*;
import Logics.*;
import java.util.Scanner;

public class Main {
    private static int type;
    private static Scanner reader = new Scanner(System.in);

        public static void main (String[] args) {
            getInput();
            Player player1 = selectPlayer(type,"Player1",1);
            getInput();
            Player player2 = selectPlayer(type,"Player2",2);
            new Board(player1,player2);
        }

    private static void getInput() {
        System.out.println("Player1:\n1.Player\n2.Bot");
        type =Integer.parseInt(reader.nextLine());
    }

    private static Player selectPlayer(int type,String name,int color) {
        switch (type) {
            case 1:
                return new Human(name, color);
            case 2:
                String heuristic = selectHeuristic();
                System.out.println("level of ia: (between 1-6)");
                int lvl = Integer.parseInt(reader.nextLine());
                System.out.println("Algoritm: \n1.MiniMax\n2.AlphaBeta");
                int algoritm = Integer.parseInt(reader.nextLine());
                return new Ia(name,color,heuristic,lvl,12,0,(algoritm == 1));
        }
        return null;
    }

    private static String selectHeuristic() {
        System.out.println("select Heuristic:\n1.checks+queens\n2.BestPosition\n3.optimal");
        int option = Integer.parseInt(reader.nextLine());
        switch (option){
            case 1:
                return "checks+queens";
            case 2:
                return "bestPosition";
            case 3:
                return "optimal";
        }
        return null;
    }

}
