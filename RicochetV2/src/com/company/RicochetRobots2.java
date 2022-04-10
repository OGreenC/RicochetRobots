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

    public static int N;
    public static int R;
    public static Node[][] board;
    public static short[] robots;
    public static Node goal;

    public static void main(String[] args) throws IOException {
        readInput();

        calculatePath(false);

    }

    public static void calculatePath(boolean debug) {

        if(debug) printMap();

        //System.out.println(board[robots[0]][robots[1]].getStopNodeU(robots).x + " " + board[robots[0]][robots[1]].getStopNodeU(robots).y);
        //System.out.println(goal.x + " - " + goal.y);
        //System.out.println(goal.pathParent.x + " - " + goal.pathParent.y);


        Path path = nodeBFS();

        String[] pathSteps = path.path.split(" ");
        for(int i = 1; i < pathSteps.length; i++) {
            System.out.println(pathSteps[i]);
        }

        if(debug) printMap();

    }


    //public static Queue<Node> q = new LinkedList<>();
    public static Queue<Path> q = new LinkedList<>();

    public static Path nodeBFS() {
        q.add(new Path(Arrays.copyOf(robots,robots.length)));

        Path foundPath = null;
        while(!q.isEmpty() && foundPath == null) {
            Path p = q.remove();

            for(int i = 0; i < p.robots.length; i=i+2) { //Iterate over all robots for each node in each part of the path.
                Node n = board[p.robots[i]][p.robots[i+1]];

                //Visiting upwards
                Node nU = n.getStopNodeU(p.robots);
                if(nU != null) {
                    short[] updatedRobotLoc = Arrays.copyOf(p.robots,p.robots.length);
                    updatedRobotLoc[i] = nU.y;
                    updatedRobotLoc[i + 1] = nU.x;
                    Path newPath = new Path(updatedRobotLoc);
                    newPath.path = p.path;
                    newPath.addPath(i/2,'U');

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
                    newPath.path = p.path;
                    newPath.addPath(i/2,'D');

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
                    newPath.path = p.path;
                    newPath.addPath(i/2,'L');

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
                    newPath.path = p.path;
                    newPath.addPath(i/2,'R');

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





    public static void printMap() {
        //Create map
        String[][] viewBoard = new String[N+2][N+2];
        for (int y = 0; y < N+2; y++) {
            for (int x = 0; x < N+2; x++) {
                if((y == 0 || y == N+1) && (x == 0 || x == N+1)) {
                    viewBoard[y][x] = " * ";
                    continue;
                }
                if(y == 0 || y == N+1) {
                    viewBoard[y][x] = "---";
                    continue;
                }
                if(x == 0 || x == N+1) {
                    viewBoard[y][x] = " | ";
                    continue;
                }
                viewBoard[y][x] = (board[y-1][x-1] == null) ? " # " : "   ";
            }
        }
        viewBoard[goal.x+1][goal.y+1] = " G ";
        for (int i = 0; i < robots.length; i = i + 2) {
            viewBoard[robots[i]][robots[i + 1]] = " " + ((char) (i / 2)) + " ";
        }

        //print map
        for (String[] cRow: viewBoard) {
            for (String c: cRow) {
                System.out.print(c);
            }
            System.out.println();
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
        robots = new short[2*R];  //([com.company.Robot][0: x, 1: y])


        short y = 0;
        short x = 0;

        // # = 35
        // <space> = 32
        // G = 71
        while(y < N) {
            int input = br.read();
            if( input == 13) { continue; }//In codejudge a 13 (Carriage return) is also send, before line feed (10)
            if(input == 10) {
                y++;
                x = 0;
                continue;
            }
            if(input == 32) {
                board[y][x] = new Node(y, x);
            } else if(input == 71) {
                Node n = new Node(y, x);
                board[y][x] = n;
                goal = n;
            } else if(input == 35) {
                //Code for setting S and W for all nodes (current x,y is a wall (null))
                // - Nodes on top/left should be parents to all above nodes with no direct wall between

                //South com.company.Node
                if(y - 1 > 0 && board[y - 1][x] != null) {
                    for(int ty = y - 2; ty >= 0; ty--) {
                        if(board[ty][x] == null) {
                            break;
                        }
                        board[ty][x].D = board[y - 1][x];
                    }
                }

                //East com.company.Node
                if(x - 1 > 0 && board[y][x - 1] != null) {
                    for(int tx = x - 2; tx >= 0; tx--) {
                        if(board[y][tx] == null) {
                            break;
                        }
                        board[y][tx].R = board[y][x - 1];
                    }
                }
            } else {
                Node node = new Node(y,x);
                board[y][x] = node;
                robots[(input-48)*2] = y;
                robots[(input-48)*2 + 1] = x;
            }
            x++;
        }

        //Only thing left, is to run the West/South setting code for the East and south-most nodes.
        //East border
        for(int ty = 0; ty < N; ty++) {
            if(board[ty][N-1] == null) continue;
            for(int tx = N-2; tx >= 0; tx--) {
                if(board[ty][tx] == null) break;
                board[ty][tx].R = board[ty][N-1];
            }
        }
        //South border
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

    public void addPath(int robot, char dir) {
        path += " " + robot + dir;
    }

}


class Node {
    public Node U = null;
    public Node R = null;
    public Node D = null;
    public Node L = null;

    public short x;
    public short y;

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

    //check if nessecary to stop earlier
    public Node getStopNodeU(short[] robots) {
        Node pNode = this.U;
        if(pNode == null) return pNode;
        for(int i = 0; i < robots.length; i = i + 2) { //Getting robot indexes (looping through all robots)
            if(this.x != robots[i + 1] || this.y <= robots[i]) continue; //If not same y col or below or equal node (equal means robot is standing there,and is the moving robot)
            if(pNode.y <= robots[i]) { // if robot is between else defined goToNode and current node:
                pNode = board[robots[i] + 1][this.x];
                continue;
            }
        }
        return pNode;
    }
    public Node getStopNodeD(short[] robots) {
        Node pNode = this.D;
        if(pNode == null) return pNode;
        for(int i = 0; i < robots.length; i = i + 2) {
            if(this.x != robots[i + 1] || this.y >= robots[i]) continue;
            if(pNode.y >= robots[i]) {
                pNode = board[robots[i] - 1][this.x];
                continue;
            }
        }
        return pNode;
    }
    public Node getStopNodeL(short[] robots) {
        Node pNode = this.L;
        if(pNode == null) return pNode;
        for(int i = 0; i < robots.length; i = i + 2) {
            if(this.y != robots[i] || this.x <= robots[i + 1]) continue;
            if(pNode.x <= robots[i + 1]) {
                pNode = board[this.y][robots[i + 1] + 1];
                continue;
            }
        }
        return pNode;
    }
    public Node getStopNodeR(short[] robots) {
        Node pNode = this.R;
        if(pNode == null) return pNode;
        for(int i = 0; i < robots.length; i = i + 2) {
            if(this.y != robots[i] || this.x >= robots[i + 1]) continue;
            if(pNode.x >= robots[i + 1]) {
                pNode = board[this.y][robots[i + 1] - 1];
                continue;
            }
        }
        return pNode;
    }

    public void printParents() {
        System.out.println(" [" + " x " + "," + " y " + "]");
        System.out.println("Loc: [" + x + "," + y + "]");
        System.out.println("N:   [" + ((U == null) ? "N,N" : U.x + "," + U.y) + "]");
        System.out.println("E:   [" + ((R == null) ? "N,N" : R.x + "," + R.y) + "]");
        System.out.println("S:   [" + ((D == null) ? "N,N" : D.x + "," + D.y) + "]");
        System.out.println("W:   [" + ((L == null) ? "N,N" : L.x + "," + L.y) + "]");
    }
}
