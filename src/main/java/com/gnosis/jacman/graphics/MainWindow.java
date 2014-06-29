/**
 * 
 */
package com.gnosis.jacman.graphics;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;
import javax.swing.*;

import com.gnosis.jacman.engine.Constants;
import com.gnosis.jacman.engine.FileFilters;
import com.gnosis.jacman.engine.Game;
import com.gnosis.jacman.engine.Globals;
import com.gnosis.jacman.engine.Preloads;
import com.gnosis.jacman.engine.PropertiesLoader;

/**
 * @author Brendan McGloin
 *
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements Constants{
	
	// how many increments until the splash screen progress bar is fully loaded.
	private static final int STEPS_UNTIL_LOAD = 20;
	
	private LevelCanvas canvas;
	private JFileChooser chooser;
	private SetupTrainingModeDialog tModeWindow;
	private TrainingModeChoices tModeChoices;
	private PrintStream errorWriter;
	//because many of the internal classed, and event listeners need to manipulate
	//this specific instance, and 'this' refers to that object, a variable by
	//another name is needed, in this case: SELF
	private final MainWindow SELF = this;
	
	public MainWindow(){
		super("JacMan");
		JacManSplashScreen splash = new JacManSplashScreen("Loading Preferences");
		PropertiesLoader.load();
		splash.incrementProgress("Registering Error log");
		try{
			errorWriter = new PrintStream(new File(ERROR_LOG_PATH));
			System.setErr(errorWriter);
		} catch (IOException e){
			errorWriter = null;
		}
		chooser = new JFileChooser(Globals.currentGamePath);
		splash.incrementProgress("Loading Images");
		this.setIconImage(RESOURCE_LOADER.loadImage("other/life.gif"));
		splash.incrementProgress();
		ENEMY_IMAGE_CACHE.loadImages();
		splash.incrementProgress();
		TILE_IMAGE_CACHE.loadImages();
		splash.incrementProgress();
		PLAYER_IMAGE_CACHE.loadImages();
		splash.incrementProgress();
		OTHER_IMAGE_CACHE.loadImages();
		splash.incrementProgress("Loading Game");
		try {
			File gameFile = new File(Globals.currentGamePath);
			if (!gameFile.exists()){
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				panel.add(new JLabel("Error loading default game. Using preloaded game."));
				Globals.game = Preloads.makeGame1();
			}
			else{
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(gameFile));
				Globals.game = (Game) stream.readObject();
				stream.close();
				Globals.game.divyUpNet();
			}
		} catch (Exception e1) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("Error loading default game:"));
			panel.add(new JLabel(e1.getMessage()));
			panel.add(new JLabel("Please Select a game file."));
			JOptionPane.showMessageDialog(this, panel, "ERROR", JOptionPane.ERROR_MESSAGE);
			if (chooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION){
				try{
					ObjectInputStream stream = new ObjectInputStream(new FileInputStream(chooser.getSelectedFile()));
					Globals.game = (Game) stream.readObject();
					splash.incrementProgress();
					stream.close();
					Globals.game.divyUpNet();
				} catch (Exception e){
					JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.exit(1);
				}
			}
			else{
				System.exit(0);
			}
			e1.printStackTrace();
		}
		splash.incrementProgress();
		Globals.currentGamePath = Preloads.GAME_1_PATH;
		splash.incrementProgress("Setting up GUI");
		this.setLayout(new BorderLayout());
		splash.incrementProgress();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		splash.incrementProgress();
		tModeWindow = new SetupTrainingModeDialog();
		splash.incrementProgress();
		canvas = new LevelCanvas(Globals.game.getEnemies(), Globals.game.getBoard());
		splash.incrementProgress();
		tModeChoices = null;
		splash.incrementProgress();
		this.addWindowFocusListener(new WindowFocusListener(){
			
			public void windowGainedFocus(WindowEvent e) {
				try{
					Thread.sleep(50);
				} catch (InterruptedException ex){
					//Do nothing;
				}
				SELF.setVisible(true);
				SELF.setVisible(true);
			}
			
			public void windowLostFocus(WindowEvent e) {
				try{
					Thread.sleep(50);
				} catch (InterruptedException ex){
					//Do nothing;
				}
				canvas.setWaiting(true);
			}
			
		});
		splash.incrementProgress();
		this.addWindowListener(new WEL(this));
		splash.incrementProgress();
		chooser.setFileFilter(new FileFilters.GameFileFilter());
		splash.incrementProgress();
		this.setJMenuBar(new MainMenuBar(this));
		splash.incrementProgress();
		this.add(canvas, BorderLayout.CENTER);
		splash.incrementProgress();
		this.pack();
		splash.incrementProgress();
		//this should not be needed, but, just in case...
		splash.dispose();
		//because the method toFront() only brings the window to the front of any other Java windows
		// set visible is called twice to make this window the active window.
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setAlwaysOnTop(false);
		this.setVisible(true);
		canvas.createStrategy();
	}
	
	private void showTModeSetup(){
		tModeWindow.setVisible(true);
	}
	
	private void saveGame(){
		try{
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(Preloads.GAME_1_PATH)));
			stream.writeObject(Globals.game);
			stream.close();
		} catch (IOException e){
			JPanel panel = makePanelFromString("Unable to save:\n"+e.getMessage());
			JOptionPane.showMessageDialog(this, panel, "ERROR", JOptionPane.ERROR_MESSAGE);
			if (errorWriter != null){
				e.printStackTrace(errorWriter);
			}
		}
	}
	
	private void resetCanvas(){
		canvas.setStopped(true);
		canvas.loadGame();
		this.pack();
	}
	
	private static JPanel makePanelFromString(String s){
		Scanner scanner = new Scanner(s);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		while (scanner.hasNext()){
			panel.add(new JLabel(scanner.next()));
		}
		return panel;
	}
	
	private class SetupTrainingModeDialog extends JFrame{
		final int OFFSET = 15;
		
		private JCheckBox trainingModeEnabled;
		private JRadioButton trainAfter, trainDuring;
		private JSpinner epochsSpinner;
		private SpinnerNumberModel epochsModel;
		private JSlider previousChoicesSize;
		
		public SetupTrainingModeDialog(){
			super();
			this.setLayout(new GridBagLayout());
			this.setAlwaysOnTop(true);
			
			this.addWindowListener(new WindowListener() {
			
				public void windowOpened(WindowEvent e) {
				}
			
				public void windowIconified(WindowEvent e) {
					canvas.update();
				}
			
				public void windowDeiconified(WindowEvent e) {
				}
			
				public void windowDeactivated(WindowEvent e) {
					canvas.update();
				}
			
				public void windowClosing(WindowEvent e) {
				}
			
				public void windowClosed(WindowEvent e) {
					canvas.update();
				}
			
				public void windowActivated(WindowEvent e) {
				}
			
			});
			
			trainingModeEnabled = new JCheckBox("Training mode enabled");
			trainingModeEnabled.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					if(trainingModeEnabled.isSelected()){
						trainAfter.setEnabled(true);
						trainDuring.setEnabled(true);
						previousChoicesSize.setEnabled(true);
					}
					else{
						trainAfter.setEnabled(false);
						trainDuring.setEnabled(false);
						previousChoicesSize.setEnabled(false);
					}
					for (ActionListener al: trainAfter.getActionListeners()){
						al.actionPerformed(null);
					}
					for (ActionListener al : trainDuring.getActionListeners()){
						al.actionPerformed(null);
					}
				}
			});
			trainingModeEnabled.setSelected(!(Globals.trainingMode == TRAINING_MODE_OFF));
			
			GridBagConstraints tmeConstraints = new GridBagConstraints();
			tmeConstraints.gridx = 0;
			tmeConstraints.gridy = 0;
			
			GridBagConstraints tpConstraints = new GridBagConstraints();
			tpConstraints.gridx = 0;
			tpConstraints.gridy = 1;
			
			GridBagConstraints bpConstraints = new GridBagConstraints();
			bpConstraints.gridx = 0;
			bpConstraints.gridy = 2;
			bpConstraints.anchor = GridBagConstraints.SOUTHEAST;
			
			this.add(trainingModeEnabled, tmeConstraints);
			this.add(makeTrainingPanel(), tpConstraints);
			this.add(makeButtonPanel(), bpConstraints);
			trainingModeEnabled.getActionListeners()[0].actionPerformed(null);
			
			this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			this.pack();
		}
		
		/* (non-Javadoc)
		 * @see java.awt.Window#setVisible(boolean)
		 */
		@Override
		public void setVisible(boolean b) {
			super.setVisible(b);
			if (!b){
				canvas.update();
			}
		}
		
		private JPanel makeTrainingPanel(){
			JPanel panel = new JPanel(new GridBagLayout());
			
			ButtonGroup bg = new ButtonGroup();
			
			trainDuring = new JRadioButton("Train as you go");
			trainDuring.setToolTipText("Train while playing");
			trainDuring.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					epochsSpinner.setEnabled(false);
				}
			});
			
			trainAfter = new JRadioButton("Train after");
			trainAfter.setToolTipText("Train once you finish playing");
			trainAfter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					epochsSpinner.setEnabled(trainAfter.isSelected());
				}
			});
			if (Globals.trainingMode == TRAINING_MODE_AFTER){
				trainAfter.setSelected(true);
			}
			else if (Globals.trainingMode == TRAINING_MODE_AS_YOU_GO){
				trainDuring.setSelected(true);
			}
			
			
			bg.add(trainDuring);
			bg.add(trainAfter);
			
			GridBagConstraints tdConstraints = new GridBagConstraints();
			tdConstraints.gridx = 0;
			tdConstraints.gridy = 0;
			
			GridBagConstraints taConstraints = new GridBagConstraints();
			taConstraints.gridx = 0;
			taConstraints.gridy = 1;
			
			GridBagConstraints lapConstraints = new GridBagConstraints();
			lapConstraints.gridx = 0;
			lapConstraints.gridy = 2;
			lapConstraints.ipadx = OFFSET;
			
			GridBagConstraints epConstraints = new GridBagConstraints();
			epConstraints.gridx = 0;
			epConstraints.gridy = 3;
			epConstraints.ipadx = OFFSET;
			
			
			panel.add(trainDuring, tdConstraints);
			panel.add(trainAfter, taConstraints);
			panel.add(makeSizePanel(), lapConstraints);
			panel.add(makeEpochsPanel(), epConstraints);
			
			epochsSpinner.setEnabled(trainAfter.isSelected());
			return panel;
		}
		
		private JPanel makeEpochsPanel(){
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			
			epochsModel = new SpinnerNumberModel(600, 1, 25000, 10);
			epochsSpinner = new JSpinner(epochsModel);
			epochsSpinner.setToolTipText("How many times to run through the test data");
			
			panel.add(new JLabel("Epcohs:"));
			panel.add(epochsSpinner);
			
			return panel;
		}
		
		private JPanel makeSizePanel(){
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			
			previousChoicesSize = new JSlider(JSlider.HORIZONTAL, 1, 10, Globals.lookAhead);
			Dictionary<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
			labels.put(1, new JLabel("1"));
			labels.put(5, new JLabel("5"));
			labels.put(10, new JLabel("10"));
			previousChoicesSize.setMajorTickSpacing(5);
			previousChoicesSize.setMinorTickSpacing(1);
			previousChoicesSize.setLabelTable(labels);
			previousChoicesSize.setSnapToTicks(true);
			previousChoicesSize.setPaintTicks(true);
			previousChoicesSize.setPaintLabels(true);
			previousChoicesSize.setToolTipText("How far ahead the enemies should look ahead");
			
			panel.add(new JLabel("Look ahead"));
			panel.add(previousChoicesSize);
			
			return panel;
		}
		
		private JPanel makeButtonPanel(){
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					tModeChoices = new TrainingModeChoices(epochsModel.getNumber().intValue(), trainingModeEnabled.isSelected());
					if (trainingModeEnabled.isSelected()){
						if (trainAfter.isSelected()){
							Globals.trainingMode = TRAINING_MODE_AFTER;
						}
						else{
							Globals.trainingMode = TRAINING_MODE_AS_YOU_GO;
						}
						Globals.lookAhead = previousChoicesSize.getValue();
						Globals.game.setSetSizeQueue();
					}
					tModeWindow.setVisible(false);
				}
			
			});
			
			JButton applyButton = new JButton("Apply");
			applyButton.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					tModeChoices = new TrainingModeChoices(epochsModel.getNumber().intValue(), trainingModeEnabled.isSelected());
					if (trainingModeEnabled.isSelected()){
						if (trainAfter.isSelected()){
							Globals.trainingMode = TRAINING_MODE_AFTER;
						}
						else{
							Globals.trainingMode = TRAINING_MODE_AS_YOU_GO;
						}
						Globals.lookAhead = previousChoicesSize.getValue();
						Globals.game.setSetSizeQueue();
					}
				}
			
			});
			
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					tModeWindow.setVisible(false);
				}
			
			});
			
			panel.add(okButton);
			panel.add(applyButton);
			panel.add(cancelButton);
			
			return panel;
		}
	}
	
	private class JacManSplashScreen extends JWindow{
		int loadingCompletion;
		
		ImageComponent imgComp;
		JProgressBar pBar;
		
		JacManSplashScreen(String message){
			this.setLayout(new BorderLayout());
			imgComp = new ImageComponent();
			pBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, STEPS_UNTIL_LOAD);
			pBar.setString(message);
			pBar.setStringPainted(true);
			this.add(imgComp, BorderLayout.CENTER);
			this.add(pBar, BorderLayout.SOUTH);
			this.pack();
			this.setAlwaysOnTop(true);
			this.setLocation(150, 150);
			this.setVisible(true);
		}
		
		JacManSplashScreen(){
			this.setLayout(new BorderLayout());
			imgComp = new ImageComponent();
			pBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, STEPS_UNTIL_LOAD);
			this.add(imgComp, BorderLayout.CENTER);
			this.add(pBar, BorderLayout.SOUTH);
			this.pack();
			this.setAlwaysOnTop(true);
			this.setLocation(150, 150);
			this.setVisible(true);
		}
		
		void incrementProgress(){
			pBar.setValue(pBar.getValue()+1);
			if (pBar.getValue() >= STEPS_UNTIL_LOAD){
				try{
					Thread.sleep(500);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
				dispose();
			}
		}
		
		void incrementProgress(String string){
			pBar.setString(string);
			pBar.setValue(pBar.getValue()+1);
			if (pBar.getValue() >= STEPS_UNTIL_LOAD){
				try{
					Thread.sleep(500);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
				dispose();
			}
		}
		
		private class ImageComponent extends Component{
			BufferedImage img;
			
			ImageComponent(){
				img = RESOURCE_LOADER.loadImage("other/Jac Man Splash.gif");
			}
			
			public void paint(Graphics g){
				g.drawImage(img, 0, 0, null);
			}

			/* (non-Javadoc)
			 * @see java.awt.Component#getPreferredSize()
			 */
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(img.getWidth(), img.getHeight());
			}

			/* (non-Javadoc)
			 * @see java.awt.Component#getSize()
			 */
			@Override
			public Dimension getSize() {
				return new Dimension(img.getWidth(), img.getHeight());
			}

			/* (non-Javadoc)
			 * @see java.awt.Component#isPreferredSizeSet()
			 */
			@Override
			public boolean isPreferredSizeSet() {
				return true;
			}
		}
	}
	
	private class TrainingModeChoices{
		int epochs;
		boolean enabled;
		/**
		 * @param epochs
		 * @param enabled
		 */
		public TrainingModeChoices(int epochs, boolean enabled) {
			this.epochs = epochs;
			this.enabled = enabled;
		}
		
		
	}
	
	public class MainMenuBar extends JMenuBar{
		
		JRadioButtonMenuItem fsa, ann;
		private JMenuItem trainingMode;
		private MainWindow parent;
		private JCheckBoxMenuItem autosave;
		
		public MainMenuBar(MainWindow parent1) {
			this.parent = parent1;
			this.add(makeFileMenu());
			this.add(makeAIMenu());
			this.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					canvas.requestFocus();
					canvas.update();
				}
			
				public void focusGained(FocusEvent e) {
				}
			});
		}
		
		private JMenu makeFileMenu(){
			JMenu menu = new JMenu("File");
			menu.addFocusListener(new FocusListener() {
			
				public void focusLost(FocusEvent e) {
					canvas.requestFocus();
					canvas.update();
				}
			
				public void focusGained(FocusEvent e) {
				}
			
			});
			
			JMenuItem newGame = new JMenuItem("New Game");
			newGame.setAccelerator(KeyStroke.getKeyStroke("F2"));
			newGame.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					canvas.requestFocus();
					canvas.newGame();
				}
			});
			
			JMenuItem openGame = new JMenuItem("Open game");
			openGame.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION){
						try{
							ObjectInputStream stream = null;
							try{
								stream = new ObjectInputStream(new FileInputStream(chooser.getSelectedFile()));
								Globals.game = (Game) stream.readObject();
								Globals.currentGamePath = chooser.getSelectedFile().getCanonicalPath();
								canvas.loadGame();
								parent.resetCanvas();
							} 
							finally{
								if (stream != null){
									stream.close();
								}
							}
						} catch (IOException e1){
							JOptionPane.showMessageDialog(parent, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
						} catch (ClassNotFoundException e1){
							JOptionPane.showMessageDialog(parent, "Invalid or corrupt JacMan game file!", 
									"ERROR", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			openGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_SELECTOR_MASK));
			
			JMenuItem saveGame = new JMenuItem("Save");
			saveGame.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					try{
						ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(Globals.currentGamePath)));
						stream.writeObject(Globals.game);
					} catch (IOException e1){
						JOptionPane.showMessageDialog(parent, e1.getMessage(), "Error", 
								JOptionPane.ERROR_MESSAGE);
						if (errorWriter != null){
							e1.printStackTrace(errorWriter);
						}
					}
				}
			
			});
			saveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SELECTOR_MASK));
			
			JMenuItem saveGameAs = new JMenuItem("Save as...");
			saveGameAs.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION){
						try{
							ObjectOutputStream stream = null;
							try{
								String path = chooser.getSelectedFile().getPath();
								if (!path.endsWith(".jmg")){
									path += ".jmg";
								}
								stream = new ObjectOutputStream(new FileOutputStream(new File(path)));
								stream.writeObject(Globals.game);
								Globals.currentGamePath = chooser.getSelectedFile().getCanonicalPath();
							}
							finally{
								if (stream != null){
									stream.close();
								}
							}
						} catch (IOException e1){
							JOptionPane.showMessageDialog(parent, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			
			});
			saveGameAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SELECTOR_MASK|ActionEvent.SHIFT_MASK));
			
			autosave = new JCheckBoxMenuItem("Auto-save");
			autosave.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					Globals.autosave = autosave.isSelected();
				}
			
			});
			autosave.setSelected(Globals.autosave);
			
			JMenuItem quit = new JMenuItem("Quit");
			quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MENU_SELECTOR_MASK));
			quit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			
			menu.add(newGame);
			menu.add(openGame);
			menu.addSeparator();
			menu.add(saveGame);
			menu.add(saveGameAs);
			menu.addSeparator();
			menu.add(autosave);
			menu.addSeparator();
			menu.add(quit);
			
			return menu;
		}
		
		
		
		private JMenu makeAIMenu(){
			JMenu menu = new JMenu("AI");
			menu.addFocusListener(new FocusListener() {
				
				public void focusLost(FocusEvent e) {
					canvas.requestFocus();
					canvas.update();
				}
			
				public void focusGained(FocusEvent e){}
			
			});
			
			ButtonGroup group = new ButtonGroup();
			
			fsa = new JRadioButtonMenuItem("Traditional (FSA)");
			group.add(fsa);
			
			ann = new JRadioButtonMenuItem("Brain (ANN)");
			group.add(ann);
			
			fsa.setSelected(Globals.aiMode == FSA_AI_MODE);
			fsa.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (fsa.isSelected()){
						Globals.aiMode = FSA_AI_MODE;
					}
					canvas.update();
				}
			});
			
			ann.setSelected(Globals.aiMode == ANN_AI_MODE);
			ann.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (ann.isSelected()){
						Globals.aiMode = ANN_AI_MODE;
					}
					canvas.update();
				}
			});
			
			trainingMode = new JMenuItem("Training Mode Setup");
			trainingMode.setToolTipText("If enabled, then this will train the current brain while you play");
			trainingMode.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					parent.showTModeSetup();
				}
			
			});
			
			menu.add(trainingMode);
			menu.addSeparator();
			menu.add(fsa);
			menu.add(ann);
			
			return menu;
		}
	}
	
	public class WEL implements WindowListener{
		
		MainWindow parent;
		
		WEL(MainWindow parent){
			this.parent = parent;
		}
		

		public void windowActivated(WindowEvent e) {
			canvas.requestFocus();
			SELF.setVisible(true);
			SELF.setVisible(true);
		}
		
		public void windowClosed(WindowEvent e) {
			//Do nothing
		}
		
		public void windowClosing(WindowEvent e) {
			canvas.setStopped(true);
			parent.setVisible(false);
			if ((tModeChoices != null)&&(Globals.trainingMode == TRAINING_MODE_AFTER)){
				if (JOptionPane.showConfirmDialog(parent, "Do you want to train the net?", "Train?", JOptionPane.YES_NO_OPTION)
						== JOptionPane.YES_OPTION){
					TrainingDialog d = new TrainingDialog(parent, new Thread(Globals.game.makeTest(tModeChoices.epochs)));
					d.start();
					d.close();
				}
				else if (Globals.autosave){
					saveGame();
				}
			}
			PropertiesLoader.save();
		}
		
		public void windowDeactivated(WindowEvent e) {
			try{
				Thread.sleep(50);
			} catch (InterruptedException ex){
				//Do nothing;
			}
			canvas.setWaiting(true);
		}
		
		public void windowDeiconified(WindowEvent e) {
			canvas.update();
			canvas.requestFocus();
			SELF.setVisible(true);
			SELF.setVisible(true);
		}
		
		public void windowIconified(WindowEvent e) {
			try{
				Thread.sleep(50);
			} catch (InterruptedException ex){
				//Do nothing;
			}
			canvas.setWaiting(true);
			
		}
		
		public void windowOpened(WindowEvent e) {
			canvas.update();
			canvas.requestFocus();
			SELF.setVisible(true);
			SELF.setVisible(true);
			canvas.setStopped(false);
		}
	}

	private class TrainingDialog extends JWindow{
		
		Thread testThread;
		Frame parent;
		
		/**
		 * @param owner
		 */
		public TrainingDialog(Frame owner, Thread tThread) {
			this.parent = owner;
			this.testThread = tThread;
			
			this.setLayout(new GridLayout(3, 1));
			
			JProgressBar pBar = new JProgressBar(JProgressBar.HORIZONTAL);
			pBar.setIndeterminate(true);
			
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					testThread.interrupt();
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					panel.add(new JLabel("Do you want to save the"));
					panel.add(new JLabel("partially trained net?"));
					if (JOptionPane.showConfirmDialog(parent, panel, "Save?", JOptionPane.YES_NO_OPTION) ==
						JOptionPane.YES_OPTION){
						saveGame();
					}
					dispose();
				}
			});
			
			this.add(new JLabel("Training..."));
			this.add(pBar);
			this.add(cancelButton);
			this.pack();
			this.setVisible(true);
		}
		
		void start(){
			testThread.start();
			saveGame();
		}
		
		void close(){
			this.dispose();
		}
	}
	
	public static void main(String[] args){
		new MainWindow();
	}
}
