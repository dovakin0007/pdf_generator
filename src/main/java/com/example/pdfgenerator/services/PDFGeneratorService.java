package com.example.pdfgenerator.services;

import java.io.IOException;

import java.io.BufferedReader;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.HashMap;

import com.example.pdfgenerator.Pojo.*;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Image;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Paragraph;


import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;

import java.io.File;


import net.sourceforge.plantuml.SourceStringReader;


@Service
public class PDFGeneratorService {
	
	private static final int TITLE_FONT_SIZE = 24;
	private static final int TITLE_SUB_SECTION_SIZE = 18;
	private static final String PATH = "C:\\Users\\1000070564\\Downloads\\Shark_Tank_Input\\";
	
	public void export(HttpServletResponse response) throws Exception{

		BufferedReader reader = null;
		
		BufferedReader imsSectionReader = null;
		
		BufferedReader focusDataReader = null;
		BufferedReader focusSubModuleReader = null;
		
		BufferedReader pl1Reader = null;
		
		BufferedReader pl1IoDetsReader;
		
		BufferedReader pl1Db2QueryReader;
		
		BufferedReader focusComplexityReader;
	
		HashMap<String, ArrayList<JobDetails>> map= new HashMap<>();
		
		HashMap<String, ArrayList<ImsSection>> imsMap = new HashMap<>();
		
		HashMap<String, ArrayList<FocusSubData>> focusSubMap = new HashMap<>();
		
		HashMap<String, ArrayList<FocusData>> focusMap = new HashMap<>();
		
		HashMap<String, ArrayList<Pl1Details>> pl1Map = new HashMap<>();
		
		HashMap<String, ArrayList<Pl1IODets>> pl1IODetsMap = new HashMap<>();
		
		HashMap<String, ArrayList<PL1DB2Query>> Pl1Db2QueryMap = new HashMap<>();
		
		HashMap<String, ArrayList<FocusComplexity>> focusComplexityMap = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(PATH+"Job_Details.txt")); //change path where to read
			
			imsSectionReader = new BufferedReader (new FileReader(PATH+"IMS_Section.txt")); // change path where to read for IMS Db2
			
			focusDataReader = new BufferedReader(new FileReader(PATH+"FOC_Det.txt"));
			
			focusSubModuleReader = new BufferedReader(new FileReader(PATH+"FOC_SubM.txt"));
			
			if (new File(PATH + "FOC_Complexity.txt").exists()) {
				focusComplexityReader = new BufferedReader(new FileReader(PATH + "FOC_Complexity.txt"));
				String complexityFocLine;
				focusComplexityReader.readLine();
				while((complexityFocLine = focusComplexityReader.readLine())!= null) {
					if (complexityFocLine.contains(String.valueOf('|'))) {
					String[] fields = complexityFocLine.split("\\|");
					
					FocusComplexity focComplexityDets = new FocusComplexity();
					focComplexityDets.setJob(fields[0].trim());
					focComplexityDets.setProgram(fields[1].trim());
					focComplexityDets.setTableFiles(fields[2].trim());
					focComplexityDets.setMatchFiles(fields[3].trim());
					focComplexityDets.setJoins(fields[4].trim());
					focComplexityDets.setModifyFile(fields[5].trim());
					focComplexityDets.setRead(fields[6].trim());
					focComplexityDets.setWrite(fields[7].trim());
					focComplexityDets.setAlloc(fields[8].trim());
					focComplexityDets.setInclude(fields[9].trim());
					focComplexityDets.setEx(fields[10].trim());
					focComplexityDets.setTotalLines(fields[11].trim());
					focComplexityDets.setComplexity(fields[12].trim());
					
					if(focusComplexityMap.containsKey(fields[0].trim())){
						ArrayList<FocusComplexity> focComplexityArr = focusComplexityMap.get(fields[0].trim());
						focComplexityArr.add(focComplexityDets);
						focusComplexityMap.put(fields[0].trim(), focComplexityArr);
					}else {
						ArrayList<FocusComplexity> focComplexityArr = new ArrayList<>();
						focComplexityArr.add(focComplexityDets);
						focusComplexityMap.put(fields[0].trim(), focComplexityArr);
					}
					}
				}
			}
			
			if (new File(PATH + "PL1_DB2.txt").exists()) {
				pl1Db2QueryReader = new BufferedReader(new FileReader(PATH + "PL1_DB2.txt"));
				String pl1Db2Line;
				while((pl1Db2Line = pl1Db2QueryReader.readLine())!= null) {
					if (pl1Db2Line.contains(String.valueOf('|'))) {
						String[] fields = pl1Db2Line.split("\\|");
						
						PL1DB2Query pl1Db2QueryDets = new PL1DB2Query();
						pl1Db2QueryDets.setJobName(fields[0].trim());
						pl1Db2QueryDets.setProgramName(fields[1].trim());
						pl1Db2QueryDets.setDb2Query(fields[2].trim() + "\n");
						
						if(Pl1Db2QueryMap.containsKey(fields[0].trim()) ){
							ArrayList<PL1DB2Query> pl1Db2Arrays = Pl1Db2QueryMap.get(fields[0].trim());
							pl1Db2Arrays.add(pl1Db2QueryDets);
							Pl1Db2QueryMap.put(fields[0].trim(), pl1Db2Arrays);
						}
						else if (!(Pl1Db2QueryMap.isEmpty()) && pl1Db2QueryDets.getJobName().trim().equals("")) {
							int lastKeyPos = Pl1Db2QueryMap.keySet().size()-1;
							String keyArray[] =  Pl1Db2QueryMap.keySet().toArray(new String[0]);
							String recentKey = keyArray[lastKeyPos];
							ArrayList<PL1DB2Query> pl1Db2Arrays = Pl1Db2QueryMap.get(recentKey);
							if (pl1Db2QueryDets.getProgramName().trim().equals("")) {
								PL1DB2Query pl1Db2LastQueryDets = pl1Db2Arrays.get(pl1Db2Arrays.size()-1);
								String newPl1Query = pl1Db2LastQueryDets.getDb2Query() + pl1Db2QueryDets.getDb2Query();
								pl1Db2LastQueryDets.setDb2Query(newPl1Query);
								pl1Db2Arrays.set(pl1Db2Arrays.size()-1, pl1Db2LastQueryDets);
								Pl1Db2QueryMap.put(recentKey, pl1Db2Arrays);
							}else {
								PL1DB2Query pl1Db2LastQueryDets = pl1Db2Arrays.get(pl1Db2Arrays.size()-1);
								String newPl1Query = pl1Db2LastQueryDets.getDb2Query() + pl1Db2QueryDets.getDb2Query();
								pl1Db2LastQueryDets.setDb2Query(newPl1Query);
								pl1Db2Arrays.set(pl1Db2Arrays.size()-1, pl1Db2LastQueryDets);
								Pl1Db2QueryMap.put(recentKey, pl1Db2Arrays);
							}
						}else {
							ArrayList<PL1DB2Query> pl1Db2Arrays = new ArrayList<>();
							pl1Db2Arrays.add(pl1Db2QueryDets);
							Pl1Db2QueryMap.put(fields[0].trim(), pl1Db2Arrays);
						}
						
						
					}
				}
			}
			
			
			if (new File(PATH + "PL1_FILE" + ".txt").exists()) {
				pl1IoDetsReader = new BufferedReader(new FileReader(PATH + "PL1_FILE" + ".txt"));
				String pl1IoLine;
				while((pl1IoLine = pl1IoDetsReader.readLine())!= null) {
					if (pl1IoLine.contains(String.valueOf('|'))) {
						String[] fields = pl1IoLine.split("\\|");
						
						Pl1IODets pl1IoObjDets = new Pl1IODets();
						pl1IoObjDets.setJobName(fields[0].trim());
						pl1IoObjDets.setProgramName(fields[1].trim());
						pl1IoObjDets.setDDName(fields[2].trim());
						pl1IoObjDets.setUsage(fields[3].trim());
						pl1IoObjDets.setFileName(fields[4].trim());
						
						if(pl1IODetsMap.containsKey(fields[0].trim())){
							ArrayList<Pl1IODets> pl1IoArrays = pl1IODetsMap.get(fields[0].trim());
							pl1IoArrays.add(pl1IoObjDets);
							pl1IODetsMap.put(fields[0].trim(), pl1IoArrays);
						}else {
							ArrayList<Pl1IODets> pl1IoArrays = new ArrayList<>();
							pl1IoArrays.add(pl1IoObjDets);
							pl1IODetsMap.put(fields[0].trim(), pl1IoArrays);
						}
						
						
					}
				}
			}
			
			
			if(new File(PATH + "PL1_Det" + ".txt").exists()) {				
				pl1Reader = new BufferedReader(new FileReader(PATH + "PL1_Det.txt"));
				String pl1Line;
				while((pl1Line = pl1Reader.readLine())!= null) {
					if (pl1Line.contains(String.valueOf('|'))) {
						String[] fields = pl1Line.split("\\|");
						
						Pl1Details pl1ObjDetails = new Pl1Details();
						pl1ObjDetails.setJobName(fields[0].trim());
						pl1ObjDetails.setProgramName(fields[1].trim());
						pl1ObjDetails.setProgramType(fields[2].trim());
						pl1ObjDetails.setSubModules(fields[3].trim());
						pl1ObjDetails.setCopyBooks(fields[4].trim());
					
						if (pl1Map.containsKey(fields[0].trim())) {
							ArrayList<Pl1Details> pl1ArrayList = pl1Map.get(fields[0].trim());
							pl1ArrayList.add(pl1ObjDetails);
							pl1Map.put(fields[0].trim(), pl1ArrayList);
						}
						else {
							ArrayList<Pl1Details> pl1ArrayList = new ArrayList<>();
							pl1ArrayList.add(pl1ObjDetails);
	 						pl1Map.put(fields[0].trim(), pl1ArrayList);
						}
						
					}
				}
			}
			else {

			}
		
			String imsLine;
			imsSectionReader.readLine();
			//skipping first line
			
			while((imsLine =  imsSectionReader.readLine())!= null) {
				if (imsLine.contains(String.valueOf('|'))) {

					String[] imsFields = imsLine.split("\\|"); //creating array using split func
					ImsSection imsSectionObj = new ImsSection();

					imsSectionObj.setJobName(imsFields[0].trim());
					imsSectionObj.setPSBMember(imsFields[1].trim());
					imsSectionObj.setPGMName(imsFields[2].trim());// key of the Hashmap
					imsSectionObj.setDBDName(imsFields[3].trim());
					imsSectionObj.setDBDProcopt(imsFields[4].trim());
					imsSectionObj.setSegement(imsFields[5].trim());
					imsSectionObj.setSegProcopt(imsFields[6].trim());
					
					if (imsMap.containsKey(imsFields[0].trim())) {
						ArrayList<ImsSection> imsMapArrayList = imsMap.get(imsFields[0].trim());
						imsMapArrayList.add(imsSectionObj);
						imsMap.put(imsFields[0].trim(), imsMapArrayList);
					}
					else {
						ArrayList<ImsSection> imsMapArrayList = new ArrayList<>();
						imsMapArrayList.add(imsSectionObj);
 						imsMap.put(imsFields[0].trim(), imsMapArrayList);
					}
				}
				
			}
			
			String line;
			reader.readLine();// skips first line
			
			while((line = reader.readLine()) != null) {
				if (line.contains(String.valueOf('|'))) {
				String[] fields = line.split("\\|", -1);
				JobDetails jobDetails = new JobDetails();

				jobDetails.setJobName(fields[0].trim());
				jobDetails.setJobStep(fields[1].trim());
				jobDetails.setProcName(fields[2].trim());
				jobDetails.setProcStep(fields[3].trim());
				jobDetails.setProgramName(fields[4].trim());
				jobDetails.setStepDescription(fields[5].trim());
				jobDetails.setParameters(fields[6].trim());
				
				if (map.containsKey(fields[0].trim())) {
					ArrayList<JobDetails> newValue = map.get(fields[0].trim());
					newValue.add(jobDetails);
					map.put(fields[0].trim(), newValue);
				}
				else {
					ArrayList<JobDetails> newValue = new ArrayList<>();
					newValue.add(jobDetails);
					map.put(fields[0].trim(), newValue);				
					}
			}
			}
			
			String focusSubModule;
			
			while((focusSubModule = focusSubModuleReader.readLine()) != null) {
				
				if(focusSubModule.contains(String.valueOf("|"))) {
					String[] fields = focusSubModule.split("\\|");
					FocusSubData focusSubObj = new FocusSubData();
					focusSubObj.setJobName(fields[0].trim());
					focusSubObj.setProgramName(fields[1].trim());
					if(!(fields[2].trim().equals(""))) {
						focusSubObj.setSubModule1(fields[2].trim());
						focusSubObj.setHasSubModule1(true);
					}if(!(fields[3].trim().equals(""))) {
						focusSubObj.setSubModule2(fields[3].trim());
						focusSubObj.setHasSubModule2(true);
					}if(!(fields[4].trim().equals(""))) {
						focusSubObj.setSubModule3(fields[4].trim());
						focusSubObj.setHasSubModule3(true);
					}if(!(fields[5].trim().equals(""))) {
						focusSubObj.setSubModule4(fields[5].trim());
						focusSubObj.setHasSubModule4(true);
					}if(!(fields[6].trim().equals(""))) {
						focusSubObj.setSubModule5(fields[6].trim());
						focusSubObj.setHasSubModule5(true);
					}
					if(focusSubMap.containsKey(fields[0].trim())){
						ArrayList<FocusSubData> valueFocus= focusSubMap.get(fields[0].trim());
						valueFocus.add(focusSubObj);
						focusSubMap.put(fields[0].trim(), valueFocus);
					}else {
						ArrayList<FocusSubData> focusSubArray = new ArrayList<>();
						focusSubArray.add(focusSubObj);
						focusSubMap.put(fields[0].trim(), focusSubArray);
					}
					
				}
			}
			
			
			
			String focusLine;
			
			while((focusLine = focusDataReader.readLine()) != null) {
				if (focusLine.contains(String.valueOf('|'))) {
					String[] fields = focusLine.split("\\|");
					FocusData focusObj = new FocusData();
					focusObj.setJobName(fields[0].trim());
					focusObj.setProgramName(fields[1].trim());
					focusObj.setProgramTableOrFileName(fields[2].trim());
					focusObj.setProgramDescription(fields[3].trim());
					focusObj.setProgramType(fields[4].trim());
					focusObj.setInputOrOutput(fields[5].trim());
					focusObj.setProgramStep(fields[6].trim());
					focusObj.setFileName(fields[7]!=null?fields[7].trim():"");
					
					
					if (focusMap.containsKey(fields[0].trim())) {
						ArrayList<FocusData> focusArray = focusMap.get(fields[0].trim());
						focusArray.add(focusObj);
						focusMap.put(fields[0].trim(), focusArray);
					}
					else {
						ArrayList<FocusData> focusArray = new ArrayList<>();
						focusArray.add(focusObj);
						focusMap.put(fields[0].trim(), focusArray);
					}
					
				}
			}
			
			// work on job description
			
			
			for(String mapKey:map.keySet()) {

				ArrayList<String> appendedStrings = new ArrayList<>();
				for(JobDetails i: map.get(mapKey)) {
					if (i.getStepDescription() == "") {
						
					}
					else {
						String newString = i.getStepDescription();
						if(appendedStrings.isEmpty()) {
							appendedStrings.add(newString);
						}
						else {
						if(appendedStrings.get(appendedStrings.size()-1).equals(newString)) {
							
						}else {
						
							appendedStrings.add(newString);
						}
					}
					}
				}
				ArrayList<HashMap<String, String>> keyValueFocusSubMapList = null ;
				ArrayList<FocusSubData> focusSubDataArray ;
				if(focusSubMap.containsKey(mapKey) ) {
					 focusSubDataArray = focusSubMap.get(mapKey);
					 keyValueFocusSubMapList = new ArrayList<>();
						
						for(FocusSubData d: focusSubDataArray) {
							HashMap<String, String> keyAndValuesForFocusSubMap = new HashMap<>();
							
							if(d.isHasSubModule1()==true) {
								String mainModule = d.getProgramName();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule1());
								
							}else if(d.isHasSubModule1()== false) {
								String mainModule = d.getProgramName();
								keyAndValuesForFocusSubMap.put(mainModule, "N/A");
							}
							
							if(d.isHasSubModule2()==true) {
								String mainModule = d.getSubModule1();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule2());
							}else if(d.isHasSubModule2()== false && d.isHasSubModule1() == true) {
								String mainModule = d.getSubModule1();
								keyAndValuesForFocusSubMap.put(mainModule, "N/A");
							}
							
							if(d.isHasSubModule3()==true) {
								String mainModule = d.getSubModule2();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule3());
							}else if(d.isHasSubModule3()== false && d.isHasSubModule2() == true) {
								String mainModule = d.getSubModule2();
								keyAndValuesForFocusSubMap.put(mainModule, "N/A");
							}
							
							if(d.isHasSubModule4()==true) {
								String mainModule = d.getSubModule3();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule4());
							}else if(d.isHasSubModule4()== false && d.isHasSubModule3() == true) {
								String mainModule = d.getSubModule3();
								keyAndValuesForFocusSubMap.put(mainModule, "N/A");
							}
							
							if(d.isHasSubModule5()==true) {
								String mainModule = d.getSubModule4();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule5());
							}else if(d.isHasSubModule5()== false && d.isHasSubModule4() == true) {
								String mainModule = d.getSubModule4();
								keyAndValuesForFocusSubMap.put(mainModule, "N/A");
							}
							
							keyValueFocusSubMapList.add(keyAndValuesForFocusSubMap);
							
						}
						

				
				}
				HashMap<String, ArrayList<String>> moduleTypesKeyValues = null;
				if(keyValueFocusSubMapList== null) {
					moduleTypesKeyValues = new HashMap<>();
				}
				else {
				moduleTypesKeyValues = new HashMap<>();
					for (HashMap<String, String>data: keyValueFocusSubMapList) {
						SortedSet<String> keys = new TreeSet<>(data.keySet());
						for(String s :keys) {
							if (moduleTypesKeyValues.containsKey(s)) {
								ArrayList<String> value = moduleTypesKeyValues.get(s);
								value.add(data.get(s));
								moduleTypesKeyValues.put(s, value);
							}else {
								ArrayList<String> value = new ArrayList<>();
								value.add(data.get(s));
								moduleTypesKeyValues.put(s, value);
							}
						}
					}
					
				}
				
				HashMap<String, ArrayList<String>> programStepDetails = null;
				if(focusMap.containsKey(mapKey)) {
				programStepDetails = new HashMap<>();
				for(FocusData i: focusMap.get(mapKey)) {
					
					 if(moduleTypesKeyValues.containsKey(i.getProgramName())) {
						 if(programStepDetails.containsKey(i.getProgramName())) {
							 ArrayList<String> values = programStepDetails.get(i.getProgramName());
							 String prevValue =  values.get(values.size()-1);
							
							 if (!(prevValue.replaceAll("\\d", "").equals(i.getProgramStep().replaceAll("\\d", "")))) {
								 values.add(i.getProgramStep());
							 }
							 
							 programStepDetails.put(i.getProgramName(), values);
						 }else {
							ArrayList<String> values = new ArrayList<>();
							values.add(i.getProgramStep());
							programStepDetails.put(i.getProgramName(), values);
						 }
					 }
				}
				
				for (String keyForProgramStep: programStepDetails.keySet()) {
					ArrayList<String> programStepArray = new ArrayList<>();
					int idx = 0;
					for (String currentString:  programStepDetails.get(keyForProgramStep)) {
						idx++;
						String newString = idx+") "+currentString.substring(4);
						
						programStepArray.add(newString);
						
					}
					programStepDetails.put( keyForProgramStep,  programStepArray);
				}
				
				
				StringBuilder uml = new StringBuilder();
				File fOS= new File(PATH + mapKey + ".png");
				FontFactory.register("C:\\Windows\\Fonts\\Calibri");
				if (appendedStrings.size() == 0) {
					
				}else {
					uml.append("@startuml\n");
					uml.append("(*)");
					for (int startOfFlow = 0; startOfFlow <= appendedStrings.size()-1; startOfFlow++) {
						if (appendedStrings.get(startOfFlow).trim() == "") {
							
						}else {
						uml.append(" --> " + appendedStrings.get(startOfFlow).replaceAll("[^a-zA-z0-9\\s]", "") + "\n");
						}
					}
					uml.append("--> (*)\n");
					
					uml.append("@enduml\n");

					SourceStringReader umlReader = new SourceStringReader(uml.toString());
					
					FileOutputStream outputPNGFile = new FileOutputStream(fOS);
					umlReader.outputImage(outputPNGFile);
				}

				Document document = new Document(PageSize.A4);
				try {
					PdfWriter writer = PdfWriter.getInstance(document ,new FileOutputStream("C:\\Users\\1000070564\\Documents\\FSD-Documents\\" + mapKey + ".pdf"));//change path where to write
//					PdfWriter.getInstance(document, new FileOutputStream(mapKey));
					writer.setPageEvent(new PageNumberAndMarginHandler());
					document.open();
					
					Font fontTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					fontTitle.setSize(TITLE_FONT_SIZE);
					
					Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					tableHeaderTitle.setSize(14);
					
					PdfPCell titleMain =new PdfPCell (new Paragraph("Technical Specification document \n Job - " + mapKey +"\n" , fontTitle));
//					titleMain.setSpacingAfter(20f);
//					titleMain.setSpacingBefore();
					
					titleMain.setPadding(10f);
//					titleMain.setBorder(2);

					titleMain.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PdfPTable tableHeadingBox = new PdfPTable(1);
					tableHeadingBox.setSpacingBefore(10f);
					tableHeadingBox.addCell(titleMain);
					
					//creating table
					
					PdfPTable table1 = new PdfPTable(4);
					
					float[] width2 = {200, 300, 150, 150};
					
					table1.addCell(new Paragraph("Step Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Step Description", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Program", tableHeaderTitle));
					table1.addCell(new Paragraph("Parameters",  tableHeaderTitle));
					table1.setWidths(width2);
					for(JobDetails data: map.get(mapKey)) {//looping to the array list
						if(data.getJobStep().trim() == "") {
						
						}
						else {
						if(data.getJobStep().trim() == "") {
							table1.addCell("N/A");
						}else if (!(data.getProcStep().equals(""))) {
							table1.addCell(data.getJobStep() + "." + data.getProcStep());
						}
						else {
							table1.addCell(data.getJobStep());
						}
						
						if(data.getStepDescription().trim() == "") {
							table1.addCell("N/A");
						}else {
							table1.addCell(data.getStepDescription());
							}
						
						if(data.getProgramName().trim() == "") {
							table1.addCell("N/A");
						}
						else {
							table1.addCell(data.getProgramName());
						}
		
						}
						if(data.getParameters().trim().equals("")) {
							table1.addCell("None");
						}else {
							table1.addCell(data.getParameters().trim());
						}
						
					}

//					keyValueFocusSubMapList;
					PdfPTable programTable = new PdfPTable(4);
					programTable.addCell(new Paragraph("Program Name", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Type", tableHeaderTitle));
					programTable.addCell(new Paragraph("Process Flow", tableHeaderTitle));
					programTable.addCell(new Paragraph("Called Modules", tableHeaderTitle));
						
					float[] width = {150, 150, 400, 150};
					programTable.setWidths(width);
					Map<String, ArrayList<String>> sortedFocusProgramDataMap = new TreeMap<>(programStepDetails);
					for (String i: sortedFocusProgramDataMap.keySet()) {
						programTable.addCell(i);
						programTable.addCell("FOCUS");
						
						StringBuilder programDescriptions = new StringBuilder();
						
						for (String programDescription: sortedFocusProgramDataMap.get(i)) {
							programDescriptions.append(programDescription + "\n");
						}
						programTable.addCell(programDescriptions.toString());
						
						StringBuilder calledModules = new StringBuilder();
						for(String calledModule : moduleTypesKeyValues.get(i)) {
							calledModules.append(calledModule + "\n");
						}
						programTable.addCell(calledModules.toString());	
						
					}
	
					if (pl1Map.containsKey(mapKey)) {
						for(Pl1Details pl1Dets: pl1Map.get(mapKey)) {
						
							programTable.addCell(pl1Dets.getProgramName());
							programTable.addCell(pl1Dets.getProgramType());
							programTable.addCell(" ");
							if (pl1Dets.getSubModules().equals("")) {
								programTable.addCell("N/A");
							}else {
								programTable.addCell(pl1Dets.getSubModules().replaceAll("\\,", "\n"));
							}
							
							
						}
					}
					
					document.add(new Paragraph(" "));
					document.add(tableHeadingBox);
					document.add(titlesection("1.0 Job Details: "));
					document.add(content("Below are the high level job details"));
					document.add(table1);
					
					//adding png file;
					
					Paragraph flowChartContentEdited = content("Flow diagram depicting sequence of execution");
					flowChartContentEdited.setSpacingAfter(25f);
					flowChartContentEdited.setSpacingBefore(25f);
					
					if(new File(PATH + mapKey + ".png").exists()) {

						Image pngFile = Image.getInstance(PATH + mapKey + ".png");
						pngFile.scalePercent(60f);
						pngFile.setAlignment(Image.ALIGN_CENTER);

						document.add(flowChartContentEdited);
						document.add(pngFile);

					}
					else {
						document.add(flowChartContentEdited);
						document.add(content("N/A"));
					}
					
					Paragraph prgDetailsTitle = titlesection("2.0 Program Details");
					prgDetailsTitle.setSpacingBefore(100f);
					document.add(prgDetailsTitle);
					document.add(content("Below programs are part of this job which performs given actions."));
					if(programTable.getRows().size() == 1) {
						document.add(content("Job executing only IBM utilities"));
					}else {
						document.add(programTable);	
					}
					document.add(titlesection("3.0 Databases"));
					Paragraph indentendIMSDBName = title("3.1 IMS Databases");
					Paragraph imsDBContent = content("Below IMS databases are in use by respective programs. "
							+ "These programs can perform below actions on Database / Segment within the database. ");
					imsDBContent.setIndentationLeft(50f);
					indentendIMSDBName.setIndentationLeft(50f);
					
					document.add(indentendIMSDBName);
					document.add(imsDBContent);
					
					HashSet<String> prgNamesForPl1 = new HashSet<>();
					for (String pl1IMSProgramName: imsMap.keySet()) {
						if(imsMap.containsKey(pl1IMSProgramName)) {
							for (ImsSection e :imsMap.get(pl1IMSProgramName)) {
								prgNamesForPl1.add(e.getPGMName().trim());
							}
							
						}
					}

					ArrayList <ImsSection> sortedIMSData =   new ArrayList<>();
					if (imsMap.containsKey(mapKey)) {
						for(ImsSection i:imsMap.get(mapKey)) {
							sortedIMSData.add(i);
						}
					}
					
					HashMap<String, String> prgNamesAndPrgType = new HashMap<>();
					if (pl1Map.containsKey(mapKey)) {
						for(Pl1Details x:pl1Map.get(mapKey)){
							prgNamesAndPrgType.put(x.getProgramName().trim(), x.getProgramType());
						}
					}
					//creating table
					HashSet<String> onlyFKProgramName = new HashSet<>();

					for(String keys :sortedFocusProgramDataMap.keySet()) {
						onlyFKProgramName.add(keys);
					}
					
					
					
					ArrayList<FocusData> focusDataToBeAdded = new ArrayList<>();
					HashSet<String>keysForIMSMapFocus = new HashSet<>();
					
					//sorting out IMS data focus
					if(focusMap.containsKey(mapKey)) {
						for(FocusData j: focusMap.get(mapKey)) {
							if(mapKey.equals(j.getJobName()) && j.getProgramType().equals("IMS DATABASE")) {
								if (focusDataToBeAdded.isEmpty()) {
									focusDataToBeAdded.add(j);
								}else {
									int lengthOfFocusData = focusDataToBeAdded.size();
									FocusData prevFocusData = focusDataToBeAdded.get(lengthOfFocusData - 1);
									if(prevFocusData.getProgramName().trim().equals(j.getProgramName().trim())) {
										
									}else {
										focusDataToBeAdded.add(j);
									}
								}
								keysForIMSMapFocus.add(j.getProgramName().trim());
							}
						}	
					}
					
					//sorting out db2 data
					ArrayList<FocusData> focusDataToBeAddedDb2 = new ArrayList<>();
					HashSet<String> keysForDb2Tables = new HashSet<>();
					if(focusMap.containsKey(mapKey)) {
					
						for(FocusData j: focusMap.get(mapKey)) {
							if(mapKey.equals(j.getJobName()) && j.getProgramType().equals("DB2 TABLE")) {
								focusDataToBeAddedDb2.add(j);
								keysForDb2Tables.add(j.getProgramName().trim());
							}
						}
		
					}
					
					//sorting out copybook
					ArrayList<FocusData> focusDataToBeAddedCopyBook = new ArrayList<>();
					HashSet<String> keysForCopyBookTables = new HashSet<>();
					if(focusMap.containsKey(mapKey)) {
						for(FocusData j: focusMap.get(mapKey)) {
							if(mapKey.equals(j.getJobName()) && j.getProgramType().equals("I/O FILE")) {
								focusDataToBeAddedCopyBook.add(j);
							}
						}
					
						for (FocusData i: focusMap.get(mapKey)) {
							keysForCopyBookTables.add(i.getProgramName().trim());
						}
					}
					ArrayList<FocusData> focusDataToBeAddedIO = new ArrayList<>();
					HashSet<String> keysForIOTables = new HashSet<>();
					if(focusMap.containsKey(mapKey)) {
						for(FocusData j: focusMap.get(mapKey)) {
							if(mapKey.equals(j.getJobName()) && j.getProgramType().equals("I/O FILE")) {
								focusDataToBeAddedIO.add(j);
								keysForIOTables.add(j.getProgramName().trim());
							}
						}
		
					}
					
					if (prgNamesForPl1.isEmpty() &&focusDataToBeAdded.isEmpty()) {
						document.add(content("N/A"));
					}else {
					int documentIndex = 1;
					for(String titleImsPl1Data: prgNamesForPl1) {
						ArrayList <ImsSection> imsDataToBePrinted =  new ArrayList<>();
						
						for(ImsSection imsPl1: sortedIMSData) {

							if (titleImsPl1Data.equals(imsPl1.getPGMName().trim())) {
								 imsDataToBePrinted.add(imsPl1);

							}	
						}
						if (!(imsDataToBePrinted.isEmpty())) {
							Paragraph indentedSubTitle ;
							
						if (prgNamesAndPrgType.containsKey(titleImsPl1Data)) {
							indentedSubTitle = title("3.1." + documentIndex + " " + titleImsPl1Data + " - " + prgNamesAndPrgType.get(titleImsPl1Data));
							documentIndex++;
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							document.add(pgmTablesSCLM(imsDataToBePrinted, titleImsPl1Data));
						}else {
							indentedSubTitle = title("3.1."+ documentIndex + " " +titleImsPl1Data + " - PL1");
							documentIndex++;
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							document.add(pgmTablesSCLM(imsDataToBePrinted, titleImsPl1Data));
						}

						}
					}
					
					if (focusDataToBeAdded.isEmpty()) {

					}
				
					else {
	
						for (String data: keysForIMSMapFocus) {
							
							Paragraph indentedSubTitle = title("3.1." + documentIndex + " " + data + " - FOCUS");

							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							
							document.add(pgmTablesFocus(focusDataToBeAdded, data));
							
						}
					
					document.add(content("G - Get / Read"
							+ "\r\n"
							+ "GOT - Get When database is allocated to other process in exclusive mode"
							+ "\r\n"
							+ "I - Insert"
							+ "\r\n"
							+ "R - Replace"
							+ "\r\n"
							+ "D - Delete"
							+ "\r\n"
							+ "A - All actions"
							+ "\r\n"
							+ "P - Path Call"));
						}
					}
					Paragraph indentendDB2DBName = title("3.2 DB2 Databases");
					indentendDB2DBName.setIndentationLeft(50f);
					
					Paragraph indentedDB2Content = content("Below programs are using DB2 tables to extract or update the data.");
					indentedDB2Content.setIndentationLeft(50f);
				
					document.add(indentendDB2DBName);
					
					HashSet<String> keysForDb2TablesPl1 = new HashSet<>();
					
					if (Pl1Db2QueryMap.containsKey(mapKey)){
						for(PL1DB2Query i :Pl1Db2QueryMap.get(mapKey)) {
							keysForDb2TablesPl1.add(i.getProgramName());
						}
					}
					
					if(keysForDb2Tables.isEmpty() && keysForDb2TablesPl1.isEmpty()) {
						document.add(content("N/A"));
					}
					else {
						document.add(indentedDB2Content);
				
						Paragraph indentedSubTitle = title("3.2.1 FOCUS - DB2 Programs");
						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						document.add(indentedSubTitle);							
						document.add(db2Tables(focusDataToBeAddedDb2, "NA"));
					
						if (Pl1Db2QueryMap.containsKey(mapKey)) {
							Paragraph newIndentedSubTitle = title("3.2.2 PL1 - DB2 Programs");
							Font newFontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							newIndentedSubTitle.setFont(newFontForSubTitle);
							newIndentedSubTitle.setIndentationLeft(100f);
							document.add(newIndentedSubTitle);	
							document.add(pl1DB2QueryTables(Pl1Db2QueryMap.get(mapKey), "NA"));
						}
					}
					
					
					HashSet<String> newKeysForCopyBookDetails = new HashSet<>();
					if (pl1Map.containsKey(mapKey)) {
						for(Pl1Details pl1Dets: pl1Map.get(mapKey)) {
							if(!(pl1Dets.getCopyBooks().trim().equals(""))) {
								 newKeysForCopyBookDetails.add(pl1Dets.getProgramName());
							}
						}
					}
					
					Paragraph indentedCopyBookName = titlesection("4.0 CopyBook Details");
					Paragraph indentedCopyBookDetails = content("This section provides the details of copybooks.");
					indentedCopyBookDetails.setIndentationLeft(50f);
					
					document.add(indentedCopyBookName);

					if (newKeysForCopyBookDetails.isEmpty()) {
						document.add(content("N/A"));
					}else {
						document.add(indentedCopyBookDetails);
						
						ArrayList<Pl1Details> dataToBeAddedPl1 = new ArrayList<>();
						for (String data: newKeysForCopyBookDetails) {
						
						Paragraph indentedSubTitle = title(data);
						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						
						for(Pl1Details dataToBeAddedPl1CopyBook:pl1Map.get(mapKey)) {
							if (data.equals(dataToBeAddedPl1CopyBook.getProgramName())) {
								dataToBeAddedPl1.add(dataToBeAddedPl1CopyBook);
								
							}
						}
						
					}
					if (!(dataToBeAddedPl1.isEmpty())) {
						document.add(copyBookTables(dataToBeAddedPl1));
					}
		
					}
					
					
					Paragraph indentedInputOutput = titlesection("5.0 Input/Output Files");
					  					
					HashSet <String> programNamesPl1Io = new HashSet<>();
					
					if (pl1IODetsMap.containsKey(mapKey)) {
						ArrayList<Pl1IODets> pl1IODetsArray = pl1IODetsMap.get(mapKey);
						for (Pl1IODets data: pl1IODetsArray) {
							programNamesPl1Io.add(data.getProgramName());
						}
					}					
					
					document.add(indentedInputOutput);
					if (keysForIOTables.isEmpty() && programNamesPl1Io.isEmpty()) {
						document.add(content("N/A"));
					}
					else {
						Paragraph indentedSubTitle = title("5.1 Input/Output"+ " - FOCUS");

						Font fontForSubTitle = new Font(Font.BOLD, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						document.add(indentedSubTitle);
						if(!(focusDataToBeAddedIO.isEmpty())) {
							document.add(inputOutputTables(focusDataToBeAddedIO , "NA"));
						}
					
						Paragraph newIndentedSubTitle = title("5.2 Input/Output" + " - PL1");
					
						newIndentedSubTitle.setFont(fontForSubTitle);
						newIndentedSubTitle.setIndentationLeft(100f);
						document.add(newIndentedSubTitle);
						if (pl1IODetsMap.containsKey(mapKey)) {
							document.add(inputOutputFocusTables(pl1IODetsMap.get(mapKey), "NA"));
							}
						}
					Paragraph indentedComplexity = titlesection("6.0 Complexity");
					document.add(indentedComplexity);
					Paragraph indentedFocusComplexity = title("6.1 Focus Complexity");
					Font someFont = new Font(BaseFont.createFont(), 14);
					indentedFocusComplexity.setFont(someFont);
					indentedFocusComplexity.setIndentationLeft(50);
					document.add(indentedFocusComplexity);
					
					Paragraph focusComplexityContent = content(" below are the complexity details for each of the FOCUS programs");
					 focusComplexityContent.setIndentationLeft(60);
					document.add(focusComplexityContent);
					if (focusComplexityMap.containsKey(mapKey)) {
						ArrayList<FocusComplexity> focusComplexityList = focusComplexityMap.get(mapKey);
						PdfPTable focTable = focusComplexityTable(focusComplexityList);
						focTable.setWidthPercentage(90);
						document.add(focTable);
						Paragraph focusComplexityContent2 = content("Complexity has been determined based on below matrix");
						focusComplexityContent2.setIndentationLeft(60);
						document.add(focusComplexityContent2);
						document.add(focusMatrixComp());
												
					}else {
						Paragraph na = new Paragraph("N/A");
						na.setIndentationLeft(100f);
						document.add(na);
					}
				
					document.close();
					
				} catch (DocumentException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
				else {
					
				StringBuilder uml = new StringBuilder();
				File fOS= new File(PATH + mapKey + ".png");
				FontFactory.register("C:\\Windows\\Fonts\\Calibri");
				if (appendedStrings.size() == 0) {
					
				}else {
					uml.append("@startuml\n");
					uml.append("(*)");
					for (int startOfFlow = 0; startOfFlow <= appendedStrings.size()-1; startOfFlow++) {
						if (appendedStrings.get(startOfFlow).trim() == "") {
							
						}else {
						uml.append(" --> " + appendedStrings.get(startOfFlow).replaceAll("[^a-zA-z0-9\\s]", "") + "\n");
						}
					}
					uml.append("--> (*)\n");
					
					uml.append("@enduml\n");

					SourceStringReader umlReader = new SourceStringReader(uml.toString());
					
					FileOutputStream outputPNGFile = new FileOutputStream(fOS);
					umlReader.outputImage(outputPNGFile);
				}

				Document document = new Document(PageSize.A4);
				try {
					PdfWriter writer = PdfWriter.getInstance(document ,new FileOutputStream("C:\\Users\\1000070564\\Documents\\FSD-Documents\\" + mapKey + ".pdf"));//change path where to write
//					PdfWriter.getInstance(document, new FileOutputStream(mapKey));
					writer.setPageEvent(new PageNumberAndMarginHandler());
					document.open();
					
					Font fontTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					fontTitle.setSize(TITLE_FONT_SIZE);
					
					Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					tableHeaderTitle.setSize(14);
					
					PdfPCell titleMain =new PdfPCell (new Paragraph("Technical Specification document \n  Job - " + mapKey +"\n" , fontTitle));

					titleMain.setPadding(10f);
					titleMain.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PdfPTable tableHeadingBox = new PdfPTable(1);
					tableHeadingBox.setSpacingBefore(10f);
					tableHeadingBox.addCell(titleMain);
					
					//creating table

					PdfPTable table1 = new PdfPTable(4);
					
					table1.addCell(new Paragraph("Step Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Step Description", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Program", tableHeaderTitle));
					table1.addCell(new Paragraph("Parameters",  tableHeaderTitle));
					float[] width = {200, 300, 150, 150};
					table1.setWidths(width);
					for(JobDetails data: map.get(mapKey)) {//looping to the array list
						if(data.getJobStep().trim() == "") {
						}
						else {
							if(data.getJobStep().trim() == "") {
								table1.addCell("N/A");
							}else if (!(data.getProcStep().equals(""))) {
								table1.addCell(data.getJobStep() + "." + data.getProcStep());
							}
							else {
								table1.addCell(data.getJobStep());
							}
							if(data.getStepDescription().trim() == "") {
								table1.addCell("N/A");
							}else {
								table1.addCell(data.getStepDescription());
							}
							if(data.getProgramName().trim() == "") {
								table1.addCell("N/A");
							}
							else {
								table1.addCell(data.getProgramName());
							}
						
							}
							if(data.getParameters().trim().equals("")) {
								table1.addCell("N/A");
							}else {
								table1.addCell(data.getParameters());
							}						
					}
					
					PdfPTable programTable = new PdfPTable(4);
					programTable.addCell(new Paragraph("Program Name", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Type", tableHeaderTitle));
					programTable.addCell(new Paragraph("Process Flow", tableHeaderTitle));
					programTable.addCell(new Paragraph("Called Modules", tableHeaderTitle));
					float[] width2 = {150, 150, 400, 150};
					programTable.setWidths(width2);

					if (pl1Map.containsKey(mapKey)) {
						for(Pl1Details pl1Dets: pl1Map.get(mapKey)) {
						
							programTable.addCell(pl1Dets.getProgramName());
							programTable.addCell(pl1Dets.getProgramType());
							
							programTable.addCell(" ");
							
							if (pl1Dets.getSubModules().equals("")) {
								programTable.addCell("N/A");
							}else {
								programTable.addCell(pl1Dets.getSubModules().replaceAll("\\,", "\n"));
							}
						}
					}
					
					document.add(new Paragraph(" "));
					document.add(tableHeadingBox);
					document.add(titlesection("1.0 Job Details: "));
					document.add(content("Below are the high level job details"));
					document.add(table1);
					
					//adding png file;
					
					Paragraph flowChartContentEdited = content("Flow diagram depicting sequence of execution");
					flowChartContentEdited.setSpacingAfter(25f);
					flowChartContentEdited.setSpacingBefore(25f);
					
					if(new File(PATH + mapKey + ".png").exists()) {
						Image pngFile = Image.getInstance(PATH + mapKey + ".png");
						pngFile.scalePercent(60f);
						pngFile.setAlignment(Image.ALIGN_CENTER);
						document.add(flowChartContentEdited);
						document.add(pngFile);

					}
					else {
						document.add(flowChartContentEdited);
						document.add(content("NN"));

					}
									
					Paragraph prgDetailsTitle = titlesection("2.0 Program Details");
					prgDetailsTitle.setSpacingBefore(100f);
					document.add(prgDetailsTitle);
					document.add(content("Below programs are part of this job which performs given actions."));
					if(programTable.getRows().size() == 1) {
						document.add(content("Job executing only IBM utilities"));
					}else {
						document.add(programTable);	
					}
					
					document.add(titlesection("3.0 Databases"));
					Paragraph indentendIMSDBName = title("3.1 IMS Databases");
					HashMap<String, String> prgNamesAndPrgType = new HashMap<>();
					
					if (pl1Map.containsKey(mapKey)) {
						for(Pl1Details x:pl1Map.get(mapKey)){
							prgNamesAndPrgType.put(x.getProgramName().trim(), x.getProgramType());
						}
					}
				
					Paragraph imsDBContent = content("Below IMS databases are in use by respective programs. "
							+ "These programs can perform below actions on Database / Segment within the database. ");
					imsDBContent.setIndentationLeft(50f);
					indentendIMSDBName.setIndentationLeft(50f);
					
					document.add(indentendIMSDBName);
					
					HashSet<String> prgNamesForPl1 = new HashSet<>();
					for (String pl1IMSProgramName: imsMap.keySet()) {
						if(imsMap.containsKey(pl1IMSProgramName)) {
							for (ImsSection e :imsMap.get(pl1IMSProgramName)) {
								prgNamesForPl1.add(e.getPGMName().trim());
							}
							
						}
					}
					//creating IMS database for pl1
					ArrayList <ImsSection> sortedIMSData =   new ArrayList<>();
					if (imsMap.containsKey(mapKey)) {
						for(ImsSection i:imsMap.get(mapKey)) {
							sortedIMSData.add(i);
						}
					}
					HashSet<String> keysForDb2TablesPl1 = new HashSet<>();
					
					if (Pl1Db2QueryMap.containsKey(mapKey)){
						for(PL1DB2Query i :Pl1Db2QueryMap.get(mapKey)) {
							keysForDb2TablesPl1.add(i.getProgramName());
						}
					}
					
					if(imsMap.containsKey(mapKey)) {
					if(!(prgNamesForPl1.isEmpty()) || !(keysForDb2TablesPl1.isEmpty())) {
						document.add(imsDBContent);
					int index = 1;
					for(String titleImsPl1Data: prgNamesForPl1) {
						ArrayList <ImsSection> imsDataToBePrinted =   new ArrayList<>();
						for(ImsSection imsPl1: sortedIMSData) {
							if (titleImsPl1Data.equals(imsPl1.getPGMName().trim())) {
								 imsDataToBePrinted.add(imsPl1);
							}		
						}
						if (!(imsDataToBePrinted.isEmpty())) {
							Paragraph indentedSubTitle ;
							if (prgNamesAndPrgType.containsKey(titleImsPl1Data)) {
								indentedSubTitle = title("3.1."+ index+ " " +titleImsPl1Data + " - " + prgNamesAndPrgType.get(titleImsPl1Data));
								index++;
							}else {
								indentedSubTitle = title("3.1."+ index+ " " + titleImsPl1Data + " - PL1");
							 index++;
							}
							
							//document.add(title(data));
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							document.add(pgmTablesSCLM(imsDataToBePrinted, titleImsPl1Data));

						}
						
					}
					document.add(content("G - Get / Read"
							+ "\r\n"
							+ "GOT - Get When database is allocated to other process in exclusive mode"
							+ "\r\n"
							+ "I - Insert"
							+ "\r\n"
							+ "R - Replace"
							+ "\r\n"
							+ "D - Delete"
							+ "\r\n"
							+ "A - All actions"
							+ "\r\n"
							+ "P - Path Call"));
					
					Paragraph indentendPl1DB2Name = title("3.2 Db2 Databases");
					document.add(indentendPl1DB2Name);
					Paragraph indentedDB2Content = content("Below programs are using DB2 tables to extract or update the data.");
					indentedDB2Content.setIndentationLeft(50f);
					
					
					if (!(keysForDb2TablesPl1.isEmpty())) {
						if (Pl1Db2QueryMap.containsKey(mapKey)) {
							Paragraph indentedSubTitle = title("3.2.1 DB2" + " - PL1");
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(50f);
							document.add(indentedSubTitle);
							document.add(imsDBContent);
							document.add(pl1DB2QueryTables(Pl1Db2QueryMap.get(mapKey), "NA"));
						}
						
					}
					
					}else {
						document.add(content("N/A"));
					}
					}else {
						document.add(content("N/A"));
					}

	
					HashSet<String> newKeysForCopyBookDetails = new HashSet<>();
					if (pl1Map.containsKey(mapKey)) {
						for(Pl1Details pl1Dets: pl1Map.get(mapKey)) {
							if(!(pl1Dets.getCopyBooks().trim().equals(""))) {
								 newKeysForCopyBookDetails.add(pl1Dets.getProgramName());
							}
						}
					}
					
					Paragraph indentedCopyBookName = titlesection("4.0 CopyBook Details");

					
					Paragraph indentedCopyBookDetails = content("This section provides the details of copybooks.");
					indentedCopyBookDetails.setIndentationLeft(50f);
					
					document.add(indentedCopyBookName);
					
					if (newKeysForCopyBookDetails.isEmpty()) {
						document.add(content("N/A"));
					}else {
						document.add(indentedCopyBookDetails);
						ArrayList<Pl1Details> dataToBeAddedPl1 = new ArrayList<>();
						for (String data: newKeysForCopyBookDetails) {
				
						Paragraph indentedSubTitle = title(data);
//							document.add(title(data));
						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
					
						for(Pl1Details dataToBeAddedPl1CopyBook:pl1Map.get(mapKey)) {
							if (data.equals(dataToBeAddedPl1CopyBook.getProgramName())) {
								dataToBeAddedPl1.add(dataToBeAddedPl1CopyBook);
								
							}
						}
						
					}
					if (!(dataToBeAddedPl1.isEmpty())) {
						document.add(copyBookTables(dataToBeAddedPl1));
					}
		
					}
					
					Paragraph indentedInputOutput = titlesection("5.0 Input/Output Files");
					
					HashSet <String> programNamesPl1Io = new HashSet<>();
										
					if (pl1IODetsMap.containsKey(mapKey)) {
						ArrayList<Pl1IODets> pl1IODetsArray = pl1IODetsMap.get(mapKey);
						for (Pl1IODets data: pl1IODetsArray) {
							programNamesPl1Io.add(data.getProgramName());
						}
					}
					
					document.add(indentedInputOutput);

					if (programNamesPl1Io.isEmpty()) {
						document.add(content("N/A"));
					}else {
						
						Paragraph indentedSubTitle = title("5.1 Input/Output" + " - PL1");
						Font fontForSubTitle = new Font(Font.BOLD, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						document.add(indentedSubTitle);
						document.add(inputOutputFocusTables(pl1IODetsMap.get(mapKey), "NA"));
						
					}
					
					
					Paragraph indentedFocusComplexity = titlesection("6.0 Complexity");
					document.add(indentedFocusComplexity);
					Paragraph newIndentedFocusComplexity = title("6.1 Focus Complexity");
					Font someFont = new Font(BaseFont.createFont(), 14);
					newIndentedFocusComplexity.setFont(someFont);
					newIndentedFocusComplexity.setIndentationLeft(100);
					document.add(newIndentedFocusComplexity);
					if (focusComplexityMap.containsKey(mapKey)) {
						ArrayList<FocusComplexity> focusComplexityList = focusComplexityMap.get(mapKey);
						PdfPTable focTable = focusComplexityTable(focusComplexityList);
						float maxTableWidth = document.right() - document.left();
						 focTable.setTotalWidth(maxTableWidth);
						 focTable.setWidthPercentage(90);
						 document.add(focTable);
						 
						 Paragraph focusComplexityContent2 = content("Complexity has been determined based on below matrix");
							focusComplexityContent2.setIndentationLeft(60);
							document.add(focusComplexityContent2);
							document.add(focusMatrixComp());
							
						
						
					}else {
						Paragraph na = new Paragraph("N/A");
						na.setIndentationLeft(100f);
						document.add(na);
					}
					
					
					document.close();
					
	
				} catch (DocumentException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		}
		catch(IOException e){
			e.printStackTrace();
		}			
	}
	
	
	public Paragraph title(String contentTitle) throws DocumentException, IOException {
		Font fontParagraph = FontFactory.getFont(FontFactory.TIMES_BOLD);
		Font someFont = new Font(BaseFont.createFont(), 14,Font.UNDERLINE | Font.BOLD  );
		fontParagraph.setSize(14);
		
		Paragraph paragraph2 = new Paragraph(contentTitle, someFont);
		paragraph2.setIndentationLeft(10f);
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
		paragraph2.setSpacingAfter(20f);
		return paragraph2;
	}
	
	public Paragraph titlesection(String contentTitle) throws DocumentException, IOException {
		Font fontParagraph = FontFactory.getFont(FontFactory.TIMES_BOLD);
		Font someFont = new Font(BaseFont.createFont(), TITLE_SUB_SECTION_SIZE,Font.UNDERLINE | Font.BOLD  );
		fontParagraph.setSize(TITLE_SUB_SECTION_SIZE);
		
		Paragraph paragraph2 = new Paragraph(contentTitle, someFont);
		paragraph2.setIndentationLeft(10f);
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
		paragraph2.setSpacingAfter(20f);
		paragraph2.setSpacingBefore(20f);
		return paragraph2;
	}
	
	public Paragraph content(String content) {
		Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
		fontParagraph.setSize(14);
		
		Paragraph paragraph2 = new Paragraph(content + "", fontParagraph);
		paragraph2.setIndentationLeft(55);
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
		paragraph2.setSpacingAfter(20f);
		return paragraph2;
		
		
	}
	
	public PdfPTable pgmTablesSCLM(ArrayList<ImsSection> ImsData, String key){
		
		
		PdfPTable newTable = new PdfPTable(5);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Action", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Segment Used", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Segment Action", tableHeaderTitle));
		
		for (ImsSection data: ImsData) {
			
			if (data.getPGMName().isBlank()) {
				newTable.addCell("N/A");
			}
			
			else {
				newTable.addCell(data.getPGMName());
			}
			
			if (data.getDBDName().isBlank()) {
				newTable.addCell("N/A");
			}
			else {
				newTable.addCell(data.getDBDName());
			}
			
			if (data.getDBDProcopt().isBlank()) {
				newTable.addCell("N/A");
			}else {
				newTable.addCell(data.getDBDProcopt());
			}
			
			if (data.getSegement().trim() == "") {
				newTable.addCell("N/A");
			}
			else {
				newTable.addCell(data.getSegement());
			}
			if(data.getSegProcopt().trim() == "") {
				newTable.addCell("N/A");
			}
			else {
				newTable.addCell(data.getSegProcopt());
			}
		}
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}
	
	public PdfPTable pgmTablesFocus(ArrayList<FocusData> focusData, String key){

		
		PdfPTable newTable = new PdfPTable(3);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);

		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Action", tableHeaderTitle));// creates header
		

		int number_of_elements = 0;
		for (FocusData data: focusData) {

			if ((data.getProgramType().trim().equals("IMS DATABASE")&& data.getProgramName().trim().equals(key))) {
				
				newTable.addCell(data.getProgramName());
				newTable.addCell(data.getProgramTableOrFileName());
				newTable.addCell("READ");

				number_of_elements++;
			}
		}
		if(number_of_elements == 0) {
			newTable.addCell("N/A");
			newTable.addCell("N/A");
			newTable.addCell("N/A");
			newTable.addCell("N/A");
			newTable.addCell("N/A");
		}
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);

		return newTable;
		
	}
	
	
	public PdfPTable db2Tables(ArrayList<FocusData> focusData, String key){

		PdfPTable newTable = new PdfPTable(4);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);

		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Type", tableHeaderTitle));
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("DB2 Query", tableHeaderTitle));// creates header

		for (FocusData data: focusData) {
			if(data.getProgramType().trim().equals("DB2 TABLE")) {
				newTable.addCell(data.getProgramName());
				newTable.addCell(data.getProgramType2());
				newTable.addCell(data.getProgramDescription());
				newTable.addCell(" ");
			}

		}
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}
	
	public PdfPTable copyBookTables(ArrayList<Pl1Details> pl1Data){
		
		PdfPTable newTable = new PdfPTable(2);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("CopyBook Name", tableHeaderTitle));// creates header

		for (Pl1Details data: pl1Data) {
			newTable.addCell(new Paragraph(data.getProgramName()));
			newTable.addCell(new Paragraph(data.getCopyBooks()));
		}
		return newTable;
		
	}
	
	public PdfPTable inputOutputTables(ArrayList<FocusData> focusData, String key){		
		PdfPTable newTable = new PdfPTable(4);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("DD Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("File Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Usage(Input / Output)", tableHeaderTitle));// creates header
		
		for (FocusData data: focusData) {


		if(data.getProgramType().trim().equals("I/O FILE") && !(data.getFileName().trim().equals(""))) {
			newTable.addCell(data.getProgramName());
			newTable.addCell(data.getProgramTableOrFileName());
			newTable.addCell(data.getFileName());
			newTable.addCell(data.getInputOrOutput());

		}
		
	}
		
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}
	

	public PdfPTable inputOutputFocusTables(ArrayList<Pl1IODets> pl1IoData, String key){
		
		PdfPTable newTable = new PdfPTable(4);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("DD Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("File Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Usage(Input / Output)", tableHeaderTitle));// creates header
		
		for (Pl1IODets data: pl1IoData) {
			if (data.getFileName().trim().equals("")) {
				
			}
			else {
				newTable.addCell(data.getProgramName());
				newTable.addCell(data.getDDName());
				newTable.addCell(data.getFileName());
				newTable.addCell(data.getUsage());
			}
		}
		
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}
	

	public PdfPTable pl1DB2QueryTables(ArrayList<PL1DB2Query> pl1QueryData, String key){		
	
		PdfPTable newTable = new PdfPTable(4);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);		
		
		Font querySize = FontFactory.getFont(FontFactory.TIMES);
		querySize.setSize(8);
		
		newTable.addCell(new Paragraph("Job Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));
		newTable.addCell(new Paragraph("Query", tableHeaderTitle));// creates header
		
		for (PL1DB2Query data:  pl1QueryData) {
			
				newTable.addCell(data.getJobName());
				newTable.addCell(data.getProgramName());
				newTable.addCell("");
				newTable.addCell(new Paragraph(data.getDb2Query(), querySize));
			
		}
		
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}
	

	public PdfPTable focusComplexityTable(ArrayList<FocusComplexity> focusComplexityArr){		
		PdfPTable newTable = new PdfPTable(12);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(11);
		
		Font querySize = FontFactory.getFont(FontFactory.TIMES);
		querySize.setSize(11);
		
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Table Files", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Match Files", tableHeaderTitle));
		newTable.addCell(new Paragraph("Joins", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Modify file",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Read",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Write",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Alloc",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Include",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Ex",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Total Lines",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Complexity",  tableHeaderTitle));
		
		for (FocusComplexity data: focusComplexityArr) {
			newTable.addCell(new Paragraph(data.getProgram(), querySize));
			newTable.addCell(new Paragraph(data.getTableFiles(), querySize));
			newTable.addCell(new Paragraph(data.getMatchFiles(), querySize));
			newTable.addCell(new Paragraph(data.getJoins(), querySize));
			newTable.addCell(new Paragraph(data.getModifyFile(), querySize));
			newTable.addCell(new Paragraph(data.getRead(), querySize));
			newTable.addCell(new Paragraph(data.getWrite(), querySize));
			newTable.addCell(new Paragraph(data.getAlloc(), querySize));
			newTable.addCell(new Paragraph(data.getInclude(), querySize));
			newTable.addCell(new Paragraph(data.getEx(), querySize));
			newTable.addCell(new Paragraph(data.getTotalLines(), querySize));
			newTable.addCell(new Paragraph(data.getComplexity(), querySize));
		}
		float[] Widths = {150f, 80f, 100f, 80f, 100f, 80f, 80f, 80f, 100f, 80f, 80f, 150f};
		newTable.setWidths(Widths);
		
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}
	
	public PdfPTable focusMatrixComp(){

		PdfPTable newTable = new PdfPTable(8);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(10);
		
		
		
		Font querySize = FontFactory.getFont(FontFactory.TIMES);
		querySize.setSize(10);
		
		newTable.addCell(new Paragraph("Table Files", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Match Files", tableHeaderTitle));
		newTable.addCell(new Paragraph("Joins", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Modify files",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Include",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Ex",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Total Lines",  tableHeaderTitle));
		newTable.addCell(new Paragraph("Complexity",  tableHeaderTitle));
		

		newTable.addCell(new Paragraph("<3", querySize));
		newTable.addCell(new Paragraph("<2", querySize));
		newTable.addCell(new Paragraph("<2", querySize));
		newTable.addCell(new Paragraph("=0", querySize));
		newTable.addCell(new Paragraph("<2", querySize));
		newTable.addCell(new Paragraph("<2", querySize));
		newTable.addCell(new Paragraph("<10", querySize));
		newTable.addCell(new Paragraph("Simple", querySize));
		
		newTable.addCell(new Paragraph("<5", querySize));
		newTable.addCell(new Paragraph("<4", querySize));
		newTable.addCell(new Paragraph("<4", querySize));
		newTable.addCell(new Paragraph("<2", querySize));
		newTable.addCell(new Paragraph("<4", querySize));
		newTable.addCell(new Paragraph("<4", querySize));
		newTable.addCell(new Paragraph("<15", querySize));
		newTable.addCell(new Paragraph("Medium", querySize));
		
		newTable.addCell(new Paragraph(">5", querySize));
		newTable.addCell(new Paragraph(">4", querySize));
		newTable.addCell(new Paragraph(">4", querySize));
		newTable.addCell(new Paragraph(">2", querySize));
		newTable.addCell(new Paragraph(">4", querySize));
		newTable.addCell(new Paragraph(">4", querySize));
		newTable.addCell(new Paragraph(">15", querySize));
		newTable.addCell(new Paragraph("Complex", querySize));
		
		float[] tableWidth1 = {100f, 100f, 100f, 100f, 100f, 100f, 100f, 150f};
		newTable.setWidths(tableWidth1);
		
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}

	
	private static class PageNumberAndMarginHandler extends PdfPageEventHelper{
		private Image image;
		
		@Override
		public void onOpenDocument(PdfWriter writter, Document document) {
			try {
				image = Image.getInstance("C:\\Users\\1000070564\\Downloads\\hexaware.jfif");
				image.scaleToFit(50, 70);
			}catch (IOException | DocumentException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte canvas = writer.getDirectContent();
			
			float imageX = document.right() - image.getScaledWidth() - 10;
			float imageY = document.top() - 1;
			
			try {
				image.setAbsolutePosition(imageX, imageY);
				canvas.addImage(image);
			}catch(DocumentException e) {
				e.printStackTrace();
			}
			
			
			int pageNumber = writer.getPageNumber();
			float x = document.right() -40;
			float y = document.bottom() - 20;
			
			Phrase pageNumberPhrase = new Phrase("Page " + pageNumber);
			pageNumberPhrase.setFont(new Font(Font.TIMES_ROMAN, 8));
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, pageNumberPhrase, x, y, 0);
			
			float leftMargin = document.leftMargin();
			float bottomMargin = document.bottomMargin();
			float rightMargin = document.rightMargin();
			float topMargin = document.topMargin();
			
			canvas.setLineWidth(0.5f);
			canvas.rectangle(leftMargin, bottomMargin, document.getPageSize().getWidth()-leftMargin- rightMargin, document.getPageSize().getHeight() - topMargin - bottomMargin);
			canvas.stroke();
		}		

	}
}



