package de.florianmarsch.liveticker.twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageVariete {

	private List<String> goal = new ArrayList<String>();
	private List<String> penalty = new ArrayList<String>();
	private List<String> own = new ArrayList<String>();
	private List<String> signs = new ArrayList<String>();
	
	public MessageVariete(){
		goal.add("Toor");
		goal.add("Drin is' er");
		goal.add("Traumhaft");
		goal.add("Fabelhaft");
		goal.add("Treffer");
		goal.add("Da landet der Ball im Kasten");
		
		penalty.add("11 Meter");
		penalty.add("Strafstoß");
		penalty.add("Elfer");
		penalty.add("Elfmeter");

		own.add(" macht ein Eigentor");
		own.add(" ballert ins eigene Tor");
		own.add(" hilft beim Gegner mit einem Tor aus");
		
		signs.add(".");
		signs.add(",");
		signs.add("!");
		signs.add("!!");
	}
	
	
	public String getGoal(){
		Collections.shuffle(goal);
		return goal.iterator().next()+""+getSign();
	}
	public String getPenalty(){
		Collections.shuffle(penalty);
		return penalty.iterator().next()+""+getSign();
		
	}
	public String getOwn(){
		Collections.shuffle(own);
		return own.iterator().next();
	}
	
	public String getSign(){
		Collections.shuffle(signs);
		return signs.iterator().next();
	}
	
}
