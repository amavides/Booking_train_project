
package sample;


import java.util.HashMap;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;

public class Main extends Application {

    Scanner sc = new Scanner(System.in);
    /*Hashmap for temporary storing data from Colombo to badulla*/
    HashMap<String, String> seatCheck = new HashMap<>();
    /*Hashmap for temporary storing data from Badulla to Colombo*/
    HashMap<String, String> returnSeatCheck = new HashMap<>();
    /*Hashmap for storing data to sort booked seats customers into alphabetical order*/
    HashMap<String, String> bubbleSort = new HashMap<>();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        while (true) {
            /* Menu Display */
            System.out.println("Enter A to add a customer to a seat");
            System.out.println("Enter V to view all seats");
            System.out.println("Enter E to display all empty seats");
            System.out.println("Enter D to delete customer from seat");
            System.out.println("Enter F to find a customer's seat");
            System.out.println("Enter S to save the booking details");
            System.out.println("Enter L to load booking data from saved file");
            System.out.println("Enter O to view seats ordered alphabetically by name");
            System.out.println("Enter Q to quit the program");
            System.out.println("Enter a value from the menu to proceed:");
            String input = sc.next();

            switch (input) {
                case "A":
                case "a":
                    addCustomer(seatCheck,returnSeatCheck,bubbleSort,sc);
                    break;

                case "V":
                case "v":
                    viewAllSeats();
                    break;

                case "E":
                case "e":
                    viewEmptySeats(seatCheck,returnSeatCheck,sc);
                    break;

                case "D":
                case "d":
                    removeSeat(seatCheck,returnSeatCheck,sc);
                    break;

                case "F":
                case "f":
                    findCustomer(seatCheck,returnSeatCheck,sc);
                    break;

                case "L":
                case "l":
                    loadDataForJourney1(seatCheck);
                    loadDataForJourney2(returnSeatCheck);
                    break;

                case "S":
                case "s":
                    storeDataJourney1(seatCheck);
                    storeDataJourney2(returnSeatCheck);
                    break;

                case "O":
                case "o":
                    alphabeticalOrder(bubbleSort);
                    break;

                case "Q":
                case "q":
                    quitProgram();
                    break;

                default:
                    System.out.println("Invalid Input");
            }
        }
    }

    /*Adding a customer to a seat*/
    public static void addCustomer(HashMap<String,String>seatCheck,HashMap<String,String>returnSeatCheck,HashMap<String,String>bubbleSort,Scanner sc) {

        Stage stage = new Stage();
        /*Creating the background panes*/
        StackPane stackPane = new StackPane();
        AnchorPane anchorPane = new AnchorPane();

        /*Creating a datepicker */
        DatePicker datePicker = new DatePicker();
        datePicker.setLayoutX(200);
        datePicker.setLayoutY(50);

        /*Confirm Button details*/
        Button okButton = new Button("Confirm");
        okButton.setPadding(new Insets(10, 20, 10, 20));
        okButton.setLayoutX(260);
        okButton.setLayoutY(650);
        okButton.setStyle("-fx-font: 25 sans-serif;-fx-background-color: linear-gradient(#3098F1, #70B1E9);");

        /*Displaying button color of empty seats */
        Button emptyButton = new Button(" Empty  Seats");
        emptyButton.setLayoutX(280);
        emptyButton.setLayoutY(350);
        emptyButton.setStyle("-fx-font: 15 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");

        /*Displaying button color of booked seats*/
        Button bookedButton = new Button("Booked Seats");
        bookedButton.setLayoutX(280);
        bookedButton.setLayoutY(390);
        bookedButton.setStyle("-fx-font: 22 arial; -fx-base: #F51008;-fx-font: 15 arial;");

        /*Name input details*/
        Label nameLabel = new Label("Please enter your name -:");
        nameLabel.setLayoutX(130);
        nameLabel.setLayoutY(100);
        nameLabel.setStyle("-fx-font: 22 arial; -fx-text-fill: #FFFFFF");
        TextField textField = new TextField();
        textField.setMaxSize(200, 50);
        textField.setLayoutX(390);
        textField.setLayoutY(100);


        /*Prompting the user for journey route*/
        System.out.println("Enter 1 for journey from Colombo to Badulla");
        System.out.println("Enter 2 for journey from Badulla to Colombo");
        int journey = sc.nextInt();
        switch (journey) {
            case 1:
            case 2:
                /*Assigning the set of seat buttons created within a for loop to a VBox*/
                VBox vBox1 = new VBox();
                for (int r = 1; r < 22; r = r + 2) {
                    int number = r;
                    String seat = "Seat";
                    /*Creating Buttons*/
                    Button button = new Button(seat + number);

                    /*Changing the already booked seats and not booked seats color appropriately*/
                    if (journey == 1 && seatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                    } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                    } else {
                        button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                    }
                    /*Set all the buttons size a same value(largest button size)*/
                    button.setMaxWidth(Double.MAX_VALUE);
                    /*Aligning the Vbox in the anchorpane*/
                    vBox1.getChildren().add(button);
                    vBox1.setSpacing(6);
                    vBox1.setLayoutX(30);
                    vBox1.setLayoutY(150);

                    /*What happens when a button with seat no is selected*/
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String seatNo = seat + number;
                            /*Checking if the selected seat is already booked*/
                            while (true) {
                                if (journey == 1 && seatCheck.containsKey(seatNo)) {
                                    System.out.println("This seat is already booked try another");
                                    break;

                                } else if (journey == 2 && returnSeatCheck.containsKey(seatNo)) {
                                    System.out.println("This seat is already booked try another");
                                    break;
                                }
                                if (textField.getText().isEmpty()) {
                                    System.out.println("Please enter your name in the provided field");
                                    break;
                                } else if (journey == 1) {
                                    /*Setting button color to red when button is clicked*/
                                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                    /*Displaying which seat you booked*/
                                    System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                    /*Storing the user's data ito hashmaps*/
                                    seatCheck.put(seatNo, textField.getText().toUpperCase());
                                    bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                    break;

                                } else {
                                    /*Setting button color to red while button is clicked*/
                                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                    /*Displaying which seat you booked*/
                                    System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                    /*Storing the user's data ito hashmaps*/
                                    returnSeatCheck.put(seatNo, textField.getText().toUpperCase());
                                    bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                    break;
                                }
                            }
                        }
                    });
                    /*Closing the window when confirm button is clicked*/
                    okButton.setOnAction(event -> stage.close());
                }

                /*Assigning the set of seat buttons created within a for loop to a VBox*/
                VBox vBox2 = new VBox();
                for (int r = 2; r < 21; r = r + 2) {
                    int number = r;
                    String seat = "Seat";
                    /*Creating Buttons*/
                    Button button = new Button(seat + number);

                    /*Changing the already booked seats and not booked seats color appropriately*/
                    if (journey == 1 && seatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                    } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                    } else {
                        button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                    }
                    /*Set all the buttons size a same value(largest button size)*/
                    button.setMaxWidth(Double.MAX_VALUE);
                    /*Aligning the Vbox in the anchorpane*/
                    vBox2.getChildren().add(button);
                    vBox2.setSpacing(6);
                    vBox2.setLayoutX(150);
                    vBox2.setLayoutY(150);

                    /*What happens when a button with seat no is selected*/
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String seatNo = seat + number;
                            /*Checking if the selected seat is already booked*/
                            while (true) {
                                if (journey == 1 && seatCheck.containsKey(seatNo)) {
                                    System.out.println("This seat is already booked try another");
                                    break;

                                } else if (journey == 2 && returnSeatCheck.containsKey(seatNo)) {
                                    System.out.println("This seat is already booked try another");
                                    break;
                                }
                                if (textField.getText().isEmpty()) {
                                    System.out.println("Please enter your name in the provided field");
                                    break;
                                } else if (journey == 1) {
                                    /*Setting button color to red when button is clicked*/
                                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                    /*Displaying which seat you booked*/
                                    System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                    /*Storing the user's data ito hashmaps*/
                                    seatCheck.put(seatNo, textField.getText().toUpperCase());
                                    bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                    break;

                                } else {
                                    /*Setting button color to red while button is clicked*/
                                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                    /*Displaying which seat you booked*/
                                    System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                    /*Storing the user's data ito hashmaps*/
                                    returnSeatCheck.put(seatNo, textField.getText().toUpperCase());
                                    bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                    break;
                                }
                            }
                        }
                    });
                    /*Closing the window when confirm button is clicked*/
                    okButton.setOnAction(event -> stage.close());
                }



            /*Assigning the set of seat buttons created within a for loop to a VBox*/
            VBox vBox3 = new VBox();
            for (int r = 22; r < 44; r = r + 2) {
                int number = r;
                String seat = "Seat";
                /*Creating Buttons*/
                Button button = new Button(seat + number);

                /*Changing the already booked seats and not booked seats color appropriately*/
                if (journey == 1 && seatCheck.containsKey(seat + number)) {
                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                } else {
                    button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                }
                /*Set all the buttons size a same value(largest button size)*/
                button.setMaxWidth(Double.MAX_VALUE);
                /*Aligning the Vbox in the anchorpane*/
                vBox3.getChildren().add(button);
                vBox3.setSpacing(6);
                vBox3.setLayoutX(420);
                vBox3.setLayoutY(150);

                /*What happens when a button with seat no is selected*/
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String seatNo = seat + number;
                        /*Checking if the selected seat is already booked*/
                        while (true) {
                            if (journey == 1 && seatCheck.containsKey(seatNo)) {
                                System.out.println("This seat is already booked try another");
                                break;

                            } else if (journey == 2 && returnSeatCheck.containsKey(seatNo)) {
                                System.out.println("This seat is already booked try another");
                                break;
                            }
                            if (textField.getText().isEmpty()) {
                                System.out.println("Please enter your name in the provided field");
                                break;
                            } else if (journey == 1) {
                                /*Setting button color to red when button is clicked*/
                                button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                /*Displaying which seat you booked*/
                                System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                /*Storing the user's data ito hashmaps*/
                                seatCheck.put(seatNo, textField.getText().toUpperCase());
                                bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                break;

                            } else {
                                /*Setting button color to red while button is clicked*/
                                button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                /*Displaying which seat you booked*/
                                System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                /*Storing the user's data ito hashmaps*/
                                returnSeatCheck.put(seatNo, textField.getText().toUpperCase());
                                bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                break;
                            }
                        }
                    }
                });
                /*Closing the window when confirm button is clicked*/
                okButton.setOnAction(event -> stage.close());
            }

            /*Assigning the set of seat buttons created within a for loop to a VBox*/
            VBox vBox4 = new VBox();
            for (int r = 23; r < 42; r = r + 2) {
                int number = r;
                String seat = "Seat";
                /*Creating Buttons*/
                Button button = new Button(seat + number);

                /*Changing the already booked seats and not booked seats color appropriately*/
                if (journey == 1 && seatCheck.containsKey(seat + number)) {
                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                    button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                } else {
                    button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                }
                /*Set all the buttons size a same value(largest button size)*/
                button.setMaxWidth(Double.MAX_VALUE);
                /*Aligning the Vbox in the anchorpane*/
                vBox4.getChildren().add(button);
                vBox4.setSpacing(6);
                vBox4.setLayoutX(540);
                vBox4.setLayoutY(150);

                /*What happens when a button with seat no is selected*/
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String seatNo = seat + number;
                        /*Checking if the selected seat is already booked*/
                        while (true) {
                            if (journey == 1 && seatCheck.containsKey(seatNo)) {
                                System.out.println("This seat is already booked try another");
                                break;

                            } else if (journey == 2 && returnSeatCheck.containsKey(seatNo)) {
                                System.out.println("This seat is already booked try another");
                                break;
                            }
                            if (textField.getText().isEmpty()) {
                                System.out.println("Please enter your name in the provided field");
                                break;
                            } else if (journey == 1) {
                                /*Setting button color to red when button is clicked*/
                                button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                /*Displaying which seat you booked*/
                                System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                /*Storing the user's data ito hashmaps*/
                                seatCheck.put(seatNo, textField.getText().toUpperCase());
                                bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                break;

                            } else {
                                /*Setting button color to red while button is clicked*/
                                button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                                /*Displaying which seat you booked*/
                                System.out.println(textField.getText() + " " + "booked" + " " + seat + number);
                                /*Storing the user's data ito hashmaps*/
                                returnSeatCheck.put(seatNo, textField.getText().toUpperCase());
                                bubbleSort.put(seatNo, textField.getText().toUpperCase() + " " + seatNo);
                                break;
                            }
                        }
                    }
                });
                /*Closing the window when confirm button is clicked*/
                okButton.setOnAction(event -> stage.close());
            }
            /*Adding all elements to the Anchorpane*/
            anchorPane.getChildren().addAll(vBox1, vBox2, vBox3, vBox4, okButton, emptyButton, bookedButton, textField, nameLabel, datePicker);
            /*Adding the anchorpane to a stackpane*/
            stackPane.getChildren().add(anchorPane);
            /*Setting the scene*/
            stage.setScene(new Scene(stackPane, 650, 750));
            /*Giving an id to the anchorpane*/
            anchorPane.setId("pane");
            /*Setting the window size to be fixed*/
            stage.setResizable(false);
            /*Linking to the external stylesheet*/
            anchorPane.getStylesheets().addAll(Main.class.getResource("style.css").toExternalForm());
            /*Pop up the window*/
            stage.showAndWait();
                break;
                /*Printing the error message if journey is neither equals to 1 or 2*/
            default:
                System.out.println("Invalid Input");

        }
    }

    /*Viewing all seats*/
    private static void viewAllSeats() {

        Stage stage = new Stage();

        StackPane stackPane = new StackPane();
        AnchorPane anchorPane = new AnchorPane();

        /*Assigning the set of seat buttons created within a for loop to a VBox*/
        VBox vBox1 = new VBox();
        for (int r = 1; r < 22; r = r + 2) {
            int number = r;
            String seat = "Seat";
            Button button = new Button(seat + number);
            button.setId(String.valueOf(r));
            button.setMaxWidth(Double.MAX_VALUE);
            button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
            vBox1.getChildren().add(button);
            vBox1.setSpacing(6);
            vBox1.setLayoutX(20);
            vBox1.setLayoutY(150);
        }

        /*Assigning the set of seat buttons created within a for loop to a VBox*/
        VBox vBox2 = new VBox();
        for (int r = 2; r < 21; r = r + 2) {
            int number = r;
            String seat = "Seat";
            String seatNo = seat + number;
            Button button = new Button(seat + number);
            button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
            button.setMaxWidth(Double.MAX_VALUE);
            vBox2.getChildren().add(button);
            vBox2.setSpacing(6);
            vBox2.setLayoutX(150);
            vBox2.setLayoutY(150);
        }

        /*Assigning the set of seat buttons created within a for loop to a VBox*/
        VBox vBox3 = new VBox();
        for (int r = 22; r < 44; r = r + 2) {
            int number = r;
            String seat = "Seat";
            Button button = new Button(seat + number);
            button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
            button.setMaxWidth(Double.MAX_VALUE);
            vBox3.getChildren().add(button);
            vBox3.setSpacing(6);
            vBox3.setLayoutX(410);
            vBox3.setLayoutY(150);
        }

        /*Assigning the set of seat buttons created within a for loop to a VBox*/
        VBox vBox4 = new VBox();
        for (int r = 23; r < 42; r = r + 2) {
            int number = r;
            String seat = "Seat";
            Button button = new Button(seat + number);
            button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
            button.setMaxWidth(Double.MAX_VALUE);
            vBox4.getChildren().add(button);
            vBox4.setLayoutX(540);
            vBox4.setLayoutY(150);
            vBox4.setSpacing(6);
        }
        anchorPane.getChildren().addAll(vBox1, vBox2, vBox3, vBox4);
        stackPane.getChildren().add(anchorPane);
        stage.setScene(new Scene(stackPane, 650, 750));
        anchorPane.setId("pane");
        stage.setResizable(false);
        anchorPane.getStylesheets().addAll(Main.class.getResource("style.css").toExternalForm());
        stage.showAndWait();
    }

    /*Viewing all empty seats*/
    public static void viewEmptySeats(HashMap<String,String>seatCheck,HashMap<String,String>returnSeatCheck,Scanner sc) {
        Stage stage = new Stage();

        StackPane stackPane = new StackPane();
        AnchorPane anchorPane = new AnchorPane();

        DatePicker datePicker = new DatePicker();
        datePicker.setLayoutX(200);
        datePicker.setLayoutY(50);

        /*Displaying button color of empty seats */
        Button emptyButton = new Button(" Empty  Seats");
        emptyButton.setLayoutX(280);
        emptyButton.setLayoutY(350);
        emptyButton.setStyle("-fx-font: 15 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");

        /*Displaying button color of booked seats*/
        Button bookedButton = new Button("Booked Seats");
        bookedButton.setLayoutX(280);
        bookedButton.setLayoutY(390);
        bookedButton.setStyle("-fx-font: 22 arial; -fx-base: #F51008;-fx-font: 15 arial;");

        /*Prompting the user for journey route*/
        System.out.println("Enter 1 for journey from Colombo to Badulla");
        System.out.println("Enter 2 for journey from Badulla to Colombo");
        int journey = sc.nextInt();
        switch (journey) {
            case 1:
            case 2:
                /*Assigning the set of seat buttons created within a for loop to a VBox*/
                VBox vBox1 = new VBox();
                for (int r = 1; r < 22; r = r + 2) {
                    int number = r;
                    String seat = "Seat";

                    Button button = new Button(seat + number);
                    button.setId(String.valueOf(r));

                    /*Changing the already booked seats and not booked seats color appropriately*/
                    if (journey == 1 && seatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                    } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                    } else {
                        button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                    }

                    button.setMaxWidth(Double.MAX_VALUE);
                    vBox1.getChildren().add(button);
                    vBox1.setSpacing(6);
                    vBox1.setLayoutX(20);
                    vBox1.setLayoutY(150);

                }

                /*Assigning the set of seat buttons created within a for loop to a VBox*/
                VBox vBox2 = new VBox();
                for (int r = 2; r < 21; r = r + 2) {
                    int number = r;
                    String seat = "Seat";
                    String seatNo = seat + number;
                    Button button = new Button(seat + number);

                    /*Changing the already booked seats and not booked seats color appropriately*/
                    if (journey == 1 && seatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                    } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                    } else {
                        button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                    }
                    button.setMaxWidth(Double.MAX_VALUE);
                    vBox2.getChildren().add(button);
                    vBox2.setSpacing(6);
                    vBox2.setLayoutX(150);
                    vBox2.setLayoutY(150);

                }

                /*Assigning the set of seat buttons created within a for loop to a VBox*/
                VBox vBox3 = new VBox();
                for (int r = 22; r < 44; r = r + 2) {
                    int number = r;
                    String seat = "Seat";
                    Button button = new Button(seat + number);

                    /*Changing the already booked seats and not booked seats color appropriately*/
                    if (journey == 1 && seatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                    } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                    } else {
                        button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                    }

                    button.setMaxWidth(Double.MAX_VALUE);
                    vBox3.getChildren().add(button);
                    vBox3.setSpacing(6);
                    vBox3.setLayoutX(410);
                    vBox3.setLayoutY(150);

                }


                /*Assigning the set of seat buttons created within a for loop to a VBox*/
                VBox vBox4 = new VBox();
                for (int r = 23; r < 42; r = r + 2) {
                    int number = r;
                    String seat = "Seat";
                    Button button = new Button(seat + number);

                    /*Changing the already booked seats and not booked seats color appropriately*/
                    if (journey == 1 && seatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");
                    } else if (journey == 2 && returnSeatCheck.containsKey(seat + number)) {
                        button.setStyle("-fx-font: 22 arial; -fx-base: #F51008;");

                    } else {
                        button.setStyle("-fx-font: 22 arial; -fx-background-color: linear-gradient(#25E793 , #08CE37 );-fx-background-radius: 6, 5;");
                    }

                    button.setMaxWidth(Double.MAX_VALUE);
                    vBox4.getChildren().add(button);
                    vBox4.setLayoutX(540);
                    vBox4.setLayoutY(150);
                    vBox4.setSpacing(6);
                }
                anchorPane.getChildren().addAll(vBox1, vBox2, vBox3, vBox4, emptyButton, bookedButton, datePicker);
                stackPane.getChildren().add(anchorPane);
                stage.setScene(new Scene(stackPane, 650, 750));
                anchorPane.setId("pane");
                stage.setResizable(false);
                anchorPane.getStylesheets().addAll(Main.class.getResource("style.css").toExternalForm());
                stage.showAndWait();
                break;
            default:
                System.out.println("Invalid Input");
        }
    }

        /*Removing a customer from the seat*/
        private static void removeSeat(HashMap<String,String>seatCheck,HashMap<String,String>returnSeatCheck,Scanner sc) {
            System.out.println("Enter 1 for journey from Colombo to Badulla");
            System.out.println("Enter 2 for journey from Badulla to Colombo");
            /*Getting the user input*/
            int journey = sc.nextInt();
            switch (journey) {
                case 1:
                case 2:
                    System.out.print("Enter the name of the customer to remove:");
                    String name = sc.next().toUpperCase();
                    System.out.println("Enter the" + " " + name + "'s seat no you want to remove");
                    String seat = "Seat" + sc.next();
                    if (journey == 1) {
                        /*Checking whether the customer is there*/
                        if (seatCheck.containsKey(seat) && seatCheck.containsValue(name)) {
                            seatCheck.remove(seat, name);
                            System.out.println("Successfully removed customer" + " " + name + " " + "from" + " " + seat);
                        } else {
                            System.out.println("There is no such customer owning that seat please check your details again");
                        }
                    } else if (journey == 2) {
                        if (returnSeatCheck.containsKey(seat) && seatCheck.containsValue(name)) {
                            returnSeatCheck.remove(seat, name);
                            System.out.println("Successfully removed customer" + " " + name + " " + "from" + " " + seat);
                        } else {
                            System.out.println("There is no such customer owning that seat please check your details again");
                        }
                    }
                    break;
                    /*Default error message if journey inputs are wrong*/
                default:
                    System.out.println("Invalid Input");
            }
        }

        /*Finding a customer by name*/
    private static void findCustomer(HashMap<String,String>seatCheck,HashMap<String,String>returnSeatCheck,Scanner sc) {
        System.out.println("Enter 1 for journey from Colombo to Badulla");
        System.out.println("Enter 2 for journey from Badulla to Colombo");
        /*Taking the input from the user*/
        int journey = sc.nextInt();
        switch (journey) {
            case 1:
            case 2:
            System.out.println("Enter your name:");
            String findName = sc.next().toUpperCase();

            if (journey == 1) {
                seatCheck.forEach((key, value) -> {
                    /*Checking whether the name is available in the hashmap*/
                    if (value.equals(findName)) {
                        /*Printing the seat no if the customer is available in that seat*/
                        System.out.println(findName + "'s Seat is" + " " + key);
                    }
                });
            } else if (journey == 2) {
                seatCheck.forEach((key, value) -> {
                    if (value.equals(findName)) {
                        System.out.println(findName + "'s Seat is" + " " + key);
                    }
                });
            }
            break;
            default:
                /*Default error message if journey inputs are wrong*/
                System.out.println("Invalid Input");
        }
    }

    /*Storing data for journey 1*/
    private static void storeDataJourney1(HashMap<String,String>seatCheck) {
        /*Getting mongodb to the console and creating clients databases and collections appropriately*/
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
        MongoClient mClient =MongoClients.create();
        MongoDatabase mongoDatabase = mClient.getDatabase("Journey-1");
        MongoCollection mCollection1;
        mCollection1 = mongoDatabase.getCollection("Colombo_to_Badulla");

        /*Creating a document an appending the hashmap data to the document*/
        Document document = new Document();
        seatCheck.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s1) {
                document.append(s1, s);
            }
        });
        mCollection1.insertOne(document);
        System.out.println("Colombo to Badulla entries saved");
    }

    /*Storing data for journey 2*/
    private static void storeDataJourney2(HashMap<String,String>returnSeatCheck) {
        /*Getting mongodb to the console and creating clients databases and collections appropriately*/
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
        MongoClient mClient = MongoClients.create();
        MongoDatabase mongoDatabase = mClient.getDatabase("Journey-2");
        MongoCollection mCollection2;
        mCollection2 = mongoDatabase.getCollection("Badulla_to_Colombo");
        /*Creating a document an appending the hashmap data to the document*/
        Document document = new Document();
        returnSeatCheck.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                document.append(s2, s);
            }
        });
        mCollection2.insertOne(document);
        System.out.println("Badulla to Colombo entries saved");
    }

    /*Loading journey 1 data*/
    private static void loadDataForJourney1(HashMap<String,String>seatCheck) {
        /*Getting mongodb data to the console*/
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
        MongoClient mClient = MongoClients.create();
        MongoDatabase mongoDatabase = mClient.getDatabase("Journey-1");
        MongoCollection mCollection1;
        mCollection1 = mongoDatabase.getCollection("Colombo_to_Badulla");
        mCollection1.find().forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                /*Getting the data from the document and putting them into hashmaps*/
                for (String id : document.keySet()) {
                    if (id.equals("_id")) continue;
                    seatCheck.put(document.getString(id), document.getString(id));
                }
            }
        });
    }
    /*Loading journey 2 data */
    private static void loadDataForJourney2(HashMap<String,String>returnSeatCheck) {
        /*Getting mongodb data to the console*/
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
        MongoClient mClient = MongoClients.create();
        MongoDatabase mongoDatabase = mClient.getDatabase("Journey-2");
        MongoCollection mCollection2;
        mCollection2 = mongoDatabase.getCollection("Badulla_to_Colombo");
        mCollection2.find().forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                /*Getting the data from the document and putting them into hashmaps*/
                for (String id : document.keySet()) {
                    if (id.equals("_id")) continue;
                    returnSeatCheck.put(document.getString(id), document.getString(id));
                }
            }
        });
    }

    /*Sorting customer to alphabetical order*/
    private static void alphabeticalOrder(HashMap<String,String>bubbleSort) {
        /*Getting the bubblesort hashmap data and assigning it into an array*/
        String names[] = bubbleSort.values().toArray(new String[0]);
        String sort;
        System.out.println("Seats booked in alphabetical order...");
        /*Implementing bubble sort algorithm*/
        for (int j = 0; j < names.length; j++) {
            for (int i = j + 1; i < names.length; i++) {
                // string compare
                if (names[i].compareTo(names[j]) < 0) {
                    sort = names[j];
                    names[j] = names[i];
                    names[i] = sort;
                }
            }
            System.out.println(names[j]);
        }
    }

    private static void quitProgram() {
        /*Quiting the program*/
        System.exit(0);
    }

}





