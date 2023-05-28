package com.example.pdfgenerator.services;

import java.io.IOException;

import java.io.BufferedReader;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.HashMap;

import com.example.pdfgenerator.Pojo.ImsSection;
import com.example.pdfgenerator.Pojo.FocusData;
import com.example.pdfgenerator.Pojo.FocusSubData;
import com.example.pdfgenerator.Pojo.JobDetails;
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
	private static final int TITLE_SECTION_SIZE = 22;
	private static final int TITLE_SUB_SECTION_SIZE = 18;
	private static final String PATH = "C:\\Users\\1000070564\\Downloads\\";
	
	public void export(HttpServletResponse response) throws Exception{

		BufferedReader reader = null;
		
		BufferedReader imsSectionReader = null;
		
		BufferedReader focusDataReader = null;
		BufferedReader focusSubModuleReader = null;
	
		HashMap<String, ArrayList<JobDetails>> map= new HashMap<>();
		
		HashMap<String, ArrayList<ImsSection>> imsMap = new HashMap<>();
		
		HashMap<String, ArrayList<FocusSubData>> focusSubMap = new HashMap<>();
		
		HashMap<String, ArrayList<FocusData>> focusMap = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(PATH+"Job_details (1).txt")); //change path where to read
			
			imsSectionReader = new BufferedReader (new FileReader(PATH+"IMS_Section (1).txt")); // change path where to read for IMS Db2
			
			focusDataReader = new BufferedReader(new FileReader(PATH+"FOC_Det (1).txt"));
			
			focusSubModuleReader = new BufferedReader(new FileReader(PATH+"FOC_SubM (1).txt"));
			
			
			
			String imsLine;
			imsSectionReader.readLine();
			//skipping first line
			
			while((imsLine =  imsSectionReader.readLine())!= null) {
				if (imsLine.contains(String.valueOf('|'))) {

					String[] imsFields = imsLine.split("\\|"); //creating array using split func
					ImsSection imsSectionObj = new ImsSection();
					
//					System.out.println(Arrays.toString(imsFields));
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
				String[] fields = line.split("\\|");
				JobDetails jobDetails = new JobDetails();
				jobDetails.setJobName(fields[0].trim());
				jobDetails.setJobStep(fields[1].trim());
				jobDetails.setProcName(fields[2].trim());
				jobDetails.setProcStep(fields[3].trim());
				jobDetails.setProgramName(fields[4].trim());
				jobDetails.setStepDescription(fields[5].trim());
				
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
					
						appendedStrings.add(newString);
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
								keyAndValuesForFocusSubMap.put(mainModule, "NA");
							}
							
							if(d.isHasSubModule2()==true) {
								String mainModule = d.getSubModule1();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule2());
							}else if(d.isHasSubModule2()== false && d.isHasSubModule1() == true) {
								String mainModule = d.getSubModule1();
								keyAndValuesForFocusSubMap.put(mainModule, "NA");
							}
							
							if(d.isHasSubModule3()==true) {
								String mainModule = d.getSubModule2();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule3());
							}else if(d.isHasSubModule3()== false && d.isHasSubModule2() == true) {
								String mainModule = d.getSubModule2();
								keyAndValuesForFocusSubMap.put(mainModule, "NA");
							}
							
							if(d.isHasSubModule4()==true) {
								String mainModule = d.getSubModule3();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule4());
							}else if(d.isHasSubModule4()== false && d.isHasSubModule3() == true) {
								String mainModule = d.getSubModule3();
								keyAndValuesForFocusSubMap.put(mainModule, "NA");
							}
							
							if(d.isHasSubModule5()==true) {
								String mainModule = d.getSubModule4();
								keyAndValuesForFocusSubMap.put(mainModule, d.getSubModule5());
							}else if(d.isHasSubModule5()== false && d.isHasSubModule4() == true) {
								String mainModule = d.getSubModule4();
								keyAndValuesForFocusSubMap.put(mainModule, "NA");
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
//							 String prevValue = values.get(values.size()-1);
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
						uml.append(" --> " + appendedStrings.get(startOfFlow) + "\n");
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
					PdfWriter writer = PdfWriter.getInstance(document ,new FileOutputStream(PATH + mapKey + ".pdf"));//change path where to write
//					PdfWriter.getInstance(document, new FileOutputStream(mapKey));
					writer.setPageEvent(new PageNumberAndMarginHandler());
					document.open();
					
					Font fontTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					fontTitle.setSize(TITLE_FONT_SIZE);
					
					Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					tableHeaderTitle.setSize(14);
					
					PdfPCell titleMain =new PdfPCell (new Paragraph("Functional Specification document  Job - " + mapKey +"\n" , fontTitle));
//					titleMain.setSpacingAfter(20f);
//					titleMain.setSpacingBefore();
					
					titleMain.setPadding(10f);
//					titleMain.setBorder(2);

					titleMain.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PdfPTable tableHeadingBox = new PdfPTable(1);
					tableHeadingBox.setSpacingBefore(10f);
					tableHeadingBox.addCell(titleMain);
					
					//creating table
					PdfPTable table1 = new PdfPTable(5);
					
					table1.addCell(new Paragraph("Step Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Proc Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Proc Step", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Program", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Step Description", tableHeaderTitle));// creates header
					for(JobDetails data: map.get(mapKey)) {//looping to the array list
						if(data.getJobStep().trim() == "") {
						
						}
						else {
						if(data.getJobStep().trim() == "") {
							table1.addCell("NA");
						}else {
							table1.addCell(data.getJobStep());
						}
						
						if (data.getProcName().trim() == "") {
							table1.addCell("NA");
						}else {
							table1.addCell(data.getProcName());
						}
						
						if(data.getProcStep().trim() == "") {
							table1.addCell("NA");
						}
						else {
							table1.addCell(data.getProcStep());
						}
						
						if(data.getProgramName().trim() == "") {
							table1.addCell("NA");
						}
						else {
							table1.addCell(data.getProgramName());
						}
						if(data.getStepDescription().trim() == "") {
							table1.addCell("NA");
						}else {
							table1.addCell(data.getStepDescription());
							}
						}
						
					}
//					keyValueFocusSubMapList;
					PdfPTable programTable = new PdfPTable(4);
					programTable.addCell(new Paragraph("Program Name", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Type", tableHeaderTitle));
					programTable.addCell(new Paragraph("Called Modules", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Description", tableHeaderTitle));
					
//					for (JobDetails prgData: map.get(mapKey)) {
//						if (prgData.getProgramName().trim() == "") {
//							
//						}
//						else {
//							if (prgData.getProgramName().trim() == "") {
//								programTable.addCell("NA");
//							} else {
//								programTable.addCell(prgData.getProgramName());
//							}
//							if (prgData.getProgramName().trim() == "") {
//								programTable.addCell("NA");
//							}else {
//								programTable.addCell(prgData.getProgramName());
//							}
//							programTable.addCell("NA");
//							programTable.addCell("NA");
//						}
//					}
					Map<String, ArrayList<String>> sortedFocusProgramDataMap = new TreeMap<>(programStepDetails);
					for (String i: sortedFocusProgramDataMap.keySet()) {
						programTable.addCell(i);
						programTable.addCell("FOCUS");
//						programTable.addCell(moduleTypesKeyValues.get(i).toString());
//						programTable.addCell(sortedFocusProgramDataMap.get(i).toString());
						StringBuilder calledModules = new StringBuilder();
						for(String calledModule : moduleTypesKeyValues.get(i)) {
							calledModules.append(calledModule + "\n");
						}
						programTable.addCell(calledModules.toString());
						
						StringBuilder programDescriptions = new StringBuilder();
						
						for (String programDescription: sortedFocusProgramDataMap.get(i)) {
							programDescriptions.append(programDescription + "\n");
						}
						programTable.addCell(programDescriptions.toString());
						
					}
//					document.add(new Paragraph("hi"));
					document.add(new Paragraph(" "));
					document.add(tableHeadingBox);
					document.add(titlesection("Job Details: "));
					document.add(content("Below are the high level job details"));
					document.add(table1);
					
					//adding png file;
					
					Paragraph flowChartContentEdited = content("Flow diagram depicting sequence of execution");
					flowChartContentEdited.setSpacingAfter(25f);
					flowChartContentEdited.setSpacingBefore(25f);
					
					if(new File(PATH + mapKey + ".png").exists()) {
//						document.add(title("FSD Flow chart:"));
						Image pngFile = Image.getInstance(PATH + mapKey + ".png");
						pngFile.scalePercent(60f);
						pngFile.setAlignment(Image.ALIGN_CENTER);
//						pngFile.setSpacingAfter(50f);
//						pngFile.setSpacingBefore(50f);
						document.add(flowChartContentEdited);
						document.add(pngFile);
//						document.add(title("Program Details"));
//						document.add(content("Below programs are part of this job which performs given actions."));
//						document.add(programTable);
					}
					else {
						document.add(flowChartContentEdited);
						document.add(content("NN"));
//						document.add(title("Program Details"));
//						document.add(content("Below programs are part of this job which performs given actions."));
//						document.add(programTable);
					}
					
					
					Paragraph prgDetailsTitle = titlesection("Program Details");
					prgDetailsTitle.setSpacingBefore(100f);
					document.add(prgDetailsTitle);
					document.add(content("Below programs are part of this job which performs given actions."));
					document.add(programTable);	
					document.add(titlesection("Databases"));
					Paragraph indentendIMSDBName = title("IMS Databases");
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
					
//					for(String i: prgNamesForPl1) {
//						System.out.println(i);
//					}
//					
					
					//creating IMS database for pl1
					ArrayList <ImsSection> sortedIMSData =   new ArrayList<>();
					if (imsMap.containsKey(mapKey)) {
						for(ImsSection i:imsMap.get(mapKey)) {
							sortedIMSData.add(i);
						}
					}
					
					
					
					for(String titleImsPl1Data: prgNamesForPl1) {
						ArrayList <ImsSection> imsDataToBePrinted =   new ArrayList<>();
						for(ImsSection imsPl1: sortedIMSData) {
//							System.out.println(imsPl1.getPGMName());
							if (titleImsPl1Data.equals(imsPl1.getPGMName().trim())) {
								 imsDataToBePrinted.add(imsPl1);
//								 System.out.println("Ok");
							}
							
						}
						if (!(imsDataToBePrinted.isEmpty())) {
							Paragraph indentedSubTitle = title(titleImsPl1Data + " - PL/1");
//						document.add(title(data));
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							document.add(pgmTablesSCLM(imsDataToBePrinted, titleImsPl1Data));
						}
					}
						
					//creating table
//					HashSet<String> keysForIMSMapSCLM = new HashSet<>();
					HashSet<String> onlyFKProgramName = new HashSet<>();
//					ArrayList
					
					for(String keys :sortedFocusProgramDataMap.keySet()) {
						onlyFKProgramName.add(keys);
					}
					
					
					
					ArrayList<FocusData> focusDataToBeAdded = new ArrayList<>();
					HashSet<String>keysForIMSMapFocus = new HashSet<>();
					
					
					//sorting out IMS data focus
					if(focusMap.containsKey(mapKey)) {
						for(FocusData j: focusMap.get(mapKey)) {
							if(mapKey.equals(j.getJobName()) && j.getProgramType().equals("IMS DATABASE")) {
								focusDataToBeAdded.add(j);
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
					
					
//					if(keysForIMSMapSCLM.contains(null)) {
//						document.add(content("NA"));
//					}else {
//					
//					for (String data: keysForIMSMapSCLM) {
////						
//						if (imsMap.containsKey(data)) {
//							
//							Paragraph indentedSubTitle = title(data + "-SCLM");
////							document.add(title(data));
//							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
//							indentedSubTitle.setFont(fontForSubTitle);
//							indentedSubTitle.setIndentationLeft(100f);
//							document.add(indentedSubTitle);
//							ArrayList<ImsSection> pgmTablesEntry = imsMap.get(data);
//							
////							document.add(pgmTablesSCLM(pgmTablesEntry));
//							}					
//						}
//					}
//					for(String i: keysForIMSMapFocus) {
////						System.out.println(i);
//					}
					
					if (keysForIMSMapFocus.contains(null)) {
						document.add(content("NA"));
					}
				
					else {
					for (String data: keysForIMSMapFocus) {

						Paragraph indentedSubTitle = title(data + "-FOCUS");
//							document.add(title(data));
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
				
					
					
					Paragraph indentendDB2DBName = title("DB2 Databases");
					indentendDB2DBName.setIndentationLeft(50f);
					
					Paragraph indentedDB2Content = content("Queries executed in programs are provided herewith.");
					indentedDB2Content.setIndentationLeft(50f);
					
					
					
					document.add(indentendDB2DBName);
					document.add(indentedDB2Content);
					for (String data: keysForDb2Tables) {					
//							document.add(title(data));
							
						Paragraph indentedSubTitle = title(data);
//							document.add(title(data));
						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						document.add(indentedSubTitle);	
//						ArrayList<FocusData> pgmTablesEntry = focusDataToBeAddedDb2;
						document.add(db2Tables(focusDataToBeAddedDb2, data));
					
					}
					
					Paragraph indentedCopyBookName = titlesection("CopyBook Details");
					//indentedCopyBookName.setIndentationLeft(50f);
					
					Paragraph indentedCopyBookDetails = content("This section provides the details of copybooks.");
					indentedCopyBookDetails.setIndentationLeft(50f);
					
					document.add(indentedCopyBookName);
					document.add(indentedCopyBookDetails);
					for (String data: keysForCopyBookTables) {
				
						Paragraph indentedSubTitle = title(data);
//							document.add(title(data));
						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						//document.add(indentedSubTitle); need copy book details in focus file
							
						//document.add(copyBookTables(focusDataToBeAddedCopyBook, data));
												
					}
					
					Paragraph indentedInputOutput = titlesection("Input/Output Files");
					//indentedInputOutput.setIndentationLeft(50f);
					
					document.add(indentedInputOutput);
					for (String data: keysForIOTables) {	
//							document.add(title(data));
						Paragraph indentedSubTitle = title(data);
//							document.add(title(data));
						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						document.add(indentedSubTitle);
						
						document.add(inputOutputTables(focusDataToBeAddedIO , data));
						}					
					
					
//					for(int i = 0;i< document.getPageNumber(); i++) {
////						PdfPage page
//					}
					document.close();
					
	
				} catch (DocumentException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}
				else if(!(focusSubMap.containsKey(mapKey)) && imsMap.containsKey(mapKey)) {
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
						uml.append(" --> " + appendedStrings.get(startOfFlow) + "\n");
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
					PdfWriter writer = PdfWriter.getInstance(document ,new FileOutputStream(PATH + mapKey + ".pdf"));//change path where to write
//					PdfWriter.getInstance(document, new FileOutputStream(mapKey));
					writer.setPageEvent(new PageNumberAndMarginHandler());
					document.open();
					
					Font fontTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					fontTitle.setSize(TITLE_FONT_SIZE);
					
					Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					tableHeaderTitle.setSize(14);
					
					PdfPCell titleMain =new PdfPCell (new Paragraph("Functional Specification document  Job - " + mapKey +"\n" , fontTitle));
//					titleMain.setSpacingAfter(20f);
//					titleMain.setSpacingBefore();
					
					titleMain.setPadding(10f);
//					titleMain.setBorder(2);

					titleMain.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					
					PdfPTable tableHeadingBox = new PdfPTable(1);
					tableHeadingBox.setSpacingBefore(10f);
					tableHeadingBox.addCell(titleMain);
					
					//creating table
					PdfPTable table1 = new PdfPTable(5);
					
					table1.addCell(new Paragraph("Step Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Proc Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Proc Step", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Program", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Step Description", tableHeaderTitle));// creates header
					for(JobDetails data: map.get(mapKey)) {//looping to the array list
						if(data.getJobStep().trim() == "") {
						
						}
						else {
						if(data.getJobStep().trim() == "") {
							table1.addCell("NA");
						}else {
							table1.addCell(data.getJobStep());
						}
						
						if (data.getProcName().trim() == "") {
							table1.addCell("NA");
						}else {
							table1.addCell(data.getProcName());
						}
						
						if(data.getProcStep().trim() == "") {
							table1.addCell("NA");
						}
						else {
							table1.addCell(data.getProcStep());
						}
						
						if(data.getProgramName().trim() == "") {
							table1.addCell("NA");
						}
						else {
							table1.addCell(data.getProgramName());
						}
						if(data.getStepDescription().trim() == "") {
							table1.addCell("NA");
						}else {
							table1.addCell(data.getStepDescription());
							}
						}
						
					}
//					keyValueFocusSubMapList;
					PdfPTable programTable = new PdfPTable(4);
					programTable.addCell(new Paragraph("Program Name", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Type", tableHeaderTitle));
					programTable.addCell(new Paragraph("Called Modules", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Description", tableHeaderTitle));
					
//					for (JobDetails prgData: map.get(mapKey)) {
//						if (prgData.getProgramName().trim() == "") {
//							
//						}
//						else {
//							if (prgData.getProgramName().trim() == "") {
//								programTable.addCell("NA");
//							} else {
//								programTable.addCell(prgData.getProgramName());
//							}
//							if (prgData.getProgramName().trim() == "") {
//								programTable.addCell("NA");
//							}else {
//								programTable.addCell(prgData.getProgramName());
//							}
//							programTable.addCell("NA");
//							programTable.addCell("NA");
//						}
//					}
//					
//					document.add(new Paragraph("hi"));
					document.add(new Paragraph(" "));
					document.add(tableHeadingBox);
					document.add(titlesection("Job Details: "));
					document.add(content("Below are the high level job details"));
					document.add(table1);
					
					//adding png file;
					
					Paragraph flowChartContentEdited = content("Flow diagram depicting sequence of execution");
					flowChartContentEdited.setSpacingAfter(25f);
					flowChartContentEdited.setSpacingBefore(25f);
					
					if(new File(PATH + mapKey + ".png").exists()) {
//						document.add(title("FSD Flow chart:"));
						Image pngFile = Image.getInstance(PATH + mapKey + ".png");
						pngFile.scalePercent(60f);
						pngFile.setAlignment(Image.ALIGN_CENTER);
//						pngFile.setSpacingAfter(50f);
//						pngFile.setSpacingBefore(50f);
						document.add(flowChartContentEdited);
						document.add(pngFile);
//						document.add(title("Program Details"));
//						document.add(content("Below programs are part of this job which performs given actions."));
//						document.add(programTable);
					}
					else {
						document.add(flowChartContentEdited);
						document.add(content("NN"));
//						document.add(title("Program Details"));
//						document.add(content("Below programs are part of this job which performs given actions."));
//						document.add(programTable);
					}
					
					
					Paragraph prgDetailsTitle = titlesection("Program Details");
					prgDetailsTitle.setSpacingBefore(100f);
					document.add(prgDetailsTitle);
					document.add(content("Below programs are part of this job which performs given actions."));
					document.add(programTable);	
					document.add(titlesection("Databases"));
					Paragraph indentendIMSDBName = title("IMS Databases");
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
					
//					for(String i: prgNamesForPl1) {
//						System.out.println(i);
//					}
//					
					
					//creating IMS database for pl1
					ArrayList <ImsSection> sortedIMSData =   new ArrayList<>();
					if (imsMap.containsKey(mapKey)) {
						for(ImsSection i:imsMap.get(mapKey)) {
							sortedIMSData.add(i);
						}
					}
					
					
					
					for(String titleImsPl1Data: prgNamesForPl1) {
						ArrayList <ImsSection> imsDataToBePrinted =   new ArrayList<>();
						for(ImsSection imsPl1: sortedIMSData) {
//							System.out.println(imsPl1.getPGMName());
							if (titleImsPl1Data.equals(imsPl1.getPGMName().trim())) {
								 imsDataToBePrinted.add(imsPl1);
//								 System.out.println("Ok");
							}
							
						}
						if (!(imsDataToBePrinted.isEmpty())) {
							Paragraph indentedSubTitle = title(titleImsPl1Data + " - SCLM");
//						document.add(title(data));
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
					//creating table
//					HashSet<String> keysForIMSMapSCLM = new HashSet<>();
					
					
					
					
					
//					if(keysForIMSMapSCLM.contains(null)) {
//						document.add(content("NA"));
//					}else {
//					
//					for (String data: keysForIMSMapSCLM) {
////						
//						if (imsMap.containsKey(data)) {
//							
//							Paragraph indentedSubTitle = title(data + "-SCLM");
////							document.add(title(data));
//							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
//							indentedSubTitle.setFont(fontForSubTitle);
//							indentedSubTitle.setIndentationLeft(100f);
//							document.add(indentedSubTitle);
//							ArrayList<ImsSection> pgmTablesEntry = imsMap.get(data);
//							
////							document.add(pgmTablesSCLM(pgmTablesEntry));
//							}					
//						}
//					}
//					for(String i: keysForIMSMapFocus) {
////						System.out.println(i);
//					}
					

//					}
				
					
					
					Paragraph indentendDB2DBName = title("DB2 Databases");
					indentendDB2DBName.setIndentationLeft(50f);
					
					Paragraph indentedDB2Content = content("Queries executed in programs are provided herewith.");
					indentedDB2Content.setIndentationLeft(50f);
					
					
					
					document.add(indentendDB2DBName);
					document.add(indentedDB2Content);
//					for (String data: keysForDb2Tables) {					
////							document.add(title(data));
//							
//						Paragraph indentedSubTitle = title(data);
////							document.add(title(data));
//						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
//						indentedSubTitle.setFont(fontForSubTitle);
//						indentedSubTitle.setIndentationLeft(100f);
//						document.add(indentedSubTitle);	
////						ArrayList<FocusData> pgmTablesEntry = focusDataToBeAddedDb2;
//						document.add(db2Tables(focusDataToBeAddedDb2, data));
//					
//					}
					
					Paragraph indentedCopyBookName = titlesection("CopyBook Details");
					//indentedCopyBookName.setIndentationLeft(50f);
					
					Paragraph indentedCopyBookDetails = content("This section provides the details of copybooks.");
					indentedCopyBookDetails.setIndentationLeft(50f);
					
					document.add(indentedCopyBookName);
					document.add(indentedCopyBookDetails);
//					for (String data: keysForCopyBookTables) {
//				
//						Paragraph indentedSubTitle = title(data);
////							document.add(title(data));
//						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
//						indentedSubTitle.setFont(fontForSubTitle);
//						indentedSubTitle.setIndentationLeft(100f);
//						//document.add(indentedSubTitle); need copy book details in focus file
//							
//						//document.add(copyBookTables(focusDataToBeAddedCopyBook, data));
//												
//					}
					
					Paragraph indentedInputOutput = titlesection("Input/Output Files");
					//indentedInputOutput.setIndentationLeft(50f);
					
					document.add(indentedInputOutput);
//					for (String data: keysForIOTables) {	
////							document.add(title(data));
//						Paragraph indentedSubTitle = title(data);
////							document.add(title(data));
//						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
//						indentedSubTitle.setFont(fontForSubTitle);
//						indentedSubTitle.setIndentationLeft(100f);
//						document.add(indentedSubTitle);
//						
//						document.add(inputOutputTables(focusDataToBeAddedIO , data));
//						}					
//					
					
//					for(int i = 0;i< document.getPageNumber(); i++) {
////						PdfPage page
//					}
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
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(5);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Action", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Segment Used", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Segment Action", tableHeaderTitle));
		
		for (ImsSection data: ImsData) {
//			newTable.addCell(data.getPSBMember());
			
			if (data.getPGMName().isBlank()) {
				newTable.addCell("NA");
			}
			
			else {
				newTable.addCell(data.getPGMName());
			}
			
			if (data.getDBDName().isBlank()) {
				newTable.addCell("NA");
			}
			else {
				newTable.addCell(data.getDBDName());
			}
			
			if (data.getDBDProcopt().isBlank()) {
				newTable.addCell("NA");
			}else {
				newTable.addCell(data.getDBDProcopt());
			}
			
			if (data.getSegement().trim() == "") {
				newTable.addCell("NA");
			}
			else {
				newTable.addCell(data.getSegement());
			}
			if(data.getSegProcopt().trim() == "") {
				newTable.addCell("NA");
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
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(5);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Action", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Segment Used", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Segment Action", tableHeaderTitle));	
		

		int number_of_elements = 0;
		for (FocusData data: focusData) {
//			newTable.addCell(data.getPSBMember());
//			System.out.print("OK\n");
			if ((data.getProgramType().trim().equals("IMS DATABASE")&& data.getProgramName().trim().equals(key))) {
				
				newTable.addCell(data.getProgramName());
				newTable.addCell(data.getProgramTableOrFileName());
				newTable.addCell(data.getProgramDescription());
				newTable.addCell("NA");
				newTable.addCell("NA");
				number_of_elements++;
			}
		}
		if(number_of_elements == 0) {
			newTable.addCell("NA");
			newTable.addCell("NA");
			newTable.addCell("NA");
			newTable.addCell("NA");
			newTable.addCell("NA");
		}
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);

		return newTable;
		
	}
	
	
	public PdfPTable db2Tables(ArrayList<FocusData> focusData, String key){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		PdfPTable newTable = new PdfPTable(3);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("DB2 Query", tableHeaderTitle));// creates header

		for (FocusData data: focusData) {
//////			newTable.addCell(data.getPSBMember());
//			newTable.addCell(data.getPGMName());
//			newTable.addCell(data.getDBDName());
//			newTable.addCell(data.getDBDProcopt());
			if(data.getProgramType().trim().equals("DB2 TABLE") && data.getProgramName().trim().equals(key)) {
				newTable.addCell(data.getProgramName());
				newTable.addCell(data.getProgramDescription());
				newTable.addCell("NA");
			}

		}
		newTable.setSpacingAfter(20);
		newTable.setSpacingBefore(20);
		return newTable;
		
	}
	
	public PdfPTable copyBookTables(ArrayList<FocusData> focusData, String key){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(2);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("CopyBook Name", tableHeaderTitle));// creates header

		for (FocusData data: focusData) {
//////		newTable.addCell(data.getPSBMember());
//		newTable.addCell(data.getPGMName());
//		newTable.addCell(data.getDBDName());
//		newTable.addCell(data.getDBDProcopt());
//		if(data.getProgramType().trim().equals("I/O FILE") && data.getProgramName().trim().equals(key)) {
//			newTable.addCell(data.getProgramName());
//			newTable.addCell(data.getProgramTableOrFileName());
//		}else if(!(data.getProgramType().trim().equals("I/O FILE"))) {
//			
//		}

	}
		return newTable;
		
	}
	
	public PdfPTable inputOutputTables(ArrayList<FocusData> focusData, String key){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(4);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("DD Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("File Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Usage(Input / Output)", tableHeaderTitle));// creates header
		
		for (FocusData data: focusData) {


		if(data.getProgramType().trim().equals("I/O FILE") && data.getProgramName().trim().equals(key)) {
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



