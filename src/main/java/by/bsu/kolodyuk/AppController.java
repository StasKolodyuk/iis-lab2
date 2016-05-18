package by.bsu.kolodyuk;


import by.bsu.kolodyuk.model.Example;
import by.bsu.kolodyuk.model.Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    private FlowPane propertiesPane;
    @FXML
    private AnchorPane answersPane;
    private List<ToggleButton> toggleButtons;

    private List<Type> types;
    private List<String> properties;

    private double[] x;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        learn();
        toggleButtons = new ArrayList<>();
        properties.forEach(p -> createToggleButton(p));

    }

    @FXML
    public void onRecognizeButtonPressed() {
        recognize();
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
                        b[index] += 1.0 / types.size();
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
                //System.out.println(Arrays.toString(a));
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void recognize() {
        double[] result = new double[types.size()];

        for(int i = 0; i < types.size(); i++) {
            double sum = 0;
            for(Example example : types.get(i).getExamples()) {
                for(int j = 0; j < properties.size(); j++) {
                    String property = properties.get(j);
                    double y = example.getProperties().contains(property) ? 1 : 0;
                    if(x[j] == y) {
                        sum += types.get(i).getDeltas()[j];
                    } else {
                        sum -= types.get(i).getDeltas()[j];
                    }
                }
            }
            result[i] = Math.max(0, sum);
        }

        double max = Arrays.stream(result).max().getAsDouble();
        System.out.println(Arrays.toString(result));
        for(int i = 0; i < result.length; i++) {
            if(result[i] == max) {
                System.out.println(types.get(i).getName());
            }
        }
    }

    public ToggleButton createToggleButton(String property) {
        final ToggleButton button = new ToggleButton(property);
        propertiesPane.getChildren().add(button);
        toggleButtons.add(button);
        int index = toggleButtons.size();
        button.setOnAction(e -> {
             x[index] = button.isSelected() ? 1 : 0;
        });

        return button;
    }

}
