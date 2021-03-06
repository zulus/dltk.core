/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.breakpoints;

import org.eclipse.dltk.dbgp.breakpoints.IDbgpBreakpoint;

public class DbgpBreakpoint implements IDbgpBreakpoint {
	private final String id;

	private final boolean enabled;

	// Number of breakpoint hits
	private final int hitCount;

	// Hit value for hit condition
	private final int hitValue;

	// Hit condition
	private final int hitCondition;

	protected int convertHitCondition(String s) {
		if (">=".equals(s)) { //$NON-NLS-1$
			return HIT_CONDITION_GREATER_OR_EQUAL;
		} else if ("==".equals(s)) { //$NON-NLS-1$
			return HIT_CONDITION_EQUAL;
		} else if ("%".equals(s)) { //$NON-NLS-1$
			return HIT_CONDITION_MULTIPLE;
		} else if ("".equals(s)) { //$NON-NLS-1$
			return HIT_NOT_SET;
		}

		throw new IllegalArgumentException(
				Messages.DbgpBreakpoint_invalidHitConditionValue);
	}

	public DbgpBreakpoint(String id, boolean enabled, int hitValue,
			int hitCount, String hitCondition) {
		this.id = id;
		this.enabled = enabled;
		this.hitValue = hitValue;
		this.hitCount = hitCount;
		this.hitCondition = convertHitCondition(hitCondition);
	}

	@Override
	public int getHitCondition() {
		return hitCondition;
	}

	@Override
	public int getHitCount() {
		return hitCount;
	}

	@Override
	public int getHitValue() {
		return hitValue;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
