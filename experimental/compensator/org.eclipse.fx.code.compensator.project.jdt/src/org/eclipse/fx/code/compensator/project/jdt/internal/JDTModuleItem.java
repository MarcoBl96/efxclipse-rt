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
package org.eclipse.fx.code.compensator.project.jdt.internal;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.eclipse.fx.code.compensator.model.workbench.Module;
import org.eclipse.fx.code.compensator.project.Constants;
import org.eclipse.fx.code.compensator.project.ProjectNavigatorItem;
import org.eclipse.fx.code.compensator.project.navigator.FileItem;
import org.eclipse.fx.code.compensator.project.navigator.FolderItem;
import org.eclipse.fx.code.compensator.project.navigator.PathItem;
import org.eclipse.fx.core.function.ExExecutor;
import org.eclipse.fx.core.log.Logger;
import org.eclipse.fx.core.log.LoggerCreator;
import org.osgi.service.event.Event;

public class JDTModuleItem implements ProjectNavigatorItem, JDTPathItem {
	private final Module module;
	private final JDTInstanceProject instanceProject;
	private ObservableList<ProjectNavigatorItem> children;
	private Path path;
	private static Logger LOGGER;
	private final ProjectNavigatorItem parent;
	
	public JDTModuleItem(ProjectNavigatorItem parent, Module module, JDTInstanceProject instanceProject) {
		this.parent = parent;
		this.module = module;
		this.instanceProject = instanceProject;
		this.module.setUserdata(this);
		
		this.path = Paths.get(URI.create(module.getRootFolderUrl()));
//		this.instanceProject.getEventBroker().subscribe(Constants.TOPIC_NEW_FILE, this::handleNewFile);
	}
	
	@Override
	public ProjectNavigatorItem getParent() {
		return this.parent;
	}

	@Override
	public JDTModuleItem getModuleItem() {
		return this;
	}

	@Override
	public JDTInstanceProject getJdtProject() {
		return this.instanceProject;
	}

	public Path getModuleRelativePath(Path path) {
		return getPath().relativize(path);
	}

	private static Logger getLogger() {
		if( LOGGER == null ) {
			LOGGER = LoggerCreator.createLogger(JDTModuleItem.class);
		}
		return LOGGER;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public CharSequence getLabel() {
		return module.getName();
	}

	@Override
	public org.eclipse.fx.core.URI getIcon() {
		return org.eclipse.fx.core.URI.createPlatformPluginURI("org.eclipse.fx.code.compensator.project.jdt","css/icons/16/prj_obj.png");
	}

	@Override
	public Module getDomainObject() {
		return module;
	}

	@Override
	public String getOpenCommandId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public ObservableList<ProjectNavigatorItem> getChildren() {
		if( children == null ) {
			children = FXCollections.observableArrayList();
			children.addAll(module.getModuleList().stream().map( m -> new JDTModuleItem(this,m, instanceProject) ).collect(Collectors.toList()));
			Future<List<String>> folders = instanceProject.getProjectServer().getSourceFolders(module.getName());

			Optional<List<JDTPackageFragmentRootItem>> items = ExExecutor.executeSupplier(folders::get, "Unable to retrieve").map(srcfList -> {
				return srcfList.stream().map(srcf -> new JDTPackageFragmentRootItem(this, this.path.resolve(srcf))).collect(Collectors.toList());
			});
			if( items.isPresent() ) {
				children.addAll(items.get());
			}

			Optional<List<PathItem>> paths = ExExecutor.executeSupplier(() -> Files.walk(path, 1), "Unable to fetch inspect '"+path+"'")
				.map((s) -> s.filter(p -> ! p.equals(path)).map((p) -> {
					if( Files.isDirectory(p) ) {
						return new FolderItem(this,p);
					} else {
						return new FileItem(this,p);
					}
				}).collect(Collectors.toList()));
			if( paths.isPresent() ) {
				children.addAll(paths.get());
			}
		}
		return children;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

//	public static void main(String[] args) {
//		Path p = Paths.get(uri);
//	}
}
