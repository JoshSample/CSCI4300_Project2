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

		// The following chunks of code are for the gui of the server
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

		// this is the main thread that connects clients to the server
		try {
			while (true) {
				Game game = new Game();
				Game.Player player1 = game.new Player(listener.accept(), 'X');
				Game.Player player2 = game.new Player(listener.accept(), 'O');
				player1.setOpponent(player2);
				player2.setOpponent(player1);
				game.currentPlayer = player1;
				player1.start();
				player2.start();
			}
		} finally {
			listener.close();
		}
	}
}

//creates a game for the two players to play
class Game {

	// This makes the game board with 9 squares, they start out as null since they are unoccupied by either player.
	//when a player makes a move the spaces are no longer null
	private Player[] board = {
			null, null, null,
			null, null, null,
			null, null, null};

	// a variable to track the current player
	Player currentPlayer;

	// checks the board for a players win condition whether it is horizontal vertical or diagonal
	public boolean hasWinner() {
		return  (board[0] != null && board[0] == board[1] && board[0] == board[2])
				||(board[3] != null && board[3] == board[4] && board[3] == board[5])
				||(board[6] != null && board[6] == board[7] && board[6] == board[8])
				||(board[0] != null && board[0] == board[3] && board[0] == board[6])
				||(board[1] != null && board[1] == board[4] && board[1] == board[7])
				||(board[2] != null && board[2] == board[5] && board[2] == board[8])
				||(board[0] != null && board[0] == board[4] && board[0] == board[8])
				||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
	}

	// checks if board is full, i.e. square is filled by X or O
	public boolean filledBoard() {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == null) {
				return false;
			}
		}
		return true;
	}

	// The Synchronized function checks to see if the move the player has made was legal.
	//It also checks if the person who made the move was the current player.
	//Finally it updates and sets the next player to current player and shows the move of the players on both clients.
	public synchronized boolean legalMove(int location, Player player) {
		if (player == currentPlayer && board[location] == null) {
			board[location] = currentPlayer;
			currentPlayer = currentPlayer.opponent;
			currentPlayer.otherPlayerMoved(location);
			return true;
		}
		return false;
	}

	//This class sets the two players identities as either the x icon or the o icon
	class Player extends Thread {
		char mark;
		Player opponent;
		Socket socket;
		BufferedReader input;
		PrintWriter output;

		// Here is where the socket is created and the first two messages are displayed on the screen
		public Player(Socket s, char m) {
			socket = s;
			mark = m;
			try {
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				output.println("WELCOME " + mark);
				output.println("MESSAGE Listening for connections...");
			} catch (IOException e) {
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame, "Player has left the server", "Error", 1);
			}
		}

		// Sets that an opponent has joined
		public void setOpponent(Player rival) {
			opponent = rival;
		}

		// controls the opponent player messages and movement
		public void otherPlayerMoved(int location) {
			output.println("OPPONENT_MOVED " + location);
			if (hasWinner()) 
				output.println("DEFEAT");
			else if (filledBoard()) 
				output.println("TIE");
		}

		// a method of the thread class, this handles the threads
		public void run() {
			try {
				// The thread is only started after everyone connects.
				output.println("MESSAGE All players connected");

				// Tell the first player that it is her turn.
				if (mark == 'X') 
					output.println("MESSAGE Your move");

				// Repeatedly get commands from the client and process them.
				while (true) {
					String command = input.readLine();
					if (command.startsWith("MOVE")) {
						int location = Integer.parseInt(command.substring(5));
						if (legalMove(location, this)) {
							output.println("VALID_MOVE");
							if (hasWinner()) 
								output.println("DEFEAT");
							else if (filledBoard()) 
								output.println("TIE");
						} 
						else 
							output.println("INVALID_MOVE");
					} 
					else if (command.startsWith("QUIT")) return;
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


