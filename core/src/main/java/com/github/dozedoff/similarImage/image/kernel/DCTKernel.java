/*  Copyright (C) 2017  Nicholas Wright
    
    This file is part of similarImage - A similar image finder using pHash
    
    similarImage is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dozedoff.similarImage.image.kernel;

import com.google.common.primitives.Doubles;

public class DCTKernel {
	public static final int DEFAULT_MATRIX_SIZE = 8;
	private final int N; // matrix size
	private final int matrixArea;
	private final double[] dctCoefficients;

	/**
	 * Create a new DCT kernel for a 8x8 matrix;
	 */
	public DCTKernel() {
		this(DEFAULT_MATRIX_SIZE);
	}

	/**
	 * Create a new DCT kernel for the given size.
	 * 
	 * @param matrixSize
	 *            size of the matrix that this kernel will opoerate on
	 */
	public DCTKernel(int matrixSize) {
		this.N = matrixSize;
		this.matrixArea = N*N;
		dctCoefficients = new double[N];
		initCoefficients();
	}

	private void initCoefficients() {
		for (int i = 1; i < N; i++) {
			dctCoefficients[i] = 1;
		}

		dctCoefficients[0] = 1 / Math.sqrt(2.0);
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 * 
	 * @see DCT function from http://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java
	 */
	public double[] transformDCT(double[] matrix) {
		double[] F = new double[matrixArea];

		for (int u = 0; u < N; u++) {
			for (int v = 0; v < N; v++) {
				double sum = 0.0;
				for (int g = 0; g < matrixArea; g++) {
					sum += Math.cos(((2 * (g / N) + 1) / (2.0 * N)) * u * Math.PI)
							* Math.cos(((2 * (g % N) + 1) / (2.0 * N)) * v * Math.PI) * (matrix[g]);
				}
				
				sum *= ((dctCoefficients[u] * dctCoefficients[v]) / 4.0);
				F[u*N + v] = sum;
			}
		}

		return Doubles.concat(F);
	}
}
