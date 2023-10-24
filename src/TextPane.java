import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class TextPane extends BorderPane {
    private TextArea txtArea = new TextArea();
    TextPane(){
        txtArea.setWrapText(true);
        txtArea.setEditable(true);
        setCenter(txtArea);
    }
}