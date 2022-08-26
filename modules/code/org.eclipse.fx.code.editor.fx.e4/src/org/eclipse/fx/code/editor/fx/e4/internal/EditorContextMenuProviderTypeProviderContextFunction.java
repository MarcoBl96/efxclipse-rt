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
package org.eclipse.fx.code.editor.fx.e4.internal;

import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.fx.code.editor.e4.InputBasedContextFunction;
import org.eclipse.fx.code.editor.fx.services.EditorContextMenuProvider;
import org.eclipse.fx.code.editor.fx.services.EditorContextMenuProviderTypeProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javafx.scene.control.Control;

@SuppressWarnings("restriction")
@Component(service=IContextFunction.class,property="service.context.key:String=org.eclipse.fx.code.editor.fx.services.EditorContextMenuProvider")
public class EditorContextMenuProviderTypeProviderContextFunction extends InputBasedContextFunction<EditorContextMenuProvider, EditorContextMenuProviderTypeProvider> {

	@Reference(cardinality=ReferenceCardinality.MULTIPLE,policy=ReferencePolicy.DYNAMIC,policyOption=ReferencePolicyOption.GREEDY)
	public void registerService(EditorContextMenuProviderTypeProvider service, Map<String, Object> properties) {
		super.registerService(service, properties);
	}

	public void unregisterService(EditorContextMenuProviderTypeProvider service) {
		super.unregisterService(service);
	}

	@Override
	protected EditorContextMenuProvider createDefault(IEclipseContext context) {
		return ContextInjectionFactory.make(ModelEditorContextMenuProvider.class, context);
	}

	static class ModelEditorContextMenuProvider implements EditorContextMenuProvider {
		private final EMenuService menuService;

		@Inject
		public ModelEditorContextMenuProvider(EMenuService menuService) {
			this.menuService = menuService;
		}

		@Override
		public void attacheMenu(Control styledText, Type type) {
			menuService.registerContextMenu(styledText, "editor.contextmenu."+type);
		}
	}
}
