package com.kyrobot.howfar.model;

import java.util.Optional;

public final class ElevationMilestone {
	public final HighTarget target;
	public final Integer attained;
	public final Double progress;

	public ElevationMilestone(HighTarget somethingTall, Optional<Integer> completed, Double progress) {
		this.target = somethingTall;
		this.attained = completed.orElse(null);
		this.progress = progress;

	}

}
