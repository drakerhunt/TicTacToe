//package sample;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {
    ObjectOutputStream toServer = null;
    ObjectInputStream fromServer = null;
    ArrayList<String> taStringList = new ArrayList<>();
    boolean yourTurn = true;

    @Override
    public void start(Stage primaryStage) {

        //Make panes
        BorderPane gamePane = new BorderPane();
        HBox topHB = new HBox();
        HBox middleHB = new HBox();
        HBox bottomHB = new HBox();
        VBox vBox = new VBox();

        //PaneLayouts
        String cssLayout = "-fx-border-color: black;\n" +
                "-fx-border-insets: 0;\n" +
                "-fx-border-width: 1;\n" +
                "-fx-border-style: none;\n";

        vBox.setStyle(cssLayout);
        topHB.setStyle(cssLayout);
        middleHB.setStyle(cssLayout);
        bottomHB.setStyle(cssLayout);

        //Add panes to one another
        gamePane.setCenter(vBox);
        vBox.getChildren().addAll(topHB, middleHB, bottomHB);

        //Create TextAreas
        TextArea topLeftTA = new TextArea("Here");
        TextArea topMiddleTA = new TextArea("Here");
        TextArea topRightTA = new TextArea("Here");
        TextArea middleLeftTA = new TextArea("Here");
        TextArea middleMiddleTA = new TextArea("Here");
        TextArea middleRightTA = new TextArea("Here");
        TextArea bottomLeftTA = new TextArea("Here");
        TextArea bottomMiddleTA = new TextArea("Here");
        TextArea bottomRightTA = new TextArea("Here");

        ArrayList<TextArea> textAreas = new ArrayList<>();
        textAreas.add(topLeftTA);
        textAreas.add(topMiddleTA);
        textAreas.add(topRightTA);
        textAreas.add(middleLeftTA);
        textAreas.add(middleMiddleTA);
        textAreas.add(middleRightTA);
        textAreas.add(bottomLeftTA);
        textAreas.add(bottomMiddleTA);
        textAreas.add(bottomRightTA);

        topLeftTA.setPrefSize(100, 100);
        topMiddleTA.setPrefSize(100,100);
        topRightTA.setPrefSize(100, 100);
        middleLeftTA.setPrefSize(100,100);
        middleMiddleTA.setPrefSize(100,100);
        middleRightTA.setPrefSize(100,100);
        bottomLeftTA.setPrefSize(100,100);
        bottomMiddleTA.setPrefSize(100,100);
        bottomRightTA.setPrefSize(100,100);

        //create clear button
        Button clearBT = new Button("Clear");
        Button submitBT = new Button("Submit");
        HBox btHbox = new HBox(50);
        btHbox.getChildren().addAll(clearBT, submitBT);
        vBox.getChildren().add(btHbox);

        //Add TextAreas to panes
        topHB.getChildren().addAll(topLeftTA, topMiddleTA, topRightTA);
        middleHB.getChildren().addAll(middleLeftTA, middleMiddleTA, middleRightTA);
        bottomHB.getChildren().addAll(bottomLeftTA, bottomMiddleTA, bottomRightTA);

        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(new Scene(gamePane, 375, 375));
        primaryStage.show();

        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 8000);
                System.out.println(fromServer.readObject());

                while (true) {
                    toServer = new ObjectOutputStream(socket.getOutputStream());
                    fromServer = new ObjectInputStream(socket.getInputStream());

                    //TextArea Actions
                    topLeftTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(topLeftTA);
                        }
                    });
                    topMiddleTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(topMiddleTA);
                        }
                    });
                    topRightTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(topRightTA);
                        }
                    });
                    middleLeftTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(middleLeftTA);
                        }
                    });
                    middleRightTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(middleRightTA);
                        }
                    });
                    middleMiddleTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(middleMiddleTA);
                        }
                    });
                    bottomLeftTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(bottomLeftTA);
                        }
                    });
                    bottomMiddleTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(bottomMiddleTA);
                        }
                    });
                    bottomRightTA.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                            selection(bottomRightTA);
                        }
                    });

                    for (int i = 0; i < textAreas.size(); i++) {
                        taStringList.add(textAreas.get(i).getText());
                    }

                    //Button Action
                    clearBT.setOnAction(event -> {
                        textAreas.forEach(ta -> {
                            ta.setText("Here");
                            ta.setStyle("-fx-font-alignment: center");
                            ta.setFont(Font.font("Sans", 10));
                            ta.setFocusTraversable(true);
                            ta.setMouseTransparent(false);
                        });
                    });
                    submitBT.setOnAction(event -> {
                        System.out.println("Trying to send");
                        try {
                            toServer.writeObject(taStringList);
                            System.out.println("Sent");
                        } catch (IOException ex) {
                            System.out.println("ArrayList not Sent");
                        }
                    });

                    try {
                        System.out.println("In try");
                        ArrayList<TextArea> taList = (ArrayList<TextArea>)fromServer.readObject();
                        System.out.println("HERE");
                        for (int i = 0; i < taList.size(); i++) {
                            if (taList.get(i).getText() != textAreas.get(i).getText()) {
                                textAreas.get(i).setText(taList.get(i).getText());
                            }
                        }
                    } catch (ClassNotFoundException c) {
                        System.out.println("ClassNotFound");
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    public void selection(TextArea ta) {
        if (yourTurn) {
            ta.setStyle("-fx-font-alignment: center");
            ta.setFont(Font.font("Zapfino", 20));
            ta.setText("X");
            ta.setMouseTransparent(true);
            ta.setFocusTraversable(false);
            yourTurn = false;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
