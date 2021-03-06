/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal;

import org.eclipse.core.runtime.ListenerList;

public abstract class DbgpTermination implements IDbgpTermination {
	private final ListenerList<IDbgpTerminationListener> listeners = new ListenerList<>();

	protected void fireObjectTerminated(final Exception e) {
		Thread thread = new Thread(() -> {
			for (IDbgpTerminationListener listener : listeners) {
				listener.objectTerminated(DbgpTermination.this, e);
			}
		});

		thread.start();
	}

	@Override
	public void addTerminationListener(IDbgpTerminationListener listener) {
		listeners.add(listener);

	}

	@Override
	public void removeTerminationListener(IDbgpTerminationListener listener) {
		listeners.remove(listener);
	}
}
