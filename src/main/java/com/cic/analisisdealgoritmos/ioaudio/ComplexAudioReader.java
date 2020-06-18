package com.cic.analisisdealgoritmos.ioaudio;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math3.complex.Complex;

public class ComplexAudioReader implements AutoCloseable {
    public static final int sampleSize = 1024;
    private final File file;
    private final AudioInputStream audioInputStream;
    private int bytesPerFrame;
    private int numBytes;
    private byte[] audioBytes;
    private int numBytesRead;
    private int numFramesRead;
    private int totalFramesRead;

    public ComplexAudioReader(String path) throws UnsupportedAudioFileException, IOException {
        this.file = new File(path);
        this.audioInputStream = AudioSystem.getAudioInputStream(file);
        this.bytesPerFrame = audioInputStream.getFormat().getFrameSize();
        this.numBytes = sampleSize * bytesPerFrame;
        this.audioBytes = new byte[numBytes];
        this.numBytesRead = 0;
        this.numFramesRead = 0;
        this.totalFramesRead = 0;
    }

    public Complex[] readComplexAudioFile() throws UnsupportedAudioFileException, IOException {
        // Try to read numBytes bytes from the file.
        if ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
            // Calculate the number of frames actually read.
            numFramesRead = numBytesRead / bytesPerFrame;
            totalFramesRead += numFramesRead;
            if (this.audioInputStream.getFormat().getSampleSizeInBits() == 16) {
                return convertByteArrayToComplex16(audioBytes);
            } else {
                return convertByteArrayToComplex(audioBytes);
            }
        } else {
            // End of file
            return null;
        }

    }

    private Complex[] convertByteArrayToComplex(byte[] bytes) {
        Complex[] complex = new Complex[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            complex[i] = new Complex((double) (Byte.toUnsignedInt(bytes[i])));
        }
        return complex;
    }

    private Complex[] convertByteArrayToComplex16(byte[] bytes) {
        Complex[] complex = new Complex[bytes.length / 2];
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        for (int i = 0; i < shorts.length; i++) {
            complex[i] = new Complex((double) (shorts[i]));
        }
        return complex;
    }

    @Override
    public void close() throws IOException {
        this.audioInputStream.close();
    }

    public int getTotalFramesRead() {
        return totalFramesRead;
    }

    public AudioFormat getFormat() {
        return audioInputStream.getFormat();
    }

    public int getCurrentOffest() {
        return (totalFramesRead) * bytesPerFrame;
    }

    public int getSampleSize() {
        return sampleSize;
    }

}