package de.florianmarsch.liveticker.twitter;

import de.florianmarsch.liveticker.liveticker.Event;

public class Tweet {

	private String text;
	private String id;
	private String image;

	public Tweet(Event aEvent) {

		MessageVariete mv = new MessageVariete();

		String name = aEvent.getName();
		String owner = aEvent.getOwner();
		String type = aEvent.getType();
		String tag = aEvent.getGameTag();
		String result = aEvent.getResult();
		id = aEvent.getId();
		
		setImage("http://fussballmanager.herokuapp.com/screen/"+aEvent.getMatch());

		if (type.equalsIgnoreCase("Goal")) {
			text = mv.getGoal() + " " + name + " trifft für " + owner
					+ " zum "+result+" . #comunioLDC " + tag ;
		}
		if (type.equalsIgnoreCase("Penalty")) {
			text = mv.getPenalty() + " " + name + " verwandelt für " + owner
					+ " zum "+ result+". #comunioLDC " + tag ;
		}
		if (type.equalsIgnoreCase("Own")) {
			text = name + mv.getOwn() + " ... Schade für " + owner
					+ ".Es steht "+result+". #comunioLDC " + tag ;
		}
		if (aEvent.getFakeGame()) {
			text = name + " trifft auf dem Übungsplatz von " + owner
					+" #comunioLDC " + tag ;
			setImage(null);
		}
		if (aEvent.getByeGame()) {
			text = name + " ist beflügelt durch das Freilos von " + owner
					+" #comunioLDC " + tag ;
			setImage(null);
		}
		if (type.equalsIgnoreCase("Error")) {
			text = "Fehlalarm : Es war doch nicht "+ name + " für " + owner
					+ " #comunioLDC " + tag ;
			setImage(null);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
