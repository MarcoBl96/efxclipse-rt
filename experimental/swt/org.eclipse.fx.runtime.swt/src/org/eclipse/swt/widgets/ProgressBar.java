/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation, BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * 	   IBM Corporation - initial API
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.widgets;

import javafx.scene.layout.Region;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Device.NoOpDrawableGC;
import org.eclipse.swt.internal.Util;

public class ProgressBar extends Control {
	private int minimum = 0;
	private int maximum = 100;
	
	private javafx.scene.control.ProgressBar bar;
	
	public ProgressBar(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Object createWidget() {
		bar = new javafx.scene.control.ProgressBar();
		bar.setProgress(0.0);
		return bar;
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean flushCache) {
		System.err.println("COMPUTE: " + wHint + "/" +hHint );
		return super.computeSize(wHint, hHint, flushCache);
	}
	
	@Override
	public Region internal_getNativeObject() {
		return bar;
	}
	
	@Override
	public void internal_dispose_GC(DrawableGC gc) {
	}
	
	public int getMaximum() {
		return maximum;
	}
	
	public int getMinimum() {
		return minimum;
	}
	
	public int getSelection() {
		return (int)(minimum + ((maximum - minimum) * bar.getProgress()));
	}
	
	public int getState() {
		Util.logNotImplemented();
		return SWT.NORMAL;
	}
	
	public void setMaximum(int value) {
		this.maximum = value;
	}
	
	public void setMinimum(int value) {
		this.minimum = value;
	}
	
	public void setSelection(int value) {
		double v = Math.max(0,Math.min(value / (maximum - minimum * 1.0), 100));
		bar.setProgress(v);
	}
	
	public void setState (int state) {
		Util.logNotImplemented();
	}
}
