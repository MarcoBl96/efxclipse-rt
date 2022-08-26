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
 * example snippet: set icons with different resolutions
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.0
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class Snippet138 implements SWTAppStart {
	@Override
	public BlockCondition createApp(Display display) {
		Image small = new Image(display, 16, 16);
		GC gc = new GC(small);
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillArc(0, 0, 16, 16, 45, 270);
		gc.dispose();

		Image large = new Image(display, 32, 32);
		gc = new GC(large);
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillArc(0, 0, 32, 32, 45, 270);
		gc.dispose();

		/*
		 * Provide different resolutions for icons to get high quality rendering
		 * wherever the OS needs large icons. For example, the ALT+TAB window on
		 * certain systems uses a larger icon.
		 */
		Shell shell = new Shell(display);
		shell.setText("Small and Large icons");
		shell.setImages(new Image[] { small, large });

		/*
		 * No large icon: the OS will scale up the small icon when it needs a
		 * large one.
		 */
		Shell shell2 = new Shell(display);
		shell2.setText("Small icon");
		shell2.setImage(small);

		shell.open();
		shell2.open();
		SWTUtil.getInstance().block(display,
				SWTUtil.getInstance().createShellBlockCondition(shell));
		small.dispose();
		large.dispose();
		return null;
	}

	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet138());
	}
}
