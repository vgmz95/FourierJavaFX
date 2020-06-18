# FourierJavaFX
Este proyecto en Java calcula la FFT y IFFT de un archivo WAV, graficando el proceso utilizando JavaFX, el nombre del archivo se especifica por línea de comandos. El archivo resultante se almacena en la misma carpeta del archivo original y se le concatena la palabra "SALIDA".
## Compilación y ejecución
Para poder ejecutar el proyecto se debe de contar con maven instalado y JDK 8+.\
Para compilar se ejecuta el siguiente comando en una terminal:\
`mvn clean compile`\
Para ejecutar el programa:\
`mvn exec:java -Dexec.mainClass="com.cic.analisisdealgoritmos.App" -Dexec.args="ruta del archivo"`\
Un ejemplo de ejecución seria: \
`mvn exec:java -Dexec.mainClass="com.cic.analisisdealgoritmos.App" -Dexec.args="./demos/AM.wav"`\

## Capturas de pantalla
### Archivo sine.wav
![Señal senoidal](img/sine.png)
### Archivo AM.wav
![Señal de AM](img/AM.png)
### Archivo ImperialMarch60.wav
![Archivo de audio](img/ImperialMarch60.png)

## Video

