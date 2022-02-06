package tictactoe;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;

public class Main {

    private static final int FIELD_NULL = 0;
    private static final int FIELD_X = 10;
    private static final int FIELD_0 = 100;
    private static int[][] matrix = new int[3][3];
    private static int moves = 0;
    private static int ourSeed;
    private static int oppSeed;

    public Main() {

    }

    public static int checkState() {
        int diag = 0, diag2 = 0;

        for (int i = 0; i < 3; i++) {
            diag += matrix[i][i];
            diag2 += matrix[i][2 - i];
        }

        if (diag == FIELD_0*3 || diag == FIELD_X*3) {
            return diag;
        }
        if (diag2 == FIELD_0*3 || diag2 == FIELD_X*3) {
            return diag2;
        }

        for (int i = 0; i < 3; i++) {

            int check_i = 0, check_j = 0;

            for (int j = 0; j < 3; j++) {
                check_i += matrix[i][j];
                check_j += matrix[j][i];
            }
            if (check_i == FIELD_0*3 || check_i == FIELD_X*3) {
                return check_i;
            }
            if (check_j == FIELD_0*3 || check_j == FIELD_X*3) {
                return check_j;
            }
        }

        return moves;
    }

    public static boolean checkWin(int state) {
        switch (state) {
            case FIELD_X*3:
                System.out.println("X wins");
                return true;
            case FIELD_0*3:
                System.out.println("O wins");
                return true;
            case 9:
                System.out.println("Draw");
                return true;
            default:
                //System.out.println("Game not finished");
                return false;
        }
    }

    public static void print() {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                switch (matrix[i][j]) {
                    case FIELD_X:
                        System.out.print("X ");
                        break;
                    case FIELD_0:
                        System.out.print("O ");
                        break;
                    default:
                        System.out.print("  ");
                }
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    public static void enter(int field) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the coordinates: ");

        try {
            int row = in.nextInt();
            int col = in.nextInt();
            moveUser(row, col, field);
        } catch (InputMismatchException e) {
            System.out.println("You should enter numbers!");
            enter(field);
        }
    }

    public static void moveUser(int row, int col, int field) {
        if (row < 1 || row > 3 || col < 1 || col > 3) {
            System.out.println("Coordinates should be from 1 to 3!");
            enter(field);
        } else if (matrix[row - 1][col - 1] == FIELD_NULL) {
            matrix[row - 1][col - 1] = field;
        } else {
            System.out.println("This cell is occupied! Choose another one!");
            enter(field);
        }
    }

    public static void moveBotEasy(int field) {
        Random random = new Random();

        int row = random.nextInt(3);
        int col = random.nextInt(3);

        if (matrix[row][col] == FIELD_NULL) {
            matrix[row][col] = field;
        } else {
            moveBotEasy(field);
        }
    }

    public static void moveBotMedium(int own) {
        int enemy = (own == FIELD_X) ? FIELD_0 : FIELD_X;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[i][j] == FIELD_NULL) {
                    matrix[i][j] = own;
                    if (checkWin(checkState())) {
                        return;
                    } else {
                        matrix[i][j] = FIELD_NULL;
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[i][j] == FIELD_NULL) {
                    matrix[i][j] = enemy;
                    if (checkWin(checkState())) {
                        matrix[i][j] = own;
                        return;
                    } else {
                        matrix[i][j] = FIELD_NULL;
                    }
                }
            }
        }

        moveBotEasy(own);
    }

    public static void moveBotHard(int field) {
        ourSeed = field;
        oppSeed = (field == FIELD_X) ? FIELD_0 : FIELD_X;
        int[] bestMove = miniMax(0, ourSeed);
        matrix[bestMove[0]][bestMove[1]] = ourSeed;
    }

    public static int[] miniMax(int depth, int seed) {
        int bestScore = (seed == ourSeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int[] bestMove = {-1, -1, bestScore};

        if (moves == 9) {
            bestScore = evaluateMove();
        } else {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == FIELD_NULL) {
                        matrix[i][j] = seed;
                        moves++;
                        if (seed == ourSeed) {
                            int score = miniMax(depth + 1, oppSeed)[2];
                            if (score > bestScore) {
                                bestScore = score;
                                bestMove[0] = i;
                                bestMove[1] = j;
                            }
                        } else {
                            int score = miniMax(depth + 1, ourSeed)[2];
                            if (score < bestScore) {
                                bestScore = score;
                                bestMove[0] = i;
                                bestMove[1] = j;
                            }
                        }
                        matrix[i][j] = FIELD_NULL;
                        moves--;
                    }
                }
            }
        }

        bestMove[2] = bestScore;
        return bestMove;
    }

    public static int evaluateMove() {
        if (checkState() == ourSeed*3) {
            return 10;
        } else if (checkState() == oppSeed*3) {
            return -10;
        } else {
            return 0;
        }
    }

    public static void move(String p, int field) {
        final String USER = "user";
        final String EASY = "easy";
        final String MEDIUM = "medium";

        if (p.equals(USER)) {
            enter(field);
        } else if (p.equals(EASY)){
            System.out.println("Making move level \"easy\"");
            moveBotEasy(field);
        } else if (p.equals(MEDIUM)){
            System.out.println("Making move level \"medium\"");
            moveBotMedium(field);
        } else {
            System.out.println("Making move level \"hard\"");
            moveBotHard(field);
        }
        moves++;
    }

    public void init() {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the cells: ");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                try {
                    int x = System.in.read();
                    char s = (char) x;
                    switch (s) {
                        case 'X':
                            matrix[i][j] = FIELD_X;
                            moves++;
                            break;
                        case 'O':
                            matrix[i][j] = FIELD_0;
                            moves++;
                            break;
                        default:
                            matrix[i][j] = FIELD_NULL;
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }
    }

    public static void fight(String p1, String p2) {
        do {
            if (moves % 2 == 0) {
                move(p1, FIELD_X);
            } else {
                move(p2, FIELD_0);
            }
            print();
        } while (!checkWin(checkState()));
    }

    public static void main(String[] args) {
        final String START = "start";
        final String EXIT = "exit";
        Scanner in = new Scanner(System.in);
        new Main();

        while (true) {
            System.out.print("Input command: ");
            String command = in.nextLine();
            String[] commands = command.split(" ");

            if (commands[0].equals(START) && commands.length == 3) {
                print();
                fight(commands[1], commands[2]);
            } else if (commands[0].equals(EXIT)) {
                break;
            } else {
                System.out.println("Bad parameters!");
            }
        }
    }
}