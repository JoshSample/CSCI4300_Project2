package tictactoe;

//Josh Sample(B01218906) & Jack Thurber (B01224237)
//CSCI 4300
//Project 2

// libraries used, includes socket communication and swing libraries
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// client class
public class tictactoeclient {
	// variables needed for socket communication and gui elements
    private JFrame frame;	// names the frame
    private JLabel message;	// used for setting text on client window
    private ImageIcon icon;	// the primary icon, either x or o, for this client
    private ImageIcon opponentIcon;	// the oppoenents icon, dependent on if this client is x or o
    private Square[] board;	// tic tac toe board, will have 9 squares
    private Square currentSquare;	// keeps track of current square that was clicked
    private Socket socket;	// used for connecting to server
    private BufferedReader input;	// gets input from client
    private PrintWriter output;	// outputs to server

    // default constructor, connects to server and creates GUI
    public tictactoeclient(String serverAddress) throws Exception {
    	frame = new JFrame("TicTacToe By Jack and Josh");
    	message = new JLabel("", SwingConstants.CENTER);
    	message.setFont(new Font("OCR A Extended", Font.PLAIN, message.getFont().getSize()));
    	board = new Square[9];
    	// custom icon for application
    	frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon.png"));
        // connect to socket
        socket = new Socket(serverAddress, 9010);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
        // Layout GUI
        frame.add(message, BorderLayout.NORTH);
        // the following code creates a 3x3 tic tac toe board
        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5));
        // fills up the board with squares
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentSquare = board[j];
                    output.println("MOVE " + j);}});
            boardPanel.add(board[i]);
        }
        frame.add(boardPanel, "Center");
    }

    // this thread handles messages from the server and processes them
    public void play() throws Exception {
        String response;
        try {
            response = input.readLine();
            // "WELCOME" is a mark which decides if a player will be an x or o
            if (response.startsWith("WELCOME")) {
                char mark = response.charAt(8);
                // sets icon to either x or o depending on when this client connected
                if (mark == 'X') {
                	icon = new ImageIcon(tictactoeclient.class.getClassLoader().getResource("x.png"));
                	opponentIcon = new ImageIcon(tictactoeclient.class.getClassLoader().getResource("o.png"));
                }
                else {
                	icon = new ImageIcon(tictactoeclient.class.getClassLoader().getResource("o.png"));
                	opponentIcon = new ImageIcon(tictactoeclient.class.getClassLoader().getResource("x.png"));
                }
                
                frame.setTitle("Tic Tac Toe - Player " + mark);
            }
            while (true) {
                response = input.readLine();
                // "VALID_MOVE" sets icon in the square, sends message back to client
                if (response.startsWith("VALID_MOVE")) {
                    message.setText("Valid move, opponents turn");
                    currentSquare.setIcon(icon);
                    currentSquare.repaint();
                } 
                // "OPPONENT_MOVED" sets opponent icon to square and sends message to other client
                else if (response.startsWith("OPPONENT_MOVED")) {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].setIcon(opponentIcon);
                    board[loc].repaint();
                    message.setText("Opponent moved, your turn");
                } 
                // if there is a winner, send message to winning client
                else if (response.startsWith("VICTORY")) {
                    message.setText("You win!");
                    break;
                }
                // if winner, sends message to losing client
                else if (response.startsWith("DEFEAT")) {
                    message.setText("You lose :(");
                    break;
                } 
                // if there's a tie, sends message to both clients
                else if (response.startsWith("TIE")) {
                    message.setText("Draw");
                    break;
                } 
                // for miscellaneous messages
                else if (response.startsWith("MESSAGE")) {
                    message.setText(response.substring(8));
                }
                // for when an illegal move is performed
                else if (response.startsWith("INVALID_MOVE")) {
                	message.setText("Invalid move");
                }
            }
            output.println("QUIT");
        }
        finally {
            socket.close();
        }
    }

    // asks clients to a rematch
    private boolean playAgain() {
        int response = JOptionPane.showConfirmDialog(frame, "Rematch?", "TicTacToe Jack and Josh", JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    // a square in the board, sets an icon to it once a player selects an avaliable square
    static class Square extends JPanel {
        JLabel label = new JLabel();
        public Square() {
            setBackground(Color.LIGHT_GRAY);
            add(label);
        }
        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
    }

    // the driver that executes all of the clients methods
    public static void main(String[] args) throws Exception {
        while (true) {
            String serverAddress = ("localhost");
            tictactoeclient client = new tictactoeclient(serverAddress);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(750, 800);
            client.frame.setVisible(true);
            client.frame.setResizable(true);
            client.play();
            if (!client.playAgain()) {
                break;
            }
        }
    }
}
