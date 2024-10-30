import java.util.*;


public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int perceptionMode = scan.nextInt();
        int keymakerX = scan.nextInt();
        int keymakerY = scan.nextInt();

        // GameBoard gameBoardAStar = new GameBoard(true, keymakerX, keymakerY);

        GameBoard gameBoardBacktracking = new GameBoard(false, keymakerX, keymakerY);
        int[] dest = {keymakerX, keymakerY};
        int[] src = {0, 0};

        Algorithms.Backtracking(gameBoardBacktracking, src, dest);
        int minPath = gameBoardBacktracking.getMinPath();
        if (minPath == Integer.MAX_VALUE) {
            System.out.println("e -1");
        } else {
            System.out.println("e " + minPath);
        }

        //Algorithms.A_star(gameBoard, src, dest);

    }
}

class Cell {
    private int parent_i, parent_j;
    private int i, j;
    private String perception;
    private boolean visited = false;
    private int distance = Integer.MAX_VALUE;

    Cell(int i, int j) {
        this.parent_i = 0;
        this.parent_j = 0;
        this.i = i;
        this.j = j;
        this.perception = null;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void setParent_i(int parent_i) {
        this.parent_i = parent_i;
    }

    public void setParent_j(int parent_j) {
        this.parent_j = parent_j;
    }

    public void setPerception(String perception) {
        this.perception = perception;
    }

    public String getPerception() {
        return perception;
    }

    public int getParent_i() {
        return parent_i;
    }

    public int getParent_j() {
        return parent_j;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}

class AStarCell extends Cell implements Comparable<AStarCell> {
    int finalCost, currentCost, estimatedCost;

    AStarCell(int i, int j) {
        super(i, j);
        this.finalCost = Integer.MAX_VALUE;
        this.currentCost = Integer.MAX_VALUE;
        this.estimatedCost = Integer.MAX_VALUE;
    }

    public void setCurrentCost(int currentCost) {
        this.currentCost = currentCost;
    }

    public void setEstimatedCost(int estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public void setFinalCost(int finalCost) {
        this.finalCost = finalCost;
    }

    public int getCurrentCost() {
        return currentCost;
    }

    public int getFinalCost() {
        return finalCost;
    }

    public int getEstimatedCost() {
        return estimatedCost;
    }

    @Override
    public int compareTo(AStarCell other) {
        int result = Double.compare(this.finalCost, other.finalCost);
        if (result == 0) {
            return Double.compare(this.estimatedCost, other.estimatedCost);
        }
        return result;
    }
}

class GameBoard {
    private static final int SIZE = 9;
    private Cell[][] boardCells;
    private Cell initialNode;
    private Cell finalNode;
    private int minPath = Integer.MAX_VALUE;

    public GameBoard(boolean isAStar, int keymakerX, int keymakerY) {

        if (isAStar) {
            boardCells = new AStarCell[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    boardCells[i][j] = new AStarCell(i, j);
                }
            }
            // set -1 as parent of initial node
            boardCells[0][0].setParent_i(-1);
            boardCells[0][0].setParent_j(-1);
        } else {
            boardCells = new Cell[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    boardCells[i][j] = new Cell(i, j);
                }
            }
        }

        initialNode = boardCells[0][0];
        finalNode = boardCells[keymakerX][keymakerY];
        updateCell(keymakerX, keymakerY, "goal");
    }

    public void updateCell(int x, int y, String perception) {
        if (perception.equals("K")) {
            boardCells[x][y].setPerception("goal");
        } else if (perception.equals("B")) {
            boardCells[x][y].setPerception(null);
        } else {
            boardCells[x][y].setPerception("blocked");
        }
    }

    public Cell[][] getBoardCells() {
        return boardCells;
    }

    public void setMinPath(int minPath) {
        this.minPath = minPath;
    }

    public int getMinPath() {
        return minPath;
    }
}



class Algorithms {

    private static int[][] moveDirections = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public static void Backtracking(GameBoard gameBoard, int[] src, int[] dest) {
        backtracking(gameBoard, src[0], src[1], dest, 0);
    }

    public static void readMapResponse(GameBoard gameBoard) {
        Scanner scanner = new Scanner(System.in);
        int dangerCount = scanner.nextInt();
        for (int k = 0; k < dangerCount; k++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            String perception = scanner.next();
            gameBoard.updateCell(x, y, perception);
        }
    }

    private static boolean isValid(GameBoard gameBoard, int col, int row) {
        return col >= 0 && col < gameBoard.getBoardCells().length && row >= 0 && row < gameBoard.getBoardCells().length
                && (gameBoard.getBoardCells()[col][row].getPerception() == null
                || gameBoard.getBoardCells()[col][row].getPerception().equals("goal"))
                && !gameBoard.getBoardCells()[col][row].isVisited();
    }

    private static void backtracking(GameBoard gameBoard, int col, int row, int[] dest, int depth) {
        Cell current = gameBoard.getBoardCells()[col][row];

        System.out.println("m " + current.getI() + " " + current.getJ());
        readMapResponse(gameBoard);


        // base
        if (current.getI() == dest[0] && current.getJ() == dest[1]) {
            // update minPath
            gameBoard.setMinPath(Math.min(gameBoard.getMinPath(), depth));
            current.setVisited(false);
            return;
        }

        // body

        // update -- don't visit cells that have been visited with the shortest path
        if (gameBoard.getMinPath() <= depth || depth >= current.getDistance()) {
            return;
        }


        // mark cell as visited
        current.setVisited(true);
        current.setDistance(Math.min(gameBoard.getMinPath(), depth));

        // check all possible directions
        for (int[] direction : moveDirections) {
            int nextCol = col + direction[0];
            int nextRow = row + direction[1];
            if (isValid(gameBoard, nextCol, nextRow)) {
                backtracking(gameBoard, nextCol, nextRow, dest, depth + 1);
                System.out.println("m " + current.getI() + " " + current.getJ());
                readMapResponse(gameBoard);
            }
        }


        // unmark cell
        current.setVisited(false);
    }

}


