package com.jcx.jiachangcai.module.ai.service;

public interface SpeechService {

    /**
     * Convert speech audio to text
     * @param audio audio bytes
     * @param contentType the MIME type of the audio (e.g. "audio/webm")
     * @return recognized text
     */
    String recognize(byte[] audio, String contentType);
}
