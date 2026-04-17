package com.example.ai.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface SequentialWorkflow {

	@WorkflowMethod
	Output run(Input input);

	record Input(String topic) {
	}

	record Output(String originalCopy, QualityMetrics qualityMetrics, String finalCopy, boolean regenerated) {
	}

	record QualityMetrics(boolean hasCallToAction, int emotionalAppeal, int clarity) {
	}

	record MarketingCopyRevisionRequest(String originalCopy, List<String> improvementGoals) {
	}
}
