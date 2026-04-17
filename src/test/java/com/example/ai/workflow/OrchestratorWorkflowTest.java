package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrchestratorWorkflowTest extends WorkflowTestSupport {

	@Test
	void fansOutPlannedFileChangesAndAggregatesResults() {
		startWithActivities(new Activities(), OrchestratorWorkflowImpl.class);
		OrchestratorWorkflow workflow = workflowClient.newWorkflowStub(
			OrchestratorWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		OrchestratorWorkflow.Output output = workflow.run(new OrchestratorWorkflow.Input("Add dark mode"));

		assertThat(output.plan().files()).hasSize(2);
		assertThat(output.changes()).hasSize(2);
		assertThat(output.summary()).contains("2 file changes");
	}

	private static class Activities extends UnsupportedActivities {
		@Override
		public OrchestratorWorkflow.ImplementationPlan planImplementation(String featureRequest) {
			return new OrchestratorWorkflow.ImplementationPlan(
				List.of(
					new OrchestratorWorkflow.PlannedFile("Add toggle", "src/ui/Toggle.java", OrchestratorWorkflow.ChangeType.CREATE),
					new OrchestratorWorkflow.PlannedFile("Update theme service", "src/theme/ThemeService.java", OrchestratorWorkflow.ChangeType.MODIFY)
				),
				OrchestratorWorkflow.Complexity.MEDIUM
			);
		}

		@Override
		public OrchestratorWorkflow.FileChangeProposal implementFileChange(OrchestratorWorkflow.FileTask fileTask) {
			return new OrchestratorWorkflow.FileChangeProposal("Changed " + fileTask.filePath(), "// code");
		}
	}
}
