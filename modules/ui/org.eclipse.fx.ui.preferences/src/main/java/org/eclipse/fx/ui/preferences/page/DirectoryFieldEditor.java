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

import java.io.File;

import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 * <p>
 * A Field editor for directories.
 * </p>
 */
public class DirectoryFieldEditor extends StringFieldEditor {

	/**
	 * CSS Style Class for the Container HBox of this {@link DirectoryFieldEditor}
	 */
	public static final String DIRECTORY_EDITOR_STYLE = "directory-field-container";
	
	public DirectoryFieldEditor(String name, String label) {
		super(name, label);
		Button browse = new Button("...");
		getTextContainer().getChildren().add(browse);
		getTextContainer().getStyleClass().add(DIRECTORY_EDITOR_STYLE);
		browse.setOnAction(event -> browse(browse.getScene().getWindow()));
	}

	private void browse(Window parent) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(getCurrentFolder());
		File result = chooser.showDialog(parent);
		if (result != null) {
			setCurrentFolder(result);
		}
	}

	private void setCurrentFolder(File result) {
		getTextField().setText(result.getPath());
	}

	private File getCurrentFolder() {
		String textValue = getTextField().getText();
		File folder = new File(textValue);
		// Find the first existing folder in the current path hierarchy
		while (folder != null && !folder.isDirectory()) {
			folder = folder.getParentFile();
		}
		return folder;
	}

	public DirectoryFieldEditor(String name) {
		this(name, null);
	}

}
