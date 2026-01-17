import { useState } from "react";

const AudioUploader = () => {
    
    const [file, setFile] = useState(null);
    const [transcription, setTranscription] = useState("");


    async function handleUpload(){
        if (!file){
                alert("Please select a file first.");
                return;
            }
        const formData = new FormData();
        formData.append('file', file);
        
        try { 
            
            const response = await fetch("http://localhost:8080/api/transcribe", {
                method: "POST",
                body: formData, // sending file not text (json)
            })

            // handle errors
            if (!response.ok){
                const errorData = await response.json();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorData.message}`)
            }
            const result = await response.text();
            setTranscription(result);
        } catch (error){
            console.error('Error:', error);
        }
    }


    return (
        <div className="container">
            <h1>Audio to Text Transcriber</h1>
            <div className="file-input">
                <input type="file" id="fileInput" accept="audio/*"
                onChange={e => setFile(e.target.files[0])}/>
                <p>{file? file.name : "No file selected"}</p>
            </div>
            <button className="upload-button" onClick={handleUpload}>Upload and Transcribe</button>
            <div className="transcription-result">
                <h2>Transcription Result</h2>
                <p>{transcription}</p>
            </div>
        </div>
    );
}

export default AudioUploader;