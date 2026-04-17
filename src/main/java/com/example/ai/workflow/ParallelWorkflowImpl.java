package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class ParallelWorkflowImpl implements ParallelWorkflow {

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
		Promise<SecurityReview> securityPromise = Async.function(activities::reviewSecurity, input.code());
		Promise<PerformanceReview> performancePromise = Async.function(activities::reviewPerformance, input.code());
		Promise<MaintainabilityReview> maintainabilityPromise =
			Async.function(activities::reviewMaintainability, input.code());

		SecurityReview securityReview = securityPromise.get();
		PerformanceReview performanceReview = performancePromise.get();
		MaintainabilityReview maintainabilityReview = maintainabilityPromise.get();
		String summary = activities.summarizeReviews(
			new ReviewBundle(securityReview, performanceReview, maintainabilityReview)
		);

		return new Output(securityReview, performanceReview, maintainabilityReview, summary);
	}
}
