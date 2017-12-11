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

    /**Button pro nacteni dat*/
    public Button loadDataBtn;
    /**Button pro spusteni simulace*/
    public Button playBtn;
    /**Button otvirajici okno s detailnimi informacemi o siti*/
    public Button detailsBtn;
    /**Button pozastavujici a spoustejici simulaci*/
    public Button pausePlayBtn;
    /**Button pro provedeni jednoho kroku simelace*/
    public Button nextTickBtn;

    /**Canvas pro graficky vystup*/
    public Canvas visualCnv;

    /**TextArea pro vypis logu*/
    public TextArea logTxt;

    /**CheckBox zda se maji zapisovat odeslana data*/
    public CheckBox sDataChBox;
    /**CheckBox zda se maji zapisovat cesty odeslanych dat*/
    public CheckBox dataPathChBox;
    /**CheckBox zda se ma zapisovat id routeru, kam se data ulozila*/
    public CheckBox dataDestChBox;
    /**CheckBox zda se ma zapisovat vyuziti pameti routeru*/
    public CheckBox memUsageChBox;
    /**CheckBox zda se ma zapisovat vytizenost site*/
    public CheckBox trafficChBox;
    /**CheckBox pro zaskrnuti / odskrtnuti ostatnich CheckBoxu*/
    public CheckBox allChBox;

    /**Label zobrazujici cislo ticku*/
    public Label tickNumberLbl;
    /**Label zobrazujici vyuziti pameti routeru*/
    public Label memUsageLbl;
    /**Label zobrazujici vytizenost site*/
    public Label trafficLbl;

    /**Ridici trida*/
    private World world;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        allChBox.setSelected(true);
        setAll(null);

        dataDestChBox.setTooltip(new Tooltip("Id of router where data are being saved."));

        visualCnv.setHeight(600);
        visualCnv.setWidth(600);

        world = new World(visualCnv.getGraphicsContext2D(), tickNumberLbl, memUsageLbl, trafficLbl);

        //world.start();
    }

    /**
     * Nacte graf site
     *
     * @param actionEvent
     * @throws IOException chyba pri nacitani souboru
     */
    public void handleBtnLoadData(ActionEvent actionEvent) throws IOException {
        world.createWeb();
        world.logData(logTxt, /*tickNumChBox.isSelected(), */sDataChBox.isSelected(), dataPathChBox.isSelected(),
                dataDestChBox.isSelected(), memUsageChBox.isSelected(), trafficChBox.isSelected());

        if (world.getLog() == null) {
            System.out.println("App will be closed automatically.");
            Platform.exit();
        }

        loadDataBtn.setDisable(true);
        playBtn.setDisable(false);
        detailsBtn.setDisable(false);
        pausePlayBtn.setDisable(false);
        nextTickBtn.setDisable(false);
    }

    /**
     * Spusti / Pozastavi simulaci site
     *
     * @param actionEvent
     * @throws IOException
     * @throws InterruptedException
     */
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
    }

    /**
     * Otevre okno s deaily o siti
     *
     * @param actionEvent
     * @throws IOException
     */
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

    /**
     * Zaskrne / Odskrtne vsechny CheckBoxy
     *
     * @param mouseEvent
     */
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

    /**
     * Provede jeden tick simulace site
     *
     * @param mouseEvent
     * @throws IOException
     * @throws InterruptedException
     */
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
