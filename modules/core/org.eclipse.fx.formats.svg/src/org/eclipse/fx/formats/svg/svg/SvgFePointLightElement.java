/*******************************************************************************
 * Copyright (c) 2012 BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.formats.svg.svg;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fe Point Light Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fx.formats.svg.svg.SvgFePointLightElement#getX <em>X</em>}</li>
 *   <li>{@link org.eclipse.fx.formats.svg.svg.SvgFePointLightElement#getY <em>Y</em>}</li>
 *   <li>{@link org.eclipse.fx.formats.svg.svg.SvgFePointLightElement#getZ <em>Z</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fx.formats.svg.svg.SvgPackage#getSvgFePointLightElement()
 * @model
 * @generated
 */
public interface SvgFePointLightElement extends SvgElement, CoreAttributes, LightSourceElement, ContentElement<SvgElement> {
	/**
	 * Returns the value of the '<em><b>X</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>X</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>X</em>' attribute.
	 * @see #setX(Double)
	 * @see org.eclipse.fx.formats.svg.svg.SvgPackage#getSvgFePointLightElement_X()
	 * @model dataType="org.eclipse.fx.formats.svg.svg.Number"
	 * @generated
	 */
	Double getX();

	/**
	 * Sets the value of the '{@link org.eclipse.fx.formats.svg.svg.SvgFePointLightElement#getX <em>X</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>X</em>' attribute.
	 * @see #getX()
	 * @generated
	 */
	void setX(Double value);

	/**
	 * Returns the value of the '<em><b>Y</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Y</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Y</em>' attribute.
	 * @see #setY(Double)
	 * @see org.eclipse.fx.formats.svg.svg.SvgPackage#getSvgFePointLightElement_Y()
	 * @model dataType="org.eclipse.fx.formats.svg.svg.Number"
	 * @generated
	 */
	Double getY();

	/**
	 * Sets the value of the '{@link org.eclipse.fx.formats.svg.svg.SvgFePointLightElement#getY <em>Y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Y</em>' attribute.
	 * @see #getY()
	 * @generated
	 */
	void setY(Double value);

	/**
	 * Returns the value of the '<em><b>Z</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Z</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Z</em>' attribute.
	 * @see #setZ(Double)
	 * @see org.eclipse.fx.formats.svg.svg.SvgPackage#getSvgFePointLightElement_Z()
	 * @model dataType="org.eclipse.fx.formats.svg.svg.Number"
	 * @generated
	 */
	Double getZ();

	/**
	 * Sets the value of the '{@link org.eclipse.fx.formats.svg.svg.SvgFePointLightElement#getZ <em>Z</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Z</em>' attribute.
	 * @see #getZ()
	 * @generated
	 */
	void setZ(Double value);

} // SvgFePointLightElement
