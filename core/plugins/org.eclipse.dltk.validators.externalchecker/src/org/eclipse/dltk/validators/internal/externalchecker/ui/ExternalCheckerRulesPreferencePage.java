/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.externalchecker.ui;

import org.eclipse.dltk.validators.internal.externalchecker.core.CustomWildcard;
import org.eclipse.dltk.validators.internal.externalchecker.core.ExternalCheckerPlugin;
import org.eclipse.dltk.validators.internal.externalchecker.core.ExternalCheckerWildcardManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ExternalCheckerRulesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	// private static final String EXTERNALCHECKER_PREFERENCE_PAGE =
	// ValidatorsUI.PLUGIN_ID + ".ExternalCheckerPreferencePage";

	private ExternalCheckerRulesBlock fRulesBlock;

	public ExternalCheckerRulesPreferencePage() {
		super();
		setTitle(Messages.ExternalCheckerRulesPreferencePage_externalCheckerRules);
		setDescription(Messages.ExternalCheckerRulesPreferencePage_externalCheckerRules);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite ancestor) {
		initializeDialogUnits(ancestor);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);

		fRulesBlock = createRulesBlock();
		fRulesBlock.createControl(ancestor);
		Control control = fRulesBlock.getControl();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);

		applyDialogFont(ancestor);
		return ancestor;
	}

	private ExternalCheckerRulesBlock createRulesBlock() {
		return new ExternalCheckerRulesBlock();
	}

	@Override
	public boolean performOk() {

		CustomWildcardsList wlist = fRulesBlock.getWlist();

		CustomWildcard[] customWildcards = wlist.getWildcards();
		ExternalCheckerWildcardManager.storeWildcards(customWildcards);
		return true;
	}

	@Override
	protected void performDefaults() {
		// System.out.println("Defaults");
		String xmlString = ExternalCheckerWildcardManager.getDefaultWildcards();
		// ValidatorsCore.getDefault().getPluginPreferences().setDefault(
		// "wildcards",
		// xmlString);
		ExternalCheckerPlugin.getDefault().getPluginPreferences().setValue(ExternalCheckerWildcardManager.WILDCARDS,
				xmlString);
		ExternalCheckerPlugin.getDefault().savePluginPreferences();
		fRulesBlock.removeAll();
		fRulesBlock.loadWildcards();
	}
}
