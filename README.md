# 🎙️ Audio Transcription Web App

## Tech Stack

### Frontend
- React (Vite)
- JavaScript
- CSS
- Fetch API

### Backend
- Java 17+
- Spring Boot
- Spring AI (OpenAI integration)
- Multipart file upload handling

### AI
- OpenAI Whisper (`whisper-1`)

## Getting Started (Local Setup)

### Prerequisites
- Java 17+
- Node.js 18+ (Node 20 recommended)
- npm
- OpenAI API key

## Environment Variable Setup

The OpenAI API key is configured using environment variables.

### IntelliJ IDEA

1. Open **Run → Edit Configurations**
2. Select the Spring Boot application
3. Add the following environment variable:
OPENAI_API_KEY=your_api_key_here
4. Click Apply → OK
5. Restart the backend
   
Spring Boot will automatically read this variable using:
```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

## Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on:

http://localhost:8080

API endpoint:

POST /api/transcribe

## Running the Frontend
```bash
cd frontend/audio-transcribe-frontend
npm install
npm run dev
```
Frontend runs on:

http://localhost:5173

## API Usage
Endpoint
POST /api/transcribe

## Request
- multipart/form-data
- field name: file

## Response
Hello everyone, welcome to the meeting...
