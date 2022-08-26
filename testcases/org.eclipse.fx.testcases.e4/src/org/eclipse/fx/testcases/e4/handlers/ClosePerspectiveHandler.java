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
package org.eclipse.fx.testcases.e4.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.fx.ui.workbench.services.ELifecycleService;
import org.eclipse.fx.ui.workbench.services.lifecycle.annotation.PreClose;

public class ClosePerspectiveHandler {

	@Execute
	public void closePerspective(MWindow window, EModelService modelService, EPartService partService, ELifecycleService lifecycleService) {

		MPerspectiveStack activeStack = (MPerspectiveStack) window
				.getSelectedElement();
		if (activeStack == null)
			return;

		// find the next perspective to activate, and the selected one
		MPerspective activePerspective = activeStack.getSelectedElement();
		List<MPart> partsInActive = modelService.findElements(window, null, MPart.class, null,
				EModelService.IN_ACTIVE_PERSPECTIVE);
		List<MPart> allParts = modelService.findElements(window, null, MPart.class, null,
				EModelService.IN_ANY_PERSPECTIVE);
		List<MPart> partsToRemove = new ArrayList<MPart>();
		for (MPart p : partsInActive) {
			if (!allParts.contains(p)) partsToRemove.add(p);
		}
		for (MPart p : partsToRemove) {
			partService.hidePart(p, true);//don't care about saving here
		}
//		AbstractRenderer renderer = (AbstractRenderer) activePerspective.getRenderer();
//		if (renderer.validateLifecycleAnnotation(PreClose.class, null, renderer.getModelContext(activePerspective)
//				, activePerspective))
		if (lifecycleService.validateAnnotation(PreClose.class, activePerspective, activePerspective.getContext()))
		modelService.removePerspectiveModel(activePerspective, window);//this one includes selecting the next active

	}

}