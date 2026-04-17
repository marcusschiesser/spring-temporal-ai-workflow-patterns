package com.example.ai.activity;

import com.example.ai.workflow.EvaluatorWorkflow;
import com.example.ai.workflow.OrchestratorWorkflow;
import com.example.ai.workflow.ParallelWorkflow;
import com.example.ai.workflow.RoutingWorkflow;
import com.example.ai.workflow.SequentialWorkflow;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AiActivities {

	String createMarketingCopy(String topic);

	SequentialWorkflow.QualityMetrics evaluateMarketingCopy(String copy);

	String improveMarketingCopy(SequentialWorkflow.MarketingCopyRevisionRequest request);

	ParallelWorkflow.SecurityReview reviewSecurity(String code);

	ParallelWorkflow.PerformanceReview reviewPerformance(String code);

	ParallelWorkflow.MaintainabilityReview reviewMaintainability(String code);

	String summarizeReviews(ParallelWorkflow.ReviewBundle reviewBundle);

	RoutingWorkflow.Classification classifyQuery(String query);

	String answerQuery(RoutingWorkflow.ResponseRequest request);

	String translate(EvaluatorWorkflow.TranslationRequest request);

	EvaluatorWorkflow.Evaluation evaluateTranslation(EvaluatorWorkflow.EvaluationRequest request);

	String improveTranslation(EvaluatorWorkflow.ImprovementRequest request);

	OrchestratorWorkflow.ImplementationPlan planImplementation(String featureRequest);

	OrchestratorWorkflow.FileChangeProposal implementFileChange(OrchestratorWorkflow.FileTask fileTask);
}
