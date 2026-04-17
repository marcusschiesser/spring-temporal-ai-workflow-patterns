package com.example.ai.api;

import com.example.ai.service.WorkflowService.PatternName;

public record StartWorkflowResponse(PatternName pattern, String workflowId, String runId, String temporalUiUrl) {
}
