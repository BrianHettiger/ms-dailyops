package com.moblize.ms.dailyops.dto;

import java.util.List;

public class DrillingProfileDTO {

	private String wellUid;

	private List<DrillingProfileDataRecord> drillingProfileDataList;

	public String getWellUid() {
		return wellUid;
	}

	public void setWellUid(String wellUid) {
		this.wellUid = wellUid;
	}

	public List<DrillingProfileDataRecord> getDrillingProfileDataList() {
		return drillingProfileDataList;
	}

	public void setDrillingProfileDataList(
			List<DrillingProfileDataRecord> drillingProfileDataList) {
		this.drillingProfileDataList = drillingProfileDataList;
	}

	public static class DrillingProfileDataRecord {

		private Float holeDepth;

	    private Float cost;

	    private Float mudWeight;

	    private Float days;

	    public Float getHoleDepth() {
			return holeDepth;
		}

		public void setHoleDepth(Float holeDepth) {
			this.holeDepth = holeDepth;
		}

		public Float getCost() {
			return cost;
		}

		public void setCost(Float cost) {
			this.cost = cost;
		}

		public Float getMudWeight() {
			return mudWeight;
		}

		public void setMudWeight(Float mudWeight) {
			this.mudWeight = mudWeight;
		}

		public Float getDays() {
			return days;
		}

		public void setDays(Float days) {
			this.days = days;
		}
	}
}
