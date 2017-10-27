/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.filter.spatial;

import org.geotools.filter.GeometryFilterImpl;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;

/**
 * A base class for GeometryFilters that will use PreparedGeometries when the Expression
 * is a Literal Expression. 
 * 
 * <p>
 * This class determines when the expressions are set which expressions are literals.  The protected
 * field {@link #literals} contains the {@link Literals} enumerated value that indicates if:
 * </p>
 * <ul>
 * <li><strong>NEITHER</strong> ({@link Literals#NEITHER) of the expressions are literal and is a JTS Geometry (also non-null)</li>
 * <li><strong>BOTH</strong> ({@link Literals#BOTH}) expressions are literals and is a JTS Geometry (also non-null)</li>
 * <li>the <strong>LEFT</strong> ({@link Literals#LEFT}) expression (Expression1) is a literal and is a JTS Geometry (also non-null)</li>
 * <li>or the <strong>RIGHT</strong> ({@link Literals#RIGHT}) expression (Expression2) is a literal and is a JTS Geometry (also non-null)</li>
 * </ul>
 * <p>
 * If <strong>BOTH</strong> of the expressions are literals then a cached value is generated by calling {@link #basicEvaluate(Geometry, Geometry)}.   
 * </p>
 * <p>
 * The method {@link #basicEvaluate(Geometry, Geometry)} is required to be implemented so that a cached value can be generated in the case
 * that both expressions are literals
 * </p>
 * <hr/><br>
 * Example usage for intersects filter:
 * <pre><code>
    public boolean evaluate(Object feature) {
        if (feature instanceof SimpleFeature
                && !validate((SimpleFeature) feature)) {
            // we could not obtain a geometry for both left and right hand sides
            // so default to false
            return false;
        }
        Geometry left;
        Geometry right;

        switch (literals) {
        case BOTH:
            return cacheValue;
        case RIGHT: {
            return rightPreppedGeom.intersects(getLeftGeometry(feature));
        }
        case LEFT: {
            return leftPreppedGeom.intersects(getRightGeometry(feature));
        }
        default: {
            left = getLeftGeometry(feature);
            right = getRightGeometry(feature);
            return basicEvaluate(left, right);
        }
        }
    }

    protected final boolean basicEvaluate(Geometry left, Geometry right) {
        Envelope envLeft = left.getEnvelopeInternal();
        Envelope envRight = right.getEnvelopeInternal();
        return envRight.intersects(envLeft) && left.intersects(right);
    }
    </code></pre>
 * </p>
 * 
 * @author jesse
 *
 *
 *
 * @source $URL$
 */
public abstract class AbstractPreparedGeometryFilter extends GeometryFilterImpl {

    /**
     * Constant that identifies which expressions are Literal and JTS Geometries
     * 
     * @author jesse
     */
    protected enum Literals {
        /**
         * Neither Expression are {@link Literal}s
         */
        NEITHER,
        /**
         * Expression2 the "right" geometry is a {@link Literal}
         */
        RIGHT, 
        /**
         * Expression1 the "left" geometry is a {@link Literal}
         */
        LEFT,
        /**
         * Both expressions are {@link Literal} expressions
         */
        BOTH;

        private static Literals calculate(Expression expression1,
                Expression expression2) {
            boolean left = expression1 instanceof Literal && ((Literal)expression1).getValue() instanceof Geometry;
            boolean right = expression2 instanceof Literal && ((Literal)expression2).getValue() instanceof Geometry;
            if (left && right) {
                return BOTH;
            }
            if (left) {
                return LEFT;
            }
            if (right) {
                return RIGHT;
            }
            return NEITHER;
        }
    }

    private final PreparedGeometryFactory pGeomFac;

    /**
     * Indicates which expressions are {@link Literal}s 
     */
    protected Literals literals;
    /**
     * The PreparedGeometry for the left Geometry.  Null if the left geometry is not a 
     * {@link Literal}
     */
    protected PreparedGeometry leftPreppedGeom;
    /**
     * The PreparedGeometry for the right Geometry.  Null if the right geometry is not a 
     * {@link Literal}
     */
    protected PreparedGeometry rightPreppedGeom;
    /**
     * If both expressions are literals the value will never change.  In that
     * case this field is that calculated value.  It is false otherwise.
     */
    protected boolean cacheValue;

    protected AbstractPreparedGeometryFilter(Expression e1, Expression e2) {
        super(e1, e2);
        pGeomFac = new PreparedGeometryFactory();
        if (e1 != null)
            setExpression1(e1);
        if (e2 != null)
            setExpression2(e2);

    }

    protected AbstractPreparedGeometryFilter(Expression e1, Expression e2, MatchAction matchAction) {
        super(e1, e2, matchAction);
        pGeomFac = new PreparedGeometryFactory();
        if (e1 != null)
            setExpression1(e1);
        if (e2 != null)
            setExpression2(e2);
    }

	private void prepare() {
		if( expression1==null || expression2==null ){
			// filter not yet fully configured so wait
			return;
		}
		literals = Literals.calculate(expression1, expression2);
        switch (literals) {
        case BOTH: {
            Geometry left = (Geometry) ((Literal) expression1).getValue();
            Geometry right = (Geometry) ((Literal) expression2).getValue();
            cacheValue = basicEvaluate(left, right);
            leftPreppedGeom = rightPreppedGeom = null;
            break;
        }
        case LEFT: {
            Geometry left = (Geometry) ((Literal) expression1).getValue();
            leftPreppedGeom = pGeomFac.create(left);
            rightPreppedGeom = null;
            cacheValue = false;
            break;
        }
        case RIGHT: {
            Geometry right = (Geometry) ((Literal) expression2).getValue();
            rightPreppedGeom = pGeomFac.create(right);
            leftPreppedGeom = null;
            cacheValue = false;
            break;
        }
        default: {
            leftPreppedGeom = rightPreppedGeom = null;
            cacheValue = false;
        }
        }
	}

    @Override
    public void setExpression1(Expression expression) {
    	super.setExpression1(expression);
    	prepare();
    }
    
    @Override
    public void setExpression2(Expression expression) {
    	super.setExpression2(expression);
    	prepare();

    }
    
    /**
     * Performs the calculation on the two geometries.  This is used to calculate the cached value
     * in the case that both geometries are Literals.  But in practice it is useful to extract this functionality
     * into its own method.
     * 
     * @param left the geometry on the left of the equations (the geometry obtained from evaluating Expression1)
     * @param right the geometry on the right of the equations (the geometry obtained from evaluating Expression2)
     * @return true if the filter evaluates to true for the two geometries
     */
    protected abstract boolean basicEvaluate(Geometry left, Geometry right);
}
