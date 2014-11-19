package com.salumificiomeoni.ssop.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;

public class FileUtils {
	
	
	private String outputFolder;
	private String templateFolder;
	private int firstYear;
	private int lastYear;
	private int lastMonth;
	private int lastDay;
	
	private static final String[] labels = new String[] {"Cottura", "Lavorazione", "Sezionamento", "Macellazione"};
	private static final String[] internalLabels = new String[] {"pre", "operativa", "post"};
	private static Map<String, Parameter> parametersMap;
	private static boolean firstMacelloYes = false;
	private static boolean firstSezionamentoYes = false;
	
	private void setParamters(){
		parametersMap = new HashMap<String, Parameter>();
		parametersMap.put(internalLabels[0]+labels[0], new Parameter(templateFolder,"/preCottura.odt", 6));
		parametersMap.put(internalLabels[1]+labels[0], new Parameter(templateFolder, "/operativaCottura.odt", 8));
		parametersMap.put(internalLabels[2]+labels[0], new Parameter(templateFolder, "/postCottura.odt", 12));
		
		parametersMap.put(internalLabels[0]+labels[1], new Parameter(templateFolder, "/preLavorazione.odt", 6));
		parametersMap.put(internalLabels[1]+labels[1], new Parameter(templateFolder, "/operativaLavorazione.odt", 8));
		parametersMap.put(internalLabels[2]+labels[1], new Parameter(templateFolder, "/postLavorazione.odt", 12));
		
		parametersMap.put(internalLabels[0]+labels[2], new Parameter(templateFolder, "/preSezionamento.odt", 6));
		parametersMap.put(internalLabels[1]+labels[2], new Parameter(templateFolder, "/operativaSezionamento.odt", 8));
		parametersMap.put(internalLabels[2]+labels[2], new Parameter(templateFolder, "/postSezionamento.odt", 12));

		parametersMap.put(internalLabels[0]+labels[3], new Parameter(templateFolder, "/preMacello.odt", 6));
		parametersMap.put(internalLabels[1]+labels[3], new Parameter(templateFolder, "/operativaMacello.odt", 8));
		parametersMap.put(internalLabels[2]+labels[3], new Parameter(templateFolder, "/postMacello.odt", 12));
		
	}
	public FileUtils(int firstYear, String outputFolder, String templateFolder) {
		super();
		this.firstYear = firstYear;
		this.outputFolder = outputFolder;
		this.templateFolder = templateFolder;
		setLastparameters();
		setParamters();
	}

	private void changeSavingDate(String name, int year, int month, int day, int hours, int minutes) throws IOException{
		File file = new File(name);
		
		Date date = getDate(year, month, day, hours, minutes);
		file.setLastModified(date.getTime());
		
		
	}
	private void saveFolder(String name, int year, int month, int day, int hours, int minutes) throws IOException{
		File file = new File(name);
		boolean fileCreated = file.mkdir();
		
		Date date = getDate(year, month, day, hours, minutes);
		if (fileCreated){
			file.setLastModified(date.getTime());
		}
		
	}

	private Date getDate(int year, int month, int day, int hours, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hours, minutes);
		Random rand = new Random();
		calendar.set(Calendar.SECOND, rand.nextInt(60));
		return calendar.getTime();
	}
	private void setLastparameters(){
		Calendar calendar = Calendar.getInstance();
		lastYear = calendar.get(Calendar.YEAR);
		lastMonth  = calendar.get(Calendar.MONTH);
		lastMonth++;
		lastDay = calendar.get(Calendar.DAY_OF_MONTH);
		
	}
	
	public void createFolders() throws IOException, DocumentTemplateException{
		for (int i = 0; i < labels.length; i++) {		
			saveFolder(outputFolder+"/"+labels[i], firstYear, 1, 1, 6, 34);
			for (int j = 0; j < internalLabels.length; j++) {
				saveFolder(outputFolder+"/"+labels[i]+"/"+ internalLabels[j], firstYear, 1, 1, 7, 34);
				int year = firstYear;
				while(year <= lastYear){ //year
					saveFolder(outputFolder+"/"+labels[i]+"/"+ internalLabels[j] + "/" + year , firstYear, 1, 1, 7, 34);
					int stopMonth = (year == lastYear ? lastMonth : 12);
					
					for (int k = 0; k < stopMonth ; k++) { //month
						saveFolder(outputFolder+"/"+labels[i]+"/"+ internalLabels[j] + "/" + year +"/"+twoChar(k+1) , firstYear, 1, 1, 7, 34);
						switch (i) {
						case 0:
							saveCottura(outputFolder+"/"+labels[i]+"/"+ internalLabels[j] + "/" + year +"/"+twoChar(k+1), year, k+1, internalLabels[j] );
							break;
						case 1:
							saveLavorazione(outputFolder+"/"+labels[i]+"/"+ internalLabels[j] + "/" + year +"/"+twoChar(k+1), year, k+1, internalLabels[j] );
							break;
						case 2:
							saveSezionamento(outputFolder+"/"+labels[i]+"/"+ internalLabels[j] + "/" + year +"/"+twoChar(k+1), year, k+1, internalLabels[j] );
							break;
						case 3:
							saveMacellazione(outputFolder+"/"+labels[i]+"/"+ internalLabels[j] + "/" + year +"/"+twoChar(k+1), year, k+1, internalLabels[j] );
							break;

						default:
							break;
						}
					}
					
					year ++;
				}
			}
		}
		
	}
	private void saveMacellazione(String path, int year, int month, String internalLabel) throws IOException, DocumentTemplateException {
		saveEachDay(path, year, month, internalLabel+labels[3], Calendar.MONDAY);
		saveEachDay(path, year, month, internalLabel+labels[3], Calendar.TUESDAY);
		firstSezionamentoYes = saveAlternatevelyWeekEachDay(path, year, month, internalLabel+labels[3], Calendar.WEDNESDAY, firstMacelloYes);

	}
	private void saveSezionamento(String path, int year, int month, String internalLabel) throws IOException, DocumentTemplateException {
		saveEachDay(path, year, month, internalLabel+labels[2], Calendar.MONDAY);
		saveEachDay(path, year, month, internalLabel+labels[2], Calendar.TUESDAY);
		firstSezionamentoYes = saveAlternatevelyWeekEachDay(path, year, month, internalLabel+labels[2], Calendar.WEDNESDAY, firstSezionamentoYes);
	}
	private void saveLavorazione(String path, int year, int month, String internalLabel) throws IOException, DocumentTemplateException {
		saveEachDay(path, year, month, internalLabel+labels[1], Calendar.TUESDAY);
		saveEachDay(path, year, month, internalLabel+labels[1], Calendar.WEDNESDAY);
		saveEachDay(path, year, month, internalLabel+labels[1], Calendar.THURSDAY);
		saveEachDay(path, year, month, internalLabel+labels[1], Calendar.FRIDAY);

		
	}
	private void saveEachDay(String path, int year, int month,
			String internalLabel, int dayOfTheWeek)
			throws IOException, DocumentTemplateException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(year, month -1, 1);
		// get the day
		int firstDayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int amount = 0;
		if (firstDayOfTheWeek <= dayOfTheWeek){
			amount = dayOfTheWeek - firstDayOfTheWeek;
		}else{
			amount = dayOfTheWeek + 7 - firstDayOfTheWeek;
		}
		
		calendar.add(Calendar.DAY_OF_YEAR, amount);
		int indexDay = 0;
		for (int i = 1;true ; i++) { //each Tuesday
			//calendar.clear(Calendar.DAY_OF_MONTH); // so doesn't override
			//calendar.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
			try{
				calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, i);
			}catch(ArrayIndexOutOfBoundsException ex){
				break;
			}
			if (indexDay < calendar.get(Calendar.DAY_OF_MONTH)){
				String name1 = internalLabel + year + twoChar(month) + twoChar(calendar.get(Calendar.DAY_OF_MONTH));
				saveFileFromTemplate(path, name1, year, month,calendar.get(Calendar.DAY_OF_MONTH), internalLabel);
				indexDay = calendar.get(Calendar.DAY_OF_MONTH);
			}else{
				break;
			}
		}
	}

	private boolean saveAlternatevelyWeekEachDay(String path, int year, int month,
			String internalLabel, int dayOfTheWeek, boolean startBoolean)
			throws IOException, DocumentTemplateException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(year, month -1, 1);
		// get the day
		int firstDayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int amount = 0;
		if (firstDayOfTheWeek <= dayOfTheWeek){
			amount = dayOfTheWeek - firstDayOfTheWeek;
		}else{
			amount = dayOfTheWeek + 7 - firstDayOfTheWeek;
		}
		
		calendar.add(Calendar.DAY_OF_YEAR, amount);

		int indexDay = 0;
		for (int i = 1;true ; i++) { //each Tuesday
			//calendar.clear(Calendar.DAY_OF_MONTH); // so doesn't override
			//calendar.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
			try{
				calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, i);
			}catch(ArrayIndexOutOfBoundsException ex){
				break;
			}
			if (indexDay < calendar.get(Calendar.DAY_OF_MONTH)){
				if (startBoolean){
					String name1 = internalLabel + year + twoChar(month) + twoChar(calendar.get(Calendar.DAY_OF_MONTH));
					saveFileFromTemplate(path, name1, year, month,calendar.get(Calendar.DAY_OF_MONTH), internalLabel);
					startBoolean = false;
				}else{
					startBoolean = true;
				}
				indexDay = calendar.get(Calendar.DAY_OF_MONTH);
			}else{
				break;
			}
		}
		return startBoolean;
	}

	private  void saveCottura(String path, int year, int month, String internalLabel) throws IOException, DocumentTemplateException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(year, month -1, 1);
		
		// get the day
		int firstDayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int amount = 0;
		if (firstDayOfTheWeek <= Calendar.THURSDAY){
			amount = Calendar.THURSDAY - firstDayOfTheWeek;
		}else{
			amount = Calendar.THURSDAY + 7 - firstDayOfTheWeek;
		}
		
		calendar.add(Calendar.DAY_OF_YEAR, amount);

		String name1 = internalLabel+ labels[0] + year + twoChar(month) + twoChar(calendar.get(Calendar.DAY_OF_MONTH));
		saveFileFromTemplate(path, name1, year, month,calendar.get(Calendar.DAY_OF_MONTH), internalLabel+labels[0]);
		calendar.add(Calendar.DAY_OF_YEAR, 14);
		
		String name2 = internalLabel+ labels[0] + year + twoChar(month) + twoChar(calendar.get(Calendar.DAY_OF_MONTH));
		saveFileFromTemplate(path, name2, year, month,calendar.get(Calendar.DAY_OF_MONTH), internalLabel+labels[0]);
		
	}

	private void saveFileFromTemplate(String path, String name, int year, int month, int day, String label) throws IOException, DocumentTemplateException {
		
		if (MyResourceBundle.isHoliday(twoChar(month) +twoChar(day), year)){
			return;
		}
		if (year >= lastYear && month >= lastMonth && day>= lastDay){
			return;
		}
		DocumentTemplate template =  parametersMap.get(label).getTemplate();
		Map<String, String> data = new HashMap<String, String>();
		data.put("Date", twoChar(day) + "/" + twoChar(month) + "/" + year);
		Random rand= new Random();
		int minutes = rand.nextInt(60);
		data.put("hours", twoChar(parametersMap.get(label).getHour())+":"+twoChar(minutes));
		String nameFile = path + "/" + name +".odt";
		File file = new File(nameFile);
		if (!file.exists()){
			System.out.println(nameFile);
			FileOutputStream fileOutputStream = new FileOutputStream(nameFile );
			
			template.createDocument(data, fileOutputStream );
			fileOutputStream.close();
			changeSavingDate(nameFile, year, month, day, parametersMap.get(label).getHour(), minutes);
			// create empty sic file
			File fileSic = new File(nameFile+".sic");
			fileSic.createNewFile();
			changeSavingDate(nameFile+".sic", year, month, day, parametersMap.get(label).getHour(), minutes+ rand.nextInt(11));
		}

	}
	private int getMaximumDay(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		return calendar.getActualMinimum(Calendar.DAY_OF_MONTH);		
	}
	private String twoChar(int i) {
		StringBuilder strBuilder = new StringBuilder(2);
		strBuilder.append(i);
		if (strBuilder.length() == 1){
			strBuilder.insert(0, '0');
		}
		return strBuilder.toString();
	}
	
	public static void main(String[] args) {
		FileUtils fileUtils = new FileUtils(2007, "/home/massimo/testMeoni/SSOP/output", "/home/massimo/workspaceJava/SSOP/resources/template");
		try {
			fileUtils.createFolders();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentTemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
