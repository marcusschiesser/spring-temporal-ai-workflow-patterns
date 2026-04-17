package com.example.ai.api;

public record WorkflowStatusResponse(String workflowId, String runId, String status, Object result, String failure) {
}
