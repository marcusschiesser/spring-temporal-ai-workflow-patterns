package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParallelWorkflowTest extends WorkflowTestSupport {

	@Test
	void aggregatesParallelReviewResults() {
		startWithActivities(new Activities(), ParallelWorkflowImpl.class);
		ParallelWorkflow workflow = workflowClient.newWorkflowStub(
			ParallelWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		ParallelWorkflow.Output output = workflow.run(new ParallelWorkflow.Input("class Demo {}"));

		assertThat(output.securityReview().riskLevel()).isEqualTo(ParallelWorkflow.RiskLevel.HIGH);
		assertThat(output.performanceReview().impact()).isEqualTo(ParallelWorkflow.ImpactLevel.MEDIUM);
		assertThat(output.maintainabilityReview().qualityScore()).isEqualTo(6);
		assertThat(output.summary()).contains("3 findings");
	}

	private static class Activities extends UnsupportedActivities {
		@Override
		public ParallelWorkflow.SecurityReview reviewSecurity(String code) {
			return new ParallelWorkflow.SecurityReview(List.of("SQL injection"), ParallelWorkflow.RiskLevel.HIGH, List.of("Use parameters"));
		}

		@Override
		public ParallelWorkflow.PerformanceReview reviewPerformance(String code) {
			return new ParallelWorkflow.PerformanceReview(List.of("N+1 requests"), ParallelWorkflow.ImpactLevel.MEDIUM, List.of("Batch requests"));
		}

		@Override
		public ParallelWorkflow.MaintainabilityReview reviewMaintainability(String code) {
			return new ParallelWorkflow.MaintainabilityReview(List.of("Long method"), 6, List.of("Extract helpers"));
		}

		@Override
		public String summarizeReviews(ParallelWorkflow.ReviewBundle reviewBundle) {
			return "3 findings and 3 actions";
		}
	}
}
