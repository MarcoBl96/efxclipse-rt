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

import java.util.List;
import java.util.Optional;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.fx.code.compensator.model.workbench.LocalProject;
import org.eclipse.fx.code.compensator.model.workbench.Project;
import org.eclipse.fx.code.compensator.project.ProjectService;
import org.eclipse.fx.core.di.Service;

public class ProjectServiceExtractorCF extends ContextFunction {
	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		MPerspective p = context.get(MPerspective.class);
		ProjectService rv = (ProjectService) p.getContext().get("_cached_perspective_prjs");
		if( rv == null ) {
			rv = (ProjectService) ContextInjectionFactory.invoke(new ProjectServiceHelper(), Execute.class, p.getContext());
			p.getContext().set("_cached_perspective_prjs", rv);
		}

		return rv;
	}

	static class ProjectServiceHelper {
		@Execute
		public ProjectService getVersionControl(@Service List<ProjectService> service, Project p) {
			if(p instanceof LocalProject) {
				LocalProject lp = (LocalProject) p;
				Optional<ProjectService> first = service.stream().filter((ps) -> ps.getId().equals(lp.getProjectType())).findFirst();
				if( first.isPresent() ) {
					return first.get();
				}
			}

			return null;
		}
	}
}
