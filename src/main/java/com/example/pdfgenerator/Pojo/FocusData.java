package com.example.pdfgenerator.Pojo;

public class FocusData {
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getProgramTableOrFileName() {
		return programTableOrFileName;
	}
	public void setProgramTableOrFileName(String programTableOrFileName) {
		this.programTableOrFileName = programTableOrFileName;
	}
	public String getProgramDescription() {
		return programDescription;
	}
	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getInputOrOutput() {
		return inputOrOutput;
	}
	public void setInputOrOutput(String inputOrOutput) {
		this.inputOrOutput = inputOrOutput;
	}
	public String getProgramStep() {
		return programStep;
	}
	public void setProgramStep(String programStep) {
		this.programStep = programStep;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getProgramType2() {
		return programType2;
	}
	public void setProgramType2(String programType2) {
		this.programType2 = programType2;
	}
	private String jobName;
	private String programName;
	private String programTableOrFileName;
	private String programDescription;
	private String programType;
	private String inputOrOutput;
	private String programStep;
	private String fileName;
	private String programType2 = "Focus";
}
