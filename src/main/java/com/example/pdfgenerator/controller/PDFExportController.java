package com.example.pdfgenerator.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.pdfgenerator.services.PDFGeneratorService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PDFExportController {
	private final PDFGeneratorService pdfGeneratorService;

	
	public PDFExportController(PDFGeneratorService pdfGeneratorService) {
		this.pdfGeneratorService = pdfGeneratorService;
	}
	
	@GetMapping("/pdf/generate")
	public void generatePdf(HttpServletResponse httpServletResponse) throws Exception {
		httpServletResponse.setContentType("application/pdf");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
        Random rand = new Random();
        
        // Generate random integers in range 0 to 999
        int rand_int1 = rand.nextInt(1000);
        int rand_int2 = rand.nextInt(1000);
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=pdf_" + rand_int1 + ".pdf";
		httpServletResponse.setHeader(headerKey, headerValue);
		
		this.pdfGeneratorService.export(httpServletResponse);
	}
}
