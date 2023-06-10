package org_example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
/**
 *@author Kayrat Japarbek
 */

public class VideoConverter {
    private String ffmpegPath;
    private String outputDirectory;
    private List<VideoAudioPair> videoAudioPairs;

    public VideoConverter(String ffmpegPath, String outputDirectory) {
        this.ffmpegPath = ffmpegPath;
        this.outputDirectory = outputDirectory;
        this.videoAudioPairs = new ArrayList<>();
    }

    public void addVideoAudioPair(String videoFile, String audioFile) {
        videoAudioPairs.add(new VideoAudioPair(videoFile, audioFile));
    }

    public void convertVideos() {
        int index = 1;
        for (VideoAudioPair pair : videoAudioPairs) {
            String videoFile = pair.getVideoFile();
            String audioFile = pair.getAudioFile();
            String outputFile = generateOutputFilePath(videoFile, index);

            String[] command = {ffmpegPath, "-i", videoFile, "-i", audioFile, "-c", "copy", "-shortest", outputFile};

            try {
                Process process = Runtime.getRuntime().exec(command);

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.out.println(errorLine);
                }

                BufferedReader progressReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String progressLine;
                while ((progressLine = progressReader.readLine()) != null) {
                    System.out.println(progressLine);
                }

                int exitCode = process.waitFor();
                System.out.println("Конвертирование завершено. Код завершения: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            index++;
        }
    }

    private String generateOutputFilePath(String videoFile, int index) {
        Path outputPath = Paths.get(outputDirectory, generateOutputFileName(videoFile, index));
        return outputPath.toString();
    }

    private String generateOutputFileName(String videoFile, int index) {
        String prefix = index + "_";
        int lastSlashIndex = videoFile.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            lastSlashIndex = videoFile.lastIndexOf("\\");
        }
        String fileName = videoFile.substring(lastSlashIndex + 1);
        return prefix + fileName;
    }

    static class VideoAudioPair {
        private final String videoFile;
        private final String audioFile;

        public VideoAudioPair(String videoFile, String audioFile) {
            this.videoFile = videoFile;
            this.audioFile = audioFile;
        }

        public String getVideoFile() {
            return videoFile;
        }

        public String getAudioFile() {
            return audioFile;
        }
    }

    /**
     * Главный метод, отвечающий за запуск программы
     */
    public static void main(String[] args) {
        String ffmpegPath = "C:\\Program Files\\ffmpeg\\bin\\ffmpeg.exe";
        String outputDirectory = "C:\\output_directory\\";

        // Создание экземпляра VideoConverter с указанием путей к ffmpeg и директории вывода
        VideoConverter converter = new VideoConverter(ffmpegPath, outputDirectory);

        // Добавление видео-аудио пар в список для конвертации
        converter.addVideoAudioPair("src/main/resources/video1.mp4", "src/main/resources/audio1.mp4");
        converter.addVideoAudioPair("src/main/resources/video2.mp4", "src/main/resources/audio2.mp4");

        // Запуск процесса конвертации видео
        converter.convertVideos();
    }
}
