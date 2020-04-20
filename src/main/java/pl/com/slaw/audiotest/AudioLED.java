/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.com.slaw.audiotest;
import com.tagtraum.jipes.audio.FFT;
import com.tagtraum.jipes.math.FFTFactory;
import com.tagtraum.jipes.math.Transform;
import javax.sound.sampled.*;
/**
 *
 * @author slaw
 */
public class AudioLED {

    private static final float NORMALIZATION_FACTOR_2_BYTES = Short.MAX_VALUE + 1.0f;

    public static void main(final String[] args) throws Exception {
        // use only 1 channel, to make this easier
        final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
        final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        final TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open();
        targetLine.start();
        final AudioInputStream audioStream = new AudioInputStream(targetLine);

        final byte[] buf = new byte[256]; // <--- increase this for higher frequency resolution
        final int numberOfSamples = buf.length / format.getFrameSize();
        
        final Transform fft = FFTFactory.getInstance().create(numberOfSamples);
        
        
        while (true) {
            // in real impl, don't just ignore how many bytes you read
            audioStream.read(buf);
            // the stream represents each sample as two bytes -> decode
            final float[] samples = decode(buf, format);
            final float[][] transformed = fft.transform(samples);
            final float[] realPart = transformed[0];
            final float[] imaginaryPart = transformed[1];
            final double[] magnitudes = toMagnitudes(realPart, imaginaryPart);
            
            double max = magnitudes[0];
            for(int i=0;i<magnitudes.length;i++)
            {
                //System.out.println("magnitudes[" + i + "]: " + magnitudes[i]);
                
                if (magnitudes[i] > max) {
                    max = magnitudes[i];
                }
            }
            // do something with magnitudes...
            System.out.println("max: " + max);
        }
    }

    private static float[] decode(final byte[] buf, final AudioFormat format) {
        final float[] fbuf = new float[buf.length / format.getFrameSize()];
        for (int pos = 0; pos < buf.length; pos += format.getFrameSize()) {
            final int sample = format.isBigEndian()
                    ? byteToIntBigEndian(buf, pos, format.getFrameSize())
                    : byteToIntLittleEndian(buf, pos, format.getFrameSize());
            // normalize to [0,1] (not strictly necessary, but makes things easier)
            fbuf[pos / format.getFrameSize()] = sample / NORMALIZATION_FACTOR_2_BYTES;
        }
        return fbuf;
    }

    private static double[] toMagnitudes(final float[] realPart, final float[] imaginaryPart) {
        final double[] powers = new double[realPart.length / 2];
        for (int i = 0; i < powers.length; i++) {
            powers[i] = Math.sqrt(realPart[i] * realPart[i] + imaginaryPart[i] * imaginaryPart[i]);
        }
        return powers;
    }

    private static int byteToIntLittleEndian(final byte[] buf, final int offset, final int bytesPerSample) {
        int sample = 0;
        for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
            final int aByte = buf[offset + byteIndex] & 0xff;
            sample += aByte << 8 * (byteIndex);
        }
        return sample;
    }

    private static int byteToIntBigEndian(final byte[] buf, final int offset, final int bytesPerSample) {
        int sample = 0;
        for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
            final int aByte = buf[offset + byteIndex] & 0xff;
            sample += aByte << (8 * (bytesPerSample - byteIndex - 1));
        }
        return sample;
    }

}