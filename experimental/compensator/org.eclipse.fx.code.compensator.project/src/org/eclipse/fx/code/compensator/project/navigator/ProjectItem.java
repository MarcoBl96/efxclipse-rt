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
package org.eclipse.fx.code.compensator.project.navigator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.eclipse.fx.code.compensator.model.workbench.LocalProject;
import org.eclipse.fx.code.compensator.project.InstanceProject;
import org.eclipse.fx.code.compensator.project.ProjectNavigatorItem;
import org.eclipse.fx.core.URI;

public class ProjectItem implements ProjectNavigatorItem {
	private final InstanceProject project;
	private final ObservableList<ProjectNavigatorItem> children = FXCollections.observableArrayList();
	private VCSContainerItem vcsContainerItem;

	public ProjectItem(InstanceProject project) {
		this.project = project;
		children.add(new ModuleContainerItem(this,project));
		children.add(new BugtrackerContainerItem(this,project));
		children.add(new BuildServerItem(this));
		if( project.getProject() instanceof LocalProject ) {
			vcsContainerItem = new VCSContainerItem(this,project);
			children.add(vcsContainerItem);
		}
	}
	
	@Override
	public ProjectNavigatorItem getParent() {
		return null;
	}
	
//	public VCSRootNavigatorItem getVCSRoot(Path path) {
//		return vcsContainerItem.getRootItem(path);
//	}

	@Override
	public CharSequence getLabel() {
		return project.getProject().getName();
	}

	@Override
	public URI getIcon() {
		return URI.createPlatformPluginURI("org.eclipse.fx.code.compensator.project","css/icons/16/module_view.png");
	}

	@Override
	public Object getDomainObject() {
		return project;
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
		return children;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
}
