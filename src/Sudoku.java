/////////////////////////////////////////////////////////////////////////////////
// CS 430 - Artificial Intelligence
// Project 4 - Sudoku Solver w/ Variable Ordering and Forward Checking
// File: Sudoku.java
//
// Group Member Names: Kyle Nakamura and Joshua Sasaki
// Due Date: Today
// 
//
// Description: A Backtracking program in Java to solve the Sudoku problem.
// Code derived from a C++ implementation at:
// http://www.geeksforgeeks.org/backtracking-set-7-suduku/
/////////////////////////////////////////////////////////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Random;


public class Sudoku
{
    // Constants
    final static int UNASSIGNED = 0; //UNASSIGNED is used for empty cells in sudoku grid
    final static int N = 9;//N is used for size of Sudoku grid. Size will be NxN
    static int numBacktracks = 0;

    /////////////////////////////////////////////////////////////////////
    // Main function used to test solver.
    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner scan = new Scanner(System.in);

        // Reads in from TestCase.txt (sample sudoku puzzle).
        // 0 means unassigned cells - You can search the internet for more test cases.
        System.out.println("Which test case would you like to use?");
        System.out.println("1.) Case1.txt\n" +
                "2.) Case2.txt\n" +
                "3.) Case3.txt\n" +
                "4.) Case4.txt\n" +
                "5.) Case5.txt");
        int fileSelection = scan.nextInt();

        File file;
        switch (fileSelection) {
            case 1:
                file = new File("Case1.txt");
                break;
            case 2:
                file = new File("Case2.txt");
                break;
            case 3:
                file = new File("Case3.txt");
                break;
            case 4:
                file = new File("Case4.txt");
                break;
            case 5:
                file = new File("Case5.txt");
                break;
            default:
                file = new File("Case1.txt");
                System.out.println("No file selected; using default file: Case1.txt");
                break;
        }
        Scanner fileScan = new Scanner(file);

        // Reads case into grid 2D int array
        int grid[][] = new int[9][9];
        for (int r = 0; r < 9; r++)
        {
            String row = fileScan.nextLine();
            String [] cols = row.split(",");
            for (int c = 0; c < cols.length; c++)
                grid[r][c] = Integer.parseInt(cols[c].trim());
        }

        // Prints out the unsolved sudoku puzzle (as is)
        System.out.println("Unsolved sudoku puzzle:");
        printGrid(grid);

        // Setup timer - Obtain the time before solving
        long stopTime = 0L;
        long startTime = System.currentTimeMillis();

        // Attempts to solve and prints results
        System.out.println("Which ordering would you like to use?");
        System.out.println("1.) Default static ordering\n" +
                "2.) Your original static ordering\n" +
                "3.) Random ordering\n" +
                "4.) Minimum Remaining Values Ordering\n" +
                "5.) Maximum Remaining Values Ordering");
        int variableChoice = scan.nextInt();

        System.out.println("Choose an interface method:\n" +
                "1.) None\n" +
                "2.) Forward Checking\n");
        int interfaceChoice = scan.nextInt();

        if (SolveSudoku(grid, variableChoice, interfaceChoice) == true)
        {
            // Get stop time once the algorithm has completed solving the puzzle
            stopTime = System.currentTimeMillis();
            System.out.println("Algorithmic runtime: " + (stopTime - startTime) + "ms");
            System.out.println("Number of backtracks: " + numBacktracks);

            // Sanity check to make sure the computed solution really IS solved
            if (!isSolved(grid))
            {
                System.err.println("An error has been detected in the solution.");
                System.exit(0);
            }
            System.out.println("\n\nSolved sudoku puzzle:");
            printGrid(grid);
        }
        else
            System.out.println("No solution exists");
    }

    /////////////////////////////////////////////////////////////////////
    // Write code here which returns true if the sudoku puzzle was solved
    // correctly, and false otherwise. In short, it should check that each
    // row, column, and 3x3 square of 9 cells maintain the ALLDIFF constraint.
    private static boolean isSolved(int[][] grid) {
        // Checking each row
        for (int i=0; i<9; i++) {
            Set rowSet = new HashSet();    // New set per row
            for (int j=0; j<9; j++) {
                rowSet.add(grid[i][j]);
            }

            // Validate that row contains every number 1 to 9
            for (int k = 1; k <= 9; k++)
                if (!rowSet.contains(k))
                    return false;
        }

        // Checking each column
        for (int i=0; i<9; i++) {
            Set colSet = new HashSet();    // New set per row
            for (int j=0; j<9; j++) {
                colSet.add(grid[j][i]);
            }

            // Validate that column contains every number 1 to 9
            for (int k = 1; k <= 9; k++)
                if (!colSet.contains(k))
                    return false;
        }

        // Checking each 3x3 grid
        for (int bigRow=0; bigRow<=2; bigRow++) {
            for (int i = bigRow * 3; i <= bigRow * 3 + 2; i++) {
                Set gridSet = new HashSet();    // New set per 3x3 grid
                for (int bigCol=0; bigCol<=2; bigCol++) {
                    for (int j = bigCol * 3; j <= bigCol * 3 + 2; j++)
                        gridSet.add(grid[i][j]);
                }

                // Validate that 3x3 grid contains 9 unique ints
                if (gridSet.size() != 9)
                    return false;

                // Validate that 3x3 grid contains every number 1 to 9
                for (int k = 1; k <= 9; k++)
                    if (!gridSet.contains(k))
                        return false;
            }
        }

        return true;
    }

    /////////////////////////////////////////////////////////////////////
    // Takes a partially filled-in grid and attempts to assign values to
    // all unassigned locations in such a way to meet the requirements
    // for Sudoku solution (non-duplication across rows, columns, and boxes)
    /////////////////////////////////////////////////////////////////////
    static boolean SolveSudoku(int grid[][], int variableChoice, int interfaceChoice)
    {
        // Select next unassigned variable
        SudokuCoord variable;
        variable = FindUnassignedVariable(grid);

        switch (variableChoice) {
            case 1:
                variable = FindUnassignedVariable(grid);
                break;
            case 2:
                variable = MyOriginalStaticOrderingOpt2(grid);
                break;
            case 3:
                variable = MyOriginalRandomOrderingOpt3(grid);
                break;
            case 4:
                variable = MyMinRemainingValueOrderingOpt4(grid);
                break;
            case 5:
                variable = MyMaxRemainingValueOrderingOpt5(grid);
                break;
            default:
                System.out.println("INPUT AN INTEGER FROM 1 TO 5");
                break;
        }

        // If there is no unassigned location, we are done
        if (variable == null)
            return true; // success!

        int row = variable.row;
        int col = variable.col;

        // consider digits 1 to 9
        for (int num = 1; num <= 9; num++)
        {
            // if looks promising
            if (isSafe(grid, row, col, num))
            {
                // make tentative assignment
                grid[row][col] = num;

                // return, if success, yay!
                if (SolveSudoku(grid, variableChoice, interfaceChoice))
                    return true;

                // failure, un-assign & try again
                grid[row][col] = UNASSIGNED;
            }
        }

        // Increment the number of backtracks
        numBacktracks++;
        return false; // This triggers backtracking
    }

    /////////////////////////////////////////////////////////////////////
    // Searches the grid to find an entry that is still unassigned. If
    // found, the reference parameters row, col will be set the location
    // that is unassigned, and true is returned. If no unassigned entries
    // remain, null is returned.
    /////////////////////////////////////////////////////////////////////
    static SudokuCoord FindUnassignedVariable(int grid[][])
    {
        for (int row = 0; row < N; row++)
            for (int col = 0; col < N; col++)
                if (grid[row][col] == UNASSIGNED)
                    return new SudokuCoord(row, col);
        return null;
    }

    /////////////////////////////////////////////////////////////////////
    // TODO: Implement the following orderings, as specified in the
    // project description. You MAY feel free to add extra parameters if
    // needed (you shouldn't need to for the first two, but it may prove
    // helpful for the last two methods).
    /////////////////////////////////////////////////////////////////////
    static SudokuCoord MyOriginalStaticOrderingOpt2(int grid[][]) {
        for (int row = 0; row < N; row++)
            for (int col = 0; col < N; col++)
                if (grid[col][row] == UNASSIGNED)
                    return new SudokuCoord(col, row);
        return null;
    }
    static SudokuCoord MyOriginalRandomOrderingOpt3(int grid[][]) {
        while (!isSolved(grid)) {
            Random rand = new Random();
            int row = rand.nextInt(8) + 1;
            int col = rand.nextInt(8) + 1;
            if (grid[row][col] == UNASSIGNED)
                return new SudokuCoord(row, col);
        }
        return null;
    }
    static SudokuCoord MyMinRemainingValueOrderingOpt4(int grid[][]) {
        int[][] remainingValues = new int [9][9];
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                remainingValues[i][j] = 0;

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (grid[row][col] == UNASSIGNED) {
                    for (int num = 1; num <= N; num++) {
                        if (isSafe(grid, row, col, num)) {
                            remainingValues[row][col]++;    // increment remainingValues for the coordinate
                        }
                    }
                }
            }
        }

        int minRemaining = 999;
        SudokuCoord coordWithMinValues = new SudokuCoord(); // instantiate temp coordinate
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (remainingValues[row][col] != 0 && remainingValues[row][col] < minRemaining) {
                    minRemaining = remainingValues[row][col];
                    coordWithMinValues = new SudokuCoord(row, col); // update temp coordinate
                }
            }
        }

        if (!isSolved(grid)) {
            return coordWithMinValues;  // keep searching for min value
        } else {
            return null;    // all values assigned
        }
    }
    static SudokuCoord MyMaxRemainingValueOrderingOpt5(int grid[][]) {
        int[][] remainingValues = new int [9][9];
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                remainingValues[i][j] = 0;

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (grid[row][col] == UNASSIGNED) {
                    for (int num = 1; num <= N; num++) {
                        if (isSafe(grid, row, col, num)) {
                            remainingValues[row][col]++;    // increment remainingValues for the coordinate
                        }
                    }
                }
            }
        }

        int maxRemaining = -999;
        SudokuCoord coordWithMaxValues = new SudokuCoord(); // instantiate temp coordinate
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (remainingValues[row][col] != 0 && remainingValues[row][col] > maxRemaining) {
                    maxRemaining = remainingValues[row][col];
                    coordWithMaxValues = new SudokuCoord(row, col); // update temp coordinate
                }
            }
        }

        if (!isSolved(grid)) {
            return coordWithMaxValues;  // keep searching for min value
        } else {
            return null;    // all values assigned
        }
    }

    /////////////////////////////////////////////////////////////////////
    // Returns a boolean which indicates whether any assigned entry
    // in the specified row matches the given number.
    /////////////////////////////////////////////////////////////////////
    static boolean UsedInRow(int grid[][], int row, int num)
    {
        for (int col = 0; col < N; col++)
            if (grid[row][col] == num)
                return true;
        return false;
    }

    /////////////////////////////////////////////////////////////////////
    // Returns a boolean which indicates whether any assigned entry
    // in the specified column matches the given number.
    /////////////////////////////////////////////////////////////////////
    static boolean UsedInCol(int grid[][], int col, int num)
    {
        for (int row = 0; row < N; row++)
            if (grid[row][col] == num)
                return true;
        return false;
    }

    /////////////////////////////////////////////////////////////////////
    // Returns a boolean which indicates whether any assigned entry
    // within the specified 3x3 box matches the given number.
    /////////////////////////////////////////////////////////////////////
    static boolean UsedInBox(int grid[][], int boxStartRow, int boxStartCol, int num)
    {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (grid[row+boxStartRow][col+boxStartCol] == num)
                    return true;
        return false;
    }

    /////////////////////////////////////////////////////////////////////
    // Returns a boolean which indicates whether it will be legal to assign
    // num to the given row, col location.
    /////////////////////////////////////////////////////////////////////
    static boolean isSafe(int grid[][], int row, int col, int num)
    {
        // Check if 'num' is not already placed in current row,
        // current column and current 3x3 box
        return !UsedInRow(grid, row, num) &&
                !UsedInCol(grid, col, num) &&
                !UsedInBox(grid, row - row%3 , col - col%3, num);
    }

    /////////////////////////////////////////////////////////////////////
    // A utility function to print grid
    /////////////////////////////////////////////////////////////////////
    static void printGrid(int grid[][])
    {
        for (int row = 0; row < N; row++)
        {
            for (int col = 0; col < N; col++)
            {
                if (grid[row][col] == 0)
                    System.out.print("- ");
                else
                    System.out.print(grid[row][col] + " ");

                if ((col+1) % 3 == 0)
                    System.out.print(" ");
            }
            System.out.print("\n");
            if ((row+1) % 3 == 0)
                System.out.println();
        }
    }
}