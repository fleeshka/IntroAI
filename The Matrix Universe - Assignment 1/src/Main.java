import java.util.*;
public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int perceptionMode = scan.nextInt();
        int keymakerX = scan.nextInt();
        int keymakerY = scan.nextInt();

        int[] dest = {keymakerX, keymakerY};
        int[] src = {0, 0};


        // A* Algorithm
        GameBoard gameBoardAStar = new GameBoard(true, keymakerX, keymakerY);
        Algorithms.A_star(gameBoardAStar, src, dest);


        // Backtracking Algorithm
        /*
        GameBoard gameBoardBacktracking = new GameBoard(false, keymakerX, keymakerY);

        Algorithms.Backtracking(gameBoardBacktracking, src, dest);
        int minPath = gameBoardBacktracking.getMinPath();
        if (minPath == Integer.MAX_VALUE) {
            System.out.println("e -1");
        } else {
            System.out.println("e " + minPath);
        }

        */

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


    public static void A_star(GameBoard gameBoard, int[] src, int[] dest) {
        aStarAlgo(gameBoard, src, dest);
    }

    private static boolean isUnBlocked(GameBoard gameBoard, int col, int row) {
        Cell[][] boardCells = gameBoard.getBoardCells();
        return boardCells[col][row].getPerception() == null || !boardCells[col][row].getPerception().equals("blocked");
    }

    private static boolean isDestination(GameBoard gameBoard, int col, int row, int[] dest) {
        // Cell cell = gameBoard.getBoardCells()[col][row];
        if (col == dest[0] && row == dest[1] ) {
            //return cell.getPerception() != null && cell.getPerception().equals("goal");
            return true;
        }
        return false;
    }

    private static int manhattanDistance(int col, int row, int[] dest) {
        return Math.abs(col - dest[0]) + Math.abs(row - dest[1]);
    }



    private static void traceBackToNeighbor(GameBoard gameBoard, AStarCell[][] cellDetails, int currentCol, int currentRow, int prevCol, int prevRow) {
        while (prevCol >= 0 && prevRow >= 0 && !areConnected(cellDetails[currentCol][currentRow], prevCol, prevRow)) {
            System.out.println("m " + cellDetails[prevCol][prevRow].getParent_i() + " " + cellDetails[prevCol][prevRow].getParent_j());
            readMapResponse(gameBoard);
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


    private static boolean areConnected(AStarCell current, int prevCol, int prevRow) {
        return current.getParent_i() == prevCol && current.getParent_j() == prevRow;
    }





    private static void printPathFromCellToCommonParentAndBack(GameBoard gameBoard,AStarCell[][] cellDetails, AStarCell cell1, AStarCell cell2) {
        // Найти общую родительскую клетку
        AStarCell commonParent = findCommonParent(cellDetails, cell1, cell2);
        if (commonParent == null) {
            System.out.println("No common parent found.");
            return;
        }

        // Вывести путь от cell1 до commonParent
        printPathFromCellToParent( gameBoard,cellDetails, cell1, commonParent);

        // Вывести путь от commonParent до cell2
        printPathFromParentToCell( gameBoard,cellDetails, commonParent, cell2);
    }


    private static AStarCell findCommonParent(GameBoard gameBoard, AStarCell[][] cellDetails, AStarCell fromCell, AStarCell toCell) {

        // current cell is cell that whe want achieve fromCell
        Set<AStarCell> path1 = new HashSet<>();
        Set<AStarCell> path2 = new HashSet<>();

        // find path from (fromCell) to root
        AStarCell current = fromCell;
        while (current.getParent_i() != -1 || current.getParent_j() != -1) {
            path1.add(current);
            current = cellDetails[current.getParent_i()][current.getParent_j()];
        }
        path1.add(current);

        // find path from (toCell) to root
        current = toCell;
        while (current.getParent_i() != -1 || current.getParent_j() != -1) {
            path2.add(current);
            current = cellDetails[current.getParent_i()][current.getParent_j()];
        }
        path2.add(current);

        // find intersection
        path1.retainAll(path2);

        // Возвращаем первого общего родителя
        for (AStarCell cell : path1) {
            return cell;
        }

        // Если общий родитель не найден, возвращаем null
        return null;
    }

    private static AStarCell findCommonParent(AStarCell[][] cellDetails, AStarCell cell1, AStarCell cell2) {
        // Создаем множества для хранения путей от каждой клетки до начальной
        Set<AStarCell> path1 = new HashSet<>();
        Set<AStarCell> path2 = new HashSet<>();

        // Проходим по родительским ссылкам от cell1 до начальной клетки
        AStarCell current = cell1;
        while (current != null) {
            path1.add(current);
            if (current.getParent_i() == -1 || current.getParent_j() == -1) {
                break;
            }
            current =  cellDetails[current.getParent_i()][current.getParent_j()];
        }

        // Проходим по родительским ссылкам от cell2 до начальной клетки
        current = cell2;
        while (current != null) {
            path2.add(current);
            if (current.getParent_i() == -1 || current.getParent_j() == -1) {
                break;
            }
            current = cellDetails[current.getParent_i()][current.getParent_j()];
        }

        // Находим пересечение путей
        path1.retainAll(path2);

        // Возвращаем первого общего родителя
        for (AStarCell cell : path1) {
            return cell;
        }

        // Если общий родитель не найден, возвращаем null
        return null;
    }

    private static void printPathFromCellToParent(GameBoard gameBoard, AStarCell[][] cellDetails, AStarCell cell, AStarCell parent) {
        AStarCell current = cellDetails[cell.getParent_i()][cell.getParent_j()];
        while (current != parent) {
            System.out.println("m " + current.getI() + " " + current.getJ());
            readMapResponse(gameBoard);
            if (current.getParent_i() == -1 || current.getParent_j() == -1) {
                break;
            }
            current =  cellDetails[current.getParent_i()][current.getParent_j()];
        }
    }

    private static void printPathFromParentToCell(GameBoard gameBoard, AStarCell[][] cellDetails, AStarCell parent, AStarCell cell) {
        AStarCell current = cell;
        List<AStarCell> path = new ArrayList<>();

        // Проходим по родительским ссылкам от cell до parent
        while (current != parent) {
            path.add(current);

            if (current.getParent_i() == -1 || current.getParent_j() == -1) {
                break;
            }
            current = cellDetails[current.getParent_i()][current.getParent_j()];
        }
        path.add(parent); // Добавляем parent в путь

        // Выводим путь в обратном порядке (от parent до cell)
        for (int i = path.size() - 1; i > 0; i--) {
            AStarCell c = path.get(i);
            System.out.println("m " + c.getI() + " " + c.getJ());
            readMapResponse(gameBoard);
        }
    }

    private static void markPathAsUnvisited(GameBoard gameBoard, AStarCell[][] cellDetails, AStarCell cell, AStarCell parent) {
        AStarCell current = cellDetails[cell.getParent_i()][cell.getParent_j()];
        while (current != parent) {
            current.setVisited(false);
            if (current.getParent_i() == -1 || current.getParent_j() == -1) {
                break;
            }
            current = cellDetails[current.getParent_i()][current.getParent_j()];
        }
    }


    private static void aStarAlgo(GameBoard gameBoard, int[] src, int[] dest) {

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

        int prevRow = -1, prevCol = -1;

        while (!openList.isEmpty()) {
            /*System.out.println("start new loop");

            System.out.println("Open list before loop: ");
            for (AStarCell cell : openList) {
                System.out.println("Cell: " + cell.getI() + " " + cell.getJ()  + " current "
                        + cell.getCurrentCost() + " estimated " + cell.getEstimatedCost() + " final " + cell.getFinalCost());
            }*/


            AStarCell current = openList.poll();
            // get i and j from current cell in cellDetails

            /*System.out.println("Take cell " + current.getI() + " " + current.getJ());
*/

            int i = current.getI();
            int j = current.getJ();

            if (isValid(gameBoard, i, j)) {

                /*
                if (!areConnected(current, prevCol, prevRow) ) {
                    traceBackToNeighbor(gameBoard, cellDetails, i, j, prevCol, prevRow);
                }
                */
                
                if (!areConnected(current, prevCol, prevRow)) {
                    printPathFromCellToCommonParentAndBack(gameBoard,cellDetails, cellDetails[prevCol][prevRow], current);

                    markPathAsUnvisited(gameBoard, cellDetails, cellDetails[prevCol][prevRow], current);
                } else {
                    System.out.println("m " + i + " " + j);

                    // update the map with the new perception
                    readMapResponse(gameBoard);
                }
                

                closedList[i][j] = true;



                //check all the neighbours
                // go up


                for (int[] dir : moveDirections) {
                    int newCol = i + dir[0];
                    int newRow = j + dir[1];

                    if (isValid(gameBoard, newCol, newRow)) {
                        if (isDestination(gameBoard, newCol, newRow, dest)) {
                            cellDetails[newCol][newRow].setParent_i(i);
                            cellDetails[newCol][newRow].setParent_j(j);
                            System.out.println("m " + newCol + " " + newRow);
                            readMapResponse(gameBoard);
                            System.out.println("e " + cellDetails[i][j].finalCost);
                            foundDest = true;
                            return;
                        } else if ( closedList[newCol][newRow] == false && isUnBlocked(gameBoard, newCol, newRow)) {
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


                // remove current cell from open list
                openList.remove(cellDetails[i][j]);
                // mark current cell as closed
                closedList[i][j] = true;
            }
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


