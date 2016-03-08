package com.kyrobot.howfar.model;

public final class ElevationMilestone {
	public final HighTarget goal;
	public final double completion;
	
	public ElevationMilestone(HighTarget somethingTall, double completion) {
		this.goal = somethingTall;
		this.completion = completion;
	}
	
}