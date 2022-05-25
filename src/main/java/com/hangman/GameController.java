package com.hangman;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class GameController {
    public Label AvailableWords;
    public Label TotalScore;
    public Label SuccessPercent;

    public TextField LoadField;

    public TextField CreateField1;
    public TextField CreateField2;

    public Label PercentSix;
    public Label PercentSevenNine;
    public Label PercentTenMore;

    public TableView<Record> RoundsTable;

    public TextField PositionField;
    public TextField LetterField;

    public Label GuessLabel;
    public javafx.scene.image.ImageView ImageView;
    public TableView<PositionProbabilities> ProbabilityTable;

    private Librarian librarian = new Librarian();
    private Historian historian = new Historian("save.log");

    private Dictionary dictionary;
    private SecretWord secret_word;
    private Referee    referee;

    private Stage popup_stage = new Stage();

    public void UpdateLabels() {
        if (dictionary != null)
            AvailableWords.setText("Available Words: " + (int) dictionary.getStats()[0]);
        else
            AvailableWords.setText("");

        if (referee != null) {
            TotalScore.setText("Total Score: " + referee.getScore());
            SuccessPercent.setText("Success Percent: " + referee.getSuccessPercent());
        }
        else {
            TotalScore.setText("");
            SuccessPercent.setText("");
        }
    }

    public void UpdateView() {
        if (referee != null) {
            GuessLabel.setText(secret_word.getGuess());
            Image image = new Image(getClass().getResourceAsStream("image" + referee.getFailed() + ".png"));
            ImageView.setImage(image);
            ImageView.setViewport(new Rectangle2D(image.getWidth()/4, image.getHeight()/6, image.getWidth()*3/10, image.getHeight()*4/5));

            final ObservableList<PositionProbabilities> position_probabilities = FXCollections.observableArrayList(secret_word.getProbabilities());

            TableColumn positionCol = new TableColumn("Position");
            positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
            positionCol.setPrefWidth(100);

            TableColumn probabilitiesCol = new TableColumn("Possible Letters");
            probabilitiesCol.setCellValueFactory(new PropertyValueFactory<>("probabilities"));
            probabilitiesCol.setPrefWidth(250);

            ProbabilityTable.setItems(position_probabilities);
            ProbabilityTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ProbabilityTable.getColumns().addAll(positionCol, probabilitiesCol);
        }
        else {
            GuessLabel.setText("");
            ImageView.setImage(null);
        }
    }

    public void Start() {
        if (dictionary != null) {
            secret_word = dictionary.getSecretWord();
            referee = new Referee();
            System.out.println(secret_word.reveal());

            UpdateLabels();
            UpdateView();
        }
    }

    public void LoadMenu() {
        try {
            popup_stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("load-popup.fxml"));
            loader.setController(this);
            popup_stage.setScene(new Scene(loader.load()));
            popup_stage.setTitle("Load Dictionary");
            popup_stage.show();
        }
        catch (Exception e) {
            Label message = new Label(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setContent(message);
            alert.showAndWait();
        }
    }

    public void CreateMenu() {
        try {
            popup_stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("create-popup.fxml"));
            loader.setController(this);
            popup_stage.setScene(new Scene(loader.load()));
            popup_stage.setTitle("Create Dictionary");
            popup_stage.show();
        }
        catch (Exception e) {
            Label message = new Label(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setContent(message);
            alert.showAndWait();
        }
    }

    public void LoadDictionary() {
        try {
            dictionary = librarian.loadDictionary(LoadField.getText());

            popup_stage.close();

            secret_word = null;
            referee     = null;

            UpdateLabels();
            UpdateView();
        }
        catch (Exception e) {
            Label message = new Label(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setContent(message);
            alert.showAndWait();
        }
    }

    public void CreateDictionary() {
        try {
            librarian.createDictionary(CreateField1.getText(), CreateField2.getText());

            popup_stage.close();
        }
        catch (Exception e) {
            Label message = new Label(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setContent(message);
            alert.showAndWait();
        }
    }

    public void Exit() {
        historian.saveRecords();
        Platform.exit();
    }

    public void DictionaryStats() {
        try {
            popup_stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dictionary_stats-popup.fxml"));
            loader.setController(this);
            popup_stage.setScene(new Scene(loader.load()));
            popup_stage.setTitle("Dictionary Statistics");
            popup_stage.show();
        }
        catch (Exception e) {
            Label message = new Label(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setContent(message);
            alert.showAndWait();
        }

        if (dictionary != null) {
            double[] stats = dictionary.getStats();
            PercentSix.setText("" + stats[1] + "%");
            PercentSevenNine.setText("" + stats[2] + "%");
            PercentTenMore.setText("" + stats[3] + "%");
        }
        else {
            PercentSix.setText("No dictionary");
            PercentSevenNine.setText("No dictionary");
            PercentTenMore.setText("No dictionary");
        }
    }

    public void Guess() {
        if (referee != null) {
            try {
                referee.addAttempt(secret_word.guessLetter(Integer.parseInt(PositionField.getText()), LetterField.getText().charAt(0)));

                UpdateLabels();
                UpdateView();

                if (secret_word.found() || referee.lost()) {
                    String winner;
                    if (secret_word.found())
                        winner = "Player";
                    else
                        winner = "Computer";
                    historian.addEntry(referee.getScore(), winner);

                    secret_word = null;
                    referee     = null;

                    Label message = new Label("Winner: " + winner);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.getDialogPane().setContent(message);
                    alert.showAndWait();

                    UpdateLabels();
                    UpdateView();
                }
            }
            catch (Exception e) {
                Label message = new Label(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().setContent(message);
                alert.showAndWait();
            }
        }
    }

    public void Rounds() {
        try {
            popup_stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rounds-popup.fxml"));
            loader.setController(this);
            popup_stage.setScene(new Scene(loader.load()));
            popup_stage.setTitle("Round History");
            popup_stage.show();

            final ObservableList<Record> records = FXCollections.observableArrayList(historian.getRecords());

            TableColumn scoreCol = new TableColumn("Score");
            scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
            scoreCol.setPrefWidth(150);

            TableColumn winnerCol = new TableColumn("Winner");
            winnerCol.setCellValueFactory(new PropertyValueFactory<>("winner"));
            winnerCol.setPrefWidth(150);

            RoundsTable.setItems(records);
            RoundsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            RoundsTable.getColumns().addAll(scoreCol, winnerCol);
        }
        catch (Exception e) {
            Label message = new Label(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setContent(message);
            alert.showAndWait();
        }
    }

    public void Reveal() {
        if (referee != null) {
            String word = secret_word.reveal();

            historian.addEntry(referee.getScore(), "Computer");

            secret_word = null;
            referee = null;

            UpdateLabels();
            UpdateView();

            Label message = new Label("Winner: Computer\n The word was: " + word);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setContent(message);
            alert.showAndWait();
        }
    }
}