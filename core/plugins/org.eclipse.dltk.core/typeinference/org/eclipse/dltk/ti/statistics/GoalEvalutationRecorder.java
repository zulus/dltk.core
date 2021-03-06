/*******************************************************************************
 * Copyright (c) 2016 xored software, Inc. and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ti.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;

/**
 * Records all evaluation tree including evaluation times
 *
 */
public class GoalEvalutationRecorder implements IEvaluationStatisticsRequestor {

	private IGoal rootRoal;
	private Map goalStats = new HashMap();

	public GoalEvalutationRecorder duplicate() {
		GoalEvalutationRecorder n = new GoalEvalutationRecorder();
		n.rootRoal = rootRoal;
		for (Iterator iterator = goalStats.keySet().iterator(); iterator
				.hasNext();) {
			Object k = iterator.next();
			n.goalStats.put(k, goalStats.get(k));
		}
		return n;
	}

	@Override
	public void evaluationStarted(IGoal rootGoal) {
		reset();
		this.rootRoal = rootGoal;
		this.goalStats.put(rootGoal, new GoalEvaluationStatistics(rootGoal));
	}

	private void reset() {
		this.rootRoal = null;
		this.goalStats = new HashMap();
	}

	private GoalEvaluationStatistics addGoalStatistics(
			GoalEvaluationStatistics parent, IGoal g) {
		GoalEvaluationStatistics s = new GoalEvaluationStatistics(g);
		s.setParentStat(parent);
		goalStats.put(g, s);
		return s;
	}

	private GoalEvaluationStatistics[] createEmptyGoalStatistics(
			GoalEvaluationStatistics parent, IGoal[] subgoals) {
		GoalEvaluationStatistics[] r = new GoalEvaluationStatistics[subgoals.length];
		for (int i = 0; i < subgoals.length; i++) {
			r[i] = addGoalStatistics(parent, subgoals[i]);
		}
		return r;
	}

	@Override
	public void evaluatorInitialized(GoalEvaluator evaluator, IGoal[] subgoals,
			long time) {
		appendStep(evaluator, subgoals, null, time, GoalEvaluationStep.INIT);
	}

	@Override
	public void evaluatorProducedResult(GoalEvaluator evaluator, Object result,
			long time) {
		GoalEvaluationStatistics s = appendStep(evaluator, null, result, time,
				GoalEvaluationStep.RESULT);
		if (s != null) {
			s.setTimeEnd(System.currentTimeMillis());
		}
	}

	@Override
	public void evaluatorReceivedResult(GoalEvaluator evaluator,
			IGoal finishedGoal, IGoal[] subgoals, long time) {
		appendStep(evaluator, subgoals, null, time, GoalEvaluationStep.DEFAULT);
	}

	private GoalEvaluationStatistics appendStep(GoalEvaluator evaluator,
			IGoal[] subgoals, Object result, long time, int kind) {
		IGoal goal = evaluator.getGoal();
		GoalEvaluationStatistics stat = (GoalEvaluationStatistics) this.goalStats
				.get(goal);
		if (stat != null) {
			GoalEvaluationStep step = new GoalEvaluationStep(kind);
			step.setTime(time);
			if (subgoals != null) {
				step
						.setSubgoalsStats(createEmptyGoalStatistics(stat,
								subgoals));
			}
			step.setResult(result);
			stat.getSteps().add(step);
			return stat;
		} else {
			System.err.println("Unknown goal: " + goal); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public void goalEvaluatorAssigned(IGoal goal, GoalEvaluator evaluator) {
		GoalEvaluationStatistics stat = (GoalEvaluationStatistics) this.goalStats
				.get(goal);
		if (stat != null) {
			stat.setEvaluator(evaluator);
		} else {
			System.err.println("Unknown goal: " + goal); //$NON-NLS-1$
		}
	}

	@Override
	public void goalStateChanged(IGoal goal, GoalState state, GoalState oldState) {
		GoalEvaluationStatistics stat = (GoalEvaluationStatistics) this.goalStats
				.get(goal);
		if (stat != null) {
			stat.setState(state);
		} else {
			System.err.println("Unknown goal: " + goal); //$NON-NLS-1$
		}
	}

	public IGoal getRootRoal() {
		return rootRoal;
	}

	public GoalEvaluationStatistics getStatisticsForGoal(IGoal g) {
		return (GoalEvaluationStatistics) this.goalStats.get(g);
	}

}
