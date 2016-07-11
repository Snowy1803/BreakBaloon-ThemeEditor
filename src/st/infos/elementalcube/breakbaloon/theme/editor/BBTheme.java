package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.snowylangapi.LangLoader;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
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
		theme.icon = ImageIO.read(new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - 8) + ".png"));
		theme.wicon = ImageIO.read(new File(file.getParentFile(), "wicon.png"));
		theme.pump = Files.readAllBytes(new File(file.getParentFile(), "pump.wav").toPath());
		theme.wpump = Files.readAllBytes(new File(file.getParentFile(), "wpump.wav").toPath());
		return theme;
	}
	
	/**
	 * Save the theme to disk
	 * 
	 * @param file the BBTHEME emplacement
	 * @throws IOException
	 */
	public void saveToDirectory(File file) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		for (Object prop : properties.keySet()) {
			if (!properties.getProperty(prop.toString()).isEmpty()) {
				pw.println(prop + "=" + properties.getProperty(prop.toString()));
			}
		}
		pw.close();
		
		int baloons = Integer.parseInt(getMetadata("baloons", null, "" + closed.length));
		for (int i = 0; i < baloons; i++) {
			ImageIO.write(closed[i], "png", new File(file.getParentFile(), "closed" + i + ".png"));
			ImageIO.write(opened[i], "png", new File(file.getParentFile(), "opened" + i + ".png"));
			if (Boolean.parseBoolean(getMetadata("different-baloon-pumped-good", null, "" + false))) {
				ImageIO.write(openedGood[i], "png", new File(file.getParentFile(), "opened" + i + "-good.png"));
			}
		}
		ImageIO.write(cursor, "gif", new File(file.getParentFile(), "cursor.gif"));
		ImageIO.write(icon, "png", new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - 8) + ".png"));
		ImageIO.write(wicon, "png", new File(file.getParentFile(), "wicon.png"));
		Files.write(new File(file.getParentFile(), "pump.wav").toPath(), pump);
		Files.write(new File(file.getParentFile(), "wpump.wav").toPath(), wpump);
	}
	
	public void saveToZip(File zip) throws IOException {
		String id = zip.getName().substring(0, zip.getName().length() - 4);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
		//BBTHEME
		ZipEntry bbtheme = new ZipEntry(id + "/" + id + ".bbtheme");
		zos.putNextEntry(bbtheme);
		
		StringBuilder sb = new StringBuilder();
		for (Object prop : properties.keySet()) {
			if (!properties.getProperty(prop.toString()).isEmpty()) {
				sb.append(prop + "=" + properties.getProperty(prop.toString()) + "\n");
			}
		}
		
		byte[] bbtdata = sb.toString().getBytes();
		zos.write(bbtdata, 0, bbtdata.length);
		zos.closeEntry();
		//OPENED
		int baloons = Integer.parseInt(getMetadata("baloons", null, "" + closed.length));
		for (int i = 0; i < baloons; i++) {
			addToZip(zos, id + "/closed" + i + ".png", closed[i]);
			addToZip(zos, id + "/opened" + i + ".png", opened[i]);
			if (Boolean.parseBoolean(getMetadata("different-baloon-pumped-good", null, "" + false))) {
				addToZip(zos, id + "/opened" + i + "-good.png", openedGood[i]);
			}
		}
		addToZip(zos, id + "/" + id + ".png", icon);
		addToZip(zos, id + "/cursor.gif", cursor, "gif");
		addToZip(zos, id + "/wicon.png", wicon);
		addToZip(zos, id + "/pump.wav", pump);
		addToZip(zos, id + "/wpump.wav", wpump);
		zos.close();
	}
	
	private void addToZip(ZipOutputStream zos, String file, BufferedImage value) throws IOException {
		addToZip(zos, file, value, "png");
	}
	
	private void addToZip(ZipOutputStream zos, String file, BufferedImage value, String method) throws IOException {
		addToZip(zos, file, imageToByteArray(value, method));
	}
	
	private void addToZip(ZipOutputStream zos, String file, byte[] data) throws IOException {
		ZipEntry entry = new ZipEntry(file);
		zos.putNextEntry(entry);
		
		zos.write(data, 0, data.length);
		zos.closeEntry();
	}
	
	private byte[] imageToByteArray(BufferedImage image, String method) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, method, baos );
		baos.flush();
		byte[] byteValue = baos.toByteArray();
		baos.close();
		return byteValue;
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
