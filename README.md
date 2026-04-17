# Spring Temporal AI Workflow Patterns

Spring Boot port of the Vercel `ai-sdk-workflow-patterns` example using:

- Temporal Java SDK for durable orchestration
- Spring AI with OpenAI
- A small web launcher UI plus REST endpoints
- Docker Compose for local app + Temporal + PostgreSQL + Temporal UI

## What It Includes

The app implements five workflow patterns:

- [Sequential processing](src/main/java/com/example/ai/workflow/SequentialWorkflowImpl.java): fixed ordered steps where one output feeds the next
- [Parallel processing](src/main/java/com/example/ai/workflow/ParallelWorkflowImpl.java): independent tasks run at the same time and are aggregated afterward
- [Routing](src/main/java/com/example/ai/workflow/RoutingWorkflowImpl.java): an initial classification step chooses the next model, prompt, or path
- [Evaluator-optimizer](src/main/java/com/example/ai/workflow/EvaluatorWorkflowImpl.java): a result is generated, evaluated, and improved until it passes a threshold or reaches a limit
- [Orchestrator-worker](src/main/java/com/example/ai/workflow/OrchestratorWorkflowImpl.java): a planner breaks work into subtasks and specialized workers execute them

The browser UI at `http://localhost:8081/` lets you:

1. Choose one of the workflow patterns
2. Start it with sample input
3. Open the specific run directly in Temporal UI

## Pattern Guide

These labels follow the AI SDK workflow patterns guide:

- Sequential processing is the simplest chain. Use it when the task has a clear sequence of steps.
- Routing is useful when different kinds of input need different prompts, models, or handling paths.
- Parallel processing is useful when multiple independent analyses can happen simultaneously.
- Orchestrator-worker is useful when one model should plan and several specialized workers should execute pieces of the work.
- Evaluator-optimizer is useful when quality matters enough to justify an evaluation and rewrite loop.

## Requirements

- Java 21
- Docker with Docker Compose
- `OPENAI_API_KEY` set in your environment

## Run With Docker Compose

Start the full stack:

```bash
docker compose up -d --build
```

Services:

- App UI and API: `http://localhost:8081`
- Temporal UI: `http://localhost:8080`
- Temporal gRPC: `localhost:7233`
- PostgreSQL: `localhost:5432`

Stop the stack:

```bash
docker compose down
```

## Run Locally Without Docker

You need a Temporal server available on `127.0.0.1:7233` or set `TEMPORAL_TARGET`.

Then run:

```bash
./gradlew bootRun
```

## Configuration

Primary environment variables:

- `OPENAI_API_KEY`
- `TEMPORAL_TARGET`
- `TEMPORAL_NAMESPACE`
- `TEMPORAL_TASK_QUEUE`
- `TEMPORAL_UI_BASE_URL`
- `APP_AI_MODEL_SMALL`
- `APP_AI_MODEL_DEFAULT`
- `APP_AI_MODEL_STRONG`

Defaults are defined in [src/main/resources/application.yml](src/main/resources/application.yml).

## API

Start a workflow with sample input:

```bash
curl -X POST 'http://localhost:8081/api/workflows/sequential/start?useSampleInput=true'
```

Example response:

```json
{
  "pattern": "SEQUENTIAL",
  "workflowId": "sequential-42d5c345-e795-494c-8661-cbf0d4474c96",
  "runId": "019d9abe-3845-7d16-aac8-038633607e07",
  "temporalUiUrl": "http://localhost:8080/namespaces/default/workflows/sequential-42d5c345-e795-494c-8661-cbf0d4474c96/019d9abe-3845-7d16-aac8-038633607e07/timeline"
}
```

Check workflow status:

```bash
curl 'http://localhost:8081/api/workflows/<workflowId>'
```

## Development

Run tests:

```bash
./gradlew test
```

Key areas:

- Workflow contracts and implementations: `src/main/java/com/example/ai/workflow`
- AI activity layer: `src/main/java/com/example/ai/activity`
- REST API: `src/main/java/com/example/ai/api`
- Launcher UI: `src/main/resources/static/index.html`

## License

MIT. See [LICENSE](LICENSE).
