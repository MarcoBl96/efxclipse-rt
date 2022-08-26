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
package org.eclipse.fx.ui.workbench.renderers.fx.services;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.ui.workbench.renderers.base.services.PartialRestoreMinMaxService;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WWidget;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WWindow;
import org.eclipse.fx.ui.workbench.renderers.fx.addons.internal.DefaultTrimStackImpl;
import org.osgi.service.component.annotations.Component;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.stage.Stage;

@SuppressWarnings("javadoc")
@Component(property="service.ranking:Integer=-1")
public class DefaultPartialMinMaxService implements PartialRestoreMinMaxService {
	
	@Override
	public MTrimBar findTrimBarForElement(EModelService modelService, MUIElement element, MTrimmedWindow window) {
		SideValue side = null;
		WWindow<?> w = (WWindow<?>) window.getWidget();
		if( w.getWidget() instanceof Stage ) {
			Stage s = (Stage) w.getWidget();
			double winCenterX = s.getWidth() / 2;
			WWidget<?> widget = (WWidget<?>) element.getWidget();
			Node stackCtrl = (Node) widget.getWidget();
			Bounds stackBounds = stackCtrl.localToScene(stackCtrl.getBoundsInLocal());
			double stackCenterX = stackBounds.getMinX() + (stackBounds.getWidth() / 2);
			side = stackCenterX < winCenterX ? SideValue.LEFT : SideValue.RIGHT;
		} else {
			side = SideValue.LEFT;
		}
		return modelService.getTrim(window, side);
	}
	
	@Override
	public Class<? extends TrimStack> getTrimStackType() {
		return DefaultTrimStackImpl.class;
	}

	@Override
	public boolean supportPartialRestore(MUIElement element) {
		return true;
	}
	
	@Override
	public boolean isMaximizable(MUIElement element) {
		return element instanceof MPartStack && ! element.getTags().contains(TAG_NOT_MAXIMIZABLE);
	}
}