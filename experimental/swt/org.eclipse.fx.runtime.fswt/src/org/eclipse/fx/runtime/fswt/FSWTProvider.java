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
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.runtime.fswt;

import java.util.ServiceLoader;

public class FSWTProvider {
	private static boolean isOSGi() {
		return FSWTProvider.class.getClassLoader() instanceof org.osgi.framework.BundleReference;
	}

	public static FSWT getFacade() {
		if( isOSGi() ) {
			org.osgi.framework.BundleContext bundleContext = org.osgi.framework.FrameworkUtil.getBundle(FSWTProvider.class).getBundleContext();
			org.osgi.framework.ServiceReference<FSWT> ref = bundleContext.getServiceReference(FSWT.class);
			return bundleContext.getService(ref);
		} else {
			ServiceLoader<FSWT> serviceLoader = ServiceLoader.<FSWT>load(FSWT.class);
			FSWT current = null;
			for( FSWT f : serviceLoader ) {
				if( current == null || current.getRanking() < f.getRanking() ) {
					current = f;
				}
			}
			return current;
		}
	}
}