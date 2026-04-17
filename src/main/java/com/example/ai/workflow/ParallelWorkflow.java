package com.example.ai.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface ParallelWorkflow {

	@WorkflowMethod
	Output run(Input input);

	record Input(String code) {
	}

	record Output(
		SecurityReview securityReview,
		PerformanceReview performanceReview,
		MaintainabilityReview maintainabilityReview,
		String summary
	) {
	}

	record SecurityReview(List<String> vulnerabilities, RiskLevel riskLevel, List<String> suggestions) {
	}

	record PerformanceReview(List<String> issues, ImpactLevel impact, List<String> optimizations) {
	}

	record MaintainabilityReview(List<String> concerns, int qualityScore, List<String> recommendations) {
	}

	record ReviewBundle(
		SecurityReview security,
		PerformanceReview performance,
		MaintainabilityReview maintainability
	) {
	}

	enum RiskLevel {
		LOW,
		MEDIUM,
		HIGH
	}

	enum ImpactLevel {
		LOW,
		MEDIUM,
		HIGH
	}
}
