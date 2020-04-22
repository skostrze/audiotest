package pl.com.slaw.jspect;



/*
 * Java Core libarary calls
 */ 
import java.awt.*;
import javax.swing.*;


public class Sv	extends JFrame
{	
	//
	// this class will be the interface
	//	so we now need a thread for the
	//	capture of data
	//
	public Sv(){
		super("JSpect Spectrum Analysis");
		
		getContentPane().setLayout(new BorderLayout());
		
		try
                {
		
                    // set-up rta canvas, should be a class
                    Rta_canvas rt = new Rta_canvas();
                    rt.setSize(800,400);
                    getContentPane().add("Center",rt);

                    // create capture thread and start, need to link to Rta_canvas

                    Input_source i = new Input_source(rt);
                    i.start();
				
		}catch(Exception e){}
		
	}

	public static void main(String[] args){
		JFrame s = new Sv();
			
		s.setDefaultCloseOperation( EXIT_ON_CLOSE);
		s.pack();
		s.setSize(800,400);
		s.setVisible(true);
		s.setResizable(false);
	}


}
