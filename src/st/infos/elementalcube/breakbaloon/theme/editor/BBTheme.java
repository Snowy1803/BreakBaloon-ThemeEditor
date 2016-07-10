package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.snowylangapi.LangLoader;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageIO;

public class BBTheme {
	private final Properties properties;
	public BufferedImage[] opened, openedGood, closed;
	public BufferedImage icon, wicon, cursor;
	public byte[] pump, wpump;
	
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
		System.out.println("Parsing bbtheme");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		Properties properties = new Properties();
		while ((line = reader.readLine()) != null) {
			properties.setProperty(line.split("=")[0], line.split("=")[1]);
		}
		reader.close();
		BBTheme theme = new BBTheme(properties);
		int baloons = Integer.parseInt(theme.getMetadata("baloons", null, "" + 3));
		theme.closed = new BufferedImage[baloons];
		theme.opened = new BufferedImage[baloons];
		theme.openedGood = new BufferedImage[baloons];
		for (int i = 0; i < baloons; i++) {
			theme.closed[i] = ImageIO.read(new File(file.getParentFile(), "closed" + i + ".png"));
			theme.opened[i] = ImageIO.read(new File(file.getParentFile(), "opened" + i + ".png"));
			if (Boolean.parseBoolean(theme.getMetadata("different-baloon-pumped-good", null, "" + false))) {
				theme.openedGood[i] = ImageIO.read(new File(file.getParentFile(), "opened" + i + "-good.png"));
			}
		}
		theme.cursor = ImageIO.read(new File(file.getParentFile(), "cursor.gif"));
		theme.icon = ImageIO.read(new File(file.getParentFile(), file.getParentFile().getName() + ".png"));
		theme.wicon = ImageIO.read(new File(file.getParentFile(), "wicon.png"));
		theme.pump = Files.readAllBytes(new File(file.getParentFile(), "pump.wav").toPath());
		theme.wpump = Files.readAllBytes(new File(file.getParentFile(), "wpump.wav").toPath());
		System.out.println("Parsed theme successfully");
		return theme;
	}
	
	public String getMetadata(String metadata, Locale locale, String defaultValue) {
		if (locale == null) {
			return getMetadataImpl(metadata.toUpperCase(), defaultValue);
		}
		return getMetadataImpl(metadata.toUpperCase() + "_" + LangLoader.checkLocale(locale.toString()), defaultValue);
	}
	
	private String getMetadataImpl(String metadata, String defaultValue) {
		String property = properties.getProperty(metadata);
		return property == null ? defaultValue : property;
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
