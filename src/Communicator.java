/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import gnu.io.*;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Communicator implements SerialPortEventListener
{
    //passed from main GUI
    Fenetre fenetre =null;

    //for containing the ports that will be found
    private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();

    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;

    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean bConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;
    public Tracer tracer=new Tracer();

    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";
    String completData="";
    Double doubleData;
    JOptionPane erreurDialog = new JOptionPane();
    int i=0;

    public Communicator(Fenetre fenetre)
    {
        this.fenetre=fenetre;
    }

    //search for all the serial ports
    //pre: none
    //post: adds all the found ports to a combo box on the GUI
    public void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
               System.out.println("--"+curPort.getName());
               fenetre.comboListPort.addItem(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
		
    }

    //connect to the selected port in the combo box
    //pre: ports are already found by using the searchForPorts method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect()
    {
        String selectedPort = (String) fenetre.comboListPort.getSelectedItem();
        
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);

        CommPort commPort = null;
        
        try
        {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("Main", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;
            
            //for controlling GUI elements
            setConnected(true);
            
            if(fenetre.configChanged){
            	setPortParametres(fenetre.BaudeRate, fenetre.dataBits, fenetre.stopBits,fenetre.parity,fenetre.ctrlFlux);
            }else{
            	setPortParametres(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,SerialPort.PARITY_NONE,SerialPort.FLOWCONTROL_NONE);
            }
           
            System.out.println("Succé de configuration\nPort : "+fenetre.comboListPort.getSelectedItem().toString()+".\nBaude Rate : "+fenetre.BaudeRate+".\nNombre de bits : "+fenetre.dataBits+".\n");
            
            //logging
            logText = "LOG:"+selectedPort + " ouvert avec succés.";
           fenetre.donneesRecues.setForeground(new Color(0,210,0));
           System.out.println(logText + "\n");
           fenetre.donneesRecues.append(logText+"\n");
            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

            //enables the controls on the GUI if a successful connection is made
            //window.keybindingController.toggleControls();
           fenetre.connectBouton.setEnabled(false);
           fenetre.disconnectBouton.setEnabled(true);
        }
        catch (PortInUseException e)
        {
            logText = selectedPort + " est occupé par une autre application. (" + e.toString() + ")\n";
            fenetre.connectBouton.setEnabled(true);
            fenetre.disconnectBouton.setEnabled(false);
            fenetre.setEnabledComponent(true);
            JOptionPane.showMessageDialog(null, selectedPort + " est occupé par une autre application.", "Erreur",JOptionPane.ERROR_MESSAGE);
            fenetre.donneesRecues.setForeground(Color.RED);
            fenetre.donneesRecues.append("LOG:"+selectedPort + " est occupé par une autre application.\n");
           System.out.println(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "LOG:Erreur d'ouverture du port " + selectedPort + "(" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, "Erreur d'ouverture du port " + selectedPort, "Erreur",JOptionPane.ERROR_MESSAGE);
            System.out.println(logText + "\n");
            fenetre.donneesRecues.setText("LOG:Erreur d'ouverture du port " + selectedPort+"\n");
            fenetre.donneesRecues.setForeground(Color.RED);
            fenetre.donneesRecues.setBackground(Color.red);
        }
    }

    //open the input and output streams
    //pre: an open port
    //post: initialized intput and output streams for use to communicate data
    public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            writeData(0, 0);
            
            successful = true;
            return successful;
        }
        catch (IOException e) {
            logText = "LOG:I/O Streams failed to open. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, "I/O Streams failed to open.", "Erreur",JOptionPane.ERROR_MESSAGE);
            fenetre.donneesRecues.setForeground(Color.red);
            System.out.println(logText + "\n");
            fenetre.donneesRecues.setText("LOG:I/O Streams failed to open.\n");
            return successful;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre: an open serial port
    //post: an event listener for the serial port that knows when data is recieved
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            logText = "LOG:Too many listeners. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, "Too many listeners.", "Erreur",JOptionPane.ERROR_MESSAGE);
            fenetre.donneesRecues.setForeground(Color.red);
            System.out.println(logText + "\n");
            fenetre.donneesRecues.setText("LOG:Too many listeners.\n");
        }
    }

    //disconnect the serial port
    //pre: an open serial port
    //post: clsoed serial port
    public void disconnect()
    {
        //close the serial port
        try
        {
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);
            logText = "LOG:Disconnected.\n";
            fenetre.donneesRecues.append("\n"+logText);
            fenetre.donneesRecues.setForeground(Color.yellow);
            System.out.println(logText + "\n");
            fenetre.disconnectBouton.setEnabled(false);
            fenetre.connectBouton.setEnabled(true);
        }
        catch (Exception e)
        {
            logText = "LOG:Erreur de fermuture. " + serialPort.getName() + "(" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, "Erreur de fermuture de " + serialPort.getName() , "Erreur",JOptionPane.ERROR_MESSAGE);
            fenetre.donneesRecues.append("LOG:Erreur de fermuture.\n");
            fenetre.donneesRecues.setForeground(new Color(255,161,161));
            System.out.println(logText + "\n");
        }
    }

    final public boolean getConnected()
    {
        return bConnected;
    }

    public void setConnected(boolean bConnected)
    {
        this.bConnected = bConnected;
    }

    //what happens when data is received
    //pre: serial event is triggered
    //post: processing on the data it reads
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte singleData = (byte)input.read();
                if (singleData != NEW_LINE_ASCII)
                {
                	// Lecture d'un symbole et le met dans logText
                    logText = new String(new byte[] {singleData});
                    // Cumule des symboles lus dans une chaine de caractères completData :
                    completData+=logText;
                    // affichage du symbole lu dans la zone TextArea :
                    	fenetre.donneesRecues.append(logText);
                    // Colorage du texte dans la zone TextArea par le vert :
                    	fenetre.donneesRecues.setForeground(Color.green);
                }
                else
                {
                	/*
                	 * System.out.println("completData = "+completData);
                	*/ 
                	// Affichage d'une ligne complete (chaine completData) :
                	if( completData!="" && completData.length()>0 && completData!="\n" && completData!=null){
	                	try{
	                		doubleData=Double.parseDouble(completData);
	                		Tracer.plot(doubleData);
	                		System.out.println("conversion = "+doubleData);
	                		completData="";
	                	}catch (NumberFormatException e){
	                		//System.err.println("LOG:Erreur de conversion String=>Double : "+completData);
	                		//this.disconnect();
	                		//this.connect();
	                		completData="";
	                	}finally{
	                	}
                	}else{
                		completData="";
                	}

                	fenetre.donneesRecues.append("\n");
                }
               
            }
            catch (Exception e)
            {
                logText = "LOG:Erreur de lecture de données. (" + e.toString() + ")";
                JOptionPane.showMessageDialog(null, "Erreur de lecture de données." , "Erreur",JOptionPane.ERROR_MESSAGE);
                fenetre.donneesRecues.setForeground(Color.red);
                System.out.println(logText + "\n");
                fenetre.donneesRecues.setText("LOG:Erreur de lecture de données.\n");
                
            }
        } 
    }

    //method that can be called to send data
    //pre: open serial port
    //post: data sent to the other device
    public void writeData(int leftThrottle, int rightThrottle)
    {
        try
        {
        	try {
        		output.write((fenetre.donneesEnv.getText().toString()+"\n").getBytes("US-ASCII"));
        		//fenetre.donneesRecues.append("Donnée ("+fenetre.donneesEnv.getText().toString()+") envoyé avec succés");
        	}
			catch (IOException e4) {}
        }
        catch (Exception e)
        {
            logText = "LOG:Erreur lors de l'écriture. (" + e.toString() + ")";
            fenetre.donneesRecues.setForeground(Color.red);
            System.out.println(logText + "\n");
            fenetre.donneesRecues.setText("LOG:Erreur lors de l'écriture.\n");
        }
    }
  // Fonction de configuration du port :  
  public void setPortParametres(int baudeRate,int nbreBits,int bitStop,int parity,int ctrlFlux){
	  try {
		  // config baude rate , nbre bits , bit de stop , parité
		  serialPort.setSerialPortParams(baudeRate, nbreBits, bitStop, parity);
		  // config controle de flux :
		  serialPort.setFlowControlMode(ctrlFlux);
		  // message informatif
		  fenetre.donneesRecues.append("Configuration du port faite avec succés\n");
		  System.out.println("Configuration du port faite avec succés");
	} catch (UnsupportedCommOperationException e) {
			// erreur de configuration :
		  JOptionPane.showMessageDialog(null, "Erreur de configuration." , "Erreur",JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
  }
}
