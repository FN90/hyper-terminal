import gnu.io.SerialPort;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import org.math.plot.Plot2DPanel;


public class Fenetre extends JFrame {
	
	public boolean savedConfig=false;
	public boolean configChanged=false;
	public JLabel listPortLabel;
	public JComboBox comboListPort;
	public JLabel baudeRateLabel;
	public JComboBox comboBaudeRate;
	public JLabel dataBitsLabel;
	public JComboBox comboDataBits;
	public JRadioButton parityNone;
	public JRadioButton parityPaire;
	public JRadioButton parityImpaire;
	public ButtonGroup parityGroupe;
	public ButtonGroup bitStopGroupe;
	public JRadioButton bitStop_1;
	public JRadioButton bitStop_1_5;
	public JRadioButton bitStop_2;
	public JCheckBox fluxNone;
	public JComboBox comboFlux;
	public JButton enregConfig;
	public JButton defautConfig;
	public JButton connectBouton;
	public JButton disconnectBouton;
	public JButton envoiBouton;
	public JTextField  donneesEnv;
	JButton affGraphique;
	JPanel caractPanel=new JPanel();
	public JTextArea donneesRecues=new JTextArea();
	// Pour que le scroll affiche le dernier message toujours
	DefaultCaret caret = (DefaultCaret)donneesRecues.getCaret();
	
	int BaudeRate=9600;
	int dataBits=SerialPort.DATABITS_8;
	int stopBits=SerialPort.STOPBITS_1;
	int parity=SerialPort.PARITY_NONE;
	int ctrlFlux=SerialPort.FLOWCONTROL_NONE;
	
	
	private JScrollPane scroll = new JScrollPane(donneesRecues);
	Communicator communicator = null;
	
	
	public Fenetre(){
		initFenetre();
		initComposants();
		communicator=new Communicator(this);
		communicator.searchForPorts();
		this.setVisible(true);
	}
	
	private void initFenetre() {
		this.setTitle("Hyper Terminal");
		this.setSize(800, 500);
		this.setLocationRelativeTo(getParent());
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		this.setLocation(screenSize.width/2-this.getWidth()/2,0);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.setResizable(false);
		this.setBackground(Color.white);
	}
	@SuppressWarnings("unchecked")
	private void initComposants(){
		// panel de configuration de la port :
		JPanel configPanel=new JPanel();
		// panel liste ports :
		JPanel listPortPanel=new JPanel();
		listPortPanel.setPreferredSize(new Dimension(170,50));
		listPortPanel.setBackground(Color.white);
		listPortLabel=new JLabel("Port");
		//listPortLabel.setPreferredSize(new Dimension(70,20));
		
		comboListPort=new JComboBox();
		comboListPort.setPreferredSize(new Dimension(170,20));
		listPortPanel.add(listPortLabel);
		listPortPanel.add(comboListPort);
		
		// panel baude rate :
		JPanel baudeRatePanel=new JPanel();
		baudeRatePanel.setPreferredSize(new Dimension(80,50));
		baudeRatePanel.setBackground(Color.white);
		baudeRateLabel=new JLabel("Baude rate");
		//baudeRateLabel.setPreferredSize(new Dimension(70,20));
		comboBaudeRate=new JComboBox();
		comboBaudeRate.setPreferredSize(new Dimension(80,20));
		comboBaudeRate.addItem("300");
		comboBaudeRate.addItem("600");
		comboBaudeRate.addItem("1200");
		comboBaudeRate.addItem("2400");
		comboBaudeRate.addItem("4800");
		comboBaudeRate.addItem("9600");
		comboBaudeRate.addItem("14400");
		comboBaudeRate.addItem("115200");
		
		comboBaudeRate.setSelectedIndex(5);
		baudeRatePanel.add(baudeRateLabel);
		baudeRatePanel.add(comboBaudeRate);
		
		//panel data bits :
		JPanel dataBitsPanel=new JPanel();
		dataBitsPanel.setPreferredSize(new Dimension(63,50));
		dataBitsPanel.setBackground(Color.white);
		dataBitsLabel=new JLabel("Data bits");
		//dataBitsLabel.setPreferredSize(new Dimension(70,20));
		comboDataBits=new JComboBox();
		comboDataBits.setPreferredSize(new Dimension(63,20));
		comboDataBits.addItem("5");
		comboDataBits.addItem("6");
		comboDataBits.addItem("7");
		comboDataBits.addItem("8");
		comboDataBits.setSelectedIndex(3);
		dataBitsPanel.add(dataBitsLabel);
		dataBitsPanel.add(comboDataBits);
		
		// config parity panel :
		JPanel configParityPanel=new JPanel();
		configParityPanel.setBorder(BorderFactory.createTitledBorder("Bit de parité"));
		configParityPanel.setBackground(Color.white);
		configParityPanel.setPreferredSize(new Dimension(90, 90));
		parityGroupe=new ButtonGroup();
		parityNone=new JRadioButton("Non");
		parityPaire=new JRadioButton("Paire");
		parityImpaire=new JRadioButton("Impaire");
		parityNone.setBackground(Color.white);
		parityPaire.setBackground(Color.white);
		parityImpaire.setBackground(Color.white);
		parityNone.setPreferredSize(new Dimension(80,14));
		parityPaire.setPreferredSize(new Dimension(80,14));
		parityImpaire.setPreferredSize(new Dimension(80,14));
		parityNone.setSelected(true);
		parityGroupe.add(parityNone);
		parityGroupe.add(parityPaire);
		parityGroupe.add(parityImpaire);
		configParityPanel.add(parityNone);
		configParityPanel.add(parityPaire);
		configParityPanel.add(parityImpaire);
		
		// config bits de stop :
		JPanel configBitStop=new JPanel();
		configBitStop.setBorder(BorderFactory.createTitledBorder("Bits de stop"));
		configBitStop.setBackground(Color.white);
		configBitStop.setPreferredSize(new Dimension(234, 90));
		bitStopGroupe=new ButtonGroup();
		bitStop_1=new JRadioButton("le nombre de bits de stop est de 1");
		bitStop_1_5=new JRadioButton("le nombre de bits de stop est de 1.5");
		bitStop_2=new JRadioButton("le nombre de bits de stop est de 2");
		bitStop_1.setBackground(Color.white);
		bitStop_1_5.setBackground(Color.white);
		bitStop_2.setBackground(Color.white);
		bitStop_1.setPreferredSize(new Dimension(226,14));
		bitStop_1_5.setPreferredSize(new Dimension(226,14));
		bitStop_2.setPreferredSize(new Dimension(226,14));
		bitStop_1.setSelected(true);
		bitStopGroupe.add(bitStop_1);
		bitStopGroupe.add(bitStop_1_5);
		bitStopGroupe.add(bitStop_2);
		configBitStop.add(bitStop_1);
		configBitStop.add(bitStop_1_5);
		configBitStop.add(bitStop_2);
		
		// config controle flux :
		JPanel configCtrlFlux=new JPanel();
		configCtrlFlux.setBackground(Color.white);
		configCtrlFlux.setBorder(BorderFactory.createTitledBorder("Contrôle de flux"));
		configCtrlFlux.setPreferredSize(new Dimension(330, 90));
		fluxNone=new JCheckBox("Désactivé");
		fluxNone.setSelected(true);
		fluxNone.setBackground(Color.white);
		fluxNone.setPreferredSize(new Dimension(320,20));
		comboFlux=new JComboBox();
		comboFlux.addItem("Contrôle de flux matériel en réception");
		comboFlux.addItem("Contrôle de flux matériel en émission");
		comboFlux.addItem("Contrôle de flux logiciel en réception");
		comboFlux.addItem("Ccontrôle de flux logiciel en émission");
		comboFlux.setPreferredSize(new Dimension(313,20));
		comboFlux.setEnabled(false);
		configCtrlFlux.add(fluxNone);
		configCtrlFlux.add(comboFlux);
		
		// Boutons enregistrer/defaut les config :
		enregConfig=new JButton("Enregistrer les configurations");
		//enregConfig.setBackground(Color.cyan);
		defautConfig=new JButton("Defaut");
		//defautConfig.setBackground(Color.cyan);
		enregConfig.setPreferredSize(new Dimension(219,20));
		defautConfig.setPreferredSize(new Dimension(100,20));
		enregConfig.setEnabled(false);
		
		// Boutons connet et disconnect :
		JPanel boutonsPanel=new JPanel();
		boutonsPanel.setPreferredSize(new Dimension(340,35));
		boutonsPanel.setBackground(Color.white);
		connectBouton=new JButton("Connexion");
		connectBouton.setPreferredSize(new Dimension(160,30));
		disconnectBouton=new JButton("Déconnexion");
		disconnectBouton.setPreferredSize(new Dimension(160,30));
		disconnectBouton.setEnabled(false);
		boutonsPanel.add(connectBouton);
		boutonsPanel.add(disconnectBouton);
		
		
		// about panel :
		JPanel aboutPanel=new JPanel();
		aboutPanel.setPreferredSize(new Dimension(350,60));
		aboutPanel.setBackground(Color.white);
		aboutPanel.setBorder(BorderFactory.createTitledBorder("A propos"));
		JLabel aboutLabel=new JLabel("© Ncibi Fehmi 2013, Tous Droits resèrvés.");
		aboutLabel.setPreferredSize(new Dimension(330,20));
		aboutLabel.setForeground(Color.black);
		aboutPanel.add(aboutLabel);
		// Bouton afficher Graphique
		affGraphique=new JButton("Afficher la courbe");
		affGraphique.setPreferredSize(new Dimension(350,30));
		affGraphique.setForeground(Color.blue);
		
		configPanel.setBorder(BorderFactory.createTitledBorder("Configurations"));
		configPanel.setBackground(Color.white);
		configPanel.setPreferredSize(new Dimension(350, 340));
		configPanel.add(listPortPanel);
		configPanel.add(baudeRatePanel);
		configPanel.add(dataBitsPanel);
		configPanel.add(configParityPanel);
		configPanel.add(configBitStop);
		configPanel.add(configCtrlFlux);
		configPanel.add(enregConfig);
		configPanel.add(defautConfig);
		configPanel.add(boutonsPanel);
		
		
		// panel d'envoi/reception de donnees :
		JPanel dataPanel=new JPanel();
		dataPanel.setPreferredSize(new Dimension(400,150));
		//dataPanel.setBackground(Color.cyan);
		dataPanel.setBorder(BorderFactory.createTitledBorder("Transfert de données"));
		//donneesRecues.setPreferredSize(new Dimension(380,250));
		// Pour que le scroll affiche le dernier message toujours
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		Font font=new Font("Verdana",Font.BOLD,12);
		donneesRecues.setFont(font);
		donneesRecues.setEditable(false);
		donneesRecues.setBackground(Color.black);
		donneesRecues.setForeground(Color.green);
		donneesEnv=new JTextField();
		donneesEnv.setPreferredSize(new Dimension(300,26));
		envoiBouton=new JButton("Envoyer");
		envoiBouton.setEnabled(false);
		JLabel dataLabel=new JLabel("Données reçues");
		dataLabel.setPreferredSize(new Dimension(390,20));
		JLabel envoiLabel=new JLabel("Données à envoyer");
		envoiLabel.setPreferredSize(new Dimension(390,20));
		dataPanel.add(dataLabel);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		dataPanel.add(scroll);
		scroll.setPreferredSize(new Dimension(380,350));
		scroll.setAutoscrolls(true);
		dataPanel.add(envoiLabel);
		dataPanel.add(donneesEnv);
		dataPanel.add(envoiBouton);
		
		// Caract panel :
		caractPanel.setBorder(BorderFactory.createTitledBorder("Caractéristiques"));
		caractPanel.setPreferredSize(new Dimension(790,200));
		caractPanel.setBackground(Color.white);
		
		// *****************************************************************************************
		// define your data
		
		
		// *****************************************************************************************
		
		
		
		/*
		 * Initialisation des actions de tous les boutons :
		 * ************************************************************************************
		 */
		
		connectBouton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEnabledComponent(false);
				connectBouton.setEnabled(false);
				//donneesRecues.setBackground(Color.white);
				if(!savedConfig){
					setDefautConfig();
				}
				communicator.connect();
		        if (communicator.getConnected() == true)
		        {
		            if (communicator.initIOStream() == true)
		            {
		                communicator.initListener();
		            }
		        }
			}
		});
		
		disconnectBouton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEnabledComponent(true);
				enregConfig.setEnabled(false);
				defautConfig.setEnabled(false);
				communicator.disconnect();
			}
		});
		envoiBouton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				communicator.writeData(0, 0);
				donneesRecues.append("Donnée ("+donneesEnv.getText().toString()+") envoyé avec succés");
			}
		});
		
		defautConfig.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setDefautConfig();
				comboListPort.setSelectedIndex(0);
			}
		});
		
		enregConfig.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				savedConfig = true;
				connectBouton.setEnabled(true);
				// methode d'initialisation des parametres de port
				setChangedConfig();
			}

			
		});
		
		fluxNone.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(fluxNone.isSelected()){
					comboFlux.setEnabled(false);
				}else{
					comboFlux.setEnabled(true);
				}
			}
		});
		
		parityPaire.addActionListener(new ComposantChangedListener());
		parityImpaire.addActionListener(new ComposantChangedListener());
		bitStop_1.addActionListener(new ComposantChangedListener());
		bitStop_1_5.addActionListener(new ComposantChangedListener());
		bitStop_2.addActionListener(new ComposantChangedListener());
		fluxNone.addActionListener(new ComposantChangedListener());
		comboBaudeRate.addActionListener(new ComposantChangedListener());
		comboDataBits.addActionListener(new ComposantChangedListener());
		comboFlux.addActionListener(new ComposantChangedListener());
		
		

		affGraphique.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//communicator.tracer.frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				if(communicator.tracer.frame.isVisible()){
					communicator.tracer.frame.setVisible(false);
					affGraphique.setText("Afficher Masquer la courbe");
				}else{
					communicator.tracer.frame.setVisible(true);
					affGraphique.setText("Masquer la courbe");
				}
				//affGraphique.setEnabled(false);
			}
		});
		
		/*
		 * **********************************************************************************
		 */
		JPanel conteneur=new JPanel();
		conteneur.setPreferredSize(new Dimension(360,460));
		conteneur.add(configPanel,BorderLayout.WEST);
		conteneur.add(aboutPanel);
		conteneur.add(affGraphique);
		//conteneur.add(dataPanel);
		conteneur.setBackground(Color.white);
		
		JPanel FenetrePrincipale=new JPanel();
		FenetrePrincipale.setLayout(new BorderLayout());
		FenetrePrincipale.setBackground(Color.white);
		
		FenetrePrincipale.add(conteneur,BorderLayout.WEST);
		FenetrePrincipale.add(dataPanel,BorderLayout.EAST);
		this.getContentPane().add(FenetrePrincipale);
	}
	void setEnabledComponent(boolean enable) {
		envoiBouton.setEnabled(!enable);
		connectBouton.setEnabled(enable);
		bitStop_1.setEnabled(enable);
		bitStop_1_5.setEnabled(enable);
		bitStop_2.setEnabled(enable);
		enregConfig.setEnabled(enable);
		defautConfig.setEnabled(enable);
		comboListPort.setEditable(enable);
		comboBaudeRate.setEnabled(enable);
		comboDataBits.setEnabled(enable);
		parityNone.setEnabled(enable);
		parityPaire.setEnabled(enable);
		parityImpaire.setEnabled(enable);
		fluxNone.setEnabled(enable);
		comboFlux.setEnabled(enable);
		disconnectBouton.setEnabled(!enable);
	}
	
	public void setDefautConfig(){
		parityNone.setSelected(true);
		fluxNone.setSelected(true);
		bitStop_1.setSelected(true);
		//comboListPort.setSelectedIndex(0);
		comboBaudeRate.setSelectedIndex(5);
		comboDataBits.setSelectedIndex(3);
		defautConfig.setEnabled(false);
		enregConfig.setEnabled(false);
		connectBouton.setEnabled(true);
	}
	public void setChangedConfig() {
		// initialisation des parametres de port :
		// Baude Rate ******************************************************************
		BaudeRate=Integer.parseInt(comboBaudeRate.getSelectedItem().toString());
		
		// Nombre de bits *************************************************************
		if(comboDataBits.getSelectedIndex()==0)
			dataBits=SerialPort.DATABITS_5;
		else if(comboDataBits.getSelectedIndex()==1)
			dataBits=SerialPort.DATABITS_6;
		else if(comboDataBits.getSelectedIndex()==02)
			dataBits=SerialPort.DATABITS_7;
		else 
			dataBits=SerialPort.DATABITS_8;	
		// Parité **************************************************************************
		if(parityNone.isSelected())
			parity=SerialPort.PARITY_NONE;
		else if(parityPaire.isSelected())
			parity=SerialPort.PARITY_EVEN;
		else
			parity=SerialPort.PARITY_ODD;
		// Bits de stop **************************************************************************
		if(bitStop_1.isSelected())
			stopBits=SerialPort.STOPBITS_1;
		else if(bitStop_1_5.isSelected())
			stopBits=SerialPort.STOPBITS_1_5;
		else
			stopBits=SerialPort.STOPBITS_2;
		// Controle de flux *********************************************************************
		if(!fluxNone.isSelected()){
			if(comboFlux.getSelectedIndex()==0)
				ctrlFlux=SerialPort.FLOWCONTROL_RTSCTS_IN;
			else if(comboFlux.getSelectedIndex()==1)
				ctrlFlux=SerialPort.FLOWCONTROL_RTSCTS_OUT;
			else if(comboFlux.getSelectedIndex()==2)
				ctrlFlux=SerialPort.FLOWCONTROL_XONXOFF_IN;
			else
				ctrlFlux=SerialPort.FLOWCONTROL_XONXOFF_OUT;
		}else{
			ctrlFlux=SerialPort.FLOWCONTROL_NONE;
		}
		donneesRecues.setText("Succé de configuration\nPort : "+comboListPort.getSelectedItem().toString()+".\nBaude Rate : "+BaudeRate+".\nNombre de bits : "+dataBits+".\n" );
		System.out.println("Baude Rate = "+BaudeRate);
		System.out.println("Data bits = "+dataBits);
		System.out.println("Parité = "+parity);
		System.out.println("Bit de stop = "+stopBits);
		System.out.println("ctrl Flux = "+ctrlFlux);
	}
	
	static double f(double x) {
		return (Math.cos(x / 5) + Math.sin(x / 7) + 2) * 700 / 4;
	}
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Fenetre().setVisible(true);
            }
        });
	}
	class ComposantChangedListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			configChanged=true;
			connectBouton.setEnabled(false);
			enregConfig.setEnabled(true);
			defautConfig.setEnabled(true);
		}
	}
}
