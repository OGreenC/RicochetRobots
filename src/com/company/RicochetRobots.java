package com.company;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static com.company.RicochetRobots.board;

public class RicochetRobots {

    public static int N;
    public static int R;
    public static Node[][] board;
    public static Robot[] robots;
    public static Node goal;

    public static void main(String[] args) throws IOException {
        readInput();

        calculatePath();
    }

    public static void calculatePath() {
        nodeBFS();

        String[] path = goal.getPath().split(" ");
        for(int i = 1; i < path.length; i++) {
            System.out.println(path[i]);
        }

    }


    public static Queue<Node> q = new LinkedList<>();

    public static void nodeBFS() {
        robots[0].node.pathDepth = 0;
        q.add(robots[0].node);
        while(!q.isEmpty()) {
            Node n = q.remove();

            visitNode(n, n.U, 'U');
            if(n.U == goal) break;
            visitNode(n, n.D, 'D');
            if(n.D == goal) break;
            visitNode(n, n.L, 'L');
            if(n.L == goal) break;
            visitNode(n, n.R, 'R');
            if(n.R == goal) break;
        }
    }

    public static void visitNode(Node parentNode,Node node, char entryDir) {
        if(node != null && !node.getVisited()) {
            node.pathDepth = (short) (parentNode.pathDepth + 1);
            node.pathParent = parentNode;
            node.pathEntry = entryDir;
            q.add(node);
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
        robots = new Robot[R];  //([Robot][0: x, 1: y])


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

                //South Node
                if(y - 1 > 0 && board[y - 1][x] != null) {
                    for(int ty = y - 2; ty >= 0; ty--) {
                        if(board[ty][x] == null) {
                            break;
                        }
                        board[ty][x].D = board[y - 1][x];
                    }
                }

                //East Node
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
                robots[input-48] = new Robot(node,(short) (input-48));
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

class Node {
    public Node U = null;
    public Node R = null;
    public Node D = null;
    public Node L = null;

    public short x;
    public short y;

    public Node pathParent = null;
    public char pathEntry;
    public short pathDepth;

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

    public String getPath() {
        if(pathParent == null) return "";
        return pathParent.getPath() + " 0" + pathEntry;
    }

    public boolean getVisited() {
        return (pathParent == null) ? false : true;
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

class Robot {

    public Node node;
    public short id;

    public Robot(Node node, short id) {
        this.node = node;
        this.id = id;
    }

    public short getX() {return node.x;}
    public short getY() {return node.y;}

    public void moveU() {
        this.node = node.U;
    }
    public void moveD() {
        this.node = node.D;

    }
    public void moveR() {
        this.node = node.R;

    }
    public void moveL() {
        this.node = node.L;

    }




    public void printLoc() {
        System.out.println("Robot Loc: x:" + getX() + " y:" + getY());
    }

}