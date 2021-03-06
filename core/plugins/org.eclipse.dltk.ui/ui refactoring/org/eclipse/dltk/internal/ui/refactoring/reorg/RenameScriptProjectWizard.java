/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.refactoring.reorg;

import org.eclipse.dltk.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.ltk.core.refactoring.Refactoring;


public class RenameScriptProjectWizard extends RenameRefactoringWizard {
	
	public RenameScriptProjectWizard(Refactoring refactoring) {
		super(refactoring,
			RefactoringMessages.RenameScriptProject_defaultPageTitle, 
			RefactoringMessages.RenameScriptProject_inputPage_description, 
			DLTKPluginImages.DESC_WIZBAN_REFACTOR,
			""/*IScriptHelpContextIds.RENAME_SCRIPT_PROJECT_WIZARD_PAGE*/); //$NON-NLS-1$
	}
}
