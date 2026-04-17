package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OrchestratorWorkflowImpl implements OrchestratorWorkflow {

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
		ImplementationPlan plan = activities.planImplementation(input.featureRequest());
		List<Promise<FileChangeResult>> promises = new ArrayList<>();
		for (PlannedFile file : plan.files()) {
			promises.add(
				Async.function(() -> {
					FileChangeProposal proposal = activities.implementFileChange(
						new FileTask(file.filePath(), file.purpose(), file.changeType(), input.featureRequest())
					);
					return new FileChangeResult(file.filePath(), file.changeType(), proposal);
				})
			);
		}

		List<FileChangeResult> changes = promises.stream().map(Promise::get).toList();
		String summary = "Planned %d file changes with %s complexity."
			.formatted(changes.size(), plan.estimatedComplexity().name().toLowerCase());
		return new Output(plan, changes, summary);
	}
}
