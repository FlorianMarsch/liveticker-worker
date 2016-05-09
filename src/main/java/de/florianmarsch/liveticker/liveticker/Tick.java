package de.florianmarsch.liveticker.liveticker;

public class Tick {

	private String id;
	private int matchdayNumber;
	private String event;
	private String externId;
	private String name;	
	
	public String getEvent() {
		return event;
	}



	public void setEvent(String event) {
		this.event = event;
	}



	public String getExternId() {
		return externId;
	}



	public void setExternId(String externId) {
		this.externId = externId;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}


	public int getMatchdayNumber() {
		return matchdayNumber;
	}



	public void setMatchdayNumber(int matchdayNumber) {
		this.matchdayNumber = matchdayNumber;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externId == null) ? 0 : externId.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tick other = (Tick) obj;
		if (externId == null) {
			if (other.externId != null)
				return false;
		} else if (!externId.equals(other.externId))
			return false;
		return true;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}

}
