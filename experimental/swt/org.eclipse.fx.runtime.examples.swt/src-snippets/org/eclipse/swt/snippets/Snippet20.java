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
 * CoolBar example snippet: create a cool bar
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class Snippet20 implements SWTAppStart {

	@Override
	public BlockCondition createApp(Display display) {
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		CoolBar bar = new CoolBar(shell, SWT.BORDER);
		for (int i = 0; i < 2; i++) {
			CoolItem item = new CoolItem(bar, SWT.NONE);
			Button button = new Button(bar, SWT.PUSH);
			button.setText("Button " + i);
			Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			item.setPreferredSize(item.computeSize(size.x, size.y));
			item.setControl(button);
		}
		Rectangle clientArea = shell.getClientArea();
		bar.setLocation(clientArea.x, clientArea.y);
		bar.pack();
		SWTUtil.getInstance().openBlocking(shell);
		return null;
	}
	
	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet20());
	}
}
