package md.post;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Actions {
	private WebDriver driver;
	private String url = null;
	private String usrName = null;
	private String usrPwd = null;

	public void navigate(String url) {
		this.url = url;

		// define which browser to launch depending on operating system
		String os = System.getProperty("os.name").toLowerCase();
		// System.setProperty("webdriver.crome.driver",
		// "/Users/maximafanasiev/Documents/workspace/webDrivers");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		if (os.contains("windows")) {
			System.setProperty("webdriver.crome.driver",
					System.getProperty("user.dir") + "\\webDrivers\\cromedriver.exe");
		} else {
			System.setProperty("webdriver.crome.driver", System.getProperty("user.dir") + "/webDrivers/cromedriver");
		}

		driver.get(url);
		driver.manage().window().maximize();
	}

	public void login(String usrName, String usrPwd) {
		this.usrName = usrName;
		this.usrPwd = usrPwd;

		driver.switchTo().defaultContent(); // you are now outside of frames
		driver.switchTo().frame("topbar-panel");
		WebElement loginBtn = driver.findElement(By.xpath("//button[@id='user-login-btn']"));
		// WebElement loginBtn =
		// driver.findElement(By.xpath("//a[contains(text(),'вход')]"));
		assert loginBtn != null;
		loginBtn.click();

		driver.switchTo().defaultContent(); // you are now outside of frames
		driver.switchTo().frame("topbar-popup"); // navigate to login form

		WebElement loginName = driver.findElement(By.xpath("//input[@name='login']"));
		loginName.clear();
		loginName.sendKeys(usrName);

		WebElement password = driver.findElement(By.xpath("//input[@name='password']"));
		password.clear();
		password.sendKeys(usrPwd);

		WebElement enter = driver.findElement(By.xpath("//button[@type='submit']"));
		enter.click();
	}

	public void post() {
		WebElement addPost = driver.findElement(By.xpath("//a[@href = '/add']"));
		addPost.click();
	}

	public void quit() {
		driver.quit();
	}

	// read from excel file

	public HashMap<String, String> readFile() throws IOException {
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "credentials.xlsx";

		// open the file from a given location:
		FileInputStream file = new FileInputStream(new File(fileLocation));
		Workbook workbook = new XSSFWorkbook(file);

		// retrieve the first sheet of the file and iterate through each each
		Sheet sheet = workbook.getSheetAt(0);
		ArrayList<String> headers = new ArrayList<>();
		HashMap<String, String> data = new HashMap<String, String>();

		for (int r = 0, c = 0; r < sheet.getPhysicalNumberOfRows(); r++) {
			Row row = sheet.getRow(r);
			// data.put(headers.get(c), new String());

			if (r == 0) {
				for (Cell cell : row) {
					headers.add(cell.getStringCellValue());
				}
				continue;
			}
			for (Cell cell : row) {
				switch (cell.getCellTypeEnum()) {
				case STRING:
					data.put(headers.get(c), cell.getRichStringCellValue().getString());
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						data.put(headers.get(c), cell.getDateCellValue() + "");
					} else {
						data.put(headers.get(c), cell.getNumericCellValue() + "");
					}
					;
					break;
				case BOOLEAN:
					data.put(headers.get(c), cell.getBooleanCellValue() + "");
					break;
				case FORMULA:
					data.put(headers.get(c), cell.getCellFormula() + "");
					break;
				default:
					data.put(headers.get(c), " ");
				}
				c++;
			}
		}

		workbook.close();
		return data;
	}

	// create excel file
	public void createFile() throws IOException {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Credentials");
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);

		Row header = sheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 11);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue("Login");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(1);
		headerCell.setCellValue("Password");
		headerCell.setCellStyle(headerStyle);

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);

		Row row = sheet.createRow(1);
		Cell cell = row.createCell(0);
		cell.setCellValue("afonya");
		cell.setCellStyle(style);

		cell = row.createCell(1);
		cell.setCellValue("09051945");
		cell.setCellStyle(style);

		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "credentials.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();
	}
}
