/*******************************************************************************
* Copyright (c) 2014 BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
*
* Contributors:
* 	Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
*******************************************************************************/
package org.eclipse.fx.code.compensator.freeedit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.fx.code.compensator.editor.services.FileIconLookup;
import org.eclipse.fx.code.compensator.model.workbench.File;
import org.eclipse.fx.code.compensator.model.workbench.Folder;
import org.eclipse.fx.code.compensator.model.workbench.Resource;
import org.eclipse.fx.code.compensator.model.workbench.Workbench;
import org.eclipse.fx.code.compensator.model.workbench.WorkbenchFactory;
import org.eclipse.fx.code.compensator.model.workbench.WorkbenchPackage;
import org.eclipse.fx.ui.controls.tree.LazyTreeItem;


@SuppressWarnings("restriction")
public class FileList {
	@Inject
	private Workbench workbench;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@Inject
	private FileIconLookup lookup;

	private ObservableList<TreeItem<Resource>> inputList = FXCollections.observableArrayList();

	private TreeView<Resource> view;

	private Image fileIcon;
	private Image folderIcon;


	@PostConstruct
	public void initUI(BorderPane parent) {
		fileIcon = new Image(getClass().getClassLoader().getResource("/icons/16/application-x-zerosize.png").toExternalForm());
		folderIcon = new Image(getClass().getClassLoader().getResource("/icons/16/inode-directory.png").toExternalForm());

		view = new TreeView<>();
		view.setCellFactory(this::treeCell);
		view.setShowRoot(false);
		view.setOnDragOver(this::handleDragOver);
		view.setOnDragDropped(this::handleDrop);

		TreeItem<Resource> t = new TreeItem<>();
		for( Resource r : workbench.getResources() ) {
			if( r instanceof Folder ) {
				LazyTreeItem<Resource> ti = new LazyTreeItem<Resource>((Folder)r, FileList::treeItemChildrenFactory);
				t.getChildren().add(ti);
				inputList.add(ti);
			} else {
				TreeItem<Resource> ti = new TreeItem<>(r);
				t.getChildren().add(ti);
				inputList.add(ti);
			}
		}
		view.setRoot(t);
		view.setOnMouseClicked(this::open);
		parent.setCenter(view);
		workbench.eAdapters().add(new AdapterImpl() {
			@Override
			public void notifyChanged(Notification msg) {
				if( msg.getFeature() == WorkbenchPackage.Literals.WORKBENCH_ELEMENT__RESOURCES ) {
					if( msg.getEventType() == Notification.ADD ) {
						if( msg.getNewValue() instanceof File ) {
							TreeItem<Resource> e = new TreeItem<>((Resource) msg.getNewValue());
							t.getChildren().add(e);
							inputList.add(e);
						} else {
							LazyTreeItem<Resource> e = new LazyTreeItem<Resource>((Resource) msg.getNewValue(),FileList::treeItemChildrenFactory);
							t.getChildren().add(e);
							inputList.add(e);
						}
					}
				}
			}
		});
	}

	private void handleDragOver(DragEvent e) {
		System.err.println(e);
		if( e.getDragboard().hasFiles() ) {
			e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		} else if( e.getDragboard().hasUrl() ) {
			e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}
	}

	private void handleDrop(DragEvent e) {
		if( e.getDragboard().hasFiles() ) {
			workbench.getResources().addAll(e.getDragboard().getFiles().stream().map(this::handleDroppedFile).collect(Collectors.toList()));
		} else if( e.getDragboard().hasUrl() ) {
			if( e.getDragboard().getUrl().endsWith(".comp") ) {
				try {
					URL url = new URL( e.getDragboard().getUrl());
					String name = org.eclipse.fx.core.URI.create(e.getDragboard().getUrl()).lastSegment();

					java.io.File outFile = new java.io.File(System.getProperty("user.home")+"/.compensator/languages/"+name);
					FileOutputStream out = new FileOutputStream(outFile);
					byte[] buffer = new byte[2048];
					int l;
					InputStream in = url.openStream();
					while( (l = in.read(buffer)) != -1 ) {
						out.write(buffer, 0, l);
					}
					out.close();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	private Resource handleDroppedFile(java.io.File f) {
		if( f.isDirectory() ) {
			Folder folder = WorkbenchFactory.eINSTANCE.createFolder();
			folder.setUrl(Paths.get(f.toURI()).toUri().toString());
			return folder;
		} else {
			File file = WorkbenchFactory.eINSTANCE.createFile();
			file.setUrl(Paths.get(f.toURI()).toUri().toString());
			return file;
		}
	}

	private void open(MouseEvent event) {
		if( event.getClickCount() == 2 ) {
			Command cmd = commandService.getCommand("org.eclipse.fx.code.compensator.app.command.1");
			IEclipseContext staticCtx = EclipseContextFactory.create();
			if( view.getSelectionModel().getSelectedItem() != null && view.getSelectionModel().getSelectedItem().getValue() instanceof File ) {
				staticCtx.set(File.class, (File)view.getSelectionModel().getSelectedItem().getValue());
				ParameterizedCommand pCmd = ParameterizedCommand.generateCommand(cmd, Collections.emptyMap());
				handlerService.executeHandler(pCmd, staticCtx);
				staticCtx.dispose();
			}
		}
	}

	private TreeCell<Resource> treeCell(TreeView<Resource> param) {
		return new TreeCell<Resource>() {
			@Override
			protected void updateItem(Resource item, boolean empty) {
				if( item != null && ! empty ) {
					if( item instanceof File ) {
						org.eclipse.fx.core.URI uri = org.eclipse.fx.core.URI.create(((File) item).getUrl());
						setText(uri.lastSegment());
						org.eclipse.fx.core.URI iconUri = lookup.getFileIcon(uri.toString());
						if( iconUri == null ) {
							setGraphic(new ImageView(fileIcon));
						} else {
							//TODO We need to cache images by URI
							setGraphic(new ImageView(new Image(iconUri.toString())));
						}
					} else if( item instanceof Folder ) {
						URI uri = URI.createURI(((Folder) item).getUrl());
						setText(uri.segment(uri.segmentCount()-2));
						setGraphic(new ImageView(folderIcon));
					}
				} else {
					setText(null);
					setGraphic(null);
				}
				super.updateItem(item, empty);
			}
		};
	}

	static List<TreeItem<Resource>> treeItemChildrenFactory(TreeItem<Resource> parent) {
		try {
			java.net.URI uri = new java.net.URI(((Folder)parent.getValue()).getUrl());
			Path path = Paths.get(uri);
			List<TreeItem<Resource>> l = new ArrayList<>();
			Files.newDirectoryStream(path).forEach((p) -> {
				if( Files.isDirectory(p) ) {
					Folder f = WorkbenchFactory.eINSTANCE.createFolder();
					f.setUrl(p.toUri().toString());
					l.add(new LazyTreeItem<Resource>(f,FileList::treeItemChildrenFactory));
				} else {
					File f = WorkbenchFactory.eINSTANCE.createFile();
					f.setUrl(p.toUri().toString());
					l.add(new TreeItem<>(f));
				}
			});
			return l;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

}