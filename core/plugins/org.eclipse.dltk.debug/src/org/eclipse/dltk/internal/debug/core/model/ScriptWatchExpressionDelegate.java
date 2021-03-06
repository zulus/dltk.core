/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationEngine;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationListener;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;

public class ScriptWatchExpressionDelegate implements IWatchExpressionDelegate {
	protected static class ListenerAdpater
			implements IScriptEvaluationListener {
		protected final IWatchExpressionListener listener;

		public ListenerAdpater(IWatchExpressionListener listener) {
			this.listener = listener;
		}

		@Override
		public void evaluationComplete(IScriptEvaluationResult result) {
			listener.watchEvaluationFinished(
					new ScriptWatchExpressionResult(result));
		}
	}

	protected static IScriptThread getScriptThread(Object context) {
		if (context instanceof IScriptThread) {
			return (IScriptThread) context;
		} else if (context instanceof IScriptStackFrame) {
			return (IScriptThread) ((IScriptStackFrame) context).getThread();
		}

		return null;
	}

	protected static IScriptStackFrame getStackFrame(IDebugElement context) {
		try {
			if (context instanceof IScriptThread) {
				IStackFrame[] frames = ((IScriptThread) context)
						.getStackFrames();
				if (frames.length > 0)
					return (IScriptStackFrame) frames[0];
			} else if (context instanceof IScriptStackFrame) {
				return (IScriptStackFrame) context;
			}
		} catch (DebugException e) {
		}

		return null;
	}

	@Override
	public void evaluateExpression(String expression, IDebugElement context,
			IWatchExpressionListener listener) {

		IScriptThread thread = getScriptThread(context);
		IScriptStackFrame frame = getStackFrame(context);
		if (thread != null && frame != null) {
			IScriptEvaluationEngine engine = thread.getEvaluationEngine();
			if (engine != null) {
				engine.asyncEvaluate(prepareExpression(expression), frame,
						createListener(listener, expression));
				return;
			}
		}
		listener.watchEvaluationFinished(
				new NoWatchExpressionResult(expression));
	}

	protected String prepareExpression(String expression) {
		return expression;
	}

	protected ListenerAdpater createListener(IWatchExpressionListener listener,
			String expression) {
		return new ListenerAdpater(listener);
	}
}
