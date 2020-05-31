package com.cic.analisisdealgoritmos.ioaudio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Precision;

public class ComplexAudioWriter implements AutoCloseable {
    private File file;
    private List<Complex> complexAudio;
    private AudioFormat format;
    private AudioInputStream audioInputStream;

    public ComplexAudioWriter(String outputPath, AudioFormat format) {
        this.file = new File(outputPath);
        this.complexAudio = new ArrayList<>();
        this.format = format;
    }

    public void writeToBuffer(Complex[] complexAudio) {
        this.complexAudio.addAll(Arrays.asList(complexAudio));
    }

    public void saveToFile() throws IOException {
        byte[] byteBuffer = convertComplexArrayToByte(complexAudio.toArray(new Complex[0]));
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        audioInputStream = new AudioInputStream(bais, format, byteBuffer.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
    }    

    public static byte[] convertComplexArrayToByte(Complex[] audioComplex) {
        byte[] result = new byte[audioComplex.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Precision.round(audioComplex[i].getReal(), 0);
        }
        return result;

    }

    @Override
    public void close() throws IOException {
        audioInputStream.close();
    }
    

}