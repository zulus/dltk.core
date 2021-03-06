/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.workingsets;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * Menu contribution item which shows and lets select a working set.
 */
public class WorkingSetMenuContributionItem extends ContributionItem {

	private int fId;
	private IWorkingSet fWorkingSet;
	private WorkingSetFilterActionGroup fActionGroup;
	private Image fImage;

	/**
	 * Constructor for WorkingSetMenuContributionItem.
	 *
	 * @param id the id
	 * @param actionGroup the action group
	 * @param workingSet the working set
	 */
	public WorkingSetMenuContributionItem(int id, WorkingSetFilterActionGroup actionGroup, IWorkingSet workingSet) {
		super(getId(id));
		fId= id;
		fActionGroup= actionGroup;
		fWorkingSet= workingSet;
	}

	/*
	 * Overrides method from ContributionItem.
	 */
	@Override
	public void fill(Menu menu, int index) {
		MenuItem mi= new MenuItem(menu, SWT.RADIO, index);

		String name= fWorkingSet.getLabel();

		mi.setText("&" + fId + " " + name);  //$NON-NLS-1$  //$NON-NLS-2$
		if (fImage == null) {
			ImageDescriptor imageDescriptor= fWorkingSet.getImageDescriptor();
			if (imageDescriptor != null)
				fImage= imageDescriptor.createImage();
		}
		mi.setImage(fImage);
		mi.setSelection(fWorkingSet.equals(fActionGroup.getWorkingSet()));
		mi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkingSetManager manager= PlatformUI.getWorkbench().getWorkingSetManager();
				fActionGroup.setWorkingSet(fWorkingSet, true);
				manager.addRecentWorkingSet(fWorkingSet);
			}
		});
	}

	@Override
	public void dispose() {
		if (fImage != null && !fImage.isDisposed())
			fImage.dispose();
		fImage= null;

		super.dispose();
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	static String getId(int id) {
		return WorkingSetMenuContributionItem.class.getName() + "." + id;  //$NON-NLS-1$
	}
}
