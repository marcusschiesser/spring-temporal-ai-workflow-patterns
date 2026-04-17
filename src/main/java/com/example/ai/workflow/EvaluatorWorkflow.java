package com.example.ai.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface EvaluatorWorkflow {

	@WorkflowMethod
	Output run(Input input);

	record Input(String sourceText, String targetLanguage, int qualityThreshold, int maxIterations) {
	}

	record Output(String finalTranslation, List<IterationResult> iterations, int completedIterations, StopReason stopReason) {
	}

	record IterationResult(int cycle, String translation, Evaluation evaluation) {
	}

	record Evaluation(
		int qualityScore,
		boolean preservesTone,
		boolean preservesNuance,
		boolean culturallyAccurate,
		List<String> specificIssues,
		List<String> improvementSuggestions
	) {
	}

	record TranslationRequest(String sourceText, String targetLanguage) {
	}

	record EvaluationRequest(String sourceText, String translation) {
	}

	record ImprovementRequest(String sourceText, String currentTranslation, List<String> feedback) {
	}

	enum StopReason {
		QUALITY_THRESHOLD_MET,
		MAX_ITERATIONS_REACHED
	}
}
