package com.heroku.devcenter.twitter;

import com.heroku.devcenter.liveticker.Event;

public class Tweet {

	private String text;
	private String id;

	public Tweet(Event aEvent) {

		MessageVariete mv = new MessageVariete();

		String name = aEvent.getName();
		String owner = aEvent.getOwner();
		String type = aEvent.getType();
		String tag = aEvent.getGameTag();
		String result = aEvent.getResult();
		id = aEvent.getId();

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
		}
		if (aEvent.getByeGame()) {
			text = name + " ist beflügelt durch das Freilos von " + owner
					+" #comunioLDC " + tag ;
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

}
