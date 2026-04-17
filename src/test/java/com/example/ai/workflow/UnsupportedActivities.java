package com.example.ai.workflow;

import com.example.ai.activity.AiActivities;

abstract class UnsupportedActivities implements AiActivities {

	@Override
	public String createMarketingCopy(String topic) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SequentialWorkflow.QualityMetrics evaluateMarketingCopy(String copy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String improveMarketingCopy(SequentialWorkflow.MarketingCopyRevisionRequest request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParallelWorkflow.SecurityReview reviewSecurity(String code) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParallelWorkflow.PerformanceReview reviewPerformance(String code) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParallelWorkflow.MaintainabilityReview reviewMaintainability(String code) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String summarizeReviews(ParallelWorkflow.ReviewBundle reviewBundle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RoutingWorkflow.Classification classifyQuery(String query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String answerQuery(RoutingWorkflow.ResponseRequest request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String translate(EvaluatorWorkflow.TranslationRequest request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EvaluatorWorkflow.Evaluation evaluateTranslation(EvaluatorWorkflow.EvaluationRequest request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String improveTranslation(EvaluatorWorkflow.ImprovementRequest request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OrchestratorWorkflow.ImplementationPlan planImplementation(String featureRequest) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OrchestratorWorkflow.FileChangeProposal implementFileChange(OrchestratorWorkflow.FileTask fileTask) {
		throw new UnsupportedOperationException();
	}
}
