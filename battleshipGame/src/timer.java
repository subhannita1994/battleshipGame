/*
 * class for displaying timer panel in attack phase
 * @author Group 3
 * @version 1.2
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class timer extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel;
    private JButton startBtn = new JButton("Start");
    private JButton stopBtn = new JButton("Stop");
    private CountTimer cntd;
    private Screen player;

    //constructs a new timer panel for scrreen p
    public timer(Screen p) {
    	super();
    	this.player = p;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        startBtn.addActionListener(this);
        stopBtn.addActionListener(this);
        JPanel cmdPanel = new JPanel();
        cmdPanel.setLayout(new GridLayout());
        cmdPanel.add(startBtn);
        cmdPanel.add(stopBtn);

        cntd = new CountTimer();
    }

    //return this timer panel
    public JPanel getPanel() {
    	return this.panel;
    }
    
    //return start button
    public JButton getStartButton() {
    	return this.startBtn;
    }
    
    //return stop button
    public JButton getStopButton() {
    	return this.stopBtn;
    }
    
    //set label of timer
    private void setTimerText(String sTime) {
        player.getTimerLabel().setText(sTime);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        JButton btn = (JButton) e.getSource();

        if (btn.equals(startBtn)){ 
        	cntd.start(); 
        }
        else if (btn.equals(stopBtn)){ 
        	long time = cntd.stop(); 
        }
    }


    //nested class to listen to start and stop commands 
    private class CountTimer implements ActionListener {

    	private static final int ONE_SECOND = 1000;
        private int count = 0;
        private boolean isTimerActive = false;
        private long start = 0;
        private long stop = 0;
        private Timer tmr = new Timer(ONE_SECOND,this);
        
        //constructs a new listener
        public CountTimer() {
            count=0;
            setTimerText(TimeFormat(count));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isTimerActive) {
                count++;
                setTimerText(TimeFormat(count));
            }
        }

        //start timer
        public void start() {
            count = 0;
            start = System.currentTimeMillis();
            tmr.start();
            isTimerActive = true;
            System.out.println(player.getName()+"'s timer started");
        }

       //stop button and return the time at which it was stopped
        public long stop() {
            stop = System.currentTimeMillis();
            tmr.stop();
            System.out.println(player.getName()+"'s timer stopped");
            return (stop - start)%1000;
        }

    }

    //returns formatted count seconds for displaying in timer label
    private String TimeFormat(int count) {

        int hours = count / 3600;
        int minutes = (count-hours*3600)/60;
        int seconds = count-minutes*60;

        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }
}