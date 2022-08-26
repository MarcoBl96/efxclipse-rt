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
 * PocketPC example snippet: Hello World
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class Snippet85 implements SWTAppStart {

	@Override
	public BlockCondition createApp(Display display) {
		/*
		 * Create a Shell with the default style i.e. full screen, no decoration
		 * on PocketPC.
		 */
		Shell shell = new Shell(display);

		/*
		 * Set a text so that the top level Shell also appears in the PocketPC
		 * task list
		 */
		shell.setText("Main");

		/*
		 * Set a menubar to follow UI guidelines on PocketPC
		 */
		Menu mb = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mb);

		SWTUtil.getInstance().openBlocking(shell);
		return null;
	}

	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet85());
	}
}
