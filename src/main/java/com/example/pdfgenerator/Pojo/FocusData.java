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
	private String programName;
	private String programTableOrFileName;
	private String programDescription;
	private String programType;
	private String inputOrOutput;
	private String programStep;
	
}
