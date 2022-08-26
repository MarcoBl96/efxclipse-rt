/*******************************************************************************
 * Copyright (c) 2012 BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.ui.workbench.renderers.fx;

import java.util.List;

import javafx.scene.control.SeparatorMenuItem;

import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.fx.ui.workbench.renderers.base.BaseMenuSeparatorRenderer;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WMenuSeparator;
import org.eclipse.fx.ui.workbench.renderers.fx.widget.WWidgetImpl;

/**
 * default renderer for menu separator
 */
public class DefMenuSeparatorRenderer extends BaseMenuSeparatorRenderer<SeparatorMenuItem> {

	@Override
	protected Class<? extends WMenuSeparator<SeparatorMenuItem>> getWidgetClass(MMenuSeparator separator) {
		return MenuSeparatorImpl.class;
	}
	
	/**
	 * @noreference
	 */
	public static class MenuSeparatorImpl extends WWidgetImpl<SeparatorMenuItem, MMenuSeparator> implements WMenuSeparator<SeparatorMenuItem> {

		@Override
		public void addStyleClasses(List<String> classnames) {
			getWidget().getStyleClass().addAll(classnames);
		}

		@Override
		public void addStyleClasses(String... classnames) {
			getWidget().getStyleClass().addAll(classnames);
		}
		
		@Override
		public void removeStyleClasses(List<String> classnames) {
			getWidget().getStyleClass().removeAll(classnames);
		}

		@Override
		public void removeStyleClasses(String... classnames) {
			getWidget().getStyleClass().removeAll(classnames);
		}

		@Override
		public void setStyleId(String id) {
			getWidget().setId(id);
		}

		@Override
		protected SeparatorMenuItem createWidget() {
			SeparatorMenuItem item = new SeparatorMenuItem();
			return item;
		}

		@Override
		protected void setUserData(WWidgetImpl<SeparatorMenuItem, MMenuSeparator> widget) {
			getWidget().setUserData(widget);
		}
	}
}
