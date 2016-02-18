package com.kyrobot.howfar.model;

public final class ElevationMilestone {
	public final TallThing milestone;
	public final double completion;
	
	public ElevationMilestone(TallThing somethingTall, double completion) {
		this.milestone = somethingTall;
		this.completion = completion;
	}
	
}