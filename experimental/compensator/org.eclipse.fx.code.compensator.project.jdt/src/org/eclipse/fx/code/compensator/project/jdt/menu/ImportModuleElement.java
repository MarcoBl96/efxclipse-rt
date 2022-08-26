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
package org.eclipse.fx.code.compensator.project.jdt.menu;

import java.nio.file.Path;
import java.util.concurrent.Future;

import javax.inject.Named;

import org.eclipse.fx.code.compensator.model.workbench.Module;
import org.eclipse.fx.code.compensator.model.workbench.Project;
import org.eclipse.fx.code.compensator.model.workbench.WorkbenchFactory;
import org.eclipse.fx.code.compensator.project.ProjectNavigatorItem;
import org.eclipse.fx.code.compensator.project.ProjectService.MenuElement;
import org.eclipse.fx.code.compensator.project.navigator.FolderItem;
import org.eclipse.fx.code.server.jdt.JDTServer;
import org.eclipse.fx.core.di.Invoke;
import org.eclipse.fx.core.function.ExExecutor;

public class ImportModuleElement extends MenuElement {

	public ImportModuleElement() {
		super("import-java-module", "Import Module");
	}

	@Override
	public boolean applies(ProjectNavigatorItem item) {
		if( item instanceof FolderItem ) {
			return item.getChildren()
				.stream()
				.filter(i -> i.getDomainObject() instanceof Path && ((Path)i.getDomainObject()).getFileName().toString().equals(".project"))
				.findFirst().isPresent();
		}
		return false;
	}

	@Invoke
	public void run(JDTServer server, @Named("navigatorSelection") ProjectNavigatorItem item, Project project) {
		Path domainObject = (Path)item.getDomainObject();
		Future<String> future = server.registerModule(domainObject.toUri());
		ExExecutor.executeSupplier(future::get, "Failed to retrieve value").ifPresent( name -> {
			Module m = WorkbenchFactory.eINSTANCE.createModule();
			m.setName(name);
			m.setRootFolderUrl(domainObject.toUri().toString());
			project.getModuleList().add(m);
		});
	}
}
