package com.kyrobot.howfar.responses;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.kyrobot.howfar.model.ElevationMilestone;

public final class ElevationResponse {
	private final Collection<ElevationMilestone> majorMilestones;
	private final Collection<ElevationMilestone> closestAchievements;
	
	private ElevationResponse(Collection<ElevationMilestone> majorMilestones,
			Collection<ElevationMilestone> closestAchievements)
	{
		this.majorMilestones = majorMilestones;
		this.closestAchievements = closestAchievements;
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
		Set<ElevationMilestone> majors = Sets.newConcurrentHashSet();
		Set<ElevationMilestone> closest = Sets.newConcurrentHashSet();
		
		private Builder() {
		}
		
		public ElevationResponse.Builder majorMilestones(Collection<ElevationMilestone> milestones)
		{
			majors.addAll(milestones);
			return this;
		}
		
		public ElevationResponse.Builder closest(Collection<ElevationMilestone> milestones) {
			closest.addAll(milestones);
			return this;
		}
		
		public ElevationResponse build() {
			return new ElevationResponse(majors, closest);
		}
		
		
	}
	
	
	
}