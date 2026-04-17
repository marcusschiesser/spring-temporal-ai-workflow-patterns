package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SequentialWorkflowImpl implements SequentialWorkflow {

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
		String originalCopy = activities.createMarketingCopy(input.topic());
		QualityMetrics qualityMetrics = activities.evaluateMarketingCopy(originalCopy);

		boolean regenerate = !qualityMetrics.hasCallToAction()
			|| qualityMetrics.emotionalAppeal() < 7
			|| qualityMetrics.clarity() < 7;

		if (!regenerate) {
			return new Output(originalCopy, qualityMetrics, originalCopy, false);
		}

		List<String> goals = new ArrayList<>();
		if (!qualityMetrics.hasCallToAction()) {
			goals.add("- A clear call to action");
		}
		if (qualityMetrics.emotionalAppeal() < 7) {
			goals.add("- Stronger emotional appeal");
		}
		if (qualityMetrics.clarity() < 7) {
			goals.add("- Improved clarity and directness");
		}

		String finalCopy = activities.improveMarketingCopy(new MarketingCopyRevisionRequest(originalCopy, goals));
		return new Output(originalCopy, qualityMetrics, finalCopy, true);
	}
}
