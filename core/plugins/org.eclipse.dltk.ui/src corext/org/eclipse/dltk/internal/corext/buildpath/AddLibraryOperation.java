/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.buildpath;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.internal.ui.wizards.buildpath.newsourcepage.BuildpathModifierQueries.IAddLibrariesQuery;
import org.eclipse.dltk.internal.ui.wizards.buildpath.newsourcepage.DialogPackageExplorerActionGroup;


/**
 * Operation to add libraries to the buildpath
 *
 */
public class AddLibraryOperation extends BuildpathModifierOperation {

    /**
     * Constructor
     *
     * @param listener a <code>IBuildpathModifierListener</code> that is notified about
     * changes on buildpath entries or <code>null</code> if no such notification is
     * necessary.
     * @param informationProvider a provider to offer information to the action
     *
     * @see IBuildpathInformationProvider
     * @see BuildpathModifier
     */
    public AddLibraryOperation(IBuildpathModifierListener listener, IBuildpathInformationProvider informationProvider) {
        super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddLibCP_tooltip, IBuildpathInformationProvider.ADD_LIB_TO_BP);
    }

    /**
     * Method which runs the actions with a progress monitor.<br>
     *
     * This operation requires the following query from the provider:
     * <li>IAddLibrariesQuery</li>
     *
     * @param monitor a progress monitor, can be <code>null</code>
     */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
        List result= null;
        fException= null;
        try {
            IScriptProject project= fInformationProvider.getScriptProject();
            IAddLibrariesQuery query= fInformationProvider.getLibrariesQuery();
            result= addLibraries(query, project, monitor);
        } catch (CoreException e) {
            fException= e;
            result= null;
        }
       super.handleResult(result, monitor);
    }

    /**
     * This particular operation is always valid.
     *
     * @param elements a list of elements
     * @param types an array of types for each element, that is,
     * the type at position 'i' belongs to the selected element
     * at position 'i'
     *
     * @return <code>true</code> if the operation can be
     * executed on the provided list of elements, <code>
     * false</code> otherwise.
     * @throws ModelException
     */
	@Override
	public boolean isValid(List elements, int[] types) throws ModelException {
        return types.length == 1 && types[0] == DialogPackageExplorerActionGroup.SCRIPT_PROJECT;
    }

	@Override
	public String getDescription(int type) {
        return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_toBuildpath_library;
    }

}
