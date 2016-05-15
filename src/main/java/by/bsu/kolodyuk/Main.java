package by.bsu.kolodyuk;

import by.bsu.kolodyuk.model.Example;
import by.bsu.kolodyuk.model.Type;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> properties = objectMapper.readValue(Main.class.getClassLoader().getResourceAsStream("properties.json"), List.class);
        List<Type> types = Arrays.asList(objectMapper.readValue(Main.class.getClassLoader().getResourceAsStream("examples.json"), Type[].class));

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


        // Recognition
        double[] x = new double[properties.size()];
        Arrays.fill(x, 0.0);
        x[5] = 1;
        x[11] = 1;
        x[12] = 1;

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

}
