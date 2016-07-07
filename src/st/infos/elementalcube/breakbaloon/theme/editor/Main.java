package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.snowylangapi.Lang;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
	private static final String ASSOC = ".bbtheme=BreakBaloon.Theme";
	
	public static void main(String[] args) {
		System.setProperty("apple.awt.application.name", "BreakBaloon Theme Editor");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		if (System.getProperty("os.name").contains("Windows")) {
			if (!execute("assoc .bbtheme").contains(ASSOC)) {
				System.out.println("Associating .bbtheme to BBThemeEditor...");
				if (execute("assoc " + ASSOC).contains(ASSOC)) {
					execute("ftype BreakBaloon.Theme=\"javaw -jar \\\"%ProgramFiles%\\BreakBaloon\\BBThemeEditor.jar\\\"\"");
				} else {
					System.out.println(execute("assoc .bbtheme"));
					JOptionPane.showMessageDialog(null, Lang.getString("windows.assoc.adminNeeded.text"),
							Lang.getString("windows.assoc.adminNeeded.title"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if (args.length >= 1) {
			new Editor(args[0]).setVisible(true);
		} else {
			new Editor().setVisible(true);
		}
	}
	
	private static String execute(String command) {
		StringBuffer output = new StringBuffer();
		
		try {
			ProcessBuilder pb = new ProcessBuilder(("cmd.exe /c " + command).split(" "));
			pb.redirectErrorStream(true);
			Process p = pb.start();
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return output.toString();
	}
}
