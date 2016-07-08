package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.snowylangapi.LangLoader;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class BBTheme {
	private final Properties properties;
	public BufferedImage[] opened, openedGood, closed;
	public BufferedImage icon, wicon, cursor;
	
	public BBTheme() {
		this.properties = new Properties();
		setDefaultValueForMetadata("name", null, "");
		setDefaultValueForMetadata("description", Locale.FRANCE, "Created with BreakBaloon Theme Editor");
		setDefaultValueForMetadata("description", Locale.US, "Créé avec BreakBaloon Theme Editor");
		setDefaultValueForMetadata("description", Locale.forLanguageTag("da-DK"), "Oprettet med BreakBaloon Theme Editor");
		setDefaultValueForMetadata("author", null, System.getProperty("user.name"));
		setDefaultValueForMetadata("version", null, "1.0");
		setDefaultValueForMetadata("baloons", null, "" + 3);
		setDefaultValueForMetadata("background", null, "" + 0xFFFFFF);
		setDefaultValueForMetadata("different-baloon-pumped-good", null, "" + false);
	}

	public BBTheme(Properties properties) {
		this.properties = properties;
	}

	public static BBTheme parseTheme(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		Properties properties = new Properties();
		while ((line = reader.readLine()) != null) {
			properties.setProperty(line.split("=")[0], line.split("=")[1]);
		}
		reader.close();
		return new BBTheme(properties);
	}
	
	public String getMetadata(String metadata, Locale locale) {
		if (locale == null) {
			return properties.getProperty(metadata.toUpperCase());
		}
		return properties.getProperty(metadata.toUpperCase() + "_" + LangLoader.checkLocale(locale.toString()));
	}
	
	public void setMetadata(String metadata, Locale locale, String value) {
		if (locale == null) {
			properties.setProperty(metadata.toUpperCase(), value);
		} else {
			properties.setProperty(metadata.toUpperCase() + "_" + LangLoader.checkLocale(locale.toString()), value);
		}
	}
	
	public void setDefaultValueForMetadata(String metadata, Locale locale, String value) {
		if (properties.getProperty(metadata.toUpperCase()) == null) {
			setMetadata(metadata, locale, value);
		}
	}
}
