/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.changes;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.reorg.INewNameQuery;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.ltk.core.refactoring.Change;

public class CopyScriptFolderChange extends PackageReorgChange {

	public CopyScriptFolderChange(IScriptFolder pack, IProjectFragment dest, INewNameQuery nameQuery) {
		super(pack, dest, nameQuery);
	}

	@Override
	protected Change doPerformReorg(IProgressMonitor pm) throws ModelException, OperationCanceledException {
		getPackage().copy(getDestination(), null, getNewName(), true, pm);
		return null;
	}

	@Override
	public String getName() {
		String packageName = ScriptElementLabels.getDefault().getElementLabel(getPackage(),
				ScriptElementLabels.ALL_DEFAULT);
		String destinationName = ScriptElementLabels.getDefault().getElementLabel(getDestination(),
				ScriptElementLabels.ALL_DEFAULT);
		return MessageFormat.format(RefactoringCoreMessages.CopyPackageChange_copy, packageName, destinationName);
	}
}
