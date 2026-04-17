package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SequentialWorkflowTest extends WorkflowTestSupport {

	@Test
	void regeneratesWhenQualityFails() {
		startWithActivities(new RegeneratingActivities(), SequentialWorkflowImpl.class);
		SequentialWorkflow workflow = workflowClient.newWorkflowStub(
			SequentialWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		SequentialWorkflow.Output output = workflow.run(new SequentialWorkflow.Input("Temporal AI workflows"));

		assertThat(output.regenerated()).isTrue();
		assertThat(output.finalCopy()).isEqualTo("Improved copy");
	}

	@Test
	void returnsOriginalWhenQualityPasses() {
		startWithActivities(new PassingActivities(), SequentialWorkflowImpl.class);
		SequentialWorkflow workflow = workflowClient.newWorkflowStub(
			SequentialWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		SequentialWorkflow.Output output = workflow.run(new SequentialWorkflow.Input("Temporal AI workflows"));

		assertThat(output.regenerated()).isFalse();
		assertThat(output.finalCopy()).isEqualTo("Original copy");
	}

	private static class RegeneratingActivities extends BaseSequentialActivities {
		@Override
		public SequentialWorkflow.QualityMetrics evaluateMarketingCopy(String copy) {
			return new SequentialWorkflow.QualityMetrics(false, 5, 6);
		}

		@Override
		public String improveMarketingCopy(SequentialWorkflow.MarketingCopyRevisionRequest request) {
			return "Improved copy";
		}
	}

	private static class PassingActivities extends BaseSequentialActivities {
		@Override
		public SequentialWorkflow.QualityMetrics evaluateMarketingCopy(String copy) {
			return new SequentialWorkflow.QualityMetrics(true, 8, 8);
		}
	}

	private abstract static class BaseSequentialActivities implements AiActivities {
		@Override
		public String createMarketingCopy(String topic) {
			return "Original copy";
		}

		@Override
		public String improveMarketingCopy(SequentialWorkflow.MarketingCopyRevisionRequest request) {
			return request.originalCopy();
		}

		@Override
		public ParallelWorkflow.SecurityReview reviewSecurity(String code) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ParallelWorkflow.PerformanceReview reviewPerformance(String code) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ParallelWorkflow.MaintainabilityReview reviewMaintainability(String code) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String summarizeReviews(ParallelWorkflow.ReviewBundle reviewBundle) {
			throw new UnsupportedOperationException();
		}

		@Override
		public RoutingWorkflow.Classification classifyQuery(String query) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String answerQuery(RoutingWorkflow.ResponseRequest request) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String translate(EvaluatorWorkflow.TranslationRequest request) {
			throw new UnsupportedOperationException();
		}

		@Override
		public EvaluatorWorkflow.Evaluation evaluateTranslation(EvaluatorWorkflow.EvaluationRequest request) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String improveTranslation(EvaluatorWorkflow.ImprovementRequest request) {
			throw new UnsupportedOperationException();
		}

		@Override
		public OrchestratorWorkflow.ImplementationPlan planImplementation(String featureRequest) {
			throw new UnsupportedOperationException();
		}

		@Override
		public OrchestratorWorkflow.FileChangeProposal implementFileChange(OrchestratorWorkflow.FileTask fileTask) {
			throw new UnsupportedOperationException();
		}
	}
}
