package by.bsu.kolodyuk;


import by.bsu.kolodyuk.model.Example;
import by.bsu.kolodyuk.model.Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable {

    @FXML
    private FlowPane propertiesPane;
    @FXML
    private AnchorPane answersPane;
    @FXML
    private ListView resultListView;
    private ObservableList<String> resultData;
    private List<ToggleButton> toggleButtons;

    private List<Type> types;
    private List<String> properties;

    private double[] x;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        learn();
        toggleButtons = new ArrayList<>();
        properties.forEach(p -> createToggleButton(p));
        resultData = FXCollections.observableArrayList();
        resultListView.setItems(resultData);
    }

    @FXML
    public void onRecognizeButtonPressed() {
        recognize();
    }

    @FXML
    public void onTryAgainButtonPressed() {
        toggleButtons.stream().forEach(b -> b.setSelected(false));
        Arrays.fill(x, 0.0);
        resultData.clear();
    }

    public void learn() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            properties = objectMapper.readValue(Main.class.getClassLoader().getResourceAsStream("properties.json"), List.class);
            types = Arrays.asList(objectMapper.readValue(Main.class.getClassLoader().getResourceAsStream("examples.json"), Type[].class));

            for(Type type : types) {
                double[] b = new double[properties.size()];
                Arrays.fill(b, 0.0);
                for(Example example : type.getExamples()) {
                    for(String property : example.getProperties()) {
                        int index = properties.indexOf(property);
                        b[index] += 1.0 / type.getExamples().size();
                    }
                }
                type.setMasses(b);
            }

            double[] b = new double[properties.size()];
            for(int i = 0; i < b.length; i++) {
                double sum = 0;
                for(int j = 0; j < types.size(); j++) {
                    sum += types.get(j).getMasses()[i];
                }
                b[i] = sum;
            }

            for(Type type : types) {
                double[] a = new double[properties.size()];
                Arrays.fill(a, 0.0);
                for(int i = 0; i < a.length; i++) {
                    a[i] = Math.abs(type.getMasses()[i] - b[i]);
                }
                type.setDeltas(a);
                System.out.println(Arrays.toString(a));
            }

            x = new double[properties.size()];

        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void recognize() {
        resultData.clear();

        double[] result = new double[types.size()];
        String[] records = new String[types.size()];

        for(int i = 0; i < types.size(); i++) {
            List<Double> sums = new ArrayList<>();
            for(Example example : types.get(i).getExamples()) {
                double sum = 0;
                for(int j = 0; j < properties.size(); j++) {
                    String property = properties.get(j);
                    double y = example.getProperties().contains(property) ? 1 : 0;
                    if(x[j] == y) {
                        sum += types.get(i).getDeltas()[j];
                    } else {
                        sum -= types.get(i).getDeltas()[j];
                    }
                }
                sum /= Arrays.stream(types.get(i).getDeltas()).sum();
                sum = Math.max(0, sum);
                sums.add(sum);
            }

            result[i] = Collections.max(sums);
            records[i] = types.get(i).getExamples().get(sums.indexOf(result[i])).getName();
            resultData.add(types.get(i).getName() + " > " + records[i] + " > " + result[i]);
        }


        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.toString(records));

        double max = Arrays.stream(result).max().getAsDouble();
        for(int i = 0; i < result.length; i++) {
            if(result[i] == max) {
                System.out.println(types.get(i).getName());
                resultListView.getSelectionModel().select(i);
                resultListView.requestFocus();
            }
        }
    }

    public ToggleButton createToggleButton(String property) {
        final ToggleButton button = new ToggleButton(property);
        propertiesPane.getChildren().add(button);
        int index = toggleButtons.size();
        toggleButtons.add(button);
        button.setOnAction(e -> {
             x[index] = button.isSelected() ? 1 : 0;
        });

        return button;
    }

}
