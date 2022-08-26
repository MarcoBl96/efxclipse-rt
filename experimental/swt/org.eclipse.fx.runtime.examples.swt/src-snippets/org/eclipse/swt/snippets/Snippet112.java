/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.snippets;

/*
 * Image example snippet: display an image in a group
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Snippet112 implements SWTAppStart {

	@Override
	public BlockCondition createApp(Display display) {
		final Image image = new Image(display, 20, 20);
		Color color = display.getSystemColor(SWT.COLOR_RED);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(image.getBounds());
		gc.dispose();

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new FillLayout());
		group.setText("a square");
		Canvas canvas = new Canvas(group, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});

		shell.pack();
		SWTUtil.getInstance().openBlocking(shell);
		image.dispose();
		return null;
	}
	
	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet112());
	}
}
