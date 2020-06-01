package com.cic.analisisdealgoritmos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.cic.analisisdealgoritmos.fourier.Fourier;
import com.cic.analisisdealgoritmos.ioaudio.ComplexAudioReader;
import com.cic.analisisdealgoritmos.ioaudio.ComplexAudioWriter;

import org.apache.commons.math3.complex.Complex;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * JavaFX Fourier App
 */
public class App extends Application {
    // Dymanic GUI objects
    // Charts
    LineChart<Number, Number> originalSignalChart, ifftSignalChart;
    ScatterChart<Number, Number> fftSignalChart;
    // Current sample index testbox
    Text currentIndexText;
    // Sample lists
    List<Complex[]> originalSamples, fftSamples, ifftSamples;
    int currentIndex = 0, maxIndex = 0;
    // Paths
    String inputPath = "C:\\Users\\victo\\Documents\\LINUX\\Music\\sin_1000Hz_-6dBFS_3s.wav";
    String outputPath = "C:\\Users\\victo\\Documents\\LINUX\\Music\\sin_1000Hz_-6dBFS_3sCOPY.wav";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        initializeGUI(stage);
        // Analize audio data on a separate thread
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws UnsupportedAudioFileException, IOException {
                processAudio();
                maxIndex = originalSamples.size();
                if (maxIndex > 0) {
                    Platform.runLater(() -> updateGUI());
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void initializeGUI(Stage stage) {
        // Windows title
        stage.setTitle("FFT dividir y vencer");
        // Original signal GUI
        originalSignalChart = initializeChart("t", "f(t)", "Señal original");
        // FFT signal GUI
        fftSignalChart = initializeFftChart("Fase", "Amplitud", "FFT");
        // IFFT signal GUI
        ifftSignalChart = initializeChart("t", "f(t)", "IFFT");

        // *******Top Pane********** //
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        // Hbox components
        // Title
        Text filenameDisplay = new Text(inputPath);
        filenameDisplay.setFill(Color.WHITE);
        filenameDisplay.setFont(Font.font(15));
        // Show next sample button
        Button beforeSample = new Button("Muestra anterior");
        beforeSample.setPrefSize(140, 20);
        beforeSample.setOnAction((ActionEvent e) -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateGUI();
            }
        });
        // Show before sample button
        Button nextSample = new Button("Muestra siguiente");
        nextSample.setPrefSize(140, 20);
        nextSample.setOnAction((ActionEvent e) -> {
            if (currentIndex < maxIndex - 1) {
                currentIndex++;
                updateGUI();
            }
        });
        // Current sample index
        currentIndexText = new Text("?/?");
        currentIndexText.setFill(Color.WHITE);
        currentIndexText.setFont(Font.font(15));
        hbox.getChildren().addAll(filenameDisplay, beforeSample, nextSample, currentIndexText);

        Scene scene = new Scene(new VBox(hbox, originalSignalChart, fftSignalChart, ifftSignalChart), 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

    void updateGUI() {
        currentIndexText.setText((currentIndex + 1) + "/" + (maxIndex));
        updateCharts(originalSamples.get(currentIndex), fftSamples.get(currentIndex), ifftSamples.get(currentIndex));
    }

    LineChart<Number, Number> initializeChart(String xAxisLabel, String yAxisLabel, String title) {
        final NumberAxis xAxis = new NumberAxis(0.0, (double) ComplexAudioReader.sampleSize, 64.0),
                yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
        LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        return chart;
    }

    ScatterChart<Number, Number> initializeFftChart(String xAxisLabel, String yAxisLabel, String title) {
        final NumberAxis xAxis = new NumberAxis(), yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
        ScatterChart<Number, Number> chart = new ScatterChart<Number, Number>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setAnimated(false);
        return chart;
    }

    void updateSignalChart(LineChart<Number, Number> chart, Complex[] data, String name) {
        Series<Number, Number> serie = new Series<Number, Number>();
        serie.setName(name);
        for (int i = 0; i < data.length; i++) {
            serie.getData().add(new Data<Number, Number>(i, data[i].getReal()));
        }
        chart.getData().clear();
        chart.getData().addAll(serie);
    }

    void updateTransformedChart(ScatterChart<Number, Number> fftSignalChart, Complex[] data, String name) {
        Series<Number, Number> serie = new Series<Number, Number>();
        serie.setName(name);
        for (int i = 0; i < data.length; i++) {
            serie.getData().add(new Data<Number, Number>(Fourier.getPhase(data[i]), Fourier.getAmplitude(data[i])));
        }
        fftSignalChart.getData().clear();
        fftSignalChart.getData().addAll(serie);
    }

    void updateCharts(Complex[] readComplexAudio, Complex[] fft, Complex[] ifft) {
        // Original
        updateSignalChart(originalSignalChart, readComplexAudio, "Señal original");
        // FFT
        updateTransformedChart(fftSignalChart, fft, "fft");
        // IFTT
        updateSignalChart(ifftSignalChart, ifft, "ifft");
    }

    void processAudio() throws UnsupportedAudioFileException, IOException {
        originalSamples = new ArrayList<>();
        fftSamples = new ArrayList<>();
        ifftSamples = new ArrayList<>();
        try (ComplexAudioReader audioReader = new ComplexAudioReader(inputPath);
                ComplexAudioWriter audioWriter = new ComplexAudioWriter(outputPath, audioReader.getFormat());) {
            Complex[] readComplexAudio;
            while ((readComplexAudio = audioReader.readComplexAudioFile()) != null) {
                originalSamples.add(readComplexAudio);
                Complex[] fft = Fourier.fft(readComplexAudio); // Get FFT
                fftSamples.add(fft);
                Complex[] ifft = Fourier.ifft(fft); // Get IFFT
                ifftSamples.add(ifft);
                audioWriter.writeToBuffer(ifft);// Write IFFT data to file buffer
            }
            audioWriter.saveToFile(); // Commit to file
        }
    }

}