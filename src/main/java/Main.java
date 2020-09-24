import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/views/LogIn.fxml"));
        primaryStage.setTitle("Fancy Picnics");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(550);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/fancy-picnics.jpg")));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}