/*******************************************************************************
* Copyright (c) 2014 BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
*
* Contributors:
* 	Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
*******************************************************************************/
package org.eclipse.fx.code.compensator.editor.contrib;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.fx.code.compensator.editor.Input;
import org.eclipse.fx.code.compensator.editor.TextEditor;

public class InputContextFunction extends ContextFunction {
	@Override
	public Object compute(IEclipseContext context) {
		Input<?> input = (Input<?>) context.get("localInput");
		if( input == null ) {
			MPart part = context.get(MPart.class);
			String url = part.getPersistedState().get(TextEditor.DOCUMENT_URL);

			if( url != null ) {
				ServiceCollector collector = context.get(ServiceCollector.class);
				context.set(TextEditor.DOCUMENT_URL, url);
				context.set(TextEditor.VCS_URL, part.getPersistedState().get(TextEditor.VCS_URL));
				input = ContextInjectionFactory.make(collector.createInput(url),context);
				context.set("localInput", input);
			}
		}
		return input;
	}
}
