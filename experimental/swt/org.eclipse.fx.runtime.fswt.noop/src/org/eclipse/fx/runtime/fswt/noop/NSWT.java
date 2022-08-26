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
package org.eclipse.fx.runtime.fswt.noop;

import org.eclipse.fx.runtime.fswt.FBlockCondition;
import org.eclipse.fx.runtime.fswt.FCallback;
import org.eclipse.fx.runtime.fswt.FSWT;
import org.eclipse.fx.runtime.fswt.noop.internal.widgets.NComposite;
import org.eclipse.fx.runtime.fswt.noop.internal.widgets.NControl;
import org.eclipse.fx.runtime.fswt.noop.internal.widgets.NDisplay;
import org.eclipse.fx.runtime.fswt.noop.internal.widgets.NShell;
import org.eclipse.fx.runtime.fswt.widgets.FComposite;
import org.eclipse.fx.runtime.fswt.widgets.FControl;
import org.eclipse.fx.runtime.fswt.widgets.FDisplay;
import org.eclipse.fx.runtime.fswt.widgets.FShell;
import org.eclipse.fx.runtime.fswt.widgets.FWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class NSWT implements FSWT {
	@Override
	public int getRanking() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Widget, F extends FWidget<T>> F adapt(T widget,
			Class<F> clazz) {
		if( clazz == FShell.class ) {
			return (F) new NShell<>((Shell) widget,this);
		} else if( clazz == FComposite.class ) {
			return (F) new NComposite<>((Composite) widget,this);
		} else if( clazz == FControl.class ) {
			return (F) new NControl<>((Control) widget,this);
		}
		throw new IllegalArgumentException("Unsupported adapter '"+clazz+"'");
	}

	@Override
	public void boostrap(FCallback<FDisplay<Display>, FBlockCondition> initialize) {
		Display d = new Display();
		NDisplay<Display> fxd = new NDisplay<Display>(d,this);
		FBlockCondition condition = initialize.invoke(fxd);
		if( condition != null ) {
			fxd.block(condition);
		}
		d.dispose();
	}

}
