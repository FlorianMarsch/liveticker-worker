package com.heroku.devcenter.liveticker;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Event {
	
	@Id
	private String id;
	
	@Column
	private String name;
	
	@Column
	private String type;
	
	@Column
	private String owner;
	
	@Column
	private String gameTag;
	
	@Column
	private String result;
	
	@Column
	private Boolean fakeGame;
	
	@Column
	private Boolean byeGame;
		
	@Column
	private Integer gameday;
	
	@Column
	private Date creationDate;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Event other = (Event) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getGameTag() {
		return gameTag;
	}
	public void setGameTag(String gameTag) {
		this.gameTag = gameTag;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Boolean getFakeGame() {
		return fakeGame;
	}
	public void setFakeGame(Boolean fakeGame) {
		this.fakeGame = fakeGame;
	}
	public Boolean getByeGame() {
		return byeGame;
	}
	public void setByeGame(Boolean byeGame) {
		this.byeGame = byeGame;
	}
	public Integer getGameday() {
		return gameday;
	}
	public void setGameday(Integer gameday) {
		this.gameday = gameday;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
}
