# Fraud Investigation Agent

An AI-powered fraud risk analysis agent built with Spring Boot and Claude (Anthropic API).

## Live Demo

Primary (Railway - always on, fastest response):
https://fraud-investigation-agent-production-c4c7.up.railway.app

Backup (Render - free tier, may take 30-60 seconds to spin up on first visit):
https://fraud-investigation-agent.onrender.com

## What It Does

Select six transaction signals and the agent autonomously analyzes them, weighs them
against each other, and produces a structured fraud investigation report with:

- Risk Score: HIGH / MEDIUM / LOW
- Plain English explanation of detected signals
- Agent reasoning across all six signals
- Recommended action: Approve, Decline, or Flag for Manual Review

## Transaction Signals Analyzed

1. Transaction Amount
2. Transaction Velocity
3. Geographic Match
4. Card Type
5. Time of Day
6. Channel

## How It Works

User submits 6 signals via form
↓
FraudController receives input
↓
FraudInvestigationService loads prompts from resources/prompts/
↓
ClaudeApiClient sends structured prompt to Anthropic API
↓
Claude reasons through all 6 signals and returns structured JSON
↓
Report rendered: Risk Score + Signals + Reasoning + Recommended Action

## Prompt Engineering Design

- System prompt and user prompt template are externalized to src/main/resources/prompts/
  so they can be updated without a code change
- System prompt enforces strict JSON output contract with explicit fraud analysis rules
- User prompt template uses placeholder substitution for the 6 signal values
- JSON response is parsed into a structured report object with fallback error handling

## Caching Strategy

- Local: Caffeine in-memory cache (no infrastructure needed)
- Production: Redis cache (survives app restarts)
- Spring profiles control which cache is active automatically
- Cache key is built from all 6 signal values
- TTL: 1 hour per entry

## Why Caching Matters

### Cost Savings
Each Claude API call consumes tokens. A typical fraud investigation uses approximately
400 input tokens and 250 output tokens -- roughly 650 tokens per call.

With caching:
- Identical signal combinations return instantly from Redis -- zero tokens consumed
- In a high-volume scenario with 10,000 daily investigations, even a 30% cache hit
  rate saves 3,000 API calls per day
- At Claude Sonnet pricing that translates to meaningful cost reduction at scale

### Performance
- Cache MISS: 6-10 seconds (Claude API call)
- Cache HIT: under 100 milliseconds (Redis lookup)
- 60-100x faster response on repeated signal combinations

### Real World Relevance
Fraud signal combinations are not infinitely unique. Peak shopping hours see
clusters of similar transactions -- same amount ranges, same time of day, same
channel. Caching exploits this natural repetition to serve faster decisions
at lower cost without sacrificing intelligence.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Thymeleaf
- Anthropic Claude API (claude-sonnet-4-6)
- Caffeine (local cache)
- Redis (production cache)
- Docker
- Render + Railway (hosting)

## Local Setup

1. Clone the repo
2. Set environment variable: ANTHROPIC_API_KEY=sk-ant-...
3. Run: ./mvnw spring-boot:run
4. Open: http://localhost:8080

## Screenshots

### Input Form
![Input Form](docs/images/screenshot-input.png)

### Fraud Investigation Report
![Fraud Report](docs/images/screenshot-report.png)

## Author

Shruti Vargantwar

Senior Software Engineer