package com.example.ai.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private final Temporal temporal = new Temporal();
	private final Models models = new Models();
	private final Evaluator evaluator = new Evaluator();

	public Temporal getTemporal() {
		return temporal;
	}

	public Models getModels() {
		return models;
	}

	public Evaluator getEvaluator() {
		return evaluator;
	}

	public static class Temporal {

		@NotBlank
		private String namespace = "default";

		@NotBlank
		private String taskQueue = "ai-workflows";

		@NotBlank
		private String target = "127.0.0.1:7233";

		@NotBlank
		private String uiBaseUrl = "http://localhost:8080";

		private boolean workersEnabled = true;

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String getTaskQueue() {
			return taskQueue;
		}

		public void setTaskQueue(String taskQueue) {
			this.taskQueue = taskQueue;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getUiBaseUrl() {
			return uiBaseUrl;
		}

		public void setUiBaseUrl(String uiBaseUrl) {
			this.uiBaseUrl = uiBaseUrl;
		}

		public boolean isWorkersEnabled() {
			return workersEnabled;
		}

		public void setWorkersEnabled(boolean workersEnabled) {
			this.workersEnabled = workersEnabled;
		}
	}

	public static class Models {

		@NotBlank
		private String small = "gpt-4o-mini";

		@NotBlank
		private String defaultModel = "gpt-4o-mini";

		@NotBlank
		private String strong = "gpt-4o";

		public String getSmall() {
			return small;
		}

		public void setSmall(String small) {
			this.small = small;
		}

		public String getDefaultModel() {
			return defaultModel;
		}

		public void setDefaultModel(String defaultModel) {
			this.defaultModel = defaultModel;
		}

		public String getStrong() {
			return strong;
		}

		public void setStrong(String strong) {
			this.strong = strong;
		}
	}

	public static class Evaluator {

		@Min(1)
		@Max(10)
		private int qualityThreshold = 8;

		@Min(1)
		@Max(10)
		private int maxIterations = 3;

		public int getQualityThreshold() {
			return qualityThreshold;
		}

		public void setQualityThreshold(int qualityThreshold) {
			this.qualityThreshold = qualityThreshold;
		}

		public int getMaxIterations() {
			return maxIterations;
		}

		public void setMaxIterations(int maxIterations) {
			this.maxIterations = maxIterations;
		}
	}
}
