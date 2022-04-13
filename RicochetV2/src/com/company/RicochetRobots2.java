package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.company.RicochetRobots2.board;

public class RicochetRobots2 {

    /**
     * GEM ROBOTS SOM EN INTEGER I STEDET! NUMMERER ALLE POSITIONER LEFT TO RIGHT EACH ROW
     */


    public static int N;
    public static int R;
    public static Node[][] board;
    //public static short[] robots;
    public static Node[] robots;
    public static Node goal;
    public static Queue<Path> q = new LinkedList<>();
    public static Path foundPath = null;

    public static void main(String[] args) throws IOException {
        readInput(); //Read data from Console, and generate board



        Set <Node> set1 = new HashSet<Node>();
        set1.add(new Node((short) 4,(short) 3));
        set1.add(new Node((short) 1,(short) 2));
        set1.add(new Node((short) 2,(short) 3));

        Set <Node> set2 = new HashSet<Node>();
        set2.add(new Node((short) 4,(short) 3));
        set2.add(new Node((short) 1,(short) 2));
        set2.add(new Node((short) 2,(short) 3));

        System.out.println("set1: " + set1.hashCode());
        System.out.println("set2: " + set2.hashCode());
        System.out.println("set1: " + set1.hashCode());


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

        q.add(new Path(robots)); //Add initial path array, with initial robot placement.

        //Modified BFS, as we have to test every scenario.
        //Stop when queue is empty, or path found (stop BFS when first (and thus fastest) path found)
        while(!q.isEmpty() && foundPath == null) {
            Path p = q.remove(); //Pop path from queue

            //Iterate over all robots (i=i+2 as 1 robot takes 2 spaces in array)
            for(int i = 0; i < R; i++) {
                Node n = p.robots[i];

                //Visiting upwards
                visitNode(n.getStopNodeU(p.robots), p, i, 'U');

                //Visiting downwards
                visitNode(n.getStopNodeD(p.robots), p, i, 'D');

                //Visiting Left
                visitNode(n.getStopNodeL(p.robots), p, i, 'L');

                //Visiting Right
                visitNode(n.getStopNodeR(p.robots), p, i, 'R');

            }
        }
        return foundPath;
    }

    public static void visitNode(Node visitNode, Path path, int robotN, char dirChar) {
        if(visitNode != null) {
            Node[] updatedRobotLoc = new Node[R];
            Node[] setArray = new Node[R - 1];

            updatedRobotLoc[0] = path.robots[0];
            for(int j = 1; j < R; j++) {
                updatedRobotLoc[j] = path.robots[j];
                setArray[j] = path.robots[j];
            }
            Arrays.sort(setArray);

            Node[] updatedRobotLoc = Arrays.copyOf(path.robots,path.robots.length);
            updatedRobotLoc[robotN] = visitNode;
            Set <Node> set = new HashSet<Node>();
            for(int j = 1; j < R; j++) {
                set.add(updatedRobotLoc[j]);
            }

            if(updatedRobotLoc[0].seenLayouts.add(set)) {

                Path newPath = new Path(updatedRobotLoc);
                newPath.path = path.path + " " + robotN + dirChar;
                if (visitNode == goal && robotN == 0) {
                    foundPath = newPath;
                }
                q.add(newPath);
            }
        }
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
        robots = new Node[R];  //([com.company.Robot][0: x, 1: y])

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
                    robots[(input-48)] = node;
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
    public Node[] robots;

    public Path(Node[] robots) {
        this.robots = robots;
    }
}

class mapLayout {
    String key;


    public mapLayout(String key) {

    }



}


class Node implements Comparable<Node> {
    public Node U,R,D,L = null;
    public short x,y;
    public Set <Set<Node>> seenLayouts = new HashSet<Set<Node>>();

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

    @Override
    public int compareTo(Node that) {
        //returns -1 if "this" object is less than "that" object
        //returns 0 if they are equal
        //returns 1 if "this" object is greater than "that" object
        if(this.y < that.y) return -1;
        if(this.y > that.y) return 1;
        if(this.x < that.x) return -1;
        if(this.x > that.x) return 1;
        return 0;
    }

    //check if nessecary to stop earlier
    public Node getStopNodeU(Node[] robots) {
        if(this.U == null) return null;
        short y = U.y;
        short x = U.x;
        for(int i = 0; i < robots.length; i++) { //Getting robot indexes (looping through all robots)
            if(this.x != robots[i].x || this.y <= robots[i].y) continue; //If not same y col or below or equal node (equal means robot is standing there,and is the moving robot)
            if(y <= robots[i].y) { // if robot is between else defined goToNode and current node:
                y = (short) (robots[i].y + 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeD(Node[] robots) {
        if(this.D == null) return null;
        short y = D.y;
        short x = D.x;
        for(int i = 0; i < robots.length;i++) {
            if(this.x != robots[i].x || this.y >= robots[i].y) continue;
            if(y >= robots[i].y) {
                y = (short) (robots[i].y - 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeL(Node[] robots) {
        if(this.L == null) return null;
        short y = L.y;
        short x = L.x;
        for(int i = 0; i < robots.length;i++) {
            if(this.y != robots[i].y || this.x <= robots[i].x) continue;
            if(x <= robots[i].x) {
                x = (short) (robots[i].x + 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeR(Node[] robots) {
        if(this.R == null) return null;
        short y = R.y;
        short x = R.x;
        for(int i = 0; i < robots.length;i++) {
            if(this.y != robots[i].y || this.x >= robots[i].x) continue;
            if(x >= robots[i].x) {
                x = (short) (robots[i].x - 1);
                continue;
            }
        }
        return board[y][x];
    }

}

