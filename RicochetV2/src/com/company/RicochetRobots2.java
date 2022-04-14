package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import static com.company.RicochetRobots2.board;

public class RicochetRobots2 {

    public static int N;
    public static int R;
    public static Node[][] board;
    public static Robot[] robots;
    public static Node goal;
    public static Queue<Path> q = new LinkedList<>();
    public static Path foundPath = null;


    public static void main(String[] args) throws IOException {
        readInput(); //Read data from Console, and generate board
        calculatePath(); //Run path finding algorithm.
    }

    public static void calculatePath() {
        Path path = pathBFS(); //run BFS algorithm.

        //Print path from String, steps seperated with spaces
        ArrayList<String> pathSteps = new ArrayList<>();
        Path pathStep = path;
        while(pathStep.parent != null) {
            pathSteps.add("" + pathStep.moveNum + pathStep.moveDir);
            pathStep = pathStep.parent;
        }
        for(int i = pathSteps.size() - 1; i >= 0; i--) {
            System.out.println(pathSteps.get(i));
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
                Robot r = p.robots[i];

                boolean doBackCheck = p.moveNum == r.num;

                //Visiting upwards
                if(!(doBackCheck && p.moveDir == 'D')) {
                    visitNode(r.node.getStopNodeU(p.robots), p, i, 'U');
                }

                //Visiting downwards
                if(!(doBackCheck && p.moveDir == 'U')) {
                    visitNode(r.node.getStopNodeD(p.robots), p, i, 'D');
                }

                //Visiting Left
                if(!(doBackCheck && p.moveDir == 'R')) {
                    visitNode(r.node.getStopNodeL(p.robots), p, i, 'L');
                }

                //Visiting Right
                if(!(doBackCheck && p.moveDir == 'L')) {
                    visitNode(r.node.getStopNodeR(p.robots), p, i, 'R');
                }
            }
        }
        return foundPath;
    }

    public static void visitNode(Node visitNode, Path path, int robotN, char dirChar) {
        if(visitNode != null) {

            Robot[] updatedRobotLoc = new Robot[R];
            for(int i = 0; i < R; i++) {
                updatedRobotLoc[i] = new Robot(path.robots[i].node,path.robots[i].num);
            }

            //Sort array;
            int i = robotN;
            Robot tR = updatedRobotLoc[i];
            //Sort higher
            if(robotN > 0 && robotN < (R - 1) && visitNode.compareTo(updatedRobotLoc[i + 1].node) > 0 ) {
                for(i = robotN; i < R-1 && visitNode.compareTo(updatedRobotLoc[i+1].node) > 0; i++) {
                    updatedRobotLoc[i] = updatedRobotLoc[i+1];
                }
            //Sort lower
            } else if(robotN > 1 && visitNode.compareTo(updatedRobotLoc[i - 1].node) < 0) {
                for(i = robotN; i > 1 && visitNode.compareTo(updatedRobotLoc[i-1].node) < 0 ; i--) {
                    updatedRobotLoc[i] = updatedRobotLoc[i-1];
                }
            }
            tR.node = visitNode;
            updatedRobotLoc[i] = tR;

            MapLayout mapLayout = new MapLayout(updatedRobotLoc);
            if(updatedRobotLoc[0].node.seenLayouts.add(mapLayout)) {
                Path newPath = new Path(updatedRobotLoc);
                newPath.moveDir = dirChar;
                newPath.moveNum = updatedRobotLoc[i].num;
                newPath.parent = path;
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
        robots = new Robot[R];  //([com.company.Robot][0: x, 1: y])

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
                    robots[(input-48)] = new Robot(node, (input-48));
                }
            }
        }
        if(R > 1) {
            Robot[] sentinelRobots = new Robot[R-1];
            for(int j = 1; j < R; j++) {
                sentinelRobots[j-1] = robots[j];
            }
            Arrays.sort(sentinelRobots);
            for(int j = 0; j < sentinelRobots.length; j++) {
                robots[j+1] = sentinelRobots[j];
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
    public Robot[] robots;
    public Path parent = null;
    public char moveDir;
    public int moveNum;

    public Path(Robot[] robots) {
        this.robots = robots;
    }
}

class Robot implements Comparable<Robot> {
    public Node node;
    public int num;

    public Robot(Node n, int num) {
        this.node = n;
        this.num = num;
    }

    @Override
    public int compareTo(Robot that) {
        return this.node.compareTo(that.node);
    }

}

class MapLayout {
    int hashCode = 0;
    Robot[] robotArray;

    public MapLayout(Robot[] robots) {
        robotArray = robots;
        int l = robotArray.length;
        for(int i = 1; i < l; i++) {
            hashCode = 37 * hashCode + (robotArray[i].node.y * 1000 + robotArray[i].node.x);
        }
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        int l = robotArray.length;
        MapLayout other = (MapLayout) o;
        for(int i = 1; i < l; i++) {
            if(this.robotArray[i].node != other.robotArray[i].node) {
                return false;
            }
        }
        return true;
    }
}


class Node implements Comparable<Node> {
    public Node U,R,D,L = null;
    public short x,y;
    public Set <MapLayout> seenLayouts = new HashSet<MapLayout>();

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
    public Node getStopNodeU(Robot[] robots) {
        if(this.U == null) return null;
        short y = U.y;
        short x = U.x;
        for(int i = 0; i < robots.length; i++) { //Getting robot indexes (looping through all robots)
            if(this.x != robots[i].node.x || this.y <= robots[i].node.y) continue; //If not same y col or below or equal node (equal means robot is standing there,and is the moving robot)
            if(y <= robots[i].node.y) { // if robot is between else defined goToNode and current node:
                y = (short) (robots[i].node.y + 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeD(Robot[] robots) {
        if(this.D == null) return null;
        short y = D.y;
        short x = D.x;
        for(int i = 0; i < robots.length;i++) {
            if(this.x != robots[i].node.x || this.y >= robots[i].node.y) continue;
            if(y >= robots[i].node.y) {
                y = (short) (robots[i].node.y - 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeL(Robot[] robots) {
        if(this.L == null) return null;
        short y = L.y;
        short x = L.x;
        for(int i = 0; i < robots.length;i++) {
            if(this.y != robots[i].node.y || this.x <= robots[i].node.x) continue;
            if(x <= robots[i].node.x) {
                x = (short) (robots[i].node.x + 1);
                continue;
            }
        }
        return board[y][x];
    }
    public Node getStopNodeR(Robot[] robots) {
        if(this.R == null) return null;
        short y = R.y;
        short x = R.x;
        for(int i = 0; i < robots.length;i++) {
            if(this.y != robots[i].node.y || this.x >= robots[i].node.x) continue;
            if(x >= robots[i].node.x) {
                x = (short) (robots[i].node.x - 1);
                continue;
            }
        }
        return board[y][x];
    }

}
