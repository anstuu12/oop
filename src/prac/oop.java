package labex1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AirFryerBeginner extends Application {

    String[] mode = {"ON", "OFF", "TIMER", "TEMP", "FRY"};

    int modeNumber = 0;
    int time = 1;
    int temperature = 80;
    int timeLeft = 0;
    int currentTemp = 25;

    boolean plugged = false;
    boolean basketIn = false;
    boolean powerOn = false;
    boolean timerSet = false;
    boolean tempSet = false;
    boolean changingValue = false;
    boolean frying = false;
    boolean blue = false;

    Label line1 = new Label();
    Label line2 = new Label();
    Label fan = new Label("Fan: OFF");
    Label heater = new Label("Heater: OFF");

    VBox screen = new VBox();

    Timeline countdown;
    Timeline flash;

    @Override
    public void start(Stage stage) {

        Label title = new Label("AIR FRYER");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        line1.setMaxWidth(Double.MAX_VALUE);
        line2.setMaxWidth(Double.MAX_VALUE);

        line1.setAlignment(Pos.CENTER_LEFT);
        line2.setAlignment(Pos.CENTER);

        line1.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        line2.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        screen.getChildren().addAll(line1, line2);
        screen.setPrefSize(300, 100);
        screen.setPadding(new Insets(10));

        setScreenColor("yellow");

        Button minus = new Button("-");
        Button select = new Button("PWR/SEL");
        Button plus = new Button("+");

        minus.setPrefSize(80, 50);
        select.setPrefSize(100, 50);
        plus.setPrefSize(80, 50);

        ToggleButton plugButton = new ToggleButton("Cord: OUT");
        ToggleButton basketButton = new ToggleButton("Basket: OUT");

        plugButton.setPrefWidth(150);
        basketButton.setPrefWidth(150);

        plugButton.setOnAction(e -> {

            if (plugButton.isSelected()) {
                plugged = true;
                plugButton.setText("Cord: IN");
            } else {
                plugged = false;
                plugButton.setText("Cord: OUT");

                if (powerOn) {
                    turnOff();
                }
            }
        });

        basketButton.setOnAction(e -> {

            if (basketButton.isSelected()) {
                basketIn = true;
                basketButton.setText("Basket: IN");
            } else {
                basketIn = false;
                basketButton.setText("Basket: OUT");

                if (powerOn) {
                    turnOff();
                }
            }
        });

        minus.setOnAction(e -> minusButton());
        plus.setOnAction(e -> plusButton());
        select.setOnAction(e -> selectButton());

        HBox buttons = new HBox(10, minus, select, plus);
        buttons.setAlignment(Pos.CENTER);

        HBox toggles = new HBox(10, plugButton, basketButton);
        toggles.setAlignment(Pos.CENTER);

        HBox status = new HBox(30, fan, heater);
        status.setAlignment(Pos.CENTER);

        VBox root = new VBox(
                20,
                title,
                screen,
                buttons,
                toggles,
                status
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene = new Scene(root, 450, 400);

        stage.setTitle("Air Fryer");
        stage.setScene(scene);
        stage.show();

        showMode();
    }

    public void minusButton() {

        if (frying) {
            changeMode(-1);
        } else if (changingValue) {
            changeValue(-1);
        } else {
            changeMode(-1);
        }
    }

    public void plusButton() {

        if (frying) {
            changeMode(1);
        } else if (changingValue) {
            changeValue(1);
        } else {
            changeMode(1);
        }
    }

    public void changeMode(int number) {

        modeNumber = modeNumber + number;

        if (modeNumber > 4) {
            modeNumber = 0;
        }

        if (modeNumber < 0) {
            modeNumber = 4;
        }

        if (mode[modeNumber].equals("FRY")) {

            if (!canFry()) {
                changeMode(number);
                return;
            }
        }

        showMode();
    }

    public void showMode() {

        line1.setText(mode[modeNumber]);

        if (frying) {
            line2.setText(timeLeft + " sec");
            return;
        }

        if (mode[modeNumber].equals("ON")) {

            if (powerOn) {
                line2.setText("ON");
            } else {
                line2.setText("OFF");
            }
        }

        if (mode[modeNumber].equals("OFF")) {
            line2.setText("OFF");
        }

        if (mode[modeNumber].equals("TIMER")) {
            line2.setText(time + " sec");
        }

        if (mode[modeNumber].equals("TEMP")) {
            line2.setText(temperature + " °C");
        }

        if (mode[modeNumber].equals("FRY")) {
            line2.setText("FRY");
        }
    }

    public void selectButton() {

        String selected = mode[modeNumber];

        if (frying) {

            if (selected.equals("OFF")) {
                turnOff();
            }

            return;
        }

        if (changingValue) {

            if (selected.equals("TIMER")) {
                timerSet = true;
                line2.setText(time + " sec SET");
            }

            if (selected.equals("TEMP")) {
                tempSet = true;
                line2.setText(temperature + " °C SET");
            }

            changingValue = false;

            if (canFry()) {
                modeNumber = 4;
                line1.setText("FRY");
                line2.setText("FRY");
            }

            return;
        }

        if (selected.equals("ON")) {
            turnOn();
        }

        if (selected.equals("OFF")) {
            turnOff();
        }

        if (selected.equals("TIMER")) {

            if (powerOn) {
                changingValue = true;
                line2.setText(time + " sec");
            }
        }

        if (selected.equals("TEMP")) {

            if (powerOn) {
                changingValue = true;
                line2.setText(temperature + " °C");
            }
        }

        if (selected.equals("FRY")) {
            startFrying();
        }
    }

    public void changeValue(int number) {

        if (mode[modeNumber].equals("TIMER")) {

            time = time + number;

            if (time < 1) {
                time = 1;
            }

            if (time > 60) {
                time = 60;
            }

            line2.setText(time + " sec");
        }

        if (mode[modeNumber].equals("TEMP")) {

            temperature = temperature + (number * 5);

            if (temperature < 80) {
                temperature = 80;
            }

            if (temperature > 200) {
                temperature = 200;
            }

            line2.setText(temperature + " °C");
        }
    }

    public void turnOn() {

        if (!plugged || !basketIn) {
            line2.setText("CHECK CORD/BASKET");
            return;
        }

        powerOn = true;

        line1.setText("ON");
        line2.setText("ON");

        setScreenColor("red");
    }

    public boolean canFry() {

        if (powerOn && timerSet && tempSet) {
            return true;
        }

        return false;
    }

    public void startFrying() {

        if (!canFry()) {
            return;
        }

        frying = true;
        changingValue = false;

        timeLeft = time;
        currentTemp = 25;

        line1.setText("FRY");
        line2.setText(timeLeft + " sec");

        fan.setText("Fan: SPINNING");
        heater.setText("Heater: " + currentTemp + " °C");

        if (timeLeft <= 5) {
            startFlashing();
        }

        countdown = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        e -> updateFryer()
                )
        );

        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    public void updateFryer() {

        timeLeft--;

        if (currentTemp < temperature) {

            currentTemp = currentTemp + 10;

            if (currentTemp > temperature) {
                currentTemp = temperature;
            }

            heater.setText("Heater: " + currentTemp + " °C");
        }

        line2.setText(timeLeft + " sec");

        if (timeLeft <= 5 && timeLeft > 0) {

            if (flash == null) {
                startFlashing();
            }
        }

        if (timeLeft <= 0) {
            turnOff();
        }
    }

    public void startFlashing() {

        flash = new Timeline(
                new KeyFrame(
                        Duration.seconds(0.5),
                        e -> flashScreen()
                )
        );

        flash.setCycleCount(Timeline.INDEFINITE);
        flash.play();
    }

    public void flashScreen() {

        if (blue) {
            setScreenColor("red");
        } else {
            setScreenColor("blue");
        }

        blue = !blue;
    }

    public void turnOff() {

        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }

        if (flash != null) {
            flash.stop();
            flash = null;
        }

        powerOn = false;
        frying = false;
        changingValue = false;

        timerSet = false;
        tempSet = false;
        blue = false;

        time = 1;
        temperature = 80;
        currentTemp = 25;

        modeNumber = 1;

        line1.setText("OFF");
        line2.setText("OFF");

        fan.setText("Fan: OFF");
        heater.setText("Heater: OFF");

        setScreenColor("yellow");
    }

    public void setScreenColor(String color) {

        screen.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-border-color: black;" +
                "-fx-border-width: 3;"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
