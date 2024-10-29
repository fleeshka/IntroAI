import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int perceptionMode = scan.nextInt();
        int keymakerX = scan.nextInt();
        int keymakerY = scan.nextInt();

        GameBoard gameBoard = new GameBoard(true, keymakerX, keymakerY);
        int[] dest = {keymakerX, keymakerY};
        int[] src = {0, 0};

        Algorithms.A_star(gameBoard, src, dest, scan);
    }
}


class Cell {
    int parent_i, parent_j;
    int i, j;
    String perception;

    Cell(int i, int j) {
        this.parent_i = 0;
        this.parent_j = 0;
        this.i = i;
        this.j = j;
        this.perception = null;
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
        updateCell(keymakerX, keymakerY, "keymaker");
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
}

class Algorithms {
    public static void A_star(GameBoard gameBoard, int[] src, int[] dest, Scanner scanner) {
        aStarAlgo(gameBoard, src, dest, scanner);
    }

    private static boolean isValid(int col, int row, int size) {
        return (row >= 0) && (row < size) && (col >= 0) && (col < size);
    }

    private static boolean isUnBlocked(GameBoard gameBoard, int col, int row) {
        Cell[][] boardCells = gameBoard.getBoardCells();
        return boardCells[col][row].perception == null || !boardCells[col][row].perception.equals("blocked");
    }

    private static boolean isDestination(GameBoard gameBoard, int col, int row, int[] dest) {
        Cell cell = gameBoard.getBoardCells()[col][row];
        return cell.getPerception() != null && cell.getPerception().equals("goal");
    }

    private static int manhattanDistance(int col, int row, int[] dest) {
        return Math.abs(col - dest[0]) + Math.abs(row - dest[1]);
    }


    private static void tracePath(Cell[][] cellDetails, int[] dest) {
        System.out.println("The Path is ");
        int row = dest[1];
        int col = dest[0];

        List<int[]> pathList = new ArrayList<>();
        while (!(cellDetails[row][col].parent_i == row && cellDetails[row][col].parent_j == col)) {
            pathList.add(new int[]{row, col});
            int temp_row = cellDetails[row][col].parent_i;
            int temp_col = cellDetails[row][col].parent_j;
            row = temp_row;
            col = temp_col;
        }
        pathList.add(new int[]{row, col});
        Collections.reverse(pathList);

        pathList.forEach(p -> System.out.print("-> (" + p[0] + ", " + p[1] + ")"));
        System.out.println();
    }


    private static void aStarAlgo(GameBoard gameBoard, int[] src, int[] dest, Scanner scanner) {

        int size = gameBoard.getBoardCells().length;
        AStarCell[][] cellDetails = (AStarCell[][]) gameBoard.getBoardCells();


        PriorityQueue<AStarCell> openList = new PriorityQueue<>();

        gameBoard.getBoardCells()[src[0]][src[1]].setParent_i(-1);
        gameBoard.getBoardCells()[src[0]][src[1]].setParent_j(-1);
        cellDetails[src[0]][src[1]].setCurrentCost(0);
        cellDetails[src[0]][src[1]].setEstimatedCost(manhattanDistance(src[0], src[1], dest));
        cellDetails[src[0]][src[1]].setFinalCost(cellDetails[src[0]][src[1]].getCurrentCost() + cellDetails[src[0]][src[1]].getEstimatedCost());
        openList.add(cellDetails[0][0]);

        //closedList initialization
        boolean[][] closedList = new boolean[size][size];
        boolean foundDest = false;



        while (!openList.isEmpty()) {

            AStarCell current = openList.poll();
            // get i and j from current cell in cellDetails

            int i = current.getI();
            int j = current.getJ();

            if (isDestination(gameBoard, i, j, dest)) {
                tracePath(cellDetails, dest);
                foundDest = true;
                System.out.println("Reached destination!");
                return;
            }

            closedList[i][j] = true;

            // Move to the current cell and read response from the map
            System.out.println("m " + i + " " + j);

            // update the map with the new perception
            readMapResponse(gameBoard);

            //check all the neighbours
            // go up
            if (isValid(i - 1, j, size)) {
                //System.out.println("check up" + (i - 1) + " " + j);
                //check if cell is destination
                if (isDestination(gameBoard, i - 1, j, dest)) {
                    cellDetails[i - 1][j].setParent_i(i);
                    cellDetails[i - 1][j].setParent_j(j);
                    System.out.println("m " + (i-1) + " " + j);
                    readMapResponse(gameBoard);
                    System.out.println(
                            "e " + cellDetails[i][j].finalCost);
                    /*tracePath(cellDetails, dest);*/
                    foundDest = true;
                    return;
                } else if (!closedList[i - 1][j] && isUnBlocked(gameBoard, i - 1, j)) {
                    // update cell if its final cost is higher
                    if (cellDetails[i - 1][j].getFinalCost() > cellDetails[i][j].getFinalCost()
                            || cellDetails[i - 1][j].getFinalCost() == Double.POSITIVE_INFINITY) {
                        // update cell details
                        // set previous cell as parent

                        cellDetails[i - 1][j].setParent_i(i);
                        cellDetails[i - 1][j].setParent_j(j);

                        // increase the current cost (g(x)) -- > (parent cost + 1)
                        cellDetails[i - 1][j].setCurrentCost(cellDetails[i][j].getCurrentCost() + 1);
                        // calculate estimated cost (h(x)) -- > manhattan distance
                        cellDetails[i - 1][j].setEstimatedCost(manhattanDistance(i - 1, j, dest));
                        // update final cost
                        cellDetails[i - 1][j].setFinalCost(cellDetails[i - 1][j].getCurrentCost()
                                + cellDetails[i - 1][j].getEstimatedCost());

                        /*System.out.println("ADD " + (i - 1) + " " + j + " cell to open list  \n" + cellDetails[i - 1][j].currentCost
                                + " -- current cost " + cellDetails[i - 1][j].estimatedCost + " -- estimated cost "
                                + cellDetails[i - 1][j].finalCost );*/


                        // add cell to the open list
                        openList.add(cellDetails[i - 1][j]);

                    }
                }
            }

                // go right
            if (isValid(i + 1, j, size)) {
                //System.out.println("check down" + (i + 1) + " " + j);
                //check if cell is destination
                if (isDestination(gameBoard, i + 1, j, dest)) {
                        cellDetails[i + 1][j].setParent_i(i);
                        cellDetails[i + 1][j].setParent_j(j);
                        System.out.println("m " + (i+1) + " " + j);
                        System.out.println(
                                "e " + cellDetails[i][j].finalCost);
                        readMapResponse(gameBoard);
                        /*tracePath(cellDetails, dest);*/
                        foundDest = true;
                        return;
                    } else if (!closedList[i + 1][j] && isUnBlocked(gameBoard, i + 1, j)) {
                    // update cell if its final cost is higher
                    if (cellDetails[i + 1][j].getFinalCost() > cellDetails[i][j].getFinalCost()
                                || cellDetails[i + 1][j].getFinalCost() == Double.POSITIVE_INFINITY) {
                            // update cell details
                            // set previous cell as parent
                            cellDetails[i + 1][j].setParent_i(i);
                            cellDetails[i + 1][j].setParent_j(j);

                            // increase the current cost (g(x)) -- > (parent cost + 1)
                            cellDetails[i + 1][j].setCurrentCost(cellDetails[i][j].getCurrentCost() + 1);
                            // calculate estimated cost (h(x)) -- > manhattan distance
                            cellDetails[i + 1][j].setEstimatedCost(manhattanDistance(i + 1, j, dest));
                            // update final cost
                            cellDetails[i + 1][j].setFinalCost(cellDetails[i + 1][j].getCurrentCost()
                                    + cellDetails[i + 1][j].getEstimatedCost());

                            /*System.out.println("ADD " + (i + 1) + " " + j + " cell to open list  \n" + cellDetails[i + 1][j].currentCost
                                    + " -- current cost " + cellDetails[i + 1][j].estimatedCost + " -- estimated cost "
                                    + cellDetails[i + 1][j].finalCost );*/

                            // add cell to the open list
                            openList.add(cellDetails[i + 1][j]);

                        }
                }
            }

                // go to up
            if (isValid(i, j - 1, size)) {
                //System.out.println("check left" + i + " " + (j - 1));
                //check if cell is destination
                if (isDestination(gameBoard,i, j - 1, dest)) {
                        cellDetails[i][j - 1].setParent_i(i);
                        cellDetails[i][j - 1].setParent_j(j);
                        System.out.println("m " + (i) + " " + (j - 1));
                        readMapResponse(gameBoard);
                        System.out.println(
                                "e " + cellDetails[i][j].finalCost);
                        /*tracePath(cellDetails, dest);*/
                        foundDest = true;
                        return;
                    } else if (!closedList[i][j - 1] && isUnBlocked(gameBoard, i, j - 1)) {
                    // update cell if its final cost is higher
                    if (cellDetails[i][j - 1].getFinalCost() > cellDetails[i][j].getFinalCost()
                                || cellDetails[i][j - 1].getFinalCost() == Double.POSITIVE_INFINITY) {
                            // update cell details
                            // set previous cell as parent
                            cellDetails[i][j - 1].setParent_i(i);
                            cellDetails[i][j - 1].setParent_j(j);

                            // increase the current cost (g(x)) -- > (parent cost + 1)
                            cellDetails[i][j - 1].setCurrentCost(cellDetails[i][j].getCurrentCost() + 1);
                            // calculate estimated cost (h(x)) -- > manhattan distance
                            cellDetails[i][j - 1].setEstimatedCost(manhattanDistance(i, j - 1, dest));
                            // update final cost
                            cellDetails[i][j - 1].setFinalCost(cellDetails[i][j - 1].getCurrentCost()
                                    + cellDetails[i][j - 1].getEstimatedCost());

                            /*System.out.println("ADD " + i + " " + (j - 1) + " cell to open list  \n" + cellDetails[i][j - 1].currentCost
                                    + " -- current cost " + cellDetails[i][j - 1].estimatedCost + " -- estimated cost "
                                    + cellDetails[i][j - 1].finalCost );*/


                            // add cell to the open list
                            openList.add(cellDetails[i][j - 1]);

                        }
                }
            }

            // go to  DOWN
            if (isValid(i, j + 1, size)) {
                //System.out.println("check right" + i + " " + (j + 1));
                //check if cell is destination
                if (isDestination(gameBoard, i, j + 1, dest)) {
                        cellDetails[i][j + 1].setParent_i(i);
                        cellDetails[i][j + 1].setParent_j(j);
                        System.out.println("m " + i + " " + (j + 1));
                        readMapResponse(gameBoard);
                        System.out.println(
                                "e " + cellDetails[i][j].finalCost);

                        /*tracePath(cellDetails, dest);*/
                        foundDest = true;
                        return;
                    } else if (!closedList[i][j + 1] && isUnBlocked(gameBoard, i, j + 1)) {
                    // update cell if its final cost is higher
                    if (cellDetails[i][j + 1].getFinalCost()> cellDetails[i][j].getFinalCost()
                                || cellDetails[i][j + 1].getFinalCost() == Double.POSITIVE_INFINITY) {
                            // update cell details
                            // set previous cell as parent
                            cellDetails[i][j + 1].setParent_i(i);
                            cellDetails[i][j + 1].setParent_j(j);

                            // increase the current cost (g(x)) -- > (parent cost + 1)
                            cellDetails[i][j + 1].setCurrentCost(cellDetails[i][j].getCurrentCost() + 1);
                            // calculate estimated cost (h(x)) -- > manhattan distance
                            cellDetails[i][j + 1].setEstimatedCost(manhattanDistance(i, j + 1, dest));
                            // update final cost
                            cellDetails[i][j + 1].setFinalCost(cellDetails[i][j + 1].getCurrentCost()
                                    + cellDetails[i][j + 1].getEstimatedCost());

                            /*System.out.println("ADD " + i + " " + (j + 1) + " cell to open list  \n" + cellDetails[i][j + 1].currentCost
                                    + " -- current cost " + cellDetails[i][j + 1].estimatedCost + " -- estimated cost "
                                    + cellDetails[i][j + 1].finalCost );*/


                            // add cell to the open list
                            openList.add(cellDetails[i][j + 1]);

                        }
                }
            }


            // remove current cell from open list
            openList.remove(cellDetails[i][j]);
            // mark current cell as closed
            closedList[i][j] = true;
        }
        if (!foundDest ) {
            System.out.println("e -1");
        }
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

}

