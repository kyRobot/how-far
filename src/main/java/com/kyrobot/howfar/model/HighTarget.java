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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		HighTarget other = (HighTarget) obj;
		if (height != other.height)
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HighTarget [id=" + id + ", name=" + name + ", height=" + height + "]";
	}

}
