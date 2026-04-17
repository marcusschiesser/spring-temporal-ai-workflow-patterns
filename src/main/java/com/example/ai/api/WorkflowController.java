package com.example.ai.api;

import com.example.ai.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowController {

	private final WorkflowService workflowService;

	public WorkflowController(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	@PostMapping("/api/workflows/{pattern}/start")
	public StartWorkflowResponse startPattern(
		@PathVariable String pattern,
		@RequestParam(defaultValue = "false") boolean useSampleInput,
		@RequestBody(required = false) StartWorkflowEnvelope request
	) {
		return workflowService.start(
			pattern,
			useSampleInput,
			request == null ? new StartWorkflowEnvelope(null, false, null, null, null, null, null) : request
		);
	}

	@PostMapping("/api/workflows/start")
	public StartWorkflowResponse start(@Valid @RequestBody StartWorkflowEnvelope request) {
		return workflowService.start(request);
	}

	@GetMapping("/api/workflows/{workflowId}")
	public WorkflowStatusResponse status(@PathVariable String workflowId) {
		return workflowService.status(workflowId);
	}
}
