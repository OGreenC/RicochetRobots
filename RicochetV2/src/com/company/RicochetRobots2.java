package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static com.company.RicochetRobots2.board;
import static com.company.RicochetRobots2.robots;

public class RicochetRobots2 {

    /**
     * GEM ROBOTS SOM EN INTEGER I STEDET! NUMMERER ALLE POSITIONER LEFT TO RIGHT EACH ROW
     */


    public static int N;
    public static int R;
    public static Node[][] board;
    public static short[] robots;
    public static Node goal;

    public static void main(String[] args) throws IOException {
        readInput(); //Read data from Console, and generate board

        calculatePath(); //Run path finding algorithm.

    }

    public static void calculatePath() {
        Path path = pathBFS(); //run BFS algorithm.

        //Print path from String, steps seperated with spaces
        String[] pathSteps = path.path.split(" ");
        for(int i = 1; i < pathSteps.length; i++) {
            System.out.println(pathSteps[i]);
        }
    }

    public static Path pathBFS() {
        Queue<Path> q = new LinkedList<>();

        q.add(new Path(robots)); //Add initial path array, with initial robot placement.

        //Modified BFS, as we have to test every scenario.
        //Stop when queue is empty, or path found (stop BFS when first (and thus fastest) path found)
        Path foundPath = null;
        while(!q.isEmpty() && foundPath == null) {
            Path p = q.remove(); //Pop path from queue

            //Iterate over all robots (i=i+2 as 1 robot takes 2 spaces in array)
            for(int i = 0; i < p.robots.length; i=i+2) {
                //Get node robot is on from board 2D array:
                Node n = board[p.robots[i]][p.robots[i+1]];

                //Visiting upwards
                Node nU = n.getStopNodeU(p.robots);
                if(nU != null) {
                    short[] updatedRobotLoc = Arrays.copyOf(p.robots,p.robots.length);
                    updatedRobotLoc[i] = nU.y;
                    updatedRobotLoc[i + 1] = nU.x;
                    Path newPath = new Path(updatedRobotLoc);
                    newPath.path = p.path + " " + i/2 + 'U';
                    if(nU == goal && i/2 == 0) {
                        foundPath = newPath;
                        break;
                    }

                    q.add(newPath);

                }

                //Visiting downwards
                Node nD = n.getStopNodeD(p.robots);
                if(nD != null) {
                    short[] updatedRobotLoc = Arrays.copyOf(p.robots,p.robots.length);
                    updatedRobotLoc[i] = nD.y;
                    updatedRobotLoc[i + 1] = nD.x;
                    Path newPath = new Path(updatedRobotLoc);
                    newPath.path = p.path  + " " + i/2 + 'D';
                    if(nD == goal && i/2 == 0) {
                        foundPath = newPath;
                        break;
                    }
                    q.add(newPath);
                }

                //Visiting Left
                Node nL = n.getStopNodeL(p.robots);
                if(nL != null) {
                    short[] updatedRobotLoc = Arrays.copyOf(p.robots,p.robots.length);
                    updatedRobotLoc[i] = nL.y;
                    updatedRobotLoc[i + 1] = nL.x;
                    Path newPath = new Path(updatedRobotLoc);
                    newPath.path = p.path + " " + i/2 + 'L';
                    if(nL == goal && i/2 == 0) {
                        foundPath = newPath;
                        break;
                    }

                    q.add(newPath);
                }

                //Visiting Right
                Node nR = n.getStopNodeR(p.robots);
                if(nR != null) {
                    short[] updatedRobotLoc = Arrays.copyOf(p.robots,p.robots.length);
                    updatedRobotLoc[i] = nR.y;
                    updatedRobotLoc[i + 1] = nR.x;
                    Path newPath = new Path(updatedRobotLoc);
                    newPath.path = p.path  + " " + i/2 + 'R';
                    if(nR == goal && i/2 == 0) {
                        foundPath = newPath;
                        break;
                    }

                    q.add(newPath);
                }
            }
        }
        //System.out.println("BFS DONE: " + q.size() + " : " + (foundPath == null));
        return foundPath;
    }




    /**
     * Read Console Input
     *
     * Reads data from Console and initializes public map variables.
     * @throws IOException
     */

    public static void readInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        N = Integer.parseInt(br.readLine());
        R = Integer.parseInt(br.readLine());

        board = new Node[N][N]; //(1 for air : 0 for wall)
        robots = new short[2*R];  //([com.company.Robot][0: x, 1: y])

        //Run through map generation. Following inputs are to be expected:
        // 35 = '#'
        // 32 = ' '
        // 71 = 'G'
        // 10 = newline
        // 13 = Carriage return (Dont know what this is / Ignore)
        for(short y = 0; y < N; y++) {
            for(short x = 0; x < N; x++) {
                int input = br.read();

                if(input == 10 || input == 13) { //Ignore newline and Carriage return
                    x--;
                    continue;
                }

                if(input == 32) { // ' ' (open field)
                    board[y][x] = new Node(y, x);
                } else if(input == 35) { // '#' (Wall field)
                    //Walls do not have a Node, but are used to set D and R nodes on all already existing Nodes:
                    if(y - 1 > 0 && board[y - 1][x] != null) {
                        for(int ty = y - 2; ty >= 0; ty--) { //All nodes above (above = lower y)
                            if(board[ty][x] == null) {
                                break;
                            }
                            board[ty][x].D = board[y - 1][x];
                        }
                    }
                    //East com.company.Node
                    if(x - 1 > 0 && board[y][x - 1] != null) {
                        for(int tx = x - 2; tx >= 0; tx--) { //All to the left (left = lower x)
                            if(board[y][tx] == null) {
                                break;
                            }
                            board[y][tx].R = board[y][x - 1];
                        }
                    }
                } else if(input == 71) { // 'G' (Goal field)
                    Node n = new Node(y, x);
                    board[y][x] = n;
                    goal = n;
                } else { // Rest should be numbers from 0 to 9 (Robots)
                    Node node = new Node(y,x);
                    board[y][x] = node;
                    robots[(input-48)*2] = y;
                    robots[(input-48)*2 + 1] = x;
                }
            }
        }

        //Only thing left, is to run the last row/col, to set R and D.
        //Right Border
        for(int ty = 0; ty < N; ty++) {
            if(board[ty][N-1] == null) continue;
            for(int tx = N-2; tx >= 0; tx--) {
                if(board[ty][tx] == null) break;
                board[ty][tx].R = board[ty][N-1];
            }
        }
        //Down Border
        for(int tx = 0; tx < N; tx++) {
            if(board[N-1][tx] == null) continue;
            for(int ty = N-2; ty >= 0; ty--) {
                if(board[ty][tx] == null) break;
                board[ty][tx].D = board[N-1][tx];
            }
        }
    }
}

class Path {
    public String path = "";
    public short[] robots;

    public Path(short[] robots) {
        this.robots = robots;
    }
}



class Node {
    public Node U,R,D,L = null;
    public short x,y;

    public Node(short y, short x) {

        this.x = x;
        this.y = y;

        //Get parent nodes top (first wall directly up)
        short ty = (short) (y-1);
        if(ty >= 0 && board[ty][x] != null) {
            while(ty >= 0) {
                if(board[ty][x] == null && ty + 1 != y) {
                    U = board[ty + 1][x];
                    break;
                }
                if(ty == 0) U = board[0][x];
                ty--;
            }
        }
        //Get parent nodes left (first wall directly left)
        short tx = (short) (x-1);
        if(tx >= 0 && board[y][tx] != null) {
            while(tx >= 0) {
                if(board[y][tx] == null && tx + 1 != x) {
                    L = board[y][tx + 1];
                    break;
                }
                if(tx == 0) L = board[y][0];
                tx--;
            }
        }
    }


    //Når pNode er defineret til this.u, så ændre jeg vel også på this.u når jeg ændre på pNode senere hen??

    //check if nessecary to stop earlier
    public Node getStopNodeU(short[] robots) {
        if(this.U == null) return null;
        short y = U.y;
        short x = U.x;
        for(int i = 0; i < robots.length; i = i + 2) { //Getting robot indexes (looping through all robots)
            if(this.x != robots[i + 1] || this.y <= robots[i]) continue; //If not same y col or below or equal node (equal means robot is standing there,and is the moving robot)
            if(y <= robots[i]) { // if robot is between else defined goToNode and current node:
                y = (short) (robots[i] + 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeD(short[] robots) {
        if(this.D == null) return null;
        short y = D.y;
        short x = D.x;
        for(int i = 0; i < robots.length; i = i + 2) {
            if(this.x != robots[i + 1] || this.y >= robots[i]) continue;
            if(y >= robots[i]) {
                y = (short) (robots[i] - 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeL(short[] robots) {
        if(this.L == null) return null;
        short y = L.y;
        short x = L.x;
        for(int i = 0; i < robots.length; i = i + 2) {
            if(this.y != robots[i] || this.x <= robots[i + 1]) continue;
            if(x <= robots[i + 1]) {
                x = (short) (robots[i + 1] + 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeR(short[] robots) {
        if(this.R == null) return null;
        short y = R.y;
        short x = R.x;
        for(int i = 0; i < robots.length; i = i + 2) {
            if(this.y != robots[i] || this.x >= robots[i + 1]) continue;
            if(x >= robots[i + 1]) {
                x = (short) (robots[i + 1] - 1);
                continue;
            }
        }
        return board[y][x];
    }
}

