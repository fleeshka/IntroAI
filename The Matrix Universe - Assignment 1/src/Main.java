import java.util.*;
public class Main {
    /**
     * Main method for the program. It will read in the perception mode and coordinates
     * for the keymaker, and then call the A* algorithm or Backtracking algorithm to find the shortest path
     * from the start to the keymaker.
     * It will then print out the minimum path length or -1 if no path exists.
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int perceptionMode = scan.nextInt();
        int keymakerX = scan.nextInt();
        int keymakerY = scan.nextInt();

        int[] dest = {keymakerX, keymakerY};
        int[] src = {0, 0};
        

        String[] board = new String[9];
        for (int i = 0; i < 9; i++) {
            board[i] = scan.next();
        }

        scan.close();


        // A* Algorithm
        GameBoard gameBoardAStar = new GameBoard(true, keymakerX, keymakerY, board);
        Algorithms.A_star(gameBoardAStar, src, dest);


        // Backtracking Algorithm
        
        GameBoard gameBoardBacktracking = new GameBoard(false, keymakerX, keymakerY);

        Algorithms.Backtracking(gameBoardBacktracking, src, dest);
        int minPath = gameBoardBacktracking.getMinPath();
        if (minPath == Integer.MAX_VALUE) {
            System.out.println("e -1");
        } else {
            System.out.println("e " + minPath);
        }
    }

    public int runAStar(int perceptionMode, int keymakerX, int keymakerY, String[] board) {
        GameBoard gameBoardAStar = new GameBoard(true, keymakerX, keymakerY, board);
        int aStarPath = Algorithms.A_star(gameBoardAStar, new int[]{0, 0}, new int[]{keymakerX, keymakerY});
        return aStarPath;
    }

    public int runBacktracking(int perceptionMode, int keymakerX, int keymakerY, String[] board) {

        GameBoard BacktrackingGameBoard = new GameBoard(false, keymakerX, keymakerY, board);

        Algorithms.Backtracking(BacktrackingGameBoard, new int[]{0, 0}, new int[]{keymakerX, keymakerY});

        int BacktrackingPath = BacktrackingGameBoard.getMinPath();
        if (BacktrackingPath == Integer.MAX_VALUE) {
            BacktrackingPath = -1;
        }
        return BacktrackingPath;
    }
}

/*
 * Cell class for the game board that specify parents and its coordinates, marks if it has been visited, and the distance from the start.
 */
class Cell {
    private int parent_i, parent_j;
    private int i, j;
    private String perception;
    private boolean visited = false;
    private int distance = Integer.MAX_VALUE;

    /**
     * Constructs a Cell with specified coordinates.
     * 
     * @param i X-coordinate of the cell
     * @param j Y-coordinate of the cell
     */
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

/**
 * Extension of Cell used in A* algorithm, with additional costs for A* algorithm pathfinding.
 */
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

    /**
     * Compares this object with the specified object for order.       *
     * We first compare the finalCost, and if they are the same, then compare
     * the estimatedCost.
     *
     * @param other the object to be compared.
     * @return a negative integer -- object is less than other 
     *          zero -- objects are equal
     *          positive integer -- object is greater than other
     */
    @Override
    public int compareTo(AStarCell other) {
        int result = Double.compare(this.finalCost, other.finalCost);
        if (result == 0) {
            return Double.compare(this.estimatedCost, other.estimatedCost);
        }
        return result;
    }
}

/**
 * Represents the game board, holding cells, configuration for pathfinding algorithms and store min path from start to goal.
 */
class GameBoard {
    private static final int SIZE = 9;
    private Cell[][] boardCells;
    private Cell initialNode;
    private Cell finalNode;
    private int minPath = Integer.MAX_VALUE;



    /**
     * Constructs a GameBoard with specified size and initializes cells based on algorithm type.
     * 
     * @param isAStar Specifies if the board is initialized for A* algorithm
     * @param keymakerX X-coordinate of the goal
     * @param keymakerY Y-coordinate of the goal
     */
    public GameBoard(boolean isAStar, int keymakerX, int keymakerY, String[] board) {
        boardCells = isAStar ? new AStarCell[SIZE][SIZE] : new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                boardCells[i][j] = isAStar ? new AStarCell(i, j) : new Cell(i, j);
                char cell = board[i].charAt(j);
                if (cell == 'K') {
                    boardCells[i][j].setPerception("goal");
                } else if (cell == 'P' || cell == 'S' || cell == 'A') {
                    boardCells[i][j].setPerception("blocked");
                } else {
                    boardCells[i][j].setPerception(null);
                }
            }
        }
        initialNode = boardCells[0][0];
        finalNode = boardCells[keymakerX][keymakerY];
    }

    /**
     * Updates the perception to specify if a cell is open, blocked, or goal.
     *
     * @param x X-coordinate of the cell to update.
     * @param y Y-coordinate of the cell to update.
     * @param perception the perception value to set for the cell
     *                   "K": goal, "B": free, other: blocked.
     */
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


/**
 * Contains implementations of A* and Backtracking algorithms for pathfinding with additional methods.
 */
class Algorithms {

    /*
     * Alowed directions to move in the game board.
     */
    private static int[][] moveDirections = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};


    /**
     * Runs the Backtracking algorithm to find the shortest path from the start to the goal.
     *
     * @param gameBoard the game board to search in
     * @param src       the coordinates of the start
     * @param dest      the coordinates of the goal
     */
    public static void Backtracking(GameBoard gameBoard, int[] src, int[] dest) {
        backtracking(gameBoard, src[0], src[1], dest, 0);
    }

    /**
     * Checks if the given cell is valid to move to in the game board, meaning it is within the board's bounds,
     * it is not blocked, and it has not been visited before.
     *
     * @param gameBoard the game board to check in
     * @param col       the column of the cell to check
     * @param row       the row of the cell to check
     * @return true -- cell is valid, false -- otherwise
     */
    private static boolean isValid(GameBoard gameBoard, int col, int row) {
        return col >= 0 && col < gameBoard.getBoardCells().length && row >= 0 && row < gameBoard.getBoardCells().length
                && (gameBoard.getBoardCells()[col][row].getPerception() == null
                || gameBoard.getBoardCells()[col][row].getPerception().equals("goal"))
                && !gameBoard.getBoardCells()[col][row].isVisited();
    }

    /**
     * Executes the Backtracking algorithm to explore paths from a start cell to a destination cell.
     * It recursively visits unblocked, unvisited cells while updating the minimum path length.
     * The method backtracks to previous cells once all possible paths from the current cell are explored.
     * Updates: algorithm checks cell if it doesn't have other shortest path to destination cell for reducing time complexity.
     *
     * @param gameBoard the game board to search in
     * @param col       X-coordinate of the current cell
     * @param row       Y-coordinate of the current cell
     * @param dest      the coordinates of the destination cell
     * @param depth     the current path length from the start cell
     */
    private static void backtracking(GameBoard gameBoard, int col, int row, int[] dest, int depth) {
        Cell current = gameBoard.getBoardCells()[col][row];

        //System.out.println("m " + current.getI() + " " + current.getJ());
        //readMapResponse(gameBoard);
        if (current.getI() == dest[0] && current.getJ() == dest[1]) {
            gameBoard.setMinPath(Math.min(gameBoard.getMinPath(), depth));
            current.setVisited(false);
            return;
        }
        if (gameBoard.getMinPath() <= depth || depth >= current.getDistance()) {
            return;
        }
        current.setVisited(true);
        current.setDistance(Math.min(gameBoard.getMinPath(), depth));
        for (int[] direction : moveDirections) {
            int nextCol = col + direction[0];
            int nextRow = row + direction[1];
            if (isValid(gameBoard, nextCol, nextRow)) {
                backtracking(gameBoard, nextCol, nextRow, dest, depth + 1);
                //System.out.println("m " + current.getI() + " " + current.getJ());
                //readMapResponse(gameBoard);
            }
        }
        current.setVisited(false);
    }


    /**
     * Runs the Backtracking algorithm to find the shortest path from the start to the goal.
     *
     * @param gameBoard the game board to search in
     * @param src       the coordinates of the start cell
     * @param dest      the coordinates of the destination cell
     */
    public static int A_star(GameBoard gameBoard, int[] src, int[] dest) {
        return aStarAlgo(gameBoard, src, dest);
    }


    /**
     * Executes the A* algorithm to find the shortest path from the start cell to the destination cell.
     * Initializes the open list for unexplored cells and closed list for explored cells.
     * Continually evaluates the cell with the lowest final cost from the open list.
     * Recalculates the costs of the neighboring cells and updates the open list.
     * Updates the path, costs, and parent relationships of neighboring cells.
     * If the destination is reached, it outputs the path cost; otherwise, it outputs -1 if no path exists.
     *
     * @param gameBoard the game board containing cells for pathfinding
     * @param src       the coordinates of the start cell
     * @param dest      the coordinates of the destination cell
     */
    private static int aStarAlgo(GameBoard gameBoard, int[] src, int[] dest) {

        int size = gameBoard.getBoardCells().length;
        AStarCell[][] cellDetails = (AStarCell[][]) gameBoard.getBoardCells();

        PriorityQueue<AStarCell> openList = new PriorityQueue<>();

        gameBoard.getBoardCells()[src[0]][src[1]].setParent_i(-1);
        gameBoard.getBoardCells()[src[0]][src[1]].setParent_j(-1);
        cellDetails[src[0]][src[1]].setCurrentCost(0);
        cellDetails[src[0]][src[1]].setEstimatedCost(manhattanDistance(src[0], src[1], dest));
        cellDetails[src[0]][src[1]].setFinalCost(cellDetails[src[0]][src[1]].getCurrentCost() + cellDetails[src[0]][src[1]].getEstimatedCost());
        openList.add(cellDetails[0][0]);

        boolean[][] closedList = new boolean[size][size];
        boolean foundDest = false;

        int prevRow = -1, prevCol = -1;

        while (!openList.isEmpty()) {

            AStarCell current = openList.poll();
            int i = current.getI();
            int j = current.getJ();

            if (isValid(gameBoard, i, j)) {
                if (!areConnected(current, prevCol, prevRow)) {
                    traceBackToNeighbor(gameBoard, cellDetails, i, j, prevCol, prevRow);
                }

                closedList[i][j] = true;
                //System.out.println("m " + i + " " + j);
                //readMapResponse(gameBoard);
                int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

                for (int[] dir : directions) {
                    int newCol = i + dir[0];
                    int newRow = j + dir[1];

                    if (isValid(gameBoard, newCol, newRow)) {
                        if (isDestination(gameBoard, newCol, newRow, dest)) {
                            cellDetails[newCol][newRow].setParent_i(i);
                            cellDetails[newCol][newRow].setParent_j(j);
                            // System.out.println("m " + newCol + " " + newRow);
                            // readMapResponse(gameBoard);
//                            System.out.println("e " + cellDetails[i][j].finalCost);
                            foundDest = true;

                            return cellDetails[i][j].finalCost;
                        } else if (isUnBlocked(gameBoard, newCol, newRow)) {
                            if (cellDetails[newCol][newRow].getFinalCost() > cellDetails[i][j].getFinalCost()
                                    || cellDetails[newCol][newRow].getFinalCost() == Double.POSITIVE_INFINITY) {

                                cellDetails[newCol][newRow].setParent_i(i);
                                cellDetails[newCol][newRow].setParent_j(j);

                                cellDetails[newCol][newRow].setCurrentCost(cellDetails[i][j].getCurrentCost() + 1);
                                cellDetails[newCol][newRow].setEstimatedCost(manhattanDistance(newCol, newRow, dest));
                                cellDetails[newCol][newRow].setFinalCost(cellDetails[newCol][newRow].getCurrentCost()
                                        + cellDetails[newCol][newRow].getEstimatedCost());

                                openList.add(cellDetails[newCol][newRow]);
                            }
                        }
                    }
                }

                prevCol = i;
                prevRow = j;
                openList.remove(cellDetails[i][j]);
                closedList[i][j] = true;
            }
        }
        if (!foundDest) {
            return -1;
        }
        return -1;
    }



    /**
     * Checks if the cell at the given coordinates is unblocked.
     * 
     * @param gameBoard the game board to check in
     * @param col the column of the cell to check
     * @param row the row of the cell to check
     * @return true -- cell is unblocked, false -- otherwise
     */
    private static boolean isUnBlocked(GameBoard gameBoard, int col, int row) {
        Cell[][] boardCells = gameBoard.getBoardCells();
        return boardCells[col][row].getPerception() == null || !boardCells[col][row].getPerception().equals("blocked");
    }

    /**
     * Checks if the given cell is the goal cell.
     *
     * @param gameBoard the game board to check in
     * @param col the column of the cell to check
     * @param row the row of the cell to check
     * @param dest the coordinates of the destination cell
     * @return true -- cell is the destination, false -- otherwise
     */
    private static boolean isDestination(GameBoard gameBoard, int col, int row, int[] dest) {
        if (col == dest[0] && row == dest[1] ) {
            return true;
        }
        return false;
    }

    /**
     * Calculates estimated cost to reach the destination using Manhattan formula.
     * 
     * @param col X-coordinate of the cell to calculate the distance from
     * @param row Y-coordinate of the cell to calculate the distance from
     * @param dest the coordinates of the destination cell
     * @return estiamted distance between the cell and the destination
     */
    private static int manhattanDistance(int col, int row, int[] dest) {
        return Math.abs(col - dest[0]) + Math.abs(row - dest[1]);
    }

    /**
     * Method that prevent teleportation of the agent between unneibouring cells andprovide steps to go back.
     *
     * @param gameBoard the game board containing cells for pathfinding
     * @param cellDetails a 2D array of AStarCell objects representing details of each cell
     * @param currentCol X-coordinate of the current cell
     * @param currentRow Y-coordinate of the current cell
     * @param prevCol X-coordinate of the previous cell
     * @param prevRow Y-coordinate of the previous cell
     */
    private static void traceBackToNeighbor(GameBoard gameBoard, AStarCell[][] cellDetails, int currentCol, int currentRow, int prevCol, int prevRow) {
        while (isValid(gameBoard, prevCol, prevRow) && prevCol >= 0 && prevRow >= 0 && !areConnected(cellDetails[currentCol][currentRow], prevCol, prevRow)) {
            /*System.out.println("m " + cellDetails[prevCol][prevRow].getParent_i() + " " + cellDetails[prevCol][prevRow].getParent_j());
            readMapResponse(gameBoard);*/
            if (prevCol == -1 && prevRow == -1) {
                return;
            }
            int newPrevCol = cellDetails[prevCol][prevRow].getParent_i();
            int newPrevRow = cellDetails[prevCol][prevRow].getParent_j();
            if (newPrevCol >= 0 && newPrevRow >= 0) {
                prevCol = newPrevCol;
                prevRow = newPrevRow;
            } else {
                break;
            }
        }
    }


    /**
     * Compares the current cell's parent coordinates with the previous cell's coordinates.
     *
     * @param current the current AStarCell being evaluated
     * @param prevCol the column coordinate of the previous cell
     * @param prevRow the row coordinate of the previous cell
     * @return true if the current cell's parent matches the previous cell's coordinates, false otherwise
     */
    private static boolean areConnected(AStarCell current, int prevCol, int prevRow) {
        return current.getParent_i() == prevCol && current.getParent_j() == prevRow;
    }

    /**
     * Reads the map response from the standard input and updates gameBoard.
     * The map response is expected to contain the number of dangers and the coordinates and perception of each danger.
     * The game board is updated with the new perceptions of the cells.
     * 
     * @param gameBoard the game board to update
     */
    public static void readMapResponse(GameBoard gameBoard) {
        Scanner scanner = new Scanner(System.in);
        int dangerCount = scanner.nextInt();
        for (int k = 0; k < dangerCount; k++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            String perception = scanner.next();
            gameBoard.updateCell(x, y, perception);
        }
        scanner.close();
    }

}
