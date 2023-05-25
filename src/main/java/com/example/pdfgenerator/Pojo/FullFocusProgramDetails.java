package com.example.pdfgenerator.Pojo;

public class FullFocusProgramDetails {
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getCalledModules() {
		return calledModules;
	}
	public void setCalledModules(String calledModules) {
		this.calledModules = calledModules;
	}
	public String getProgramDescription() {
		return programDescription;
	}
	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}
	private String programName;
	private String programType;
	private String calledModules;
	private String programDescription;

}
