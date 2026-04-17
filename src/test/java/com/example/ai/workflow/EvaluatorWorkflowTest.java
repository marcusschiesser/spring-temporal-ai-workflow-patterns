package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluatorWorkflowTest extends WorkflowTestSupport {

	@Test
	void stopsWhenQualityThresholdIsMet() {
		startWithActivities(new PassingActivities(), EvaluatorWorkflowImpl.class);
		EvaluatorWorkflow workflow = workflowClient.newWorkflowStub(
			EvaluatorWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		EvaluatorWorkflow.Output output = workflow.run(new EvaluatorWorkflow.Input("hello", "es", 8, 3));

		assertThat(output.stopReason()).isEqualTo(EvaluatorWorkflow.StopReason.QUALITY_THRESHOLD_MET);
		assertThat(output.completedIterations()).isEqualTo(1);
	}

	@Test
	void stopsWhenMaxIterationsIsReached() {
		startWithActivities(new ImprovingActivities(), EvaluatorWorkflowImpl.class);
		EvaluatorWorkflow workflow = workflowClient.newWorkflowStub(
			EvaluatorWorkflow.class,
			io.temporal.client.WorkflowOptions.newBuilder().setTaskQueue("test-task-queue").build()
		);

		EvaluatorWorkflow.Output output = workflow.run(new EvaluatorWorkflow.Input("hello", "es", 8, 3));

		assertThat(output.stopReason()).isEqualTo(EvaluatorWorkflow.StopReason.MAX_ITERATIONS_REACHED);
		assertThat(output.completedIterations()).isEqualTo(3);
	}

	private static class PassingActivities extends UnsupportedActivities {
		@Override
		public String translate(EvaluatorWorkflow.TranslationRequest request) {
			return "hola";
		}

		@Override
		public EvaluatorWorkflow.Evaluation evaluateTranslation(EvaluatorWorkflow.EvaluationRequest request) {
			return new EvaluatorWorkflow.Evaluation(9, true, true, true, List.of(), List.of());
		}
	}

	private static class ImprovingActivities extends UnsupportedActivities {
		private int count;

		@Override
		public String translate(EvaluatorWorkflow.TranslationRequest request) {
			return "hola";
		}

		@Override
		public EvaluatorWorkflow.Evaluation evaluateTranslation(EvaluatorWorkflow.EvaluationRequest request) {
			count++;
			return new EvaluatorWorkflow.Evaluation(6, count > 3, false, false, List.of("tone"), List.of("improve nuance"));
		}

		@Override
		public String improveTranslation(EvaluatorWorkflow.ImprovementRequest request) {
			return request.currentTranslation() + "!";
		}
	}
}
