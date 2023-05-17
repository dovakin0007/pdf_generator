package com.example.pdfgenerator.services;

import java.io.IOException;
import java.io.BufferedReader;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import com.example.pdfgenerator.Pojo.CsvData;
import com.example.pdfgenerator.Pojo.ImsSection;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Paragraph;

import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.BaseFont;
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
		
		
	
		HashMap<String, ArrayList<CsvData>> map= new HashMap<>();
		
		HashMap<String, ArrayList<ImsSection>> imsMap = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\1000070564\\Downloads\\Report_pipe.txt")); //change path where to read
			
			imsSectionReader = new BufferedReader (new FileReader("C:\\Users\\1000070564\\Downloads\\IMS_Section.txt")); // change path where to read for IMS Db2
			
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
				String[] fields = line.split("\\|");
				CsvData randClass = new CsvData();
				randClass.setJobName(fields[0].trim());// key for the hashmap
				randClass.setJobStep(fields[1]);
				randClass.setStepDescription1(fields[2]);
				randClass.setProcName(fields[3]);
				randClass.setProcStep(fields[4]);
				randClass.setStepDescription2(fields[5]);
				randClass.setMainProgram(fields[6]);
				randClass.setModuleType(fields[7]);
				randClass.setProgramType(fields[8]);
				randClass.setSubModuleName(fields[9]);
				randClass.setSubModuleType(fields[10]);
				randClass.setParams(fields[11]);
				randClass.setDdName(fields[12]);
				randClass.setDsnName(fields[13]);
				randClass.setDisposition(fields[14]);
				randClass.setReadAndWrite(fields[15]);
				randClass.setDbType(fields[16]);
				randClass.setImsDBOperation(fields[17]);
				randClass.setDb2DBOperation(fields[18]);
				randClass.setComplexity(fields[19]);
				if (map.containsKey(fields[0].trim())) {
					ArrayList<CsvData> newValue = map.get(fields[0].trim());
					newValue.add(randClass);
					map.put(fields[0].trim(), newValue);
				}
				else {
					ArrayList<CsvData> newValue = new ArrayList<>();
				
					newValue.add(randClass);
					map.put(fields[0].trim(), newValue);				
					}
			}
			
			
			// work on job description
			
			
			
			for(String mapKey:map.keySet()) {
				ArrayList<String> appendedStrings = new ArrayList<>();
	
				for(CsvData i: map.get(mapKey)) {
					if (i.getStepDescription1() == "") {
						
					}
					else {
						String newString = i.getStepDescription1();
					
						appendedStrings.add(newString);
					}
				}
				StringBuilder uml = new StringBuilder();
				File fOS= new File("C:\\Users\\1000070564\\Downloads\\" + mapKey + ".png");
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

				
//				SourceStringReader umlReader = new SourceStringReader(uml.toString());
//				
//				FileOutputStream outputPNGFile = new FileOutputStream(fOS);
//				umlReader.outputImage(outputPNGFile);
			

//				Diagram diagram = new Diagram();
//				String rectangleMaster = "Process", decisionMaster = "Decision", connectorMaster = "Dynamic connector";
//				double width = 2, height = 2, pinX = 20, pinY= 10;
//				Shape[] processShapes = new Shape[appendedStrings.size()];
//				double sizeY = 1;
//				int numberOfShapes = 0;
//				for (String i: appendedStrings) {
//					long process = diagram.addShape(, line, numberOfShapes)
//				}
//				
//				
//				//set Layout options
//				LayoutOptions flowChartOptions = new LayoutOptions();
//				flowChartOptions.setLayoutStyle(LayoutStyle.FLOW_CHART);
//				flowChartOptions.setSpaceShapes(1f);
//				flowChartOptions.setEnlargePage(true);
//				
//				
//			
//				//set Layout direction
//				flowChartOptions.setDirection(LayoutDirection.LEFT_TO_RIGHT);
//				diagram.layout(flowChartOptions);
//				
//				diagram.save("C:\\Users\\1000070564\\Downloads\\" + mapKey +".jpeg" , SaveFileFormat.JPEG);
				Document document = new Document(PageSize.A4.rotate());
				try {
					PdfWriter.getInstance(document ,new FileOutputStream("C:\\Users\\1000070564\\Downloads\\" + mapKey + ".pdf"));//change path where to write
//					PdfWriter.getInstance(document, new FileOutputStream(mapKey));
					document.open();
					
					Font fontTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					fontTitle.setSize(18);
					
					Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
					tableHeaderTitle.setSize(14);
					
					Paragraph title = new Paragraph("Function Specification document \n Job-" + mapKey, fontTitle);
					title.setSpacingAfter(20f);
					title.setAlignment(Paragraph.ALIGN_CENTER);
					
					
					//creating table
					PdfPTable table1 = new PdfPTable(5);
					
					table1.addCell(new Paragraph("Step Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Proc Name", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Proc Step", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Program", tableHeaderTitle));// creates header
					table1.addCell(new Paragraph("Step Description", tableHeaderTitle));// creates header
					for(CsvData data: map.get(mapKey)) {//looping to the array list
						if(data.getJobStep().trim() == "") {
						
						}
						else {
						
						table1.addCell(data.getJobStep());
						table1.addCell(data.getProcName());
						table1.addCell(data.getProcStep());
						table1.addCell(data.getMainProgram());
						table1.addCell(data.getStepDescription1());
						}
						
					}
					
					PdfPTable programTable = new PdfPTable(4);
					programTable.addCell(new Paragraph("Program Name", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Type", tableHeaderTitle));
					programTable.addCell(new Paragraph("Called Modules", tableHeaderTitle));
					programTable.addCell(new Paragraph("Program Description", tableHeaderTitle));
					for (CsvData prgData: map.get(mapKey)) {
						if (prgData.getMainProgram().trim() == "") {
							
						}
						else {
							programTable.addCell(prgData.getMainProgram());
							programTable.addCell(prgData.getProgramType());
							programTable.addCell("");
							programTable.addCell("");
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
						pngFile.getAbsoluteX();
						pngFile.getAbsoluteY();
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
					HashSet<String> keysForIMSMap = new HashSet<>();
					for (CsvData data: map.get(mapKey)) {
						if (data.getMainProgram().trim()== "") {
							keysForIMSMap.add(null);
						}else {
							keysForIMSMap.add(data.getMainProgram().trim());
						}
					}
				
					if(keysForIMSMap.contains(null)) {
						document.add(content("NA"));
					}else {
					
					for (String data: keysForIMSMap) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							
					
						if (imsMap.containsKey(data)) {
							document.add(title(data));
							ArrayList<ImsSection> pgmTablesEntry = imsMap.get(data);
							document.add(pgmTables(pgmTablesEntry));
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
					}
					
					Paragraph indentendDB2DBName = title("DB2 Databases");
					indentendDB2DBName.setIndentationLeft(50f);
					
					Paragraph indentedDB2Content = content("Queries executed in programs are provided herewith.");
					indentedDB2Content.setIndentationLeft(50f);
					
					
					document.add(indentendDB2DBName);
					document.add(indentedDB2Content);
					for (String data: keysForIMSMap) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							
					
						if (imsMap.containsKey(data)) {
							document.add(title(data));
							ArrayList<ImsSection> pgmTablesEntry = imsMap.get(data);
							document.add(db2Tables(pgmTablesEntry));
							}					
					}
					
					Paragraph indentedCopyBookName = title("CopyBook Details");
					indentedCopyBookName.setIndentationLeft(50f);
					
					Paragraph indentedCopyBookDetails = content("This section provides the details of copybooks.");
					indentedCopyBookDetails.setIndentationLeft(50f);
					
					document.add(indentedCopyBookName);
					document.add(indentedCopyBookDetails);
					for (String data: keysForIMSMap) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							
					
						if (imsMap.containsKey(data)) {
							document.add(title(data));
							ArrayList<ImsSection> pgmTablesEntry = imsMap.get(data);
							document.add(copyBookTables(pgmTablesEntry));
							}					
					}
					
					Paragraph indentedInputOutput = title("Input/Output Files");
					indentedInputOutput.setIndentationLeft(50f);
					
					document.add(indentedInputOutput);
					for (String data: keysForIMSMap) {
//						System.out.println(data);
//						System.out.println(data.getMainProgram().trim() + ", " + (imsMap.get("AASA321")).get(0).getPGMName());
//						System.out.println();
							
					
						if (imsMap.containsKey(data)) {
							document.add(title(data));
							ArrayList<ImsSection> pgmTablesEntry = imsMap.get(data);
							document.add(inputOutputTables(pgmTablesEntry));
							}					
					}


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
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
		paragraph2.setSpacingAfter(20f);
		return paragraph2;
	}
	
	public Paragraph content(String content) {
		Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
		fontParagraph.setSize(14);
		
		Paragraph paragraph2 = new Paragraph(content + "", fontParagraph);
		paragraph2.setIndentationLeft(50);
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
		paragraph2.setSpacingAfter(20f);
		return paragraph2;
		
		
	}
	
	public PdfPTable pgmTables(ArrayList<ImsSection> ImsData){
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
			newTable.addCell(data.getPGMName());
			newTable.addCell(data.getDBDName());
			newTable.addCell(data.getDBDProcopt());
			newTable.addCell(data.getSegement());
			newTable.addCell(data.getSegProcopt());
		}
		
		return newTable;
		
	}
	
	public PdfPTable db2Tables(ArrayList<ImsSection> ImsData){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(3);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Database Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("DB2 Query", tableHeaderTitle));// creates header

//		for (ImsSection data: ImsData) {
////			newTable.addCell(data.getPSBMember());
//			newTable.addCell(data.getPGMName());
//			newTable.addCell(data.getDBDName());
//			newTable.addCell(data.getDBDProcopt());
//
//		}
		
		return newTable;
		
	}
	
	public PdfPTable copyBookTables(ArrayList<ImsSection> ImsData){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(2);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("CopyBook Name", tableHeaderTitle));// creates header

//		for (ImsSection data: ImsData) {
////			newTable.addCell(data.getPSBMember());
//			newTable.addCell(data.getPGMName());
//			newTable.addCell(data.getDBDName());
//		}
		
		return newTable;
		
	}
	
	public PdfPTable inputOutputTables(ArrayList<ImsSection> ImsData){
//		ArrayList<PdfPTable> tables = new ArrayList<>();
		
		
		PdfPTable newTable = new PdfPTable(3);
		
		Font tableHeaderTitle  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		tableHeaderTitle.setSize(14);
		
//		newTable.addCell(new Paragraph("PSB PDS MEMBER", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Program Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("File Name", tableHeaderTitle));// creates header
		newTable.addCell(new Paragraph("Usage(Input / Output)", tableHeaderTitle));// creates header

//		for (ImsSection data: ImsData) {
////			newTable.addCell(data.getPSBMember());
//			newTable.addCell(data.getPGMName());
//			newTable.addCell(data.getDBDName());
//			newTable.addCell(data.getDBDProcopt());
//
//		}
		
		return newTable;
		
	}

}




