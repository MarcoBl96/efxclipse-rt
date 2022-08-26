/*******************************************************************************
 * Copyright (c) 2018 BestSolution.at, EclipseSource and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API
 *     Camille Letavernier <cletavernier@eclipsesource.com> - initial implementation
 *******************************************************************************/
package org.eclipse.fx.ui.preferences.page;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

/**
 * <p>
 * A Field editor for boolean preferences.
 * </p>
 */
public class BooleanFieldEditor extends FieldEditor<Boolean> {

	private final CheckBox checkBox;

	public BooleanFieldEditor(String name, String label) {
		super(name, label);
		this.checkBox = new CheckBox();
		getChildren().add(checkBox);
	}

	public BooleanFieldEditor(String name) {
		this(name, null);
	}

	@Override
	protected void doLoad() {
		this.checkBox.setSelected(getMemento().get(getName(), false));
	}

	@Override
	protected void doPersist() {
		getMemento().put(getName(), this.checkBox.isSelected());
	}

	@Override
	protected void doRestoreDefaults() {
		this.checkBox.setSelected(getMemento().getDefaultBoolean(getName()));
	}

	@Override
	public ObservableValue<Boolean> getValue() {
		return this.checkBox.selectedProperty();
	}
	
	protected final CheckBox getCheckBox() {
		return this.checkBox;
	}

}
