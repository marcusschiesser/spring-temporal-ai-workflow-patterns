package com.example.ai.config;

import com.example.ai.activity.AiActivities;
import com.example.ai.workflow.EvaluatorWorkflow;
import com.example.ai.workflow.EvaluatorWorkflowImpl;
import com.example.ai.workflow.OrchestratorWorkflow;
import com.example.ai.workflow.OrchestratorWorkflowImpl;
import com.example.ai.workflow.ParallelWorkflow;
import com.example.ai.workflow.ParallelWorkflowImpl;
import com.example.ai.workflow.RoutingWorkflow;
import com.example.ai.workflow.RoutingWorkflowImpl;
import com.example.ai.workflow.SequentialWorkflow;
import com.example.ai.workflow.SequentialWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfiguration {

	@Bean
	WorkflowServiceStubs workflowServiceStubs(AppProperties properties) {
		return WorkflowServiceStubs.newServiceStubs(
			WorkflowServiceStubsOptions.newBuilder()
				.setTarget(properties.getTemporal().getTarget())
				.build()
		);
	}

	@Bean
	WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs, AppProperties properties) {
		return WorkflowClient.newInstance(
			workflowServiceStubs,
			io.temporal.client.WorkflowClientOptions.newBuilder()
				.setNamespace(properties.getTemporal().getNamespace())
				.build()
		);
	}

	@Bean(destroyMethod = "shutdown")
	WorkerFactory workerFactory(
		WorkflowClient workflowClient,
		AiActivities aiActivities,
		AppProperties properties
	) {
		WorkerFactory workerFactory = WorkerFactory.newInstance(workflowClient);
		Worker worker = workerFactory.newWorker(properties.getTemporal().getTaskQueue());
		worker.registerWorkflowImplementationTypes(
			SequentialWorkflowImpl.class,
			ParallelWorkflowImpl.class,
			RoutingWorkflowImpl.class,
			EvaluatorWorkflowImpl.class,
			OrchestratorWorkflowImpl.class
		);
		worker.registerActivitiesImplementations(aiActivities);
		return workerFactory;
	}

	@Bean
	TemporalWorkerLifecycle temporalWorkerLifecycle(WorkerFactory workerFactory, AppProperties properties) {
		return new TemporalWorkerLifecycle(workerFactory, properties.getTemporal().isWorkersEnabled());
	}
}
