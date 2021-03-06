package com.course.microservice.broker.message;

public class PerformanceAppraisalApprovedMessage {

	private String appraisalId;
	private String employeeId;
	private String grade;
	private int score;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		PerformanceAppraisalApprovedMessage other = (PerformanceAppraisalApprovedMessage) obj;

		if (appraisalId == null) {
			if (other.appraisalId != null) return false;
		} else if (!appraisalId.equals(other.appraisalId)) return false;

		if (employeeId == null) {
			if (other.employeeId != null) return false;
		} else if (!employeeId.equals(other.employeeId)) return false;

		if (grade == null) {
			if (other.grade != null) return false;
		} else if (!grade.equals(other.grade)) return false;

		if (score != other.score) return false;

		return true;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appraisalId == null) ? 0 : appraisalId.hashCode());
		result = prime * result + ((employeeId == null) ? 0 : employeeId.hashCode());
		result = prime * result + ((grade == null) ? 0 : grade.hashCode());
		result = prime * result + score;
		return result;
	}

	public String getAppraisalId() {
		return appraisalId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public String getGrade() {
		return grade;
	}

	public int getScore() {
		return score;
	}

	public void setAppraisalId(String appraisalId) {
		this.appraisalId = appraisalId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "AppraisalApprovedMessage [appraisalId=" + appraisalId + ", employeeId=" + employeeId + ", grade="
				+ grade + ", score=" + score + "]";
	}

}
