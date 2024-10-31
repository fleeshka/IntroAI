import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TestRunner {

    // Класс для представления теста
    public static class TestCase {
        int perceptionMode;
        int keymakerX;
        int keymakerY;
        String[] board;
        int expectedResult;

        public TestCase(int perceptionMode, int keymakerX, int keymakerY, String[] board, int expectedResult) {
            this.perceptionMode = perceptionMode;
            this.keymakerX = keymakerX;
            this.keymakerY = keymakerY;
            this.board = board;
            this.expectedResult = expectedResult;
        }
    }

    public static class Statistics {
        public static double mean(List<Long> times) {
            return times.stream().mapToDouble(Long::doubleValue).average().orElse(0);
        }

        public static double median(List<Long> times) {
            Collections.sort(times);
            int size = times.size();
            if (size % 2 == 0) {
                return (times.get(size / 2 - 1) + times.get(size / 2)) / 2.0;
            } else {
                return times.get(size / 2);
            }
        }

        public static long mode(List<Long> times) {
            Map<Long, Long> frequencyMap = times.stream()
                    .collect(Collectors.groupingBy(t -> t, Collectors.counting()));
            return Collections.max(frequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        }

        public static double standardDeviation(List<Long> times) {
            double mean = mean(times);
            double variance = times.stream().mapToDouble(time -> Math.pow(time - mean, 2)).average().orElse(0);
            return Math.sqrt(variance);
        }
    }

    public static List<TestCase> parseTests(String filePath) {
        List<TestCase> testCases = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Test #")) {
                    int perceptionMode = Integer.parseInt(reader.readLine().trim());
                    String[] coordinates = reader.readLine().trim().replaceAll("[()]", "").split(",");
                    int keymakerX = Integer.parseInt(coordinates[0].trim());
                    int keymakerY = Integer.parseInt(coordinates[1].trim());
                    String[] board = new String[9];
                    for (int i = 0; i < 9; i++) {
                        board[i] = reader.readLine().trim();
                    }
                    line = reader.readLine().trim();
                    int expectedResult = Integer.parseInt(line.replace("Expected result: ", "").trim());
                    testCases.add(new TestCase(perceptionMode, keymakerX, keymakerY, board, expectedResult));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testCases;
    }

    public static int runAStarTest(TestCase testCase) {
        Main mainProgram = new Main();
        return mainProgram.runAStar(testCase.perceptionMode, testCase.keymakerX, testCase.keymakerY, testCase.board);
    }

    public static int runBacktrackingTest(TestCase testCase) {
        Main mainProgram = new Main();
        return mainProgram.runBacktracking(testCase.perceptionMode, testCase.keymakerX, testCase.keymakerY, testCase.board);
    }

    public static void runTestsAndAnalyze(String testFilePath, String resultFilePath) {
        List<TestCase> testCases = parseTests(testFilePath);
        List<Long> aStarTimes = new ArrayList<>();
        List<Long> backtrackingTimes = new ArrayList<>();
        int winsAStar = 0, lossesAStar = 0;
        int winsBacktracking = 0, lossesBacktracking = 0;
        int counter = 1;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath))) {
            for (TestCase testCase : testCases) {
                writer.write(String.format("Test #%d\n", counter++));
                // Run A* test and record time
                long startAStar = System.nanoTime();
                int resultAStar = runAStarTest(testCase);
                long endAStar = System.nanoTime();
                long timeAStar = endAStar - startAStar;
                aStarTimes.add(timeAStar);

                boolean isPassedAStar = resultAStar == testCase.expectedResult;
                if (isPassedAStar) {
                    winsAStar++;
                } else {
                    lossesAStar++;
                }

                writer.write(String.format("A* Algorithm:\nExecution Time: %d ns\tExpected: %d\tActual: %d\tResult: %s\n",
                        timeAStar, testCase.expectedResult, resultAStar, isPassedAStar ? "Passed" : "Failed"));

                // Run Backtracking test and record time
                long startBacktracking = System.nanoTime();
                int resultBacktracking = runBacktrackingTest(testCase);
                long endBacktracking = System.nanoTime();
                long timeBacktracking = endBacktracking - startBacktracking;
                backtrackingTimes.add(timeBacktracking);

                boolean isPassedBacktracking = resultBacktracking == testCase.expectedResult;
                if (isPassedBacktracking) {
                    winsBacktracking++;
                } else {
                    lossesBacktracking++;
                }

                writer.write(String.format("Backtracking Algorithm:\nExecution Time: %d ns\tExpected: %d\tActual: %d\tResult: %s\n",
                        timeBacktracking, testCase.expectedResult, resultBacktracking, isPassedBacktracking ? "Passed" : "Failed"));

                writer.write("+----------------------------------------------------------------+\n");}


            writer.write("\n+------------------------+-------------------+-------------------+\n");
            writer.write("| Statistic              | A* Algorithm      | Backtracking      |\n");
            writer.write("+------------------------+-------------------+-------------------+\n");
            writer.write(String.format("| Mean Execution Time    | %14.2f ns | %14.2f ns |\n",
                    Statistics.mean(aStarTimes), Statistics.mean(backtrackingTimes)));
            writer.write(String.format("| Median Execution Time  | %14.2f ns | %14.2f ns |\n",
                    Statistics.median(aStarTimes), Statistics.median(backtrackingTimes)));
            writer.write(String.format("| Mode Execution Time    | %12d ns   | %12d ns   |\n",
                    Statistics.mode(aStarTimes), Statistics.mode(backtrackingTimes)));
            writer.write(String.format("| Standard Deviation     | %14.2f ns | %14.2f ns |\n",
                    Statistics.standardDeviation(aStarTimes), Statistics.standardDeviation(backtrackingTimes)));
            writer.write(String.format("| Wins                   | %13d     | %13d     |\n", winsAStar, winsBacktracking));
            writer.write(String.format("| Losses                 | %13d     | %13d     |\n", lossesAStar, lossesBacktracking));
            writer.write(String.format("| Win Percentage         | %14.2f %%  | %14.2f %%  |\n",
                    (winsAStar * 100.0 / testCases.size()), (winsBacktracking * 100.0 / testCases.size())));
            writer.write(String.format("| Loss Percentage        | %14.2f %%  | %14.2f %%  |\n",
                    (lossesAStar * 100.0 / testCases.size()), (lossesBacktracking * 100.0 / testCases.size())));
            writer.write("+------------------------+-------------------+-------------------+\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String testFilePath = "src/unsolvable_maps.txt";
        String resultFilePath = "src/unsolvable_maps_results.txt";
        runTestsAndAnalyze(testFilePath, resultFilePath);
    }
}

