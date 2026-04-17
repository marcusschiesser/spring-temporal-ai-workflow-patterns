package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowSmokeTest extends WorkflowTestSupport {

	@Test
	void completesOneEndToEndRunPerPattern() {
		startWithActivities(new Activities(), SequentialWorkflowImpl.class, ParallelWorkflowImpl.class, RoutingWorkflowImpl.class, EvaluatorWorkflowImpl.class, OrchestratorWorkflowImpl.class);

		SequentialWorkflow sequential = workflowClient.newWorkflowStub(
			SequentialWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);
		ParallelWorkflow parallel = workflowClient.newWorkflowStub(
			ParallelWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);
		RoutingWorkflow routing = workflowClient.newWorkflowStub(
			RoutingWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);
		EvaluatorWorkflow evaluator = workflowClient.newWorkflowStub(
			EvaluatorWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);
		OrchestratorWorkflow orchestrator = workflowClient.newWorkflowStub(
			OrchestratorWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		assertThat(sequential.run(new SequentialWorkflow.Input("topic")).finalCopy()).isNotBlank();
		assertThat(parallel.run(new ParallelWorkflow.Input("code")).summary()).isNotBlank();
		assertThat(routing.run(new RoutingWorkflow.Input("query")).response()).isNotBlank();
		assertThat(evaluator.run(new EvaluatorWorkflow.Input("text", "es", 8, 2)).finalTranslation()).isNotBlank();
		assertThat(orchestrator.run(new OrchestratorWorkflow.Input("feature")).changes()).hasSize(1);
	}

	private static class Activities extends UnsupportedActivities {
		@Override
		public String createMarketingCopy(String topic) {
			return "copy";
		}

		@Override
		public SequentialWorkflow.QualityMetrics evaluateMarketingCopy(String copy) {
			return new SequentialWorkflow.QualityMetrics(true, 8, 8);
		}

		@Override
		public ParallelWorkflow.SecurityReview reviewSecurity(String code) {
			return new ParallelWorkflow.SecurityReview(List.of("issue"), ParallelWorkflow.RiskLevel.LOW, List.of("fix"));
		}

		@Override
		public ParallelWorkflow.PerformanceReview reviewPerformance(String code) {
			return new ParallelWorkflow.PerformanceReview(List.of("issue"), ParallelWorkflow.ImpactLevel.LOW, List.of("fix"));
		}

		@Override
		public ParallelWorkflow.MaintainabilityReview reviewMaintainability(String code) {
			return new ParallelWorkflow.MaintainabilityReview(List.of("issue"), 8, List.of("fix"));
		}

		@Override
		public String summarizeReviews(ParallelWorkflow.ReviewBundle reviewBundle) {
			return "summary";
		}

		@Override
		public RoutingWorkflow.Classification classifyQuery(String query) {
			return new RoutingWorkflow.Classification("reason", RoutingWorkflow.QueryType.GENERAL, RoutingWorkflow.Complexity.SIMPLE);
		}

		@Override
		public String answerQuery(RoutingWorkflow.ResponseRequest request) {
			return "response";
		}

		@Override
		public String translate(EvaluatorWorkflow.TranslationRequest request) {
			return "translation";
		}

		@Override
		public EvaluatorWorkflow.Evaluation evaluateTranslation(EvaluatorWorkflow.EvaluationRequest request) {
			return new EvaluatorWorkflow.Evaluation(9, true, true, true, List.of(), List.of());
		}

		@Override
		public String improveTranslation(EvaluatorWorkflow.ImprovementRequest request) {
			return "improved";
		}

		@Override
		public OrchestratorWorkflow.ImplementationPlan planImplementation(String featureRequest) {
			return new OrchestratorWorkflow.ImplementationPlan(
				List.of(new OrchestratorWorkflow.PlannedFile("purpose", "file", OrchestratorWorkflow.ChangeType.CREATE)),
				OrchestratorWorkflow.Complexity.LOW
			);
		}

		@Override
		public OrchestratorWorkflow.FileChangeProposal implementFileChange(OrchestratorWorkflow.FileTask fileTask) {
			return new OrchestratorWorkflow.FileChangeProposal("done", "code");
		}
	}
}
