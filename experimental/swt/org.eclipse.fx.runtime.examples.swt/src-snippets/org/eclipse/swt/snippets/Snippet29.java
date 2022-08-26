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
 * Menu example snippet: create a bar and pull down menu (accelerators, mnemonics)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class Snippet29 implements SWTAppStart {

	@Override
	public BlockCondition createApp(Display display) {
		Shell shell = new Shell(display);
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
		fileItem.setText("&File");
		Menu submenu = new Menu (shell, SWT.DROP_DOWN);
		fileItem.setMenu (submenu);
		 MenuItem item = new MenuItem (submenu, SWT.PUSH);
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				System.out.println("Select All");
			}
		});
		item.setText ("Select &All\tCtrl+A");
		item.setAccelerator (SWT.MOD1 + 'A');
		shell.setSize(200, 200);
		SWTUtil.getInstance().openBlocking(shell);
		return null;
	}

	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet29());
	}
}
