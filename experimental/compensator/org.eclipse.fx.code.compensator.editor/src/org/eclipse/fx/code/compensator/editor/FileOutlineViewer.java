/*******************************************************************************
 * Copyright (c) 2022 BestSolution.at and others.
 *  
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *     BestSolution.at - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.code.compensator.editor;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.code.compensator.editor.Outline.OutlineItem;
import org.eclipse.fx.core.URI;
import org.eclipse.fx.ui.controls.tree.SimpleTreeCell;
import org.eclipse.fx.ui.services.resources.GraphicsLoader;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class FileOutlineViewer {
	private final GraphicsLoader nodeProvider;
	private Map<Outline, TreeView<OutlineItem>> treeCache = new WeakHashMap<>();

	private final BorderPane container;

	@Inject
	public FileOutlineViewer(BorderPane container, GraphicsLoader nodeProvider) {
		this.container = container;
		this.nodeProvider = nodeProvider;
	}

	@PostConstruct
	public void initUI() {
		HBox b = new HBox();
		b.getStyleClass().add("tool-bar");
		b.getChildren().add(new Label("Outline", nodeProvider.getGraphicsNode(URI.createPlatformPluginURI("org.eclipse.fx.code.compensator.editor","css/icons/16/outline_co.png"))));
		container.setTop(b);
	}

	private TreeView<OutlineItem> createView() {
		TreeView<OutlineItem> outlineView = new TreeView<>();
		outlineView.setShowRoot(false);
		outlineView.setCellFactory(this::createCell);
		return outlineView;
	}

	TreeCell<OutlineItem> createCell(TreeView<OutlineItem> param) {
		return new SimpleTreeCell<OutlineItem>(i -> i.getLabel(), i -> i.getGraphic(), i -> Collections.emptyList());
	}

	@Inject
	void updateInput(@Optional @Named("activeOutline") Outline outline) {
		if( outline != null ) {
			TreeView<OutlineItem> view = treeCache.get(outline);

			if( view == null ) {
				view = createView();
				TreeItem<OutlineItem> root = new TreeItem<>();
				for( OutlineItem l : outline.getRootItems() ) {
					root.getChildren().add(createRec(l));
				}
				
				outline.getRootItems().addListener( (Change<? extends OutlineItem> c) -> {
					while( c.next() ) {
						if( c.wasAdded() ) {
							root.getChildren().addAll(
									c.getAddedSubList().stream()
										.map(this::createRec)
										.collect(Collectors.toList()));							
						}
						
						if( c.wasRemoved() ) {
							root.getChildren().removeIf( t -> c.getRemoved().contains(t.getValue()));	
						}
					}
				});
				view.setRoot(root);
				treeCache.put(outline, view);
			}

			container.setCenter(view);
		} else {
			container.setCenter(null);
		}
	}

	TreeItem<OutlineItem> createRec(OutlineItem i) {
		TreeItem<OutlineItem> l = new TreeItem<Outline.OutlineItem>(i);
		for( OutlineItem c : i.getChildren() ) {
			l.getChildren().add(createRec(c));
		}

		return l;
	}
}
