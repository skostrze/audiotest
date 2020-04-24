package pl.com.slaw.jspect;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

/*
 * Current version shows the values at the appropriate single frequencies 
 *	based on a segmentation of the exponential display scale.
 *	
 * Need to average all the "skipped-over" frequency information relative
 *	to each segment, then display that. This is getting intense for java
 *	already somewhat slow. 
 *
 * Possibly need to make the display a thread that runs around 60Hz, whenever
 * 	fft is performed, update the display vector. Still gonna be O(n^2) or 
 * 	something in this slower region of execution. Now when will I get 
 * 	around to actually finishing this thing?
 *
 * ========= 5.30.03 ========================================================
 * 
 * Need to use a windowing function? - www.bores.com //jacker's link
 * 
 * ========= 6.09.03 ========================================================
 *
 * Need to more elegantly perform paint functions, need to show averages
 *	of covered area, not the "spot" values. Do I really need to thread
 *	out this display panel? Probably...
 * 
 **/ 

public class Rta_canvas extends JPanel implements Runnable
{
        final int mult = 16;
	float[] data = new float[8192];
	float[] spec = new float[8192];
	FastFourierTransform fft = new FastFourierTransform();
	Thread displayThread;

	public Rta_canvas(){
	}
	
	public void start(){
		if(displayThread == null){
			displayThread = new Thread(this);
			displayThread.start();
		}
	}

	public void run(){}

	public void stop(){
		displayThread = null;
	}
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		g.setColor(Color.black);
		g.fillRect(0,0,800,400);
		g.setColor(Color.white);		
		g.drawString("JSpect FFT: N=8192, FR=5.4Hz",20,20);
		

		
		// draw horizontal(frequency) scale
		g.drawLine(30,360,660,360);
		g.drawLine(30,360,30,355); 
		g.drawString("16Hz",32,357);
		g.drawLine(90,360,90,355);
		g.drawString("32Hz",92,357);
		g.drawLine(150,360,150,355);
		g.drawString("64Hz",152,357);
		g.drawLine(210,360,210,355);
		g.drawString("128Hz",212,357);
		g.drawLine(270,360,270,355);
		g.drawString("256Hz",272,357);
		g.drawLine(330,360,330,355);
		g.drawString("512kHz",332,357);
		g.drawLine(390,360,390,355);
		g.drawString("1kHz",392,357);
		g.drawLine(450,360,450,355);
		g.drawString("2kHz",452,357);
		g.drawLine(510,360,510,355);
		g.drawString("4kHz",512,357);
		g.drawLine(570,360,570,355);
		g.drawString("8kHz",572,357);
		g.drawLine(630,360,630,355);
		g.drawString("16kHz",632,357);
		g.drawLine(660,360,660,355);
		
		g.setColor(Color.green);

		g.drawLine(30,343,660,343);
		
		// y scale multiplier/divisor, size adjuster
		
		
		// draw frequency information (slow!)
		g.fillRect(30,340-(int)spec[2]/mult,20,3); //16Hz
		g.fillRect(50,340-(int)spec[3]/mult,20,3);
		g.fillRect(70,340-(int)spec[4]/mult,20,3);
		g.fillRect(90,340-(int)spec[5]/mult,10,3); //32Hz
		g.fillRect(100,340-(int)spec[6]/mult,10,3);
		g.fillRect(110,340-(int)spec[7]/mult,10,3);
		g.fillRect(120,340-(int)spec[8]/mult,10,3);
		g.fillRect(130,340-(int)spec[9]/mult,10,3);
		g.fillRect(140,340-(int)spec[10]/mult,10,3);
		g.fillRect(150,340-(int)spec[11]/mult,10,3);
		g.fillRect(160,340-(int)spec[12]/mult,10,3); //64Hz
		g.fillRect(170,340-(int)spec[14]/mult,10,3);
		g.fillRect(180,340-(int)spec[16]/mult,10,3);
		g.fillRect(190,340-(int)spec[18]/mult,10,3);
		g.fillRect(200,340-(int)spec[20]/mult,10,3);
		g.fillRect(210,340-(int)spec[24]/mult,10,3);
		g.fillRect(220,340-(int)spec[26]/mult,10,3); //128Hz
		g.fillRect(230,340-(int)spec[30]/mult,10,3);
		g.fillRect(240,340-(int)spec[34]/mult,10,3);
		g.fillRect(250,340-(int)spec[38]/mult,10,3);
		g.fillRect(260,340-(int)spec[42]/mult,10,3);
		g.fillRect(270,340-(int)spec[48]/mult,10,3); //256Hz
		g.fillRect(280,340-(int)spec[56]/mult,10,3);
		g.fillRect(290,340-(int)spec[64]/mult,10,3);
		g.fillRect(300,340-(int)spec[72]/mult,10,3);
		g.fillRect(310,340-(int)spec[80]/mult,10,3);
		g.fillRect(320,340-(int)spec[88]/mult,10,3);
		g.fillRect(330,340-(int)spec[96]/mult,10,3); //512Hz
		g.fillRect(340,340-(int)spec[112]/mult,10,3);
		g.fillRect(350,340-(int)spec[128]/mult,10,3);
		g.fillRect(360,340-(int)spec[144]/mult,10,3);
		g.fillRect(370,340-(int)spec[160]/mult,10,3);
		g.fillRect(380,340-(int)spec[176]/mult,10,3);
		g.fillRect(390,340-(int)spec[192]/mult,10,3); //1kHz
		g.fillRect(400,340-(int)spec[224]/mult,10,3);
		g.fillRect(410,340-(int)spec[256]/mult,10,3);
		g.fillRect(420,340-(int)spec[288]/mult,10,3);
		g.fillRect(430,340-(int)spec[320]/mult,10,3);
		g.fillRect(440,340-(int)spec[352]/mult,10,3); 
		g.fillRect(450,340-(int)spec[382]/mult,10,3); //2k
		g.fillRect(460,340-(int)spec[446]/mult,10,3);
		g.fillRect(470,340-(int)spec[510]/mult,10,3);
		g.fillRect(480,340-(int)spec[574]/mult,10,3);
		g.fillRect(490,340-(int)spec[638]/mult,10,3);
		g.fillRect(500,340-(int)spec[702]/mult,10,3);
		g.fillRect(510,340-(int)spec[766]/mult,10,3); //4k
		g.fillRect(520,340-(int)spec[894]/mult,10,3);
		g.fillRect(530,340-(int)spec[1022]/mult,10,3);
		g.fillRect(540,340-(int)spec[1150]/mult,10,3);
		g.fillRect(550,340-(int)spec[1278]/mult,10,3);
		g.fillRect(560,340-(int)spec[1406]/mult,10,3);
		g.fillRect(570,340-(int)spec[1534]/mult,10,3); //8k
		g.fillRect(580,340-(int)spec[1790]/mult,10,3);
		g.fillRect(590,340-(int)spec[2046]/mult,10,3);
		g.fillRect(600,340-(int)spec[2302]/mult,10,3);
		g.fillRect(610,340-(int)spec[2558]/mult,10,3);
		g.fillRect(620,340-(int)spec[2814]/mult,10,3);
		g.fillRect(630,340-(int)spec[3070]/mult,10,3); //16k
		g.fillRect(640,340-(int)spec[3582]/mult,10,3);
		g.fillRect(650,340-(int)spec[4094]/mult,10,3);
                
                

		//draw a level-meter, the average of all the amplitudes
		g.drawString(Integer.toString(levelSpec()),40,40);
                
                
                

		
		
	}

	/*
	 * levelSpec() - returns an int computed by averaging the spec vector
	 * 	i.e. the average amplitude across the frequency info
	 * 	returned by the fft
	 *
	 **********************/
	public int levelSpec(){
		int avg=0;
		for(int i=0;i<spec.length;i++){
			avg += (int)spec[i];
		}
		return avg/8192;
	}
	
	/*
	 * sendData() - computes fft on the float array and repaints the 
	 * 	display
	 * 
	 ****************************************/ 
	public void sendData(float[] b, int len) throws IOException
        {
		data = b;
		spec = fft.fftMag(b);
		repaint();
                
                if(levelSpec() > 10)
                    saveToFile("Soprano", true);
	}
        
        
        private void saveToFile(String name, boolean test) throws FileNotFoundException, IOException
        {
            String filename = "data/test-soprano.txt";
            
            if(!test)
                filename = "data/dbvoice.arff"; 
            
            BufferedWriter out = new BufferedWriter
            (
                new OutputStreamWriter(
                    new FileOutputStream(filename, true), // true to append
                    StandardCharsets.UTF_8                  // Set encoding
                )
            );
            
            StringBuilder sb = new StringBuilder(); 
            char comma = ',';
            
            sb.append(spec[2]/mult);
            sb.append(comma);
            sb.append(spec[3]/mult);
            sb.append(comma);
            sb.append(spec[4]/mult);
            sb.append(comma);
            sb.append(spec[5]/mult);
            sb.append(comma);
            sb.append(spec[6]/mult);
            sb.append(comma);
            sb.append(spec[7]/mult);
            sb.append(comma);
            sb.append(spec[8]/mult);
            sb.append(comma);
            sb.append(spec[9]/mult);            
            sb.append(comma);
            sb.append(spec[10]/mult);
            sb.append(comma);
            sb.append(spec[11]/mult);
            sb.append(comma);
            sb.append(spec[12]/mult);
            sb.append(comma);
            sb.append(spec[14]/mult);
            sb.append(comma);
            sb.append(spec[16]/mult);
            sb.append(comma);
            sb.append(spec[18]/mult);
            sb.append(comma);
            sb.append(spec[20]/mult);
            sb.append(comma);
            sb.append(spec[24]/mult);
            sb.append(comma);
            sb.append(spec[26]/mult);
            sb.append(comma);
            sb.append(spec[30]/mult);
            sb.append(comma);
            sb.append(spec[2]/mult);
            sb.append(comma);
            sb.append(spec[2]/mult);
            sb.append(comma);
            sb.append(spec[2]/mult);
            sb.append(comma);
            sb.append(spec[2]/mult);
            sb.append(comma);
            sb.append(spec[34]/mult);
            sb.append(comma);
            sb.append(spec[38]/mult);
            sb.append(comma);
            sb.append(spec[42]/mult);
            sb.append(comma);
            sb.append(spec[48]/mult); //256Hz
            sb.append(comma);
            sb.append(spec[56]/mult);
            sb.append(comma);
            sb.append(spec[64]/mult);
            sb.append(comma);
            sb.append(spec[72]/mult);
            sb.append(comma);
            sb.append(spec[80]/mult);
            sb.append(comma);
            sb.append(spec[88]/mult);
            sb.append(comma);
            sb.append(spec[96]/mult); //512Hz
            sb.append(comma);
            sb.append(spec[112]/mult);
            sb.append(comma);
            sb.append(spec[128]/mult);
            sb.append(comma);
            sb.append(spec[144]/mult);
            sb.append(comma);
            sb.append(spec[160]/mult);
            sb.append(comma);
            sb.append(spec[176]/mult);
            sb.append(comma);
            sb.append(spec[192]/mult); //1kHz
            sb.append(comma);
            sb.append(spec[224]/mult);
            sb.append(comma);
            sb.append(spec[256]/mult);
            sb.append(comma);
            sb.append(spec[288]/mult);
            sb.append(comma);
            sb.append(spec[320]/mult);
            sb.append(comma);
            sb.append(spec[352]/mult); 
            sb.append(comma);
            sb.append(spec[382]/mult); //2k
            sb.append(comma);
            sb.append(spec[446]/mult);
            sb.append(comma);
            sb.append(spec[510]/mult);
            sb.append(comma);
            sb.append(spec[574]/mult);
            sb.append(comma);
            sb.append(spec[638]/mult);
            sb.append(comma);
            sb.append(spec[702]/mult);
            sb.append(comma);
            sb.append(spec[766]/mult); //4k
            sb.append(comma);
            sb.append(spec[894]/mult);
            sb.append(comma);
            sb.append(spec[1022]/mult);
            sb.append(comma);
            sb.append(spec[1150]/mult);
            sb.append(comma);
            sb.append(spec[1278]/mult);
            sb.append(comma);
            sb.append(spec[1406]/mult);
            sb.append(comma);
            sb.append(spec[1534]/mult); //8k
            sb.append(comma);
            sb.append(spec[1790]/mult);
            sb.append(comma);
            sb.append(spec[2046]/mult);
            sb.append(comma);
            sb.append(spec[2302]/mult);
            sb.append(comma);
            sb.append(spec[2558]/mult);
            sb.append(comma);
            sb.append(spec[2814]/mult);
            sb.append(comma);
            sb.append(spec[3070]/mult); //16k
            sb.append(comma);
            sb.append(spec[3582]/mult);
            sb.append(comma);
            sb.append(spec[4094]/mult);
            sb.append(comma);
            sb.append(name);
            sb.append("\n");
                
            
            
            
            out.write(sb.toString());
            out.close();
        }

}
