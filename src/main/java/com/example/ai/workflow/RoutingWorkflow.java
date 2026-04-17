package com.example.ai.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface RoutingWorkflow {

	@WorkflowMethod
	Output run(Input input);

	record Input(String query) {
	}

	record Output(Classification classification, String selectedModel, String systemPrompt, String response) {
	}

	record Classification(String reasoning, QueryType type, Complexity complexity) {
	}

	record ResponseRequest(String query, String model, String systemPrompt) {
	}

	enum QueryType {
		GENERAL,
		REFUND,
		TECHNICAL
	}

	enum Complexity {
		SIMPLE,
		COMPLEX
	}
}
