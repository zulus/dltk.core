/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.launching;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallChangedListener;
import org.eclipse.dltk.launching.PropertyChangeEvent;

/**
 * Simple interpreter listener that reports whether interpreter settings have
 * changed.
 */
public class InterpreterListener implements IInterpreterInstallChangedListener {

	private boolean changed = false;

	@Override
	public void defaultInterpreterInstallChanged(IInterpreterInstall previous,
			IInterpreterInstall current) {
		changed();
	}

	@Override
	public void interpreterAdded(IInterpreterInstall Interpreter) {
		changed();
	}

	@Override
	public void interpreterChanged(PropertyChangeEvent event) {
		changed();
	}

	@Override
	public void interpreterRemoved(IInterpreterInstall Interpreter) {
		changed();
	}

	/**
	 * @since 2.0
	 */
	protected void changed() {
		changed = true;
	}

	public boolean isChanged() {
		return changed;
	}
}
