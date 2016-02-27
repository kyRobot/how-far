package com.kyrobot.howfar.model;

public final class ElevationMilestone {
	public final HighTarget milestone;
	public final double completion;
	
	public ElevationMilestone(HighTarget somethingTall, double completion) {
		this.milestone = somethingTall;
		this.completion = completion;
	}
	
}