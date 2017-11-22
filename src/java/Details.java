import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Panel pro zjisteni udaju o routerech a spojenich
 */

public class Details extends Stage{

    private RadioButton routerRBtn;
    private RadioButton linkRBtn;
    private TextArea detailsArea;
    public ListView<String> list;
    private int sceneWidth = 600;
    private int sceneHeight = 400;

    private World world;
    private Map<Integer, Router> routers = new HashMap<>();
    private Map<RouterPair, Link> links = new HashMap<>();


    public Details() {
        this.setTitle("Details");
        this.setScene(createScene());
    }

    private Scene createScene() {
        Scene scene = new Scene(createObjects(), sceneWidth, sceneHeight);
        return scene;
    }

    private Parent createObjects() {
        BorderPane panel = new BorderPane();
        panel.setTop(createTop());
        panel.setLeft(createLeft());
        panel.setCenter(createCenter());

        return panel;
    }

    /**
     * Create right side of the window (textarea)
     * @return gridpane with textarea etc.
     */
    private Node createCenter() {
        GridPane pane = new GridPane();

        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        detailsArea = new TextArea();
        detailsArea.setPrefHeight(sceneHeight - 20);
        detailsArea.setPrefWidth(sceneWidth - 50);
        detailsArea.setEditable(false);

        pane.add(detailsArea, 1, 0);
        return pane;
    }

    /**
     * Create left side of the window
     * @return gridpane list
     */
    private Node createLeft() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.setPadding(new Insets(10, 10, 10 , 10));

        list = createList();

        pane.add(list, 0, 0);

        return pane;
    }

    /**
     * Create top side of the window
     * @return gridpane with rbuttons
     */
    private Node createTop() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.setPadding(new Insets(10, 10, 10 , 10));

        routerRBtn = new RadioButton("Routers");
        linkRBtn = new RadioButton("Links");

        ToggleGroup group = new ToggleGroup();
        routerRBtn.setToggleGroup(group);
        linkRBtn.setToggleGroup(group);

        // TODO upravit pro hashmapu
//                group.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
//        {
//            @Override
//            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1)
//            {
//
//                RadioButton rBtn = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
//                ObservableList<String> observableList = FXCollections.observableArrayList();
//                list.setItems(observableList);
//                if (rBtn.getText() == "Routers") for (int i = 0; i < routers.length; i++) observableList.add(routers[i].getName());
//                else if (rBtn.getText() == "Links") for (int i = 0; i < links.length; i++) {
//                    observableList.add(links[i].getR1Id() + " ~ " + links[i].getR2Id());
//                }
//                list.setItems(observableList);
//            }
//        });

        pane.add(routerRBtn, 0, 0);
        pane.add(linkRBtn, 1, 0);

        return pane;
    }

    /**
     * Create simple list
     * @return list
     */
    private ListView<String> createList() {
        ListView<String> list = new ListView<>();
        ObservableList<String> observableList = FXCollections.observableArrayList();
        list.setItems(observableList);
        list.setPrefHeight(sceneHeight - 20);
        list.setPrefWidth(100);
//
        return list;
    }

    public void setRouters(Map<Integer, Router> routers) {
        this.routers = routers;
    }

    public void setLinks(Map<RouterPair, Link> links) {
        this.links = links;
    }

    public void setWorld(World world) {
        this.world = world;
    }


}
