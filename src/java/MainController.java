import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Button loadDataBtn;
    public Button playBtn;
    public Button powerCutBtn;
    public Button detailsBtn;
    public ComboBox router1Cmb;
    public ComboBox router2Cmb;
    public Button rndPowerCutBtn;
    public Spinner<Integer> tickSpn;
    public Canvas visualCnv;
    public TextArea logTxt;
    public VBox leftVBox;
    public ProgressBar progressBar;
    public CheckBox tickNumChBox;
    public CheckBox sDataChBox;
    public CheckBox dataPathChBox;
    public CheckBox dataDestChBox;
    public CheckBox memUsageChBox;
    public CheckBox trafficChBox;
    public CheckBox allChBox;
    public Label tickNumberLbl;

    private World world;
    private Details details;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tickSpn.tooltipProperty().set(new Tooltip("Interval (1 - 500 sec) where link between selected routers is down.\n" +
                "0 - Generates random interval between 1 - 500 sec\n" +
                "Enter integer values only."));
        tickSpn.setEditable(true);
        tickSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500));

        allChBox.setSelected(true);
        setAll(null);

        dataDestChBox.setTooltip(new Tooltip("Id of router where data are being saved."));

        visualCnv.setHeight(600);
        visualCnv.setWidth(600);

        world = new World(visualCnv.getGraphicsContext2D());

        //world.start();
    }

    public void handleBtnLoadData(ActionEvent actionEvent) throws IOException {


        world.createWeb();
        world.logData(logTxt, tickNumChBox.isSelected(), sDataChBox.isSelected(), dataPathChBox.isSelected(),
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

        playBtn.setDisable(false);
        loadDataBtn.setDisable(true);
        detailsBtn.setDisable(false);
    }

    public void handlePauseStartBtn(MouseEvent actionEvent) throws IOException, InterruptedException {
        if (world.isRunning())
            world.pause();

        if (world.getWeb() == null) {
            world.createWeb();
            world.logData(logTxt, tickNumChBox.isSelected(), sDataChBox.isSelected(), dataPathChBox.isSelected(),
                    dataDestChBox.isSelected(), memUsageChBox.isSelected(), trafficChBox.isSelected());

            if (world.getLog() == null) {
                System.out.println("App will be closed automatically.");
                Platform.exit();
            }
        }
        world.start();

        powerCutBtn.setDisable(false);
        rndPowerCutBtn.setDisable(false);
    }

    public void handleDetailsBtn(ActionEvent actionEvent) {
        details = new Details();
        details.setWorld(world);
        details.setRouters(world.getRouters());
        details.setLinks(world.getLinks());
        details.show();
    }

    public void setAll(MouseEvent mouseEvent) {
        boolean selected = allChBox.isSelected();

        tickNumChBox.setSelected(selected);
        sDataChBox.setSelected(selected);
        dataPathChBox.setSelected(selected);
        dataDestChBox.setSelected(selected);
        memUsageChBox.setSelected(selected);
        trafficChBox.setSelected(selected);
        allChBox.setSelected(selected);
    }

    public void setLogConf(MouseEvent mouseEvent) {

    }
}
