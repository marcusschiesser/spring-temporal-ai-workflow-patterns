package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EvaluatorWorkflowImpl implements EvaluatorWorkflow {

	private final AiActivities activities = Workflow.newActivityStub(
		AiActivities.class,
		ActivityOptions.newBuilder()
			.setStartToCloseTimeout(Duration.ofMinutes(2))
			.setRetryOptions(
				RetryOptions.newBuilder()
					.setInitialInterval(Duration.ofSeconds(1))
					.setMaximumInterval(Duration.ofSeconds(10))
					.setMaximumAttempts(3)
					.build()
			)
			.build()
	);

	@Override
	public Output run(Input input) {
		List<IterationResult> iterations = new ArrayList<>();
		String currentTranslation = activities.translate(new TranslationRequest(input.sourceText(), input.targetLanguage()));
		StopReason stopReason = StopReason.MAX_ITERATIONS_REACHED;

		for (int cycle = 1; cycle <= input.maxIterations(); cycle++) {
			Evaluation evaluation = activities.evaluateTranslation(
				new EvaluationRequest(input.sourceText(), currentTranslation)
			);
			iterations.add(new IterationResult(cycle, currentTranslation, evaluation));

			boolean done = evaluation.qualityScore() >= input.qualityThreshold()
				&& evaluation.preservesTone()
				&& evaluation.preservesNuance()
				&& evaluation.culturallyAccurate();
			if (done) {
				stopReason = StopReason.QUALITY_THRESHOLD_MET;
				break;
			}

			if (cycle < input.maxIterations()) {
				List<String> feedback = new ArrayList<>(evaluation.specificIssues());
				feedback.addAll(evaluation.improvementSuggestions());
				currentTranslation = activities.improveTranslation(
					new ImprovementRequest(input.sourceText(), currentTranslation, feedback)
				);
			}
		}

		return new Output(currentTranslation, iterations, iterations.size(), stopReason);
	}
}
