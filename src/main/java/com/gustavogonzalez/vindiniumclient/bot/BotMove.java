package com.gustavogonzalez.vindiniumclient.bot;

public enum BotMove {
	STAY("Stay"), WEST("West"), EAST("East"), NORTH("North"), SOUTH("South");

	private final String direction;

	BotMove(String moveName) {
		this.direction = moveName;
	}

	public String toString() {
		return direction;
	}
}
