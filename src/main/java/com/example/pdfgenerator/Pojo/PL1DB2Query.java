package com.example.pdfgenerator.Pojo;

public class PL1DB2Query {
	public String getJobName() {
		return JobName;
	}
	public void setJobName(String jobName) {
		JobName = jobName;
	}
	public String getProgramName() {
		return ProgramName;
	}
	public void setProgramName(String programName) {
		ProgramName = programName;
	}
	public String getDb2Query() {
		return Db2Query;
	}
	public void setDb2Query(String db2Query) {
		Db2Query = db2Query;
	}
	private String JobName;
	private String ProgramName;
	private String Db2Query;
}
