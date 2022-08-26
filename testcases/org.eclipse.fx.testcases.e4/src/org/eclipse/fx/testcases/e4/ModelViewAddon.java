/*******************************************************************************
 * Copyright (c) 2013 BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.testcases.e4;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.fx.ui.modelviewer.ModelProvider;

public class ModelViewAddon {
	@PostConstruct
	void init(final IEclipseContext ctx) {
		ctx.set(ModelProvider.class, new ModelProvider() {
			
			@Override
			public MApplication getApplication() {
				return ctx.get(MApplication.class);
			}
		});
	}
}
