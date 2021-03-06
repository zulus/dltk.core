/*******************************************************************************
 * Copyright (c) 2016, 2017 Jae Gangemi and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jae Gangemi - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Delegates preferences lookup to the {@link IPreferencesService preference
 * service}
 *
 * <p>
 * The following scopes are searched for the preference value:
 * </p>
 *
 * <ul>
 * <li>ProjectScope (if project != null)</li>
 * <li>InstanceScope</li>
 * <li>DefaultScope</li>
 * </ul>
 *
 * <p>
 * Plugins should include an implementation of the
 * {@link org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer} to
 * ensure the <code>DefaultScope</code> has been properly initialized.
 * </p>
 */
public class PreferencesLookupDelegate implements IPreferencesLookupDelegate {

	private IPreferencesService service;
	private IScopeContext[] contexts;

	protected IScopeContext getTopScopeContext() {
		return contexts[0];
	}

	/**
	 * Creates a new delegate instance
	 *
	 * <p>
	 * A project may be specified to retrieve project specific value.
	 * </p>
	 *
	 * @param project
	 *            project reference, may be <code>null</code>
	 */
	public PreferencesLookupDelegate(IProject project) {
		this.service = Platform.getPreferencesService();
		this.contexts = getLookupScopes(project);
	}

	public PreferencesLookupDelegate(IScriptProject scriptProject) {
		this((scriptProject == null) ? null : scriptProject.getProject());
	}

	/**
	 * Returns a string preference value
	 *
	 * @param qualifier
	 *            preference key qualifier (plugin id)
	 * @param key
	 *            preference key
	 *
	 * @return preference value or an empty string if the preference is not
	 *         defined
	 */
	@Override
	public String getString(String qualifier, String key) {
		return service.getString(qualifier, key, "", contexts); //$NON-NLS-1$
	}

	/**
	 * Returns a int preference value
	 *
	 * @param qualifier
	 *            preference key qualifier (plugin id)
	 * @param key
	 *            preference key
	 *
	 * @return preference value or an empty string if the preference is not
	 *         defined
	 */
	@Override
	public int getInt(String qualifier, String key) {
		return service.getInt(qualifier, key, 0, contexts);
	}

	/**
	 * Returns a boolean preference value
	 *
	 * @param qualifier
	 *            preference key qualifier (plugin id)
	 * @param key
	 *            preference key
	 *
	 * @return preference value or <code>false</code> if the preference is not
	 *         defined
	 */
	@Override
	public boolean getBoolean(String qualifier, String key) {
		return service.getBoolean(qualifier, key, false, contexts);
	}

	private IScopeContext[] getLookupScopes(IProject project) {
		List<IScopeContext> list = new ArrayList<>(3);
		list.add(InstanceScope.INSTANCE);
		list.add(DefaultScope.INSTANCE);

		if (project != null) {
			list.add(0, new ProjectScope(project));
		}

		return list.toArray(new IScopeContext[list.size()]);
	}

}
