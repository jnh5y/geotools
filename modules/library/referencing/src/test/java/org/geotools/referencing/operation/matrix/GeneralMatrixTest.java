/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

/**
 * Tests {@link org.geotools.referencing.operation.matrix.GeneralMatrix}.
 *
 *
 *
 * @source $URL$
 * @version $Id$
 * @author James Hughes
 */
package org.geotools.referencing.operation.matrix;

import org.junit.*;
import static org.junit.Assert.*;

public class GeneralMatrixTest {

    private static double[][] zero2 = new double[][] {
            {0.0, 0.0},
            {0.0, 0.0}
    };

    private static double[][] id2 = new double[][] {
            {1.0, 0.0},
            {0.0, 1.0}
    };

    private static double[][] id4 = new double[][]{
            {1.0, 0.0, 0.0, 0.0},
            {0.0, 1.0, 0.0, 0.0},
            {0.0, 0.0, 1.0, 0.0},
            {0.0, 0.0, 0.0, 1.0}
    };

    private static double[][] id23 = new double[][] {
            {1.0, 0.0, 0.0},
            {0.0, 1.0, 0.0}
    };

    private static double[][] id32 = new double[][] {
            {1.0, 0.0},
            {0.0, 1.0},
            {0.0, 0.0}
    };

    private static double[][] array1 = new double[][] {
            {1.2, 3.4},
            {5.6, 7.8},
            {9.0, 1.0}
    };

    private static double[] array1flatten = new double[] {
            1.2, 3.4, 5.6, 7.8, 9.0, 1.0
    };

    @Test
    public void constructorTests() {
        GeneralMatrix squareId2 = new GeneralMatrix(2);
        double[][] array2 = squareId2.getElements();
        assertArrayEquals(array2, id2);


        GeneralMatrix squareId = new GeneralMatrix(4);
        double[][] array = squareId.getElements();
        assertArrayEquals(array, id4);

        GeneralMatrix squareId23 = new GeneralMatrix(2, 3);
        double[][] array23 = squareId23.getElements();
        assertArrayEquals(array23, id23);


        GeneralMatrix squareId32 = new GeneralMatrix(3, 2);
        double[][] array32 = squareId32.getElements();
        assertArrayEquals(array32, id32);

        GeneralMatrix matrix1 = new GeneralMatrix(3, 2, array1flatten);
        double[][] matrix1array = matrix1.getElements();
        assertArrayEquals(array1, matrix1array);

        GeneralMatrix matrix2 = new GeneralMatrix(array1);
        double [][] matrix2array = matrix2.getElements();
        assertArrayEquals(array1, matrix2array);

        Matrix2 matrix2zero = new Matrix2(0, 0, 0, 0);
        GeneralMatrix gm2zero = new GeneralMatrix(matrix2zero);
        assertArrayEquals(gm2zero.getElements(), zero2);

        matrix2zero.setIdentity();
        GeneralMatrix gm2id = new GeneralMatrix(matrix2zero);
        assertArrayEquals(gm2id.getElements(), id2);



    }


}
