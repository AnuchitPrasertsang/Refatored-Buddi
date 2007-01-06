/*
 * Created on May 12, 2006 by wyatt
 */
package org.homeunix.drummer;

import java.awt.Color;

import javax.swing.JList;


/**
 * A container for constants, used throughout the rest of the program.
 * 
 * @author wyatt
 *
 */
public class Const {
	public static final String STABLE = "STABLE";
	public static final String UNSTABLE = "UNSTABLE";
	
	//Version variables
	public static final String VERSION = "2.1.6";
	public static final String BRANCH = UNSTABLE;
	public static final boolean DEVEL = true;
	
	//Language constants
	public final static String LANGUAGE_EXTENSION = ".lang";
	public final static String LANGUAGE_FOLDER = "Languages";
	
	//Data file constants
	public final static String DATA_FILE_EXTENSION = ".buddi";
	public final static String DATA_DEFAULT_FILENAME = "Data";
	
	//Web addresses
	public final static String DONATE_URL = "http://sourceforge.net/donate/index.php?group_id=167026";
	public final static String PROJECT_URL = "http://buddi.sourceforge.net/";
	public final static String DOWNLOAD_URL_STABLE = "http://buddi.sourceforge.net/Buddi";
	public final static String DOWNLOAD_URL_UNSTABLE = "http://buddi.sourceforge.net/Buddi-development";
	public final static String DOWNLOAD_URL_DMG = ".dmg";
	public final static String DOWNLOAD_URL_ZIP = ".zip";
	public final static String DOWNLOAD_URL_TGZ = ".tgz";
	public final static String VERSION_FILE = "version.txt";
	
	//Colors
	public final static Color COLOR_SELECTED = new JList().getSelectionBackground();//new Color(181, 213, 255);
	public final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 255);
	public final static Color COLOR_EVEN_ROW = new Color(237, 243, 254);
	public final static Color COLOR_ODD_ROW = Color.WHITE;

	
	//Languages which are included in the .jar file.  Needed since there
	// is no good method of reading the main jar file by itself (since
	// it may be wrapped in a .exe, etc).
	public final static String[] BUNDLED_LANGUAGES = {
		"Deutsch",
		"English_(US)",
		"English",
		"Espanol",
		"Francais",
		"Nederlands",
		"Nederlands_(BE)",
		"Norsk",
		"Portugues_(BR)",
		"Russian_(RU)"
	};
	
	public final static String[] BUNDLED_DOCUMENTS = {
		"Changelog.txt",
		"License.txt",
		"Readme.txt"
	};
	
	public final static String[] BUNDLED_LICENSES = {
		"Artistic License.txt",
		"GNU General Public License.txt",
		"GNU Lesser General Public License.txt",
		"Modified BSD License.txt"
	};
	
	//Date formats to appear in Preferences.
	public final static String[] DATE_FORMATS = {
		"yyyy-MM-dd",
		"yyyy-MMM-dd",
		"MMMM dd, yyyy",
		"MMM dd yyyy",
		"yyyy/MMM/d",
		"yyyy/MM/dd",
		"dd.MM.yyyy",
		"dd/MM/yyyy"
	};
	
	//Currency formats to appear in Preferences
	public final static String[] CURRENCY_FORMATS = {
		"$",
		"\u20ac",  //Euro
		"\u00a3",  //British Pounds
		"p.",      //Russian Ruble
		"\u00a5",  //Yen
		"\u20a3",  //French Franc
		"SFr", //Swiss Franc (?)
		"Rs", //Indian Rupees
		"Kr", //Norwegian
		"Bs", //Venezuela
		"S/.", //Peru
		"\u20b1",  //Peso
		"\u20aa"  //Israel Sheqel
	};

	//The plugins which are included in the main Buddi jar.
	public final static String[] BUILT_IN_PLUGINS = {
		"org.homeunix.drummer.plugins.reports.IncomeExpenseReportByCategory",
		"org.homeunix.drummer.plugins.reports.IncomeExpenseReportByDescription",
		"org.homeunix.drummer.plugins.graphs.IncomePieGraph",
		"org.homeunix.drummer.plugins.graphs.ExpensesPieGraph",
		"org.homeunix.drummer.plugins.graphs.ExpenseBudgetedVsActual",
		"org.homeunix.drummer.plugins.graphs.NetWorthBreakdown",
		"org.homeunix.drummer.plugins.graphs.NetWorthOverTime",
		
		"org.homeunix.drummer.plugins.exports.ExportHTML",
		"org.homeunix.drummer.plugins.exports.ExportCSV"
	};
		
	private Const(){}
}
