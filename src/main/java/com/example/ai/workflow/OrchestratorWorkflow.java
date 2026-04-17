package com.example.ai.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface OrchestratorWorkflow {

	@WorkflowMethod
	Output run(Input input);

	record Input(String featureRequest) {
	}

	record Output(ImplementationPlan plan, List<FileChangeResult> changes, String summary) {
	}

	record ImplementationPlan(List<PlannedFile> files, Complexity estimatedComplexity) {
	}

	record PlannedFile(String purpose, String filePath, ChangeType changeType) {
	}

	record FileTask(String filePath, String purpose, ChangeType changeType, String featureRequest) {
	}

	record FileChangeProposal(String explanation, String code) {
	}

	record FileChangeResult(String filePath, ChangeType changeType, FileChangeProposal proposal) {
	}

	enum Complexity {
		LOW,
		MEDIUM,
		HIGH
	}

	enum ChangeType {
		CREATE,
		MODIFY,
		DELETE
	}
}
