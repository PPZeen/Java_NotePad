import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class SaveChangePane extends BorderPane{
    Button saveBtn;
    Button dontSaveBtn;
    Button cancelBtn;

    public SaveChangePane(){
        createPane();
    }

    public void createPane(){
        HBox btnBox = new HBox();
        saveBtn = new Button("Save");
        dontSaveBtn = new Button("Don't Save");
        cancelBtn = new Button("Cancel");
        btnBox.getChildren().addAll(saveBtn,dontSaveBtn,cancelBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setSpacing(5);
        Text text = new Text("Do you want to save change?");
        text.setFont(new Font(16));
        setPadding(new Insets(5));
        setTop(text);
        setBottom(btnBox);
    }
}