package com.example.ai;

import com.example.ai.workflow.SequentialWorkflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowSerializationTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void serializesAndDeserializesWorkflowDtos() throws Exception {
		SequentialWorkflow.Output output = new SequentialWorkflow.Output(
			"copy",
			new SequentialWorkflow.QualityMetrics(true, 8, 9),
			"copy",
			false
		);

		String json = objectMapper.writeValueAsString(output);
		SequentialWorkflow.Output restored = objectMapper.readValue(json, SequentialWorkflow.Output.class);

		assertThat(restored.qualityMetrics().clarity()).isEqualTo(9);
		assertThat(restored.regenerated()).isFalse();
	}
}
