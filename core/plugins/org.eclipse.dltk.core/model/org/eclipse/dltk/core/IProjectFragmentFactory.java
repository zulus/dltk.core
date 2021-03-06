/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core;

/**
 * This interface could be used to provide special {@link IProjectFragment}
 * implementations.
 * 
 * Classes implementing this interface should be contributed via
 * <code>org.eclipse.dltk.model/projectFragment</code> extension point.
 * 
 * @since 4.0
 */
public interface IProjectFragmentFactory {

	/**
	 * Creates {@link IProjectFragment} for the specified resolvedEntry if
	 * required
	 * 
	 * @param resolvedEntry
	 * @return project fragment created or <code>null</code> if special
	 *         implementation isn't required
	 */
	IProjectFragment create(IScriptProject project,
			IBuildpathEntry resolvedEntry);

}
