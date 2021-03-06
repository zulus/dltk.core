/*******************************************************************************
 * Copyright (c) 2010, 2017 xored software, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.logconsole;

import org.eclipse.dltk.internal.logconsole.LogConsoleManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class LogConsolePlugin implements BundleActivator {

	private static LogConsolePlugin plugin = null;

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

	private LogConsoleManager consoleManager;

	public static ILogConsoleManager getConsoleManager() {
		if (plugin.consoleManager == null) {
			plugin.consoleManager = new LogConsoleManager();
		}
		return plugin.consoleManager;
	}

}
