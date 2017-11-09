package battleship;

//Author: Andrei Ghenoiu
//Fall 2011 Networks class Vermont Technical College
//if you have any questions contact me at andrei_stefang@yahoo.com
import java.io.*;
import java.net.*;

public class ClientConnect {

    public static void main(String argv[]) throws Exception {

        String ip = "127.0.0.1";
        String sentence;
        String modifiedSentence;
        Socket clientSocket;
        Socket hitOrMiss;
        String miss = "MISSSSSSSSSSSSS";
        String hit = "HITTTTTTTTT";

        //from command line
        BufferedReader inFromUser = new BufferedReader(
                new InputStreamReader(System.in));
        //login p2 name
        Player p2 = new Player();
        System.out.println("Please enter your name:");
        p2.setName(inFromUser.readLine());
        //connect server
        try{
            clientSocket = new Socket(ip,1995);
//            System.out.println(1);
        //from server	
        DataInputStream inFromServer = new DataInputStream(
               clientSocket.getInputStream());
        
        //out to server the hit or miss message
        DataOutputStream outToServer = new DataOutputStream(
                clientSocket.getOutputStream());

        outToServer.writeUTF(p2.getName());
            outToServer.flush();
                System.out.println(inFromServer.readUTF());

        //create the board
        board gameBoard = new board();
        //player is asked to add the ships to the board
        System.out.println("Below you can input where you want to"
                + " place your battleships.\n Please enter them in integers"
                + " starting with the row followed by columns\n (for example"
                + " start with the head as 11 for row 1,\n column 1 and "
                + " tail as 51 for row 5 and column 1)\nPlease input the data\n "
                + "left to right and top to bottom"
                + "Type q when done.");
        while (true) {
            System.out.println("Please enter head location:");
            String line1 = inFromUser.readLine();
            System.out.println("Please enter tail location:");
            String line2 = inFromUser.readLine();
            if (!line1.equals("q") || !line2.equals("q")) {
                int head = Integer.parseInt(line1);
                int tail = Integer.parseInt(line2);
                //we call the testPos method to verify that we can place
                //the battleship at the inputed locations
                gameBoard.testPos(head, tail);
                if (gameBoard.boatBool == true) {
                    gameBoard.createBoat(head, tail);
                    System.out.println("Creating boat at " + head + " and " + tail);
                } else {
                    System.out.println("Sorry, can't place the battleship using these locations.");
                }
            } else {
                break;
            }
        }

        gameBoard.printBoard();
        System.out.println("Wait P1...");
outToServer.writeInt(1);
            outToServer.flush();
        while(true){
            int state = inFromServer.readInt();
            if(state == 1){
                                break;
            }
        }
        System.out.println("BEGIN");
        ////////////////////////////////////
        //game starts
        ////////////////////////////////////
        
int round=1;
        while (true) {
            int hitRow;
            int hitCol;
            System.out.println("Round "+round);
            round++;
            //the client sends a hit
            System.out.println("Please send hit:");
            sentence = inFromUser.readLine();
            System.out.println(" sent hit:"+sentence);
            outToServer.writeBytes(sentence + "\n");
            outToServer.flush();
            //the client receives a hit from the server
            modifiedSentence = inFromServer.readLine();
            System.out.println("Result from P1: " + modifiedSentence);
                        if(modifiedSentence.equals("You won!"))
break;
System.out.println("Turn P1");
            modifiedSentence = inFromServer.readLine();
            System.out.println("Hit from P1: " + modifiedSentence);
            int clientInt = Integer.parseInt(modifiedSentence);
            hitRow = Math.abs(clientInt / 10) - 1;
            hitCol = clientInt % 10 - 1;

            if (gameBoard.testHit(hitRow, hitCol)) {
                gameBoard.testLoss();
                if (gameBoard.testLoss() == false) {
                    hit = miss = "You won!";
                    outToServer.writeBytes(hit + "\n");
                outToServer.flush();
                    System.out.println("Sorry, you lost!");
                    break;
                }
                outToServer.writeBytes(hit + "\n");
                outToServer.flush();
            } else {
                outToServer.writeBytes(miss + "\n");
                outToServer.flush();
            }
        }
                }catch(IOException e){}


    }

}
