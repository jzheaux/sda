package biz.keyinsights.regression.model;

import java.util.ArrayList;
import java.util.List;

import matrix.DoubleMatrix;

public class DoubleMatrixBuilder {
	private List<List<Double>> data = new ArrayList<>();
	
	public DoubleMatrixBuilder addColumns(Integer whichRow, Double... toAdd) {
		while ( data.size() <= whichRow ) {
			data.add(new ArrayList<Double>());
		}
		for ( Double t : toAdd ) {
			data.get(whichRow).add(t);
		}
		return this;
	}
	
	public DoubleMatrix toMatrix() {
		if ( data.size() == 0 || data.get(0).size() == 0 ) {
			throw new IllegalArgumentException("Must add data before calling the toMatrix method.");
		}
		double[][] db = new double[data.size()][data.get(0).size()];
		int row = 0;
		for ( List<Double> rData : data ) {
			for ( int col = 0; col < rData.size(); col++ ) {
				db[row][col] = rData.get(col);
			}
			row++;
		}
		return new DoubleMatrix(db);
	}
}
