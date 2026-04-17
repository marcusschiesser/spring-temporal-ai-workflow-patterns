package com.example.ai.api;

import com.example.ai.service.WorkflowService.PatternName;
import com.example.ai.workflow.EvaluatorWorkflow;
import com.example.ai.workflow.OrchestratorWorkflow;
import com.example.ai.workflow.ParallelWorkflow;
import com.example.ai.workflow.RoutingWorkflow;
import com.example.ai.workflow.SequentialWorkflow;

public record StartWorkflowEnvelope(
	PatternName pattern,
	boolean useSampleInput,
	SequentialWorkflow.Input sequential,
	ParallelWorkflow.Input parallel,
	RoutingWorkflow.Input routing,
	EvaluatorWorkflow.Input evaluator,
	OrchestratorWorkflow.Input orchestrator
) {
}
