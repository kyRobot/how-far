package com.kyrobot.howfar.model;

public final class HighTarget {
	private final long id;
	private final String name;
	private final int height;

	public HighTarget(long id, String name, int height) {
		this.id = id;
		this.name = name;
		this.height = height;
	}

	public final long getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final int getHeight() {
		return height;
	}

}