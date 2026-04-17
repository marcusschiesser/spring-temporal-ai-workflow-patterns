package com.example.ai.service;

import com.example.ai.api.BadRequestException;
import com.example.ai.api.StartWorkflowEnvelope;
import com.example.ai.api.StartWorkflowResponse;
import com.example.ai.api.WorkflowStatusResponse;
import com.example.ai.config.AppProperties;
import com.example.ai.workflow.EvaluatorWorkflow;
import com.example.ai.workflow.OrchestratorWorkflow;
import com.example.ai.workflow.ParallelWorkflow;
import com.example.ai.workflow.RoutingWorkflow;
import com.example.ai.workflow.SequentialWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

	public enum PatternName {
		SEQUENTIAL,
		PARALLEL,
		ROUTING,
		EVALUATOR,
		ORCHESTRATOR;

		public static PatternName fromPath(String value) {
			try {
				return PatternName.valueOf(value.trim().toUpperCase(Locale.ROOT));
			}
			catch (IllegalArgumentException ex) {
				throw new BadRequestException("Unsupported workflow pattern: " + value);
			}
		}
	}

	private final WorkflowClient workflowClient;
	private final WorkflowServiceStubs workflowServiceStubs;
	private final AppProperties properties;
	private final WorkflowSamples samples;

	public WorkflowService(
		WorkflowClient workflowClient,
		WorkflowServiceStubs workflowServiceStubs,
		AppProperties properties,
		WorkflowSamples samples
	) {
		this.workflowClient = workflowClient;
		this.workflowServiceStubs = workflowServiceStubs;
		this.properties = properties;
		this.samples = samples;
	}

	public StartWorkflowResponse start(String patternValue, boolean useSampleInput, StartWorkflowEnvelope body) {
		PatternName pattern = PatternName.fromPath(patternValue);
		return start(pattern, useSampleInput, body);
	}

	public StartWorkflowResponse start(StartWorkflowEnvelope body) {
		if (body.pattern() == null) {
			throw new BadRequestException("Pattern is required.");
		}
		return start(body.pattern(), body.useSampleInput(), body);
	}

	public WorkflowStatusResponse status(String workflowId) {
		var response = workflowServiceStubs.blockingStub().describeWorkflowExecution(
			DescribeWorkflowExecutionRequest.newBuilder()
				.setNamespace(properties.getTemporal().getNamespace())
				.setExecution(WorkflowExecution.newBuilder().setWorkflowId(workflowId).build())
				.build()
		);
		WorkflowExecutionStatus status = response.getWorkflowExecutionInfo().getStatus();
		String runId = response.getWorkflowExecutionInfo().getExecution().getRunId();
		Object result = null;
		String failure = null;

		if (status == WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED) {
			result = workflowClient.newUntypedWorkflowStub(workflowId).getResult(Object.class);
		}
		else if (
			status == WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_FAILED
				|| status == WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TERMINATED
				|| status == WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TIMED_OUT
		) {
			failure = response.getWorkflowExecutionInfo().getStatus().name();
		}

		return new WorkflowStatusResponse(workflowId, runId, status.name(), result, failure);
	}

	private StartWorkflowResponse start(PatternName pattern, boolean useSampleInput, StartWorkflowEnvelope body) {
		return switch (pattern) {
			case SEQUENTIAL -> startSequential(useSampleInput ? samples.sequential() : require(body.sequential(), "sequential"));
			case PARALLEL -> startParallel(useSampleInput ? samples.parallel() : require(body.parallel(), "parallel"));
			case ROUTING -> startRouting(useSampleInput ? samples.routing() : require(body.routing(), "routing"));
			case EVALUATOR -> startEvaluator(useSampleInput ? samples.evaluator() : require(body.evaluator(), "evaluator"));
			case ORCHESTRATOR ->
				startOrchestrator(useSampleInput ? samples.orchestrator() : require(body.orchestrator(), "orchestrator"));
		};
	}

	private StartWorkflowResponse startSequential(SequentialWorkflow.Input input) {
		SequentialWorkflow workflow = workflowClient.newWorkflowStub(
			SequentialWorkflow.class,
			options(PatternName.SEQUENTIAL)
		);
		WorkflowExecution execution = WorkflowClient.start(workflow::run, input);
		return response(PatternName.SEQUENTIAL, execution);
	}

	private StartWorkflowResponse startParallel(ParallelWorkflow.Input input) {
		ParallelWorkflow workflow = workflowClient.newWorkflowStub(
			ParallelWorkflow.class,
			options(PatternName.PARALLEL)
		);
		WorkflowExecution execution = WorkflowClient.start(workflow::run, input);
		return response(PatternName.PARALLEL, execution);
	}

	private StartWorkflowResponse startRouting(RoutingWorkflow.Input input) {
		RoutingWorkflow workflow = workflowClient.newWorkflowStub(
			RoutingWorkflow.class,
			options(PatternName.ROUTING)
		);
		WorkflowExecution execution = WorkflowClient.start(workflow::run, input);
		return response(PatternName.ROUTING, execution);
	}

	private StartWorkflowResponse startEvaluator(EvaluatorWorkflow.Input input) {
		EvaluatorWorkflow workflow = workflowClient.newWorkflowStub(
			EvaluatorWorkflow.class,
			options(PatternName.EVALUATOR)
		);
		WorkflowExecution execution = WorkflowClient.start(workflow::run, input);
		return response(PatternName.EVALUATOR, execution);
	}

	private StartWorkflowResponse startOrchestrator(OrchestratorWorkflow.Input input) {
		OrchestratorWorkflow workflow = workflowClient.newWorkflowStub(
			OrchestratorWorkflow.class,
			options(PatternName.ORCHESTRATOR)
		);
		WorkflowExecution execution = WorkflowClient.start(workflow::run, input);
		return response(PatternName.ORCHESTRATOR, execution);
	}

	private WorkflowOptions options(PatternName pattern) {
		return WorkflowOptions.newBuilder()
			.setTaskQueue(properties.getTemporal().getTaskQueue())
			.setWorkflowId(pattern.name().toLowerCase(Locale.ROOT) + "-" + java.util.UUID.randomUUID())
			.build();
	}

	private <T> T require(T value, String patternName) {
		if (value == null) {
			throw new BadRequestException("Request body for pattern '" + patternName + "' is required when useSampleInput is false.");
		}
		return value;
	}

	private StartWorkflowResponse response(PatternName pattern, WorkflowExecution execution) {
		String temporalUiUrl = properties.getTemporal()
			.getUiBaseUrl()
			+ "/namespaces/"
			+ properties.getTemporal().getNamespace()
			+ "/workflows/"
			+ execution.getWorkflowId()
			+ "/"
			+ execution.getRunId()
			+ "/timeline";
		return new StartWorkflowResponse(pattern, execution.getWorkflowId(), execution.getRunId(), temporalUiUrl);
	}
}
