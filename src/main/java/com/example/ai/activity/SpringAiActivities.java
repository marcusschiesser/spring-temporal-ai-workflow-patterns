package com.example.ai.activity;

import com.example.ai.config.AppProperties;
import com.example.ai.workflow.EvaluatorWorkflow;
import com.example.ai.workflow.OrchestratorWorkflow;
import com.example.ai.workflow.ParallelWorkflow;
import com.example.ai.workflow.RoutingWorkflow;
import com.example.ai.workflow.SequentialWorkflow;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

@Component
public class SpringAiActivities implements AiActivities {

	private final ChatClient chatClient;
	private final AppProperties properties;

	public SpringAiActivities(ChatClient.Builder chatClientBuilder, AppProperties properties) {
		this.chatClient = chatClientBuilder.build();
		this.properties = properties;
	}

	@Override
	public String createMarketingCopy(String topic) {
		return text(
			properties.getModels().getDefaultModel(),
			"You write persuasive marketing copy that emphasizes user benefits and emotional appeal.",
			"Write persuasive marketing copy for: " + topic + ". Focus on benefits and emotional appeal."
		);
	}

	@Override
	public SequentialWorkflow.QualityMetrics evaluateMarketingCopy(String copy) {
		return structured(
			properties.getModels().getStrong(),
			"You evaluate marketing copy for actionability, emotional resonance, and clarity.",
			"""
			Evaluate this marketing copy for:
			1. Presence of call to action (true/false)
			2. Emotional appeal (1-10)
			3. Clarity (1-10)

			Copy to evaluate:
			%s
			""".formatted(copy),
			SequentialWorkflow.QualityMetrics.class
		);
	}

	@Override
	public String improveMarketingCopy(SequentialWorkflow.MarketingCopyRevisionRequest request) {
		String instructions = """
			Rewrite this marketing copy with:
			%s

			Original copy:
			%s
			""".formatted(String.join("\n", request.improvementGoals()), request.originalCopy());
		return text(
			properties.getModels().getStrong(),
			"You improve marketing copy while preserving the original intent.",
			instructions
		);
	}

	@Override
	public ParallelWorkflow.SecurityReview reviewSecurity(String code) {
		return structured(
			properties.getModels().getStrong(),
			"You are an expert in code security. Focus on vulnerabilities, injection risks, and authentication issues.",
			"Review this code:\n" + code,
			ParallelWorkflow.SecurityReview.class
		);
	}

	@Override
	public ParallelWorkflow.PerformanceReview reviewPerformance(String code) {
		return structured(
			properties.getModels().getStrong(),
			"You are an expert in code performance. Focus on bottlenecks, memory use, and optimization opportunities.",
			"Review this code:\n" + code,
			ParallelWorkflow.PerformanceReview.class
		);
	}

	@Override
	public ParallelWorkflow.MaintainabilityReview reviewMaintainability(String code) {
		return structured(
			properties.getModels().getStrong(),
			"You are an expert in code quality. Focus on structure, readability, and best practices.",
			"Review this code:\n" + code,
			ParallelWorkflow.MaintainabilityReview.class
		);
	}

	@Override
	public String summarizeReviews(ParallelWorkflow.ReviewBundle reviewBundle) {
		return text(
			properties.getModels().getStrong(),
			"You are a technical lead summarizing multiple code reviews into concise next steps.",
			"""
			Synthesize these code review results into a concise summary with key actions.

			Security review:
			%s

			Performance review:
			%s

			Maintainability review:
			%s
			""".formatted(reviewBundle.security(), reviewBundle.performance(), reviewBundle.maintainability())
		);
	}

	@Override
	public RoutingWorkflow.Classification classifyQuery(String query) {
		return structured(
			properties.getModels().getStrong(),
			"You classify customer support queries by type and complexity.",
			"""
			Classify this customer query:
			%s

			Determine:
			1. Query type (general, refund, or technical)
			2. Complexity (simple or complex)
			3. Brief reasoning for classification
			""".formatted(query),
			RoutingWorkflow.Classification.class
		);
	}

	@Override
	public String answerQuery(RoutingWorkflow.ResponseRequest request) {
		return text(resolveModel(request.model()), request.systemPrompt(), request.query());
	}

	@Override
	public String translate(EvaluatorWorkflow.TranslationRequest request) {
		return text(
			properties.getModels().getSmall(),
			"You are an expert literary translator.",
			"""
			Translate this text to %s, preserving tone and cultural nuances:
			%s
			""".formatted(request.targetLanguage(), request.sourceText())
		);
	}

	@Override
	public EvaluatorWorkflow.Evaluation evaluateTranslation(EvaluatorWorkflow.EvaluationRequest request) {
		return structured(
			properties.getModels().getStrong(),
			"You are an expert in evaluating literary translations.",
			"""
			Evaluate this translation.

			Original: %s
			Translation: %s

			Consider:
			1. Overall quality
			2. Preservation of tone
			3. Preservation of nuance
			4. Cultural accuracy
			""".formatted(request.sourceText(), request.translation()),
			EvaluatorWorkflow.Evaluation.class
		);
	}

	@Override
	public String improveTranslation(EvaluatorWorkflow.ImprovementRequest request) {
		return text(
			properties.getModels().getStrong(),
			"You are an expert literary translator.",
			"""
			Improve this translation based on the following feedback:
			%s

			Original: %s
			Current Translation: %s
			""".formatted(String.join("\n", request.feedback()), request.sourceText(), request.currentTranslation())
		);
	}

	@Override
	public OrchestratorWorkflow.ImplementationPlan planImplementation(String featureRequest) {
		return structured(
			properties.getModels().getStrong(),
			"You are a senior software architect planning feature implementations.",
			"Analyze this feature request and create an implementation plan:\n" + featureRequest,
			OrchestratorWorkflow.ImplementationPlan.class
		);
	}

	@Override
	public OrchestratorWorkflow.FileChangeProposal implementFileChange(OrchestratorWorkflow.FileTask fileTask) {
		String systemPrompt = switch (fileTask.changeType()) {
			case CREATE ->
				"You are an expert at implementing new files following project conventions and best practices.";
			case MODIFY ->
				"You are an expert at modifying existing code while maintaining consistency and avoiding regressions.";
			case DELETE ->
				"You are an expert at safely removing code while minimizing breaking changes.";
		};

		return structured(
			properties.getModels().getStrong(),
			systemPrompt,
			"""
			Implement the changes for %s to support:
			%s

			Consider the overall feature context:
			%s
			""".formatted(fileTask.filePath(), fileTask.purpose(), fileTask.featureRequest()),
			OrchestratorWorkflow.FileChangeProposal.class
		);
	}

	private String text(String model, String system, String user) {
		return chatClient.prompt()
			.system(system)
			.user(user)
			.options(OpenAiChatOptions.builder().model(model).build())
			.call()
			.content();
	}

	private <T> T structured(String model, String system, String user, Class<T> type) {
		return chatClient.prompt()
			.system(system)
			.user(user)
			.options(OpenAiChatOptions.builder().model(model).build())
			.call()
			.entity(type);
	}

	private String resolveModel(String profileOrModel) {
		return switch (profileOrModel) {
			case "small" -> properties.getModels().getSmall();
			case "default" -> properties.getModels().getDefaultModel();
			case "strong" -> properties.getModels().getStrong();
			default -> profileOrModel;
		};
	}
}
