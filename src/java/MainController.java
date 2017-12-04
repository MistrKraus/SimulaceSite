import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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

    private World world;
    private Details details;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tickSpn.tooltipProperty().set(new Tooltip("Interval (1 - 500 sec) where link between selected routers is down.\n" +
                "0 - Generates random interval between 1 - 500 sec\n" +
                "Enter integer values only."));
        tickSpn.setEditable(true);
        tickSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500));

        visualCnv.setHeight(200);
        visualCnv.setWidth(200);

        world = new World(visualCnv.getGraphicsContext2D(), logTxt);

        //world.start();
    }

    public void handleBtnLoadData(ActionEvent actionEvent) {
        world.processInput();

        Dijkstra dijkstra = new Dijkstra();

        dijkstra.computePath(world.getRouters().get(0));
        List<Router> path = dijkstra.getShortestPathTo(world.getRouters().get(6));

        System.out.println(path);

        playBtn.setDisable(false);
        loadDataBtn.setDisable(true);
        detailsBtn.setDisable(false);
    }

    public void handleBtnPlay(ActionEvent actionEvent) throws IOException {
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

}
