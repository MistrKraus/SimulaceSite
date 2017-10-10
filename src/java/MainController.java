import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Button loadDataBtn;
    public Button playBtn;
    public Button powerCutBtn;
    public ComboBox router1Cmb;
    public ComboBox router2Cmb;
    public Button rndPowerCutBtn;
    public Spinner<Integer> tickSpn;
    public Canvas visualCnv;
    public TextArea logTxt;
    public VBox leftVBox;
    public ProgressBar progressBar;

    private World world;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tickSpn.tooltipProperty().set(new Tooltip("Interval (1 - 500 sec) where link between selected routers is down.\n" +
                "0 - Generates random interval between 1 - 500 sec\n" +
                "Enter integer values only."));
        tickSpn.setEditable(true);
        tickSpn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500));

        world = new World(visualCnv.getGraphicsContext2D());

        //world.start();
    }

    public void handleBtnLoadData(ActionEvent actionEvent) {

    }
}
