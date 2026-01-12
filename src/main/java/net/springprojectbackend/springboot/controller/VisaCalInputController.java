package net.springprojectbackend.springboot.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.springprojectbackend.springboot.model.AllExpenses;
import net.springprojectbackend.springboot.model.FutureExpenses;
import net.springprojectbackend.springboot.repository.VisaCalInputFutureRepository;
import net.springprojectbackend.springboot.repository.AllExpensesRepository;

@RestController
@RequestMapping("/")
public class VisaCalInputController {

	private final AllExpensesRepository visaCallRepo;
	private final VisaCalInputFutureRepository visaCallFutureRepo;
	
	public VisaCalInputController(AllExpensesRepository visaCallRepo, VisaCalInputFutureRepository visaCallFutureRepo) {
		this.visaCallRepo = visaCallRepo;
		this.visaCallFutureRepo = visaCallFutureRepo;
	}
	
	
	
	@PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> visaCalExcel(@RequestParam("file") MultipartFile file) {
		System.out.println("Received file: " + file.getOriginalFilename());
		try(InputStream is = file.getInputStream();
				Workbook wb = WorkbookFactory.create(is)){
			Sheet sheet = wb.getSheetAt(0);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			
			for(int i = 2; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if(row == null) {
					continue;
				}
				
				LocalDate purchesDate = null;
				Cell purchesDateCell = row.getCell(0);
				CellType t = purchesDateCell.getCellType();
				if(t == CellType.FORMULA) {
					t = purchesDateCell.getCachedFormulaResultType();
				}
				if(t == CellType.NUMERIC && DateUtil.isCellDateFormatted(purchesDateCell)){
					LocalDateTime ldt = purchesDateCell.getLocalDateTimeCellValue();
					purchesDate = ldt.toLocalDate();
				}
				if (t == CellType.STRING) {
					String s = purchesDateCell.getStringCellValue();
					purchesDate = LocalDate.parse(s.trim(), dtf);
				}
				
				String bussinnesName;
				Cell bussinnesNameCell = row.getCell(1);
				bussinnesName = bussinnesNameCell.getStringCellValue().trim();
				
				BigDecimal price;
				Cell priceC = row.getCell(2);
				double priceD = priceC.getNumericCellValue();
				price = BigDecimal.valueOf(priceD);
				
				String cardName;
				Cell cardNameCell = row.getCell(3);
				cardName = cardNameCell.getStringCellValue().trim();
				
				LocalDate chargeDate = null;;
				Cell chargeDateCell = row.getCell(4);
				t = chargeDateCell.getCellType();
				if(t == CellType.FORMULA) {
					t = chargeDateCell.getCachedFormulaResultType();
				}
				if(t == CellType.NUMERIC && DateUtil.isCellDateFormatted(chargeDateCell)){
					LocalDateTime ldt = chargeDateCell.getLocalDateTimeCellValue();
					chargeDate = ldt.toLocalDate();
				}
				if (t == CellType.STRING) {
					String s = chargeDateCell.getStringCellValue();
					chargeDate = LocalDate.parse(s.trim(), dtf);
				}
				
				if(row.getCell(4) == null || row.getCell(4).getStringCellValue().isBlank()) {
					FutureExpenses entityF = new FutureExpenses(purchesDate, bussinnesName, price, cardName);
					visaCallFutureRepo.save(entityF);
					continue;
				}
				AllExpenses entity = new AllExpenses(purchesDate, bussinnesName, price, cardName, chargeDate);
				visaCallRepo.save(entity);
				
			}
			return ResponseEntity.ok("Success");
		}
		  catch (Exception e) {
	        return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
	    }
				
				
	}
	
}
