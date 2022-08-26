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
package org.eclipse.fx.code.compensator.project.internal;

import java.util.Optional;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.fx.code.compensator.model.workbench.Project;
import org.eclipse.fx.code.compensator.model.workbench.Workbench;

public class ProjectExtractorCF extends ContextFunction {

	public Object compute(IEclipseContext context, String contextKey) {
		MPerspective p = context.get(MPerspective.class);
		Project rv = (Project) p.getContext().get("_cached_perspective_project");

		if( rv == null ) {
			Workbench workbench = context.get(Workbench.class);
			String projectId = p.getElementId().substring(p.getElementId().lastIndexOf('.'));
			Optional<Project> project = workbench.getProjectList().stream().filter((pl) -> projectId.endsWith(pl.getProjectId())).findFirst();
			if( project.isPresent() ) {
				rv = project.get();
				p.getContext().set("_cached_perspective_project", rv);
			}
		}
		return rv;
	}

}
