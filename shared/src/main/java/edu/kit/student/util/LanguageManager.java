package edu.kit.student.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

	private static LanguageManager instance;
	
	private ResourceBundle currentLanguage;
	private Locale currentLocal;
	
	private LanguageManager() {
		//default language
		setLanguage("en_EN");
	}
	
	public static LanguageManager getInstance() {
		if(instance == null) {
			instance = new LanguageManager();
		}
		
		return instance;
	}
	
	public void setLanguage(String lang) {
		String[] language = lang.split("_");
		currentLocal = new Locale(language[0], language[1]);
		Locale.setDefault(currentLocal);
		
		currentLanguage = ResourceBundle.getBundle("language.Lang", currentLocal);
	}
	
	public String get(String key) {
		return currentLanguage.getString(key);
	}
}
