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


    private static double[][] id4 = new double[][]{
            {1.0, 0.0, 0.0, 0.0},
            {0.0, 1.0, 0.0, 0.0},
            {0.0, 0.0, 1.0, 0.0},
            {0.0, 0.0, 0.0, 1.0}
    };

    @Test
    public void constructorTests() {
      GeneralMatrix squareId = new GeneralMatrix(4);
      double[][] array = squareId.getElements();
      assertArrayEquals(array, id4);


    }


}
