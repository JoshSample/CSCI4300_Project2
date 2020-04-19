package tictactoe;

// Josh Sample & Jack Thurber
// CSCI 4300
// Project 2

// imports used for server side
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;


public class tictactoeserver {
    
	// driver that sets up server gui, connects clients to server
    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(9010);
        JFrame frame = new JFrame();
        // custom icon for application
    	frame.setIconImage(Toolkit.getDefaultToolkit().getImage("./icon.png"));
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel header = new JLabel("");
        header.setBounds(10, 10, 416, 0);
        frame.getContentPane().add(header);

        JLabel lblServerIsRunning = new JLabel("Server Is Running!");
        lblServerIsRunning.setToolTipText("Server is Running");
        lblServerIsRunning.setBackground(Color.WHITE);
        lblServerIsRunning.setFont(new Font("OCR A Extended", Font.BOLD | Font.ITALIC, 22));
        lblServerIsRunning.setForeground(Color.GREEN);
        lblServerIsRunning.setBounds(112, 130, 289, 81);
        frame.getContentPane().add(lblServerIsRunning);

        JTextArea txtrServerIsRunning = new JTextArea();
        txtrServerIsRunning.setForeground(Color.GREEN);
        txtrServerIsRunning.setEditable(false);
        txtrServerIsRunning.setFont(new Font("OCR A Extended", Font.BOLD | Font.ITALIC, 22));
        txtrServerIsRunning.setBackground(Color.BLACK);
        txtrServerIsRunning.setBounds(2, 81, 442, 180);
        frame.getContentPane().add(txtrServerIsRunning);
        lblServerIsRunning.setLabelFor(txtrServerIsRunning);

        JLabel lblNewLabel = new JLabel("New label");
        lblNewLabel.setIcon(new ImageIcon(tictactoeserver.class.getClassLoader().getResource("XSmall.png")));
        lblNewLabel.setBounds(10, 10, 60, 60);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("New label");
        lblNewLabel_1.setIcon(new ImageIcon(tictactoeserver.class.getClassLoader().getResource("XSmall.png")));
        lblNewLabel_1.setBounds(154, 10, 60, 60);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel lblNewLabel_1_1 = new JLabel("New label");
        lblNewLabel_1_1.setIcon(new ImageIcon(tictactoeserver.class.getClassLoader().getResource("XSmall.png")));
        lblNewLabel_1_1.setBounds(303, 10, 60, 60);
        frame.getContentPane().add(lblNewLabel_1_1);

        JLabel lblNewLabel_2 = new JLabel("");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 50));
        lblNewLabel_2.setIcon(new ImageIcon(tictactoeserver.class.getClassLoader().getResource("osmall.png")));
        lblNewLabel_2.setBounds(80, 10, 60, 60);
        frame.getContentPane().add(lblNewLabel_2);

        JLabel lblNewLabel_2_1 = new JLabel("");
        lblNewLabel_2_1.setIcon(new ImageIcon(tictactoeserver.class.getClassLoader().getResource("osmall.png")));
        lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 50));
        lblNewLabel_2_1.setBounds(224, 10, 60, 60);
        frame.getContentPane().add(lblNewLabel_2_1);

        JLabel lblNewLabel_2_1_1 = new JLabel("");
        lblNewLabel_2_1_1.setIcon(new ImageIcon(tictactoeserver.class.getClassLoader().getResource("osmall.png")));
        lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.PLAIN, 50));
        lblNewLabel_2_1_1.setBounds(373, 10, 60, 60);
        frame.getContentPane().add(lblNewLabel_2_1_1);
        frame.setTitle("TicTacToe Jack and Josh - Server ");
        frame.setVisible(true);
        frame.setResizable(false);
        try {
            while (true) {
                Game game = new Game();
                Game.Player playerX = game.new Player(listener.accept(), 'X');
                Game.Player playerO = game.new Player(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                playerX.start();
                playerO.start();
            }
        } finally {
            listener.close();
        }
    }
}

/**
 * A two-player game.
 */
class Game {

    /**
     * A board has nine squares.  Each square is either unowned or
     * it is owned by a player.  So we use a simple array of player
     * references.  If null, the corresponding square is unowned,
     * otherwise the array cell stores a reference to the player that
     * owns it.
     */
    private Player[] board = {
        null, null, null,
        null, null, null,
        null, null, null};

    /**
     * The current player.
     */
    Player currentPlayer;

    /**
     * Returns whether the current state of the board is such that one
     * of the players is a winner.
     */
    public boolean hasWinner() {
        return
            (board[0] != null && board[0] == board[1] && board[0] == board[2])
          ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
          ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
          ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
          ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
          ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
          ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
          ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    /**
     * Returns whether there are no more empty squares.
     */
    public boolean boardFilledUp() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called by the player threads when a player tries to make a
     * move.  This method checks to see if the move is legal: that
     * is, the player requesting the move must be the current player
     * and the square in which she is trying to move must not already
     * be occupied.  If the move is legal the game state is updated
     * (the square is set and the next player becomes current) and
     * the other player is notified of the move so it can update its
     * client.
     */
public synchronized boolean legalMove(int location, Player player) {
        if (player == currentPlayer && board[location] == null) {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }

    /**
     * The class for the helper threads in this multithreaded server
     * application.  A Player is identified by a character mark
     * which is either 'X' or 'O'.  For communication with the
     * client the player has a socket with its input and output
     * streams.  Since only text is being communicated we use a
     * reader and a writer.
     */
    class Player extends Thread {
        char mark;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;

        /**
         * Constructs a handler thread for a given socket and mark
         * initializes the stream fields, displays the first two
         * welcoming messages.
         */
        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            try {
                input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
            	JFrame frame = new JFrame();
            	JOptionPane.showMessageDialog(frame, "Player has left the server", "Error", 1);
            }
        }

        /**
         * Accepts notification of who the opponent is.
         */
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        /**
         * Handles the otherPlayerMoved message.
         */
        public void otherPlayerMoved(int location) {
            output.println("OPPONENT_MOVED " + location);
            output.println(
                hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
        }

        /**
         * The run method of this thread.
         */
        public void run() {
            try {
                // The thread is only started after everyone connects.
                output.println("MESSAGE All players connected");

                // Tell the first player that it is her turn.
                if (mark == 'X') {
                    output.println("MESSAGE Your move");
                }

                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readLine();
                    if (command.startsWith("MOVE")) {
                        int location = Integer.parseInt(command.substring(5));
                        if (legalMove(location, this)) {
                            output.println("VALID_MOVE");
                            output.println(hasWinner() ? "VICTORY"
                                         : boardFilledUp() ? "TIE"
                                         : "");
                        } else {
                        	output.println("INVALID_MOVE");
                        }
                    } else if (command.startsWith("QUIT")) {
                        return;
                    }
                }
            } catch (IOException e) {
            	JFrame frame = new JFrame();
            	JOptionPane.showMessageDialog(frame, "Player has left the server", "Error", 1);
            } finally {
                try {
                	socket.close();
                } catch (IOException e) {}
            }
        }
    }
}


