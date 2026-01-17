package com.audio.transcribe;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

// Documentation
//https://docs.spring.io/spring-ai/reference/1.0/api/audio/transcriptions/openai-transcriptions.html

// marks class as a REST API controller in Spring Boot
// handles HTTP Requests
// all methods will return data (JSON) instead of HTML
@RestController
//set base URL for all endpoints in this controller
@RequestMapping("/api/transcribe")
public class TranscriptionController { // place where requests come in

    // create object of "transcription engine" that converts audio into text
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    // dependency injection - Spring injects the already configured transcription model
    public TranscriptionController(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @PostMapping // create post api endpoint
    public ResponseEntity<String> transcribeAudio( // will return transcription text & HTTP status code
            // where user can upload a file for app to receive
            // matches form key name: "file"
            @RequestParam("file")MultipartFile file) throws IOException{

        // create a temp .wav file on server & OpenAI expects a real file (not  bytes)
        String originalFilename =  file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        File tempFile = File.createTempFile("audio", extension);

        // transfer contents of file uploaded by user to tempfile
        file.transferTo(tempFile);

        // prepare for transcription by creating an object of OpenAiAudioTranscriptionOptions
        // to specify our options
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT) // return plain text
                .language("en")
                .temperature(0f) // deterministic (no randomness)
                .build();

        // wrap tempfile in FileSystemResource to convert it into Spring compatible resource
        FileSystemResource audioFile = new FileSystemResource(tempFile);

        // create transcription request that combines audio file and desired options
        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
        // call OpenAI by sending audio to OpenAI and receive transcription result
        AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);

        // clean up to prevent server storage buildup
        tempFile.delete();
        // get and display response
        return new ResponseEntity<>(
                response.getResult().getOutput(), // transcribed text
                HttpStatus.OK
        );
    }
}
