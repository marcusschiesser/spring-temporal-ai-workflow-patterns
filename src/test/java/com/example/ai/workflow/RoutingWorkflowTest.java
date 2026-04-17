package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoutingWorkflowTest extends WorkflowTestSupport {

	@Test
	void routesSimpleGeneralQueryToSmallModelProfile() {
		startWithActivities(new SimpleGeneralActivities(), RoutingWorkflowImpl.class);
		RoutingWorkflow workflow = workflowClient.newWorkflowStub(
			RoutingWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		RoutingWorkflow.Output output = workflow.run(new RoutingWorkflow.Input("What are your support hours?"));

		assertThat(output.selectedModel()).isEqualTo("small");
		assertThat(output.classification().type()).isEqualTo(RoutingWorkflow.QueryType.GENERAL);
	}

	@Test
	void routesComplexTechnicalQueryToStrongModelProfile() {
		startWithActivities(new ComplexTechnicalActivities(), RoutingWorkflowImpl.class);
		RoutingWorkflow workflow = workflowClient.newWorkflowStub(
			RoutingWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		RoutingWorkflow.Output output = workflow.run(new RoutingWorkflow.Input("Deployment hangs and retries package resolution"));

		assertThat(output.selectedModel()).isEqualTo("strong");
		assertThat(output.classification().type()).isEqualTo(RoutingWorkflow.QueryType.TECHNICAL);
	}

	private static class SimpleGeneralActivities extends UnsupportedActivities {
		@Override
		public RoutingWorkflow.Classification classifyQuery(String query) {
			return new RoutingWorkflow.Classification("Simple FAQ", RoutingWorkflow.QueryType.GENERAL, RoutingWorkflow.Complexity.SIMPLE);
		}

		@Override
		public String answerQuery(RoutingWorkflow.ResponseRequest request) {
			return request.model() + ":" + request.query();
		}
	}

	private static class ComplexTechnicalActivities extends UnsupportedActivities {
		@Override
		public RoutingWorkflow.Classification classifyQuery(String query) {
			return new RoutingWorkflow.Classification("Needs troubleshooting", RoutingWorkflow.QueryType.TECHNICAL, RoutingWorkflow.Complexity.COMPLEX);
		}

		@Override
		public String answerQuery(RoutingWorkflow.ResponseRequest request) {
			return request.model() + ":" + request.query();
		}
	}
}
