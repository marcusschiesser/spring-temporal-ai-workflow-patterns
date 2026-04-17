package com.example.ai.service;

import com.example.ai.config.AppProperties;
import com.example.ai.workflow.EvaluatorWorkflow;
import com.example.ai.workflow.OrchestratorWorkflow;
import com.example.ai.workflow.ParallelWorkflow;
import com.example.ai.workflow.RoutingWorkflow;
import com.example.ai.workflow.SequentialWorkflow;
import org.springframework.stereotype.Component;

@Component
public class WorkflowSamples {

	private final AppProperties properties;

	public WorkflowSamples(AppProperties properties) {
		this.properties = properties;
	}

	public SequentialWorkflow.Input sequential() {
		return new SequentialWorkflow.Input(
			"Temporal-powered Spring AI workflows for reliable AI automation that survives failures"
		);
	}

	public ParallelWorkflow.Input parallel() {
		return new ParallelWorkflow.Input(
			"""
			import org.springframework.web.bind.annotation.GetMapping;
			import org.springframework.web.bind.annotation.PathVariable;

			class UserController {
			    @GetMapping("/users/{id}")
			    public String getUser(@PathVariable String id) {
			        return new RestTemplate().getForObject("https://api.example.com/users/" + id, String.class);
			    }
			}
			"""
		);
	}

	public RoutingWorkflow.Input routing() {
		return new RoutingWorkflow.Input(
			"My deployment has been stuck in the building state for over 20 minutes and logs show repeated dependency retries. How should I fix it?"
		);
	}

	public EvaluatorWorkflow.Input evaluator() {
		return new EvaluatorWorkflow.Input(
			"Temporal helps developers build resilient distributed systems that continue executing through outages and restarts.",
			"es",
			properties.getEvaluator().getQualityThreshold(),
			properties.getEvaluator().getMaxIterations()
		);
	}

	public OrchestratorWorkflow.Input orchestrator() {
		return new OrchestratorWorkflow.Input(
			"Add a dark mode toggle to the dashboard, persist the preference per user, and update the UI without a full reload."
		);
	}
}
