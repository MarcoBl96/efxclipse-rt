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
 * Menu example snippet: show a popup menu (wait for it to close)
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
import org.eclipse.swt.widgets.*;

public class Snippet131 implements SWTAppStart {
	@Override
	public BlockCondition createApp(final Display display) {
		final Shell shell = new Shell(display);
		shell.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Menu menu = new Menu(shell, SWT.POP_UP);
				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setText("Menu Item");
				item.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						System.out.println("Item Selected");
					}
				});
				menu.setLocation(event.x, event.y);
				menu.setVisible(true);
				while (!menu.isDisposed() && menu.isVisible()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				menu.dispose();
			}
		});
		shell.pack();
		SWTUtil.getInstance().openBlocking(shell);
		return null;
	}
	
	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet131());
	}
}
