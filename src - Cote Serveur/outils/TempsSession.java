package outils;

import java.util.Timer;
import java.util.TimerTask;

import service.ServiceRéservation;

public class TempsSession {
	private static final int TEMPS_SESSION_MAX = 600;
	private static final int DELAI = 1000;
	private Timer timer;
	private TimerTask task;
	private int tempsEcoulé;
	
	public TempsSession(ServiceRéservation service){
		this.tempsEcoulé = 0;
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				if(++tempsEcoulé >= TEMPS_SESSION_MAX)
					service.terminer();	
			}
		};
				
	}
	
	public void start(){
		timer.schedule(task, 0, DELAI);
	}
	
	public void stop(){
		timer.cancel();
	}
	
	
}
