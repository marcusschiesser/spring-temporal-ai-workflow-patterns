package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import io.temporal.client.WorkflowClient;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;

abstract class WorkflowTestSupport {

	protected final TestWorkflowEnvironment environment = TestWorkflowEnvironment.newInstance();
	protected final Worker worker = environment.newWorker("test-task-queue");
	protected final WorkflowClient workflowClient = environment.getWorkflowClient();

	protected void startWithActivities(AiActivities activities, Class<?>... workflowTypes) {
		worker.registerWorkflowImplementationTypes(workflowTypes);
		worker.registerActivitiesImplementations(activities);
		environment.start();
	}

	@AfterEach
	void tearDown() {
		environment.close();
	}
}
