/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.com.slaw.audiotest;
import javax.sound.sampled.*;
import pl.com.slaw.jspect.FastFourierTransform;
/**
 *
 * @author slaw
 */
public class AudioLED 
{

    private static final float NORMALIZATION_FACTOR_2_BYTES = Short.MAX_VALUE + 1.0f;

    public static void main(final String[] args) throws Exception 
    {        
        
        // use only 1 channel, to make this easier
        final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
        final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        final int mult = 16;
        final FastFourierTransform fft = new FastFourierTransform();
        
        line.open();
        line.start();
        
        
        final AudioInputStream audioStream = new AudioInputStream(line);

        final byte[] buf = new byte[16384]; // <--- increase this for higher frequency resolution
        final int numberOfSamples = buf.length / format.getFrameSize();
                        
        
        while (true) {            
            int num = audioStream.read(buf, 0,16384);            
            float[] data = new float[8192];
            float[] spec = new float[8192];
            
            for(int i=0;i<data.length;i++)
            {
                data[i] = buf[i*2+1] & (buf[i*2] << 8);
                                  
                spec = fft.fftMag(data);
                int level = levelSpec(spec); 
                
                //System.out.printf("level: " + level);                                
            }
            
        }
        
        
    }
    
    
    
    public static int levelSpec(float[] spec){
        int avg=0;
        
        for(int i=0;i<spec.length;i++)
        {
                avg += (int)spec[i];
        }
        return avg/8192;
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