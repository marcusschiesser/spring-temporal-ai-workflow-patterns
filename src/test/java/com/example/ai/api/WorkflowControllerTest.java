package com.example.ai.api;

import com.example.ai.service.WorkflowService;
import com.example.ai.service.WorkflowService.PatternName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkflowController.class)
class WorkflowControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private WorkflowService workflowService;

	@Test
	void startEndpointReturnsIds() throws Exception {
		Mockito.when(workflowService.start(eq("sequential"), eq(true), any()))
			.thenReturn(new StartWorkflowResponse(PatternName.SEQUENTIAL, "wf-1", "run-1", "http://localhost:8080/namespaces/default/workflows/wf-1/run-1/timeline"));

		mockMvc.perform(post("/api/workflows/sequential/start").param("useSampleInput", "true"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workflowId").value("wf-1"))
			.andExpect(jsonPath("$.runId").value("run-1"))
			.andExpect(jsonPath("$.temporalUiUrl").value("http://localhost:8080/namespaces/default/workflows/wf-1/run-1/timeline"));
	}

	@Test
	void invalidPatternReturnsBadRequest() throws Exception {
		Mockito.when(workflowService.start(eq("unknown"), eq(false), any()))
			.thenThrow(new BadRequestException("Unsupported workflow pattern: unknown"));

		mockMvc.perform(
			post("/api/workflows/unknown/start")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("Unsupported workflow pattern: unknown"));
	}

	@Test
	void statusEndpointReturnsCompletedWorkflow() throws Exception {
		Mockito.when(workflowService.status("wf-1"))
			.thenReturn(new WorkflowStatusResponse("wf-1", "run-1", "WORKFLOW_EXECUTION_STATUS_COMPLETED", "done", null));

		mockMvc.perform(get("/api/workflows/wf-1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("WORKFLOW_EXECUTION_STATUS_COMPLETED"))
			.andExpect(jsonPath("$.result").value("done"));
	}
}
