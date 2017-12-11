import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Button loadDataBtn;
    public Button playBtn;
//    public Button powerCutBtn;
    public Button detailsBtn;
//    public ComboBox router1Cmb;
//    public ComboBox router2Cmb;
//    public Button rndPowerCutBtn;
//    public Spinner<Integer> tickSpn;
    public Canvas visualCnv;
    public TextArea logTxt;
//    public VBox leftVBox;
//    public ProgressBar progressBar;
//    public CheckBox tickNumChBox;
    public CheckBox sDataChBox;
    public CheckBox dataPathChBox;
    public CheckBox dataDestChBox;
    public CheckBox memUsageChBox;
    public CheckBox trafficChBox;
    public CheckBox allChBox;

    public Label tickNumberLbl;
    public Label memUsageLbl;
    public Label trafficLbl;

    public Button pausePlayBtn;
    public Button nextTickBtn;

    private World world;
    private Details details;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        tickSpn.tooltipProperty().set(new Tooltip("Interval (1 - 500 sec) where link between selected routers is down.\n" +
//                "0 - Generates random interval between 1 - 500 sec\n" +
//                "Enter integer values only."));
//        tickSpn.setEditable(true);
//        tickSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500));

        allChBox.setSelected(true);
        setAll(null);

        dataDestChBox.setTooltip(new Tooltip("Id of router where data are being saved."));

        visualCnv.setHeight(600);
        visualCnv.setWidth(600);

        world = new World(visualCnv.getGraphicsContext2D(), tickNumberLbl, memUsageLbl, trafficLbl);

        //world.start();
    }

    public void handleBtnLoadData(ActionEvent actionEvent) throws IOException {
        world.createWeb();
        world.logData(logTxt, /*tickNumChBox.isSelected(), */sDataChBox.isSelected(), dataPathChBox.isSelected(),
                dataDestChBox.isSelected(), memUsageChBox.isSelected(), trafficChBox.isSelected());

        if (world.getLog() == null) {
            System.out.println("App will be closed automatically.");
            Platform.exit();
        }

//        Dijkstra.computePath(world.getRouters().get(3));
//        List<Router> path = Dijkstra.getShortestPathTo(world.getRouters().get(8));
//
//        System.out.println(path);
//
//        world.getRouters().values().forEach(router -> {router.setPrevious(null); router.setMinDistance(Double.POSITIVE_INFINITY);});
//
//        Dijkstra.computePath(world.getRouters().get(0));
//        path = Dijkstra.getShortestPathTo(world.getRouters().get(7));
//
//        System.out.println(path);

        loadDataBtn.setDisable(true);
        playBtn.setDisable(false);
        detailsBtn.setDisable(false);
        pausePlayBtn.setDisable(false);
        nextTickBtn.setDisable(false);
    }

    public void handlePauseStartBtn(MouseEvent actionEvent) throws IOException, InterruptedException {
        if (world.isRunning()) {
            pausePlayBtn.setText("►");
            world.pause();
            return;
        }
        pausePlayBtn.setText("| |");

        if (world.getWeb() == null) {
            world.createWeb();
            world.logData(logTxt, /*tickNumChBox.isSelected(),*/ sDataChBox.isSelected(), dataPathChBox.isSelected(),
                    dataDestChBox.isSelected(), memUsageChBox.isSelected(), trafficChBox.isSelected());

            if (world.getLog() == null) {
                System.out.println("App will be closed automatically.");
                Platform.exit();
            }
        }
        world.start();

//        powerCutBtn.setDisable(false);
//        rndPowerCutBtn.setDisable(false);
    }

    public void handleDetailsBtn(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();

        stage.setTitle("Details");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/details.fxml"));

        // dependence injection
        loader.setControllerFactory(param -> {
            try {
                return param.getConstructor(Map.class, Map.class).newInstance(world.getDataManager().getRouters(),
                        world.getDataManager().getLinks());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        });
        Parent parent = loader.load();
        Scene scene = new Scene(parent, stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        stage.show();
    }

    public void setAll(MouseEvent mouseEvent) {
        boolean selected = allChBox.isSelected();

//        tickNumChBox.setSelected(selected);
        sDataChBox.setSelected(selected);
        dataPathChBox.setSelected(selected);
        dataDestChBox.setSelected(selected);
        memUsageChBox.setSelected(selected);
        trafficChBox.setSelected(selected);
        allChBox.setSelected(selected);
    }

    public void handleNextTickBtn(MouseEvent mouseEvent) throws IOException, InterruptedException {
        if (world.getWeb() == null) {
            world.createWeb();
            world.logData(logTxt, /*tickNumChBox.isSelected(),*/ sDataChBox.isSelected(), dataPathChBox.isSelected(),
                    dataDestChBox.isSelected(), memUsageChBox.isSelected(), trafficChBox.isSelected());

            if (world.getLog() == null) {
                System.out.println("App will be closed automatically.");
                Platform.exit();
            }

//            powerCutBtn.setDisable(false);
//            rndPowerCutBtn.setDisable(false);
        }

        if (!world.isRunning()) {
            world.setRunning();
            world.update();
            world.draw();
            world.pause();
        }
    }
}
