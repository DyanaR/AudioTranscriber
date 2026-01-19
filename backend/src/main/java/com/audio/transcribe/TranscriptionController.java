package com.audio.transcribe;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
@CrossOrigin(origins = "http://localhost:5173")
public class TranscriptionController { // place where requests come in

    // create object of "transcription engine" that converts audio into text
    private final OpenAiAudioTranscriptionModel transcriptionModel;


    // dependency injection - Spring provides the configured transcription model
    public TranscriptionController(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @PostMapping // creating an endpoint
    public ResponseEntity<String> transcribeAudio(
            // where user can upload a file for app to receive
            @RequestParam("file")MultipartFile file) {

        // prevent NullPointerException if file is null
        if (file == null || file.isEmpty()){
            return ResponseEntity
                    .badRequest()
                    .body("No audio file uploaded");
        }

        String originalFilename = file.getOriginalFilename();
        // prevent StringIndexOutOfBoundsException - if filename has no dot, lastIndexOf(".") returns -1
        if (originalFilename == null || !originalFilename.contains(".")){
            return ResponseEntity
                    .badRequest()
                    .body("Invalid file name");
        }

        // Handles checked IO exceptions that can occur due to external system failures
        // such as file system errors, permissions, or disk issues
        try {
            // create temp file object
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            File tempFile = File.createTempFile("audio", extension);
            // transfer contents of file uploaded by user to tempfile
            file.transferTo(tempFile);

            // prepare for transcription by creating an object of OpenAiAudioTranscriptionOptions
            // to specify our options
            OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                    .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                    .language("en")
                    .temperature(0f)
                    .build();
            // create object of FileSystemResource and wrap tempfile into it
            FileSystemResource audioFile = new FileSystemResource(tempFile);

            // create transcription request w AudioTranscriptionPrompt
            // which combines both audio file and desired options
            AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
            // Business Logic - get response by doing call to model w AudioTranscriptionPrompt
            AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);

            // clean up - delete temp file
            tempFile.delete();
            // get and display response
            return new ResponseEntity<>(
                    response.getResult().getOutput(),
                    HttpStatus.OK
            );
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing audio file");
        }
    }
}
