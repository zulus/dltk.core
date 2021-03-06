/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *

 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.dltk.core.IModelElementDelta;

/**
 * A simple model element delta that remembers the kind of changes only.
 */
public class SimpleDelta {

	/*
	 * @see IModelElementDelta#getKind()
	 */
	protected int kind = 0;

	/*
	 * @see IModelElementDelta#getFlags()
	 */
	protected int changeFlags = 0;

	/*
	 * Marks this delta as added
	 */
	public void added() {
		this.kind = IModelElementDelta.ADDED;
	}

	/*
	 * Marks this delta as changed with the given change flag
	 */
	public void changed(int flags) {
		this.kind = IModelElementDelta.CHANGED;
		this.changeFlags |= flags;
	}

	/*
	 * @see IModelElementDelta#getFlags()
	 */
	public int getFlags() {
		return this.changeFlags;
	}

	/*
	 * @see IModelElementDelta#getKind()
	 */
	public int getKind() {
		return this.kind;
	}

	/*
	 * Mark this delta has a having a modifiers change
	 */
	public void modifiers() {
		changed(IModelElementDelta.F_MODIFIERS);
	}

	/*
	 * Marks this delta as removed
	 */
	public void removed() {
		this.kind = IModelElementDelta.REMOVED;
		this.changeFlags = 0;
	}

	/*
	 * Mark this delta has a having a super type change
	 */
	public void superTypes() {
		changed(IModelElementDelta.F_SUPER_TYPES);
	}

	protected void toDebugString(StringBuilder buffer) {
		buffer.append("["); //$NON-NLS-1$
		switch (getKind()) {
		case IModelElementDelta.ADDED:
			buffer.append('+');
			break;
		case IModelElementDelta.REMOVED:
			buffer.append('-');
			break;
		case IModelElementDelta.CHANGED:
			buffer.append('*');
			break;
		default:
			buffer.append('?');
			break;
		}
		buffer.append("]: {"); //$NON-NLS-1$
		toDebugString(buffer, getFlags());
		buffer.append("}"); //$NON-NLS-1$
	}

	protected boolean toDebugString(StringBuilder buffer, int flags) {
		boolean prev = false;
		if ((flags & IModelElementDelta.F_MODIFIERS) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("MODIFIERS CHANGED"); //$NON-NLS-1$
			prev = true;
		}
		if ((flags & IModelElementDelta.F_SUPER_TYPES) != 0) {
			if (prev)
				buffer.append(" | "); //$NON-NLS-1$
			buffer.append("SUPER TYPES CHANGED"); //$NON-NLS-1$
			prev = true;
		}
		return prev;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		toDebugString(buffer);
		return buffer.toString();
	}
}
