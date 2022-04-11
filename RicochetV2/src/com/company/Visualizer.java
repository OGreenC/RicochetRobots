package com.company;

import java.awt.*;
import java.util.Arrays;

public class Visualizer {

    public Node[][] board;
    public Robot[] robots;
    public Node goal;

    public String[][] nodeBoard;

    public Visualizer(Node[][] board, Robot[] robots, Node goal) {
        this.board = board;
        this.robots = robots;
        this.goal = goal;
    }

    public void createNodeBoard() {
        int N = board.length;
        for (int y = 0; y < N+2; y++) {
            for (int x = 0; x < N+2; x++) {
                if((y == 0 || y == N+1) && (x == 0 || x == N+1)) {
                    nodeBoard[y][x] = " * ";
                    continue;
                }
                if(y == 0 || y == N+1) {
                    nodeBoard[y][x] = "---";
                    continue;
                }
                if(x == 0 || x == N+1) {
                    nodeBoard[y][x] = " | ";
                    continue;
                }
                nodeBoard[y][x] = (board[y-1][x-1] == null) ? " # " : "   ";
            }
        }
    }


    public void printMap() {
        //Create map
        String[][] viewBoard = Arrays.copyOf(nodeBoard,nodeBoard.length);

        //Insert Goal and robots:
        viewBoard[goal.x+1][goal.y+1] = " G ";
        for (Robot r : robots) {
            //viewBoard[r.getY() + 1][r.getX() + 1] = " " + ((char) (r.id + 48)) + " ";
        }

        //print map
        for (String[] cRow: viewBoard) {
            for (String c: cRow) {
                System.out.print(c);
            }
            System.out.println();
        }
    }
}
