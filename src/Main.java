import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

enum FileMenu{
    New,
    Open,
    Save,
    SaveAs,
    Exit
};

public class Main extends Application{
    private static Alert alert;
    private File file;
    private Label fType = new Label();
    private double size = 500;

    static Stage stage;

    BorderPane borderPane = new BorderPane();
    Scene s = new Scene(borderPane, size, size);
    HBox tBox = new HBox();

    Stage scStage = new Stage();
    TextArea textArea = new TextArea();
    FileChooser fileChooser = new FileChooser();
    PrintStream printS;
    boolean saveChange = false;
    boolean openEvent = false;
    boolean requestOpen = false;
    boolean readOnly = false;
    boolean fileReadOnly = false;
    String openFile = "";

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {
        Main.stage = stage;

        EnumMenu fileMenu = new EnumMenu(FileMenu.class,"File");
        MenuBar menuBar = new MenuBar(fileMenu.getMenu());
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
        
        alert = new Alert(AlertType.NONE);
        
        fileMenu.getMenuItems()[FileMenu.New.ordinal() ].setOnAction(this::onClickNew);
        fileMenu.getMenuItems()[FileMenu.Open.ordinal()].setOnAction(this::onClickOpen);
        fileMenu.getMenuItems()[FileMenu.Save.ordinal()].setOnAction(this::onClickSave);
        fileMenu.getMenuItems()[FileMenu.SaveAs.ordinal()].setOnAction(this::onClickSaveAs);
        fileMenu.getMenuItems()[FileMenu.Exit.ordinal()].setOnAction(this::onClickExit);

        ComboBox<String> cboType = new ComboBox();
        ObservableList<String> items = FXCollections.observableArrayList("Editable", "Read only" );

        cboType.getItems().addAll(items);
        cboType.setValue(items.get(0));
        cboType.setOnAction(e -> {
            if(cboType.getValue().toString().equals("Editable") && !fileReadOnly) readOnly = false;
            else readOnly = true;
        });

        tBox.getChildren().addAll(cboType, fType);
        tBox.setAlignment(Pos.CENTER);
        tBox.setSpacing(s.getWidth()/1.5);
        
        textArea.setWrapText(true);
        borderPane.setTop(menuBar);
        borderPane.setCenter(textArea);
        borderPane.setBottom(tBox);

        stage.setTitle("*.txt");
        stage.setScene(s);
        stage.show();
    }
    
    public void onClickNew(ActionEvent event) {
        SaveChangePane scPane = new SaveChangePane();
        Scene scScene = new Scene(scPane, 300, 60);
        scPane.saveBtn.setOnAction((e) -> onClickSaveAs(event));
        scPane.dontSaveBtn.setOnAction((e) -> {
            stage.setTitle("*.txt");
            textArea.setText("");
            saveChange = false;
            scStage.close();
        });
        scPane.cancelBtn.setOnAction((e) -> {
            saveChange = false;
            scStage.close();
        });
        scStage.setTitle("Save Change?");
        scStage.setScene(scScene);

        if(textArea.getText().hashCode() != 0 && !saveChange) {
            saveChange = true;
            scStage.show();
        }
    }

    public void onClickOpen(ActionEvent event) {
        openEvent = true;
        if(textArea.getText().hashCode() != 0 && !requestOpen) {
            SaveChangePane scPane = new SaveChangePane();
            Scene scScene = new Scene(scPane,300,60);

            scPane.saveBtn.setOnAction((e) -> {
                if(openFile.length() == 0) {
                    onClickSaveAs(event);
                    scStage.close();
                }else {
                    onClickSave(event);
                    requestOpen = true;
                    scStage.close();
                    onClickOpen(event);
                }
            });
            scPane.dontSaveBtn.setOnAction((e) -> {
                requestOpen = true;
                scStage.close();
                onClickOpen(event);
            });
            scPane.cancelBtn.setOnAction((e) -> {
                saveChange = true;
                scStage.close();
            });
            scStage.setTitle("Save Change?");
            scStage.setScene(scScene);
            scStage.show();
        }else {
            saveChange = false;
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("txt File",".txt"));
            file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                openFile = file.toString();
                
                if("txt".equals(openFile.substring(openFile.length()-3, openFile.length()))) {
                    stage.setTitle(file.getName());
                    textArea.clear();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null){
                            textArea.appendText(line);
                            textArea.appendText("\n");
                        }
                    } catch (IOException ex) {ex.printStackTrace();}

                    if (file.canWrite()) {
                        fileReadOnly = false;
                        fType.setText("Editable");
                    }else {
                        fileReadOnly = true;
                        fType.setText("Read Only");
                    }
                }else {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setContentText("This file is not a text file.");
                    alert.show();
                }
            }
        }
        requestOpen = false;
    }

    public void onClickSave(ActionEvent event) {
        if (fileReadOnly) {
            alert.setAlertType(AlertType.WARNING);
            alert.setContentText("This File is read only.");
            alert.show();
        }else {
            if(openEvent) {
                try {
                    file = Path.of(openFile).toFile();
                    if(file != null){
                        fileChooser.setInitialDirectory(file.getParentFile());
                        try {
                            printS = new PrintStream(file);
                            printS.print(textArea.getText());
                        }catch(FileNotFoundException e) {
                            System.out.println("File can't save");
                        }
                        if(readOnly) {
                            file.setWritable(false);
                            fType.setText("Read Only");
                        }else {
                            file.setWritable(true);
                            fType.setText("Editable");
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                onClickSaveAs(event);
            }
        }
    }
    
    public void onClickSaveAs(ActionEvent event) {
        if (fileReadOnly) {
            alert.setAlertType(AlertType.WARNING);
            alert.setContentText("This File is read only.");
            alert.show();
        }else {
            fileChooser.setTitle("Save As File");
            fileChooser.setInitialFileName("*.txt");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("text file", ".txt"));
            
            scStage.close();
            try {
                file = fileChooser.showSaveDialog(stage);
                if(file != null) {
                    fileChooser.setInitialDirectory(file.getParentFile());
                    try {
                        printS = new PrintStream(file);
                        printS.print(textArea.getText());
                        stage.setTitle(file.getName());
                    }catch(FileNotFoundException e) {
                        System.out.println("File can't save");
                    }
                    if(readOnly) {
                        file.setWritable(false);
                        fType.setText("Read Only");
                    }else {
                        file.setWritable(true);
                        fType.setText("Editable");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(saveChange) {
                stage.setTitle("*.txt");
                textArea.setText("");
                saveChange = false;
            }
        }
    }

    public void onClickExit(ActionEvent event) {
        if(textArea.getText().hashCode() != 0 && !saveChange) {
            SaveChangePane scPane = new SaveChangePane();
            Scene scScene = new Scene(scPane,300,60);
            scPane.saveBtn.setOnAction((e) -> {
                if(openFile.length() == 0){
                    onClickSaveAs(event);
                    scStage.close();
                    stage.close();
                }
                else {
                    onClickSave(event);
                    requestOpen = true;
                    scStage.close();
                    stage.close();
                }
            });
            scPane.dontSaveBtn.setOnAction((e) -> {
                saveChange = false;
                scStage.close();
                stage.close();
            });
            scPane.cancelBtn.setOnAction((e) -> {
                saveChange = false;
                scStage.close();
            });
            scStage.setTitle("Save Change?");
            scStage.setScene(scScene);
            scStage.show();
        }
        else stage.close();
    }
}