package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    public final String COMMA = ",";
    public final String SPACE = " ";

    public String readMatrixString(String path) {
        double[][] matrix = readMatrix(path);
        String str = "";
        for (int k = 0; k < matrix.length; k++) {
            for (int l = 0; l < matrix[0].length; l++) {
                str = str + matrix[k][l] + ",";
            }
            str = str + "\n";
        }
        return str;
    }

    public double[][] readMatrix(String path) {
        return readMatrix(path, COMMA);
    }

    public double[][] readMatrix(String path, String splitString) {
        BufferedReader br;
        double[][] matrix;
        List<String> list = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            while (line != null) {
                list.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        matrix = new double[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            String str[] = list.get(i).split(splitString);
            double array[] = new double[str.length];
            for (int j = 0; j < str.length; j++) {
                array[j] = Double.valueOf(str[j]);
            }
            matrix[i] = array;
        }
        return matrix;
    }
}
