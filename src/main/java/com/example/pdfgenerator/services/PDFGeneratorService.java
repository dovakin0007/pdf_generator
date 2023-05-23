package com.example.pdfgenerator.services;

import java.io.IOException;
import java.io.BufferedReader;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import com.example.pdfgenerator.Pojo.CsvData;
import com.example.pdfgenerator.Pojo.ImsSection;
import com.example.pdfgenerator.Pojo.FocusData;
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

import java.io.File;


import net.sourceforge.plantuml.SourceStringReader;
//import com.aspose.diagram.Diagram;
//import com.aspose.diagram.LayoutDirection;
//import com.aspose.diagram.Page;
//import com.aspose.diagram.SaveFileFormat;
//import com.aspose.diagram.Shape;
//import com.aspose.diagram.LayoutOptions;
//import com.aspose.diagram.LayoutStyle;


@Service
public class PDFGeneratorService {
	
	
	public void export(HttpServletResponse response) throws Exception{

		BufferedReader reader = null;
		
		BufferedReader imsSectionReader = null;
		
		BufferedReader focusDataReader = null;
	
		HashMap<String, ArrayList<JobDetails>> map= new HashMap<>();
		
		HashMap<String, ArrayList<ImsSection>> imsMap = new HashMap<>();
		
		HashMap<String, ArrayList<FocusData>> focusMap = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\1000070564\\Downloads\\Job_detail_0523.txt")); //change path where to read
			
			imsSectionReader = new BufferedReader (new FileReader("C:\\Users\\1000070564\\Downloads\\IMS_Section.txt")); // change path where to read for IMS Db2
			
			focusDataReader = new BufferedReader(new FileReader("C:\\Users\\1000070564\\Downloads\\FOC_Det.txt"));
			
			String imsLine;
			imsSectionReader.readLine();
			//skipping first line
			
			while((imsLine =  imsSectionReader.readLine())!= null) {
				if (imsLine.contains(String.valueOf('|'))) {
//					System.out.println(imsLine);
					String[] imsFields = imsLine.split("\\|"); //creating array using split func
					ImsSection imsSectionObj = new ImsSection();
					
//					System.out.println(Arrays.toString(imsFields));
					imsSectionObj.setPSBMember(imsFields[0]);
					imsSectionObj.setPGMName(imsFields[1].trim());// key of the Hashmap
					imsSectionObj.setDBDName(imsFields[2]);
					imsSectionObj.setDBDProcopt(imsFields[3]);
					imsSectionObj.setSegement(imsFields[4]);
					imsSectionObj.setSegProcopt(imsFields[5]);
					
					if (imsMap.containsKey(imsFields[1].trim())) {
						ArrayList<ImsSection> imsMapArrayList = imsMap.get(imsFields[1].trim());
						imsMapArrayList.add(imsSectionObj);
						imsMap.put(imsFields[1].trim(), imsMapArrayList);
					}
					else {
						ArrayList<ImsSection> imsMapArrayList = new ArrayList<>();
						imsMapArrayList.add(imsSectionObj);
 						imsMap.put(imsFields[1].trim(), imsMapArrayList);
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
				StringBuilder uml = new StringBuilder();
				File fOS= new File("C:\\Users\\1000070564\\Downloads\\" + mapKey + ".png");
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
//					
//					System.out.println(uml.toString());
					SourceStringReader umlReader = new SourceStringReader(uml.toString());
					
					FileOutputStream outputPNGFile = new FileOutputStream(fOS);
					umlReader.outputImage(outputPNGFile);
				}

				Document document = new Document(PageSize.A4);
				try {
					PdfWriter writer = PdfWriter.getInstance(document ,new FileOutputStream("C:\\Users\\1000070564\\Downloads\\" + mapKey + ".pdf"));//change path where to write
//					PdfWriter.getInstance(document, new FileOutputStream(mapKey));
					writer.setPageEvent(new PageNumberAndMarginHandler());
					document.open();
					
					Font fontTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					fontTitle.setSize(18);
					
					Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					tableHeaderTitle.setSize(14);
					
					Paragraph title = new Paragraph("Functional Specification document \n Job-" + mapKey, fontTitle);
					title.setSpacingAfter(20f);
					title.setAlignment(Paragraph.ALIGN_CENTER);
					
					
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
					
					PdfPTable programTable = new PdfPTable(4);
					programTable.addCell(new Paragraph("Program Name", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Type", tableHeaderTitle));
					programTable.addCell(new Paragraph("Called Modules", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Description", tableHeaderTitle));
					for (JobDetails prgData: map.get(mapKey)) {
						if (prgData.getProgramName().trim() == "") {
							
						}
						else {
							if (prgData.getProgramName().trim() == "") {
								programTable.addCell("NA");
							} else {
								programTable.addCell(prgData.getProgramName());
							}
							if (prgData.getProgramName().trim() == "") {
								programTable.addCell("NA");
							}else {
								programTable.addCell(prgData.getProgramName());
							}
							programTable.addCell("NA");
							programTable.addCell("NA");
						}
					}
					
									
					document.add(title);
					document.add(title("Job Details: "));
					document.add(content("Below are the high level job details"));
					document.add(table1);
					//adding png file;
					
					Paragraph flowChartContentEdited = content("Flow diagram depicting sequence of execution");
					flowChartContentEdited.setSpacingAfter(25f);
					flowChartContentEdited.setSpacingBefore(25f);
					
					if(new File("C:\\Users\\1000070564\\Downloads\\" + mapKey + ".png").exists()) {
//						document.add(title("FSD Flow chart:"));
						Image pngFile = Image.getInstance("C:\\Users\\1000070564\\Downloads\\" + mapKey + ".png");
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
					
					
					Paragraph prgDetailsTitle = title("Program Details");
					prgDetailsTitle.setSpacingBefore(30f);
					document.add(prgDetailsTitle);
					document.add(content("Below programs are part of this job which performs given actions."));
					document.add(programTable);	
					document.add(title("Databases"));
					Paragraph indentendIMSDBName = title("IMS Databases");
					Paragraph imsDBContent = content("Below IMS databases are in use by respective programs. "
							+ "These programs can perform below actions on Database / Segment within the database. ");
					imsDBContent.setIndentationLeft(50f);
					indentendIMSDBName.setIndentationLeft(50f);
					
					document.add(indentendIMSDBName);
					document.add(imsDBContent);
					
					//creating table
					HashSet<String> keysForIMSMapSCLM = new HashSet<>();
					HashSet<String> onlyFKProgramName = new HashSet<>();
//					ArrayList
					
					ArrayList<FocusData> focusDataToBeAdded = new ArrayList<>();
					HashSet<String>keysForIMSMapFocus = new HashSet<>();
					
					if(focusMap.containsKey(mapKey)) {
						for(FocusData j: focusMap.get(mapKey)) {
							if(mapKey.equals(j.getJobName())) {
								focusDataToBeAdded.add(j);
							}
						}
					
						for (FocusData i: focusDataToBeAdded) {
							keysForIMSMapFocus.add(i.getProgramName().trim());
						}
					}
					
					if(keysForIMSMapSCLM.contains(null)) {
						document.add(content("NA"));
					}else {
					
					for (String data: keysForIMSMapSCLM) {
//						
						if (imsMap.containsKey(data)) {
							
							Paragraph indentedSubTitle = title(data + "-SCLM");
//							document.add(title(data));
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							ArrayList<ImsSection> pgmTablesEntry = imsMap.get(data);
							
							document.add(pgmTablesSCLM(pgmTablesEntry));
							}					
						}
					}
					for(String i: keysForIMSMapFocus) {
						System.out.println(i);
					}
					
					if (keysForIMSMapSCLM.contains(null)) {
						document.add(content("NA"));
					}
				
					else {
					for (String data: keysForIMSMapFocus) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							

					
							
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
					for (String data: onlyFKProgramName) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							
					
//							document.add(title(data));
							
						Paragraph indentedSubTitle = title(data);
//							document.add(title(data));
						Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
						indentedSubTitle.setFont(fontForSubTitle);
						indentedSubTitle.setIndentationLeft(100f);
						document.add(indentedSubTitle);	
						ArrayList<FocusData> pgmTablesEntry = focusMap.get(data);
						document.add(db2Tables(pgmTablesEntry));
					
					}
					
					Paragraph indentedCopyBookName = title("CopyBook Details");
					indentedCopyBookName.setIndentationLeft(50f);
					
					Paragraph indentedCopyBookDetails = content("This section provides the details of copybooks.");
					indentedCopyBookDetails.setIndentationLeft(50f);
					
					document.add(indentedCopyBookName);
					document.add(indentedCopyBookDetails);
					for (String data: onlyFKProgramName) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							
					
						if (focusMap.containsKey(data)) {
							Paragraph indentedSubTitle = title(data);
//							document.add(title(data));
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							ArrayList<FocusData> pgmTablesEntry = focusMap.get(data);
							document.add(copyBookTables(pgmTablesEntry));
							}					
					}
					
					Paragraph indentedInputOutput = title("Input/Output Files");
					indentedInputOutput.setIndentationLeft(50f);
					
					document.add(indentedInputOutput);
					for (String data: onlyFKProgramName) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							
					
						if (focusMap.containsKey(data)) {
//							document.add(title(data));
							Paragraph indentedSubTitle = title(data);
//							document.add(title(data));
							Font fontForSubTitle = new Font(Font.TIMES_ROMAN, 14);
							indentedSubTitle.setFont(fontForSubTitle);
							indentedSubTitle.setIndentationLeft(100f);
							document.add(indentedSubTitle);
							ArrayList<FocusData> pgmTablesEntry = focusMap.get(data);
							document.add(inputOutputTables(pgmTablesEntry));
							}					
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
	
	public Paragraph content(String content) {
		Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
		fontParagraph.setSize(14);
		
		Paragraph paragraph2 = new Paragraph(content + "", fontParagraph);
		paragraph2.setIndentationLeft(55);
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
		paragraph2.setSpacingAfter(20f);
		return paragraph2;
		
		
	}
	
	public PdfPTable pgmTablesSCLM(ArrayList<ImsSection> ImsData){
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
//				System.out.println(data.getSegProcopt().trim() == "");
			}
		}
		
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
		
		
		for (FocusData data: focusData) {
//			newTable.addCell(data.getPSBMember());
//			System.out.print("OK\n");
			if ((data.getProgramType().trim().equals("IMS DATABASE")&& data.getProgramName().trim().equals(key))) {
				
				newTable.addCell(data.getProgramName());
				newTable.addCell(data.getProgramTableOrFileName());
				newTable.addCell(data.getProgramDescription());
				newTable.addCell("NA");
				newTable.addCell("NA");
			}else if((!(data.getProgramType().trim().equals("IMS DATABASE"))&& data.getProgramName().trim().equals(key))) {
				
			}
		}
		
		return newTable;
		
	}
	
	
	public PdfPTable db2Tables(ArrayList<FocusData> focusData){
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
			if(data.getProgramType().trim().equals("DB2 TABLE")) {
				newTable.addCell(data.getProgramName());
				newTable.addCell(data.getProgramTableOrFileName());
				newTable.addCell("NA");

			}

		}
		
		return newTable;
		
	}
	
	public PdfPTable copyBookTables(ArrayList<FocusData> focusData){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(2);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("CopyBook Name", tableHeaderTitle));// creates header

		HashSet<String> newHashSet = new HashSet<>();
		for (FocusData data : focusData) {
			if(data.getProgramType().trim().equals("I/O FILE")) {
				//newHash
			}
		}
		for (FocusData data: focusData) {
//////		newTable.addCell(data.getPSBMember());
//		newTable.addCell(data.getPGMName());
//		newTable.addCell(data.getDBDName());
//		newTable.addCell(data.getDBDProcopt());
		if(data.getProgramType().trim().equals("I/O FILE")) {
			newTable.addCell(data.getProgramName());
			newTable.addCell(data.getProgramTableOrFileName());
		}else if(!(data.getProgramType().trim().equals("I/O FILE"))) {
			
		}

	}
		return newTable;
		
	}
	
	public PdfPTable inputOutputTables(ArrayList<FocusData> focusData){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(3);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("File Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Usage(Input / Output)", tableHeaderTitle));// creates header

		for (FocusData data: focusData) {
//////		newTable.addCell(data.getPSBMember());
//		newTable.addCell(data.getPGMName());
//		newTable.addCell(data.getDBDName());
//		newTable.addCell(data.getDBDProcopt());
		if(data.getProgramType().trim().equals("I/O FILE")) {
			newTable.addCell(data.getProgramName());
			newTable.addCell(data.getProgramTableOrFileName());
			newTable.addCell(data.getInputOrOutput());

		}
	}
		
		return newTable;
		
	}
	
	private static class PageNumberAndMarginHandler extends PdfPageEventHelper{
		
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte canvas = writer.getDirectContent();
			
			int pageNumber = writer.getPageNumber();
			float x = document.right() -50;
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



