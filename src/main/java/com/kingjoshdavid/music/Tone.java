package com.kingjoshdavid.music;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {

    public static void main(String[] args) throws LineUnavailableException {
        Note.values();
//        System.exit(0);
        final AudioFormat af =
                new AudioFormat(META.SAMPLE_RATE, 8, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        line.open(af, META.SAMPLE_RATE);
        line.start();
        int played = 0;
        for (Note n : Note.values()) {
            played += play(line, n, 500);
            played += play(line, Note.RST, 10);
        }
        line.drain();
        line.stop();
        line.drain();
        line.close();
    }

    private static int play(SourceDataLine line, Note note, int ms) {
        return line.write(note.data(), 0, META.SAMPLE_RATE * ms / 1000);
    }
}

class META {
    public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
    public static final int MAX_SECONDS = 2;
    public static final float MAX_BYTE = Byte.MAX_VALUE;
    public static final double TAU = 2.0 * Math.PI;
    public static final float VOLUME_SCALE = 0.01f;
    public static double START_FREQ = 200;
}

enum Note {

    RST(0),
    A4_(META.START_FREQ * Math.pow(2d, ((double) 1 - 1) / 12d)),
    A4$(META.START_FREQ * Math.pow(2d, ((double) 2 - 1) / 12d)),
    B4_(META.START_FREQ * Math.pow(2d, ((double) 3 - 1) / 12d)),
    C4_(META.START_FREQ * Math.pow(2d, ((double) 4 - 1) / 12d)),
    C4$(META.START_FREQ * Math.pow(2d, ((double) 5 - 1) / 12d)),
    D4_(META.START_FREQ * Math.pow(2d, ((double) 6 - 1) / 12d)),
    D4$(META.START_FREQ * Math.pow(2d, ((double) 7 - 1) / 12d)),
    E4_(META.START_FREQ * Math.pow(2d, ((double) 8 - 1) / 12d)),
    F4_(META.START_FREQ * Math.pow(2d, ((double) 9 - 1) / 12d)),
    F4$(META.START_FREQ * Math.pow(2d, ((double) 10 - 1) / 12d)),
    G4_(META.START_FREQ * Math.pow(2d, ((double) 11 - 1) / 12d)),
    G4$(META.START_FREQ * Math.pow(2d, ((double) 12 - 1) / 12d)),
    A5_(META.START_FREQ * Math.pow(2d, ((double) 13 - 1) / 12d));

    private byte[] sinWaveBytes = new byte[META.MAX_SECONDS * META.SAMPLE_RATE];

    Note(double frequency) {
        if (frequency > 0) {
            for (int i = 0; i < sinWaveBytes.length; i++) {
                double period = (double) META.SAMPLE_RATE / frequency;
                double angle = META.TAU * i / period;
                sinWaveBytes[i] = (byte) (Math.sin(angle) * META.MAX_BYTE * META.VOLUME_SCALE);
            }
        }
    }

    public byte[] data() {
        return sinWaveBytes;
    }
}