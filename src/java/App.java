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
//        float t1 = System.nanoTime();
//        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
//
//            String sCurrentLine;
//
//            while ((sCurrentLine = br.readLine()) != null) {
//                System.out.println(sCurrentLine);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        float t2 = System.nanoTime() - t1;
//
//        int x = 5;
//
//        for (int i = 0; i < x; i++) {
//            for (int j = i; j < x; j++) {
//                System.out.println(i + " " + j);
//            }
//        }

//        int[] temp = new int[] {1,2, 2,3,3,3};
//
//        System.out.println(temp.length);
//
//        int i = temp.length - 1;
//        int max = temp[i];
//        while (temp[i] == max){
//            i--;
//        }
//        i++;
//
//        System.out.println(i);

        primaryStage.setTitle("Net simulation");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));
//
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