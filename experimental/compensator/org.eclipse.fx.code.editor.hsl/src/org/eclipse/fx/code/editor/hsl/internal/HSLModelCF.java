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
package org.eclipse.fx.code.editor.hsl.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.fx.code.compensator.editor.Input;
import org.eclipse.fx.code.editor.hsl.HSLModelProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component(service=IContextFunction.class,property={"service.context.key=org.eclipse.fx.code.compensator.hsl.hSL.Model"})
public class HSLModelCF extends ContextFunction {
	private List<HSLModelProvider> providerList = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,policy=ReferencePolicy.DYNAMIC,policyOption=ReferencePolicyOption.GREEDY)
	public void registerProvider(HSLModelProvider provider) {
		synchronized (providerList) {
			providerList.add(provider);
		}
	}
	
	public void unregisterProvider(HSLModelProvider provider) {
		synchronized (providerList) {
			providerList.remove(provider);
		}
	}
	
	@Override
	public Object compute(IEclipseContext context) {
		List<HSLModelProvider> list;
		
		synchronized (providerList) {
			list = new ArrayList<HSLModelProvider>(providerList);
		}
		
		Input<?> input = context.get(Input.class);
		
		return list.stream()
						.filter( p -> p.applies(input))
						.findFirst()
						.map( p -> p.getModel(input)).orElse(null);
	}
}
