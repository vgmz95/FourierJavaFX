package com.cic.analisisdealgoritmos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.cic.analisisdealgoritmos.ioaudio.*;
import com.cic.analisisdealgoritmos.fourier.Fourier;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;

/**
 * JavaFX App
 */
public class App extends Application {
    // Chart objects
    LineChart<Number, Number> originalSignalChart;
    LineChart<Number, Number> fftSignalChart;
    LineChart<Number, Number> ifftSignalChart;

    // Paths
    String inputPath = "C:\\Users\\victo\\Documents\\LINUX\\Music\\sin_1000Hz_-6dBFS_3s.wav";
    String outputPath = "C:\\Users\\victo\\Documents\\LINUX\\Music\\sin_1000Hz_-6dBFS_3sCOPY.wav";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        initializeGUI(stage);

        try (ComplexAudioReader audioReader = new ComplexAudioReader(inputPath);
                ComplexAudioWriter audioWriter = new ComplexAudioWriter(outputPath, audioReader.getFormat());
                SourceDataLine line = AudioSystem.getSourceDataLine(audioReader.getFormat());) {
            Complex[] readComplexAudio = audioReader.readComplexAudioFile();
            Complex[] innerreadComplexAudio = readComplexAudio;
            line.open(audioReader.getFormat());
            line.start();
            Complex[] fft = Fourier.fft(readComplexAudio); // Get FFT
            Complex[] ifft = Fourier.ifft(fft); // Get IFFT
            // Convert complex to byte array
            byte[] ifftByteData = ComplexAudioWriter.convertComplexArrayToByte(ifft);
            // Write IFFT data to file buffer
            audioWriter.writeToBuffer(ifft);
            // Update GUI
            updateCharts(innerreadComplexAudio, fft, ifft);
            // Write IFF data to the speaker's buffer
            line.write(ifftByteData, 0, ifftByteData.length);

            audioWriter.saveToFile();
            line.drain();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeGUI(Stage stage) {
        stage.setTitle("FFT"); // Windows title
        // Original signal GUI
        originalSignalChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
        originalSignalChart.setTitle("Señal original");
        originalSignalChart.setAnimated(false);
        originalSignalChart.setCreateSymbols(false);

        // FFT signal GUI
        fftSignalChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
        fftSignalChart.setTitle("FFT");
        fftSignalChart.setAnimated(false);
       // fftSignalChart.setCreateSymbols(false);

        // IFFT
        ifftSignalChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
        ifftSignalChart.setTitle("IFFT");
        ifftSignalChart.setAnimated(false);
        ifftSignalChart.setCreateSymbols(false);

        var scene = new Scene(new VBox(originalSignalChart, fftSignalChart, ifftSignalChart), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    void updateCharts(Complex[] readComplexAudio, Complex[] fft, Complex[] ifft) {
        // Original
        Series<Number, Number> originalSignalSeries = new Series<Number, Number>();
        for (int i = 0; i < readComplexAudio.length; i++) {
            originalSignalSeries.getData().add(new Data<Number, Number>(i, readComplexAudio[i].getReal()));
        }
        originalSignalChart.getData().clear();
        originalSignalSeries.setName("Original");
        originalSignalChart.getData().addAll(originalSignalSeries);

        // FFT
        Series<Number, Number> fftSignalSeries = new Series<Number, Number>();
        for (int i = 0; i < fft.length; i++) {
            if(getAmplitude(fft[i])!=0.0d)
                fftSignalSeries.getData().add(new Data<Number, Number>(getPhase(fft[i]), getAmplitude(fft[i])));
        }

        fftSignalChart.getData().clear();
        fftSignalSeries.setName("FFT");
        fftSignalChart.getData().addAll(fftSignalSeries);

        // IFTT
        Series<Number, Number> ifftSignalSeries = new Series<Number, Number>();
        for (int i = 0; i < ifft.length; i++) {
            ifftSignalSeries.getData().add(new Data<Number, Number>(i, ifft[i].getReal()));
        }
        ifftSignalChart.getData().clear();
        ifftSignalSeries.setName("ifft");
        ifftSignalChart.getData().addAll(ifftSignalSeries);

    }

    private double getAmplitude(Complex complex) {
        return FastMath.sqrt(FastMath.pow(complex.getReal(), 2.0d) + FastMath.pow(complex.getImaginary(), 2.0d));
    }

    private double getPhase(Complex complex){
        return FastMath.atan2(complex.getImaginary(), complex.getReal());
    }

}