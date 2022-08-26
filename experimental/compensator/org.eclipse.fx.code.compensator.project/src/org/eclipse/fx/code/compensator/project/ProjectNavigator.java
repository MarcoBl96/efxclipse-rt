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
package org.eclipse.fx.code.compensator.project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.fx.code.compensator.project.navigator.ProjectItem;
import org.eclipse.fx.core.URI;
import org.eclipse.fx.core.command.CommandService;
import org.eclipse.fx.core.di.ContextValue;
import org.eclipse.fx.ui.controls.tree.LazyTreeItem;
import org.eclipse.fx.ui.services.resources.GraphicsLoader;

public class ProjectNavigator {
	private final InstanceProject project;
	private final Property<Object> navigatorSelection;
	private final EMenuService menuService;
	private final GraphicsLoader nodeProvider;
	private final CommandService commandService;
	private final MPart appModelElement;

	private TreeView<ProjectNavigatorItem> tree;

	@Inject
	public ProjectNavigator(
			InstanceProject project,
			@ContextValue("navigatorSelection") Property<Object> navigatorSelection,
			EMenuService menuService,
			GraphicsLoader nodeProvider,
			CommandService commandService,
			MPart container) {
		this.project = project;
		this.navigatorSelection = navigatorSelection;
		this.menuService = menuService;
		this.nodeProvider = nodeProvider;
		this.commandService = commandService;
		this.appModelElement = container;
	}


	@PostConstruct
	void init(BorderPane parent) {
		HBox b = new HBox();
		b.getStyleClass().add("tool-bar");
		if( (MUIElement)appModelElement.getParent() instanceof MPartSashContainer ) {
			b.getChildren().add(new Label(project.getProject().getName(), nodeProvider.getGraphicsNode(URI.createPlatformPluginURI("org.eclipse.fx.code.compensator.project","css/icons/16/filenav_nav.png"))));
		}
		parent.setTop(b);

		tree = new TreeView<>(new LazyTreeItem<ProjectNavigatorItem>(new ProjectItem(project),ProjectNavigator::createChildren));
		if( (MUIElement)appModelElement.getParent() instanceof MPartSashContainer ) {
			tree.setShowRoot(false);
		}

		menuService.registerContextMenu(tree, "org.eclipse.fx.code.compensator.project.popupmenu.navigator");
		tree.setCellFactory(p -> new ProjectItemCell(nodeProvider));
		tree.setOnMouseClicked(this::open);
		tree.getSelectionModel().selectedItemProperty().addListener((ob,o,n) -> {
			if( n != null ) {
				navigatorSelection.setValue(n.getValue());
			} else {
				navigatorSelection.setValue(null);
			}

		});
		parent.setCenter(tree);
	}

	private void open(MouseEvent event) {
		if( event.getClickCount() == 2 ) {
			if( ! project.openItem(tree.getSelectionModel().getSelectedItem().getValue()) ) {
				if( tree.getSelectionModel().getSelectedItem() != null && tree.getSelectionModel().getSelectedItem().getValue().getDomainObject() instanceof Path && Files.isRegularFile((Path)tree.getSelectionModel().getSelectedItem().getValue().getDomainObject()) ) {
					Path p = (Path) tree.getSelectionModel().getSelectedItem().getValue().getDomainObject();
					commandService.execute("org.eclipse.fx.code.compensator.project.command.openresource", Collections.singletonMap("resourceURI", p.toUri().toString()));
				}
			}
		}
	}

//	@PreDestroy
//	void close() {
//		navigatorSelection.unbind();
//	}

	private static ObservableList<TreeItem<ProjectNavigatorItem>> createChildren(TreeItem<ProjectNavigatorItem> parent) {
		ObservableList<TreeItem<ProjectNavigatorItem>> rv = FXCollections.observableArrayList();
		for( ProjectNavigatorItem i : parent.getValue().getChildren() ) {
			if( i.isLeaf() ) {
				rv.add(new TreeItem<>(i));
			} else {
				rv.add(new LazyTreeItem<ProjectNavigatorItem>(i, ProjectNavigator::createChildren));
			}
		}
		parent.getValue().getChildren().addListener( (Change<? extends ProjectNavigatorItem> c) -> {
			while( c.next() ) {
				rv.addAll(c.getFrom(),c.getAddedSubList()
					.stream()
					.map(i -> i.isLeaf() ? new TreeItem<ProjectNavigatorItem>(i) : new LazyTreeItem<ProjectNavigatorItem>(i, ProjectNavigator::createChildren))
					.collect(Collectors.toList()));
			}
		});
		return rv;
	}

	static class ProjectItemCell extends TreeCell<ProjectNavigatorItem> {
		private GraphicsLoader provider;

		public ProjectItemCell(GraphicsLoader provider) {
			this.provider = provider;
		}

		@Override
		protected void updateItem(ProjectNavigatorItem item, boolean empty) {
			super.updateItem(item, empty);
			if( item != null && ! empty ) {
				setText(item.getLabel().toString());
				if( item.getIcon() != null ) {
					setGraphic(provider.getGraphicsNode(item.getIcon()));
				} else {
					setGraphic(null);
				}
			} else {
				setText(null);
				setGraphic(null);
			}
		}
	}
}
