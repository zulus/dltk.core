/*******************************************************************************
 * Copyright (c) 2008, 2017 xored software, Inc.
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
package org.eclipse.dltk.ui.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.ui.PreferencesAdapter;

/**
 * @deprecated use an implementation of
 *             {@link AbstractConfigurationBlockPropertyAndPreferencePage}
 *
 * @see AbstractTodoTaskOptionsBlock
 */
@Deprecated
public abstract class TodoTaskAbstractPreferencePage extends
		AbstractConfigurationBlockPreferencePage {

	protected abstract Preferences getPluginPreferences();

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(
			OverlayPreferenceStore overlayPreferenceStore) {
		return new TodoTaskConfigurationBlock(getPluginPreferences(),
				overlayPreferenceStore, this);
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(new PreferencesAdapter(getPluginPreferences()));
	}

}
