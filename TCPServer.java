package battleship;

//Author: Andrei Ghenoiu
//Fall 2011 Networks class
//if you have any questions contact me at andrei_stefang@yahoo.com
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TCPServer {
// Declare the JDBC objects.  
        
    protected static void saveToDataBase(String name,int result){
        System.out.println("save..."+name);
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        // Create a variable for the connection string.  
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=battleship;user=sa;password=1";
        try {
            // Establish the connection.  
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(connectionUrl);
            //check if exist
            String sql = "SELECT name, win, game FROM Player WHERE name='"+name+"'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if(!rs.wasNull()){
                while(rs.next()){
         //Retrieve by column name
         int win  = rs.getInt("win");
         win +=result;
         int game = rs.getInt("game");
         game++;
         //Update record
         String SQL = "UPDATE Player " +
"SET win='"+win+"', game='"+game+"' " +
"WHERE name='"+name+"'";
                stmt = con.createStatement();
                stmt.executeUpdate(SQL);
                return;
      }
            }
            //Add new 
            
                String SQL = "INSERT INTO Player (name, win,game)\n" +
"VALUES ('"+name+"','"+result+"','"+0+"')";
                stmt = con.createStatement();
                stmt.executeUpdate(SQL);

        } // Handle any errors that may have occurred.  
        catch (Exception e) {
            e.printStackTrace();
        }
         finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
        
    }
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String clientSentence;
        String serverSentence;
        ServerSocket welcomeSocket;
        Socket serverSocket;
        
        int whoWin = 0;
        String miss = "MISSSSSSSSS";
        String hit = "HITTTTTTTTT";
        String won = "WONNNNNNN";
        boolean turn = true;
        //Connect Database
        

        

        //from command line
        BufferedReader inFromUser = new BufferedReader(
                new InputStreamReader(System.in));
        //login p1 name
        Player p1 = new Player();
        p1.setWin(0);
        System.out.println("Please enter your name:");
        p1.setName(inFromUser.readLine());

        //create Server
        try{
            welcomeSocket = new ServerSocket(1995);
        System.out.println("Wait Player 2... ");
        serverSocket = welcomeSocket.accept();
        
        System.out.println("P2 is on!");
       
        //from client
        DataInputStream inFromClient = new DataInputStream(
                serverSocket.getInputStream());

        //out to client the hit or miss message
        DataOutputStream outToClient1
                = new DataOutputStream(serverSocket.getOutputStream());

        //get p2 name
        Player p2 = new Player();
        p2.setWin(0);
        p2.setName(inFromClient.readUTF());
        outToClient1.writeUTF("Wellcome "+p2.getName());
                outToClient1.flush();
        System.out.println("Player is "+p2.getName());
        
        
        //create the board
        board gameBoard = new board();

        //player is asked to add the ships to the board
        System.out.println("Below you can input where you want to"
                + " place your battleships.\n Please enter them in integers"
                + " starting with the row followed by columns\n (for example"
                + " start with the head as 11 for row 1,\n column 1 and "
                + " tail as 51 for row 5 and column 1)\n. Please input the data\n "
                + "left to right and top to bottom\n"
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
        System.out.println("Wait P2...");
        while(true){
            int state = inFromClient.readInt();
            if(state == 1){
                outToClient1.writeInt(1);
                outToClient1.flush();
                                break;

            }
        }
        System.out.println("BEGIN");
        ////////////////////////////////////
        //game starts
        ////////////////////////////////////
        int round=1;
        //play
        while (true) {
            int hitRow;
            int hitCol;
            System.out.println("Round "+round);
            round++;
            //the server receives a hit from the client 
            //and replies with a hit or miss
            System.out.println("Turn P2");
            clientSentence = inFromClient.readLine();
            System.out.println("Hit from P2: " + clientSentence);
            int clientInt = Integer.parseInt(clientSentence);
            hitRow = Math.abs(clientInt / 10) - 1;
            hitCol = clientInt % 10 - 1;

            if (gameBoard.testHit(hitRow, hitCol)) {
                gameBoard.testLoss();
                if (gameBoard.testLoss() == false) {
                    hit = miss = "You won!";
                    whoWin = 2;
                    outToClient1.writeBytes(hit + "\n");
                outToClient1.flush();
                    System.out.println("Sorry, you lost!");
                    break;
                }
                outToClient1.writeBytes(hit + "\n");
                outToClient1.flush();

            } else {
                outToClient1.writeBytes(miss + "\n");
                outToClient1.flush();
            }

            //the server sends a hit
            System.out.println("Please send hit: ");
            String newS = inFromUser.readLine();
            outToClient1.writeBytes(newS + "\n");
            outToClient1.flush();

            clientSentence = inFromClient.readLine();
            if(clientSentence.equals("You won!")){
                System.out.println("You won!");
                whoWin =1;
                break;
            }
            System.out.println("Result from P2: " + clientSentence);

        }
        
        if(whoWin == 1)
            p1.setWin(1);
        else
            p2.setWin(1);
        saveToDataBase(p1.getName(), p1.getWin());
         saveToDataBase(p2.getName(), p2.getWin());
         System.out.println("Save.");
        }catch(IOException e){}
        
    }

}
