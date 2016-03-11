package com.kyrobot.howfar.responses;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.kyrobot.howfar.model.ElevationMilestone;

public final class ElevationResponse {
	private final double targetHeight;
	private final Boolean heightConverted;

	private final Collection<ElevationMilestone> majorMilestones;
	private final Collection<ElevationMilestone> closestAchievements;

	private ElevationResponse(double targetHeight,
			Boolean heightConverted,
			Collection<ElevationMilestone> majorMilestones,
			Collection<ElevationMilestone> closestAchievements) {
		this.targetHeight = targetHeight;
		this.heightConverted = heightConverted;
		this.majorMilestones = majorMilestones;
		this.closestAchievements = closestAchievements;
	}

	public double getTargetHeight() {
		return targetHeight;
	}

	public Boolean isHeightConverted() {
		return heightConverted;
	}

	public Collection<ElevationMilestone> getMajorMilestones() {
		return majorMilestones;
	}

	public Collection<ElevationMilestone> getClosestAchievements() {
		return closestAchievements;
	}

	public static ElevationResponse.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final Set<ElevationMilestone> majors = Sets.newConcurrentHashSet();
		private final Set<ElevationMilestone> closest = Sets.newConcurrentHashSet();
		private double target;
		private Boolean heightConverted;

		private Builder() {
		}

		public ElevationResponse.Builder majorMilestones(Collection<ElevationMilestone> milestones) {
			majors.addAll(milestones);
			return this;
		}

		public ElevationResponse.Builder closest(Collection<ElevationMilestone> milestones) {
			closest.addAll(milestones);
			return this;
		}

		public ElevationResponse.Builder target(double height, boolean converted) {
			this.target = height;
			if (converted)
				this.heightConverted = converted;
			return this;
		}

		public ElevationResponse build() {
			return new ElevationResponse(target, heightConverted, majors, closest);
		}

	}

}
