package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class RoutingWorkflowImpl implements RoutingWorkflow {

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
		Classification classification = activities.classifyQuery(input.query());
		String selectedModel = classification.complexity() == Complexity.SIMPLE ? "small" : "strong";
		String systemPrompt = switch (classification.type()) {
			case GENERAL -> "You are an expert customer service agent handling general inquiries.";
			case REFUND ->
				"You are a customer service agent specializing in refund requests. Follow policy and collect the required details.";
			case TECHNICAL ->
				"You are a technical support specialist with deep product knowledge. Focus on clear step-by-step troubleshooting.";
		};
		String response = activities.answerQuery(new ResponseRequest(input.query(), selectedModel, systemPrompt));
		return new Output(classification, selectedModel, systemPrompt, response);
	}
}
