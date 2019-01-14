import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class TicTacToeServer extends Application {
    @Override
    public void start(Stage primaryStage) {
        TextArea taLog = new TextArea();

        Scene scene = new Scene(new ScrollPane(taLog), 200, 200);
        primaryStage.setTitle("TicTacToe Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000);

                Platform.runLater((() -> taLog.appendText(new Date() + ": Server started at socket 8000\n")));

                while (true) {
                    Platform.runLater(() -> taLog.appendText(new Date() + ": Wait for players to join session\n"));

                    Socket player1 = serverSocket.accept();

                    Platform.runLater(() -> {
                        taLog.appendText(new Date() + ": Player 1 joined the session!\n" + "Player 1 IP Address: " + player1.getInetAddress().getHostAddress() + "\n");
                    });

                    Socket player2 = serverSocket.accept();

                    Platform.runLater(() -> {
                        taLog.appendText(new Date() + ": Player 2 joined the session!\n" + "Player 2 IP Address: " + player2.getInetAddress().getHostAddress() + "\n");
                    });

                    Platform.runLater(() -> taLog.appendText(new Date() + ": Start a thread for session\n"));

                    new Thread(() -> {
                        int turnTracker = 1;
                        boolean continueToPlay = true;
                        ArrayList<String> xAndOList = new ArrayList<>();

                        ObjectInputStream fromPlayer1;
                        ObjectInputStream fromPlayer2;

                        ObjectOutputStream toPlayer1;
                        ObjectOutputStream toPlayer2;

                        for (int i = 0; i < 9; i++) {
                            xAndOList.add("Start");
                        }

                        try {
                            toPlayer1 = new ObjectOutputStream(player1.getOutputStream());
                            toPlayer2 = new ObjectOutputStream(player2.getOutputStream());
                            fromPlayer1 = new ObjectInputStream(player1.getInputStream());
                            fromPlayer2 = new ObjectInputStream(player2.getInputStream());

                            toPlayer1.writeObject("Player 1");
                            toPlayer2.writeObject("Player 2");

                            toPlayer1.writeObject(xAndOList);
                            toPlayer2.writeObject(xAndOList);

                            toPlayer1.writeInt(turnTracker);

                            while (true) {
                                try {
                                    xAndOList = (ArrayList<String>) fromPlayer1.readObject();
                                } catch (Exception e) {
                                    Platform.runLater(() -> taLog.appendText("Failed to receive list: " + e.getMessage()));
                                }
                                if (won(xAndOList) == "X") {
                                    toPlayer1.writeObject("YOU WIN");
                                    toPlayer2.writeObject("YOU LOSE");
                                    break;
                                } else if (isFull(xAndOList)) {
                                    toPlayer1.writeObject("DRAW");
                                    toPlayer2.writeObject("DRAW");
                                    break;
                                }
                                toPlayer2.writeObject(xAndOList);
                                toPlayer2.writeObject(turnTracker);

                                xAndOList = (ArrayList<String>) fromPlayer2.readObject();

                                if (won(xAndOList) == "O") {
                                    toPlayer1.writeObject("YOU LOSE");
                                    toPlayer2.writeObject("YOU WIN");
                                    break;
                                } else if (isFull(xAndOList)) {
                                    toPlayer1.writeObject("DRAW");
                                    toPlayer2.writeObject("DRAW");
                                    break;
                                }

                                toPlayer1.writeObject(xAndOList);
                                toPlayer1.writeObject(turnTracker);
                            }
                        } catch (Exception e) {
                            Platform.runLater(() -> taLog.appendText(e.getMessage()));
                        }

                    });
                }
            } catch (IOException ex) {
                Platform.runLater(() -> taLog.appendText("IOException in thread: " + ex.getMessage()));
            }
        });

    }

    public String won(ArrayList<String> list) {
        //X wins
        if (list.get(0) == "X" && list.get(1) == "X" && list.get(2) == "X") {
            return "X";
        } else  if (list.get(3) == "X" && list.get(4) == "X" && list.get(5) == "X") {
            return "X";
        } else if (list.get(6) == "X" && list.get(7) == "X" && list.get(8) == "X") {
            return "X";
        } else if (list.get(0) == "X" && list.get(3) == "X" && list.get(6) == "X") {
            return "X";
        } else if (list.get(1) == "X" && list.get(4) == "X" && list.get(7) == "X") {
            return "X";
        } else if (list.get(2) == "X" && list.get(5) == "X" && list.get(8) == "X") {
            return "X";
        } else if (list.get(0) == "X" && list.get(4) == "X" && list.get(8) == "X") {
            return "X";
        } else if (list.get(6) == "X" && list.get(4) == "X" && list.get(2) == "X") {
            return "X";
        }

        //O Wins
        if (list.get(0) == "O" && list.get(1) == "O" && list.get(2) == "O") {
            return "O";
        } else  if (list.get(3) == "O" && list.get(4) == "O" && list.get(5) == "O") {
            return "O";
        } else if (list.get(6) == "O" && list.get(7) == "O" && list.get(8) == "O") {
            return "O";
        } else if (list.get(0) == "O" && list.get(3) == "O" && list.get(6) == "O") {
            return "O";
        } else if (list.get(1) == "O" && list.get(4) == "O" && list.get(7) == "O") {
            return "O";
        } else if (list.get(2) == "O" && list.get(5) == "O" && list.get(8) == "O") {
            return "O";
        } else if (list.get(0) == "O" && list.get(4) == "O" && list.get(8) == "O") {
            return "O";
        } else if (list.get(6) == "O" && list.get(4) == "O" && list.get(2) == "O") {
            return "O";
        }
        else {
            return "NOPE";
        }
    }
    public boolean isFull(ArrayList<String> list) {
        int counter = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == "X" || list.get(i) == "O") {
                counter++;
            }
        }
        if (counter == 9) {
            return true;
        } else
            return false;
    }

    public static void main(String args) {
        launch(args);
    }
}
