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
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.ui.viewer;

import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Base interface of all viewers
 * 
 * @param <O>
 *            the domain object representing a row
 * @param <I>
 *            the input to the viewer
 * @param <C>
 *            the content provider responsible to translate the input into the
 *            internal structure
 */
public interface Viewer<I, O, C extends ContentProvider<I, O>> {
	public void setContentProvider(@NonNull Supplier<@NonNull C> contentProvider);

	public void setInput(@NonNull Supplier<@NonNull I> input);
}
