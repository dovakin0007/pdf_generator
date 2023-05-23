package com.example.pdfgenerator.Pojo;

public class JobDetails {
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobStep() {
		return jobStep;
	}
	public void setJobStep(String jobStep) {
		this.jobStep = jobStep;
	}
	public String getProcName() {
		return procName;
	}
	public void setProcName(String procName) {
		this.procName = procName;
	}
	public String getProcStep() {
		return procStep;
	}
	public void setProcStep(String procStep) {
		this.procStep = procStep;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getStepDescription() {
		return stepDescription;
	}
	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}
	private String jobName ; 
	private String jobStep ; 
	private String procName ;
	private String procStep ; 
	private String programName ; 
	private String stepDescription;

}
