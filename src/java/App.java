import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    /**
     * Inicializace okna aplikace
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Net simulation");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));

        // dependence injection
//        loader.setControllerFactory(param -> {
//            try {
//                return param.getConstructor(Data.class, Stage.class).newInstance(data, primaryStage);
//            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
        Parent parent = loader.load();

        Scene scene = new Scene(parent, primaryStage.getWidth(), primaryStage.getHeight());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
