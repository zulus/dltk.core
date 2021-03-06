/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.testing.model;

import org.eclipse.dltk.testing.ITestingClient;

/**
 * Represents a test run session.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 3.3
 */
public interface ITestRunSession extends ITestElementContainer {

	/**
	 * Returns the name of the test run. The name is the name of the launch
	 * configuration use to run this test.
	 * 
	 * @return returns the test run name
	 */
	public String getTestRunName();

	public ITestingClient getTestRunnerClient();

	public void setTotalCount(int id);

	public ITestElement[] getFailedTestElements(ITestElementPredicate predicate);

}
