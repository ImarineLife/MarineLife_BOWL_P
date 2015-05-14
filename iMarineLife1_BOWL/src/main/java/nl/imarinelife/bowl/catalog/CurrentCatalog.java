package nl.imarinelife.bowl.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.imarinelife.bowl.R;
import nl.imarinelife.lib.LibApp;
import nl.imarinelife.lib.Preferences;
import nl.imarinelife.lib.catalog.Catalog;
import nl.imarinelife.lib.divinglog.db.dive.DiveDbHelper;
import nl.imarinelife.lib.divinglog.db.res.LocationDbHelper;
import nl.imarinelife.lib.divinglog.db.res.ProfilePartDbHelper;
import nl.imarinelife.lib.fieldguide.db.FieldGuideAndSightingsEntryDbHelper;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CurrentCatalog extends Catalog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TAG = "CurrentCatalog";

	private static CurrentCatalog me = null;

	public static Catalog getInstance(Context ctx) {
		Log.d(TAG, "getInstance");
		if (me == null) {
			me = new CurrentCatalog(ctx);
			LibApp.getInstance().setCatalog(me);

			me.initializeLocations(ctx);
			me.initializeProfileParts(ctx);
			me.cleanup();

		}
		return me;
	}

	private CurrentCatalog(Context ctx) {
		super(ctx);
		// first thing to do is decide on the possible language
		possibleLanguage = ctx.getResources().getString(R.string.language);
		String currentLanguage = Preferences.getString(
				Preferences.CURRENT_LANGUAGE, null);
		String ignoredLanguage = Preferences.getString(
				Preferences.IGNORED_LANGUAGE, null);
		if (currentLanguage == null) {
			Log.d(TAG, "setting language [" + possibleLanguage
					+ "] in CURRENT_LANGUAGE");
			Preferences.setString(Preferences.CURRENT_LANGUAGE,
					possibleLanguage);
			currentLanguage = possibleLanguage;
		}
		if (ignoredLanguage==null || !ignoredLanguage.equals(possibleLanguage)) {
			ignoredLanguage = currentLanguage;
			Preferences
					.setString(Preferences.IGNORED_LANGUAGE, ignoredLanguage);
		}
		Log.d(TAG, "Language possible[" + possibleLanguage + "] current["
				+ currentLanguage + "] ignored(if not current)["
				+ ignoredLanguage + "]");

		acknowledgements = LibApp.getCurrentResources().getString(R.string.ack);
		try {
			version = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			version = 1;
		}
		
		DATAVERSION_FIELDGUIDEANDSIGHTINGS = 4;
		DATAVERSION_LOCATIONS = 3;
		DATAVERSION_PROFILEPARTS = 5;

		try {
			versionName = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "1.1";
		}

		Log.d(TAG, Preferences.DATAVERSION_FIELDGUIDEANDSIGHTINGS+": "+Preferences.getInt(Preferences.DATAVERSION_FIELDGUIDEANDSIGHTINGS, 0));
		Log.d(TAG, Preferences.DATAVERSION_LOCATIONS+": "+Preferences.getInt(Preferences.DATAVERSION_LOCATIONS, 0));
		Log.d(TAG, Preferences.DATAVERSION_PROFILEPARTS+": "+Preferences.getInt(Preferences.DATAVERSION_PROFILEPARTS, 0));
		Log.d(TAG, Preferences.DATAVERSION_DIVES+": "+Preferences.getInt(Preferences.DATAVERSION_DIVES, 0));
		
		name = LibApp.getCurrentResources().getString(R.string.catalog_name);
		hideCode = true;
		project_name = LibApp.getCurrentResources().getString(
				R.string.project_name);
		expansionFileMainVersion = Integer.parseInt(LibApp
				.getCurrentResources().getString(
						R.string.expansionfile_main_version));
		expansionFilePatchVersion = Integer.parseInt(LibApp
				.getCurrentResources().getString(
						R.string.expansionfile_patch_version));
		appName = LibApp.getCurrentResources().getString(R.string.app_name);
		introduction = LibApp.getCurrentResources().getString(
				R.string.introduction);
		help = LibApp.getCurrentResources().getString(
				R.string.help_for_observations);
		thanks = LibApp.getCurrentResources().getString(R.string.thanks);
		mailBody = LibApp.getCurrentResources().getString(R.string.mail_body);
		mailFrom = LibApp.getCurrentResources().getString(R.string.mail_from);
		mailFromPwd = LibApp.getCurrentResources().getString(
				R.string.mail_from_password);
		mailTo = LibApp.getCurrentResources().getString(R.string.mail_to);

		ids = new Integer[] { 126281, 151353, 154281, 154333, 236462, 126915,
				154373, 154388, 126933, 126913, 151302, 127196, 154165, 154210,
				151308, 126507, 154343, 151290, 126916 };

		latinIds = new Integer[] { R.string.latin126281, R.string.latin151353,
				R.string.latin154281, R.string.latin154333,
				R.string.latin236462, R.string.latin126915,
				R.string.latin154373, R.string.latin154388,
				R.string.latin126933, R.string.latin126913,
				R.string.latin151302, R.string.latin127196,
				R.string.latin154165, R.string.latin154210,
				R.string.latin151308, R.string.latin126507,
				R.string.latin154343, R.string.latin151290,
				R.string.latin126916 };

		commonIds = new HashMap<String, Integer>();
		commonIds.put("common126281", R.string.common126281);
		commonIds.put("common151353", R.string.common151353);
		commonIds.put("common154281", R.string.common154281);
		commonIds.put("common154333", R.string.common154333);
		commonIds.put("common236462", R.string.common236462);
		commonIds.put("common126915", R.string.common126915);
		commonIds.put("common154373", R.string.common154373);
		commonIds.put("common154388", R.string.common154388);
		commonIds.put("common126933", R.string.common126933);
		commonIds.put("common126913", R.string.common126913);
		commonIds.put("common151302", R.string.common151302);
		commonIds.put("common127196", R.string.common127196);
		commonIds.put("common154165", R.string.common154165);
		commonIds.put("common154210", R.string.common154210);
		commonIds.put("common151308", R.string.common151308);
		commonIds.put("common126507", R.string.common126507);
		commonIds.put("common154343", R.string.common154343);
		commonIds.put("common151290", R.string.common151290);
		commonIds.put("common126916", R.string.common126916);

		descrIds = new HashMap<String, Integer>();
		descrIds.put("descr126281", R.string.descr126281);
		descrIds.put("descr151353", R.string.descr151353);
		descrIds.put("descr154281", R.string.descr154281);
		descrIds.put("descr154333", R.string.descr154333);
		descrIds.put("descr236462", R.string.descr236462);
		descrIds.put("descr126915", R.string.descr126915);
		descrIds.put("descr154373", R.string.descr154373);
		descrIds.put("descr154388", R.string.descr154388);
		descrIds.put("descr126933", R.string.descr126933);
		descrIds.put("descr126913", R.string.descr126913);
		descrIds.put("descr151302", R.string.descr151302);
		descrIds.put("descr127196", R.string.descr127196);
		descrIds.put("descr154165", R.string.descr154165);
		descrIds.put("descr154210", R.string.descr154210);
		descrIds.put("descr151308", R.string.descr151308);
		descrIds.put("descr126507", R.string.descr126507);
		descrIds.put("descr154343", R.string.descr154343);
		descrIds.put("descr151290", R.string.descr151290);
		descrIds.put("descr126916", R.string.descr126916);

		groupIds = new HashMap<String, Integer>();
		groupIds.put("pisces", R.string.pisces);

		commonToGroup = new HashMap<String, String>();
		commonToGroup.put("common126281", "pisces");
		commonToGroup.put("common151353", "pisces");
		commonToGroup.put("common154281", "pisces");
		commonToGroup.put("common154333", "pisces");
		commonToGroup.put("common236462", "pisces");
		commonToGroup.put("common126915", "pisces");
		commonToGroup.put("common154373", "pisces");
		commonToGroup.put("common154388", "pisces");
		commonToGroup.put("common126933", "pisces");
		commonToGroup.put("common126913", "pisces");
		commonToGroup.put("common151302", "pisces");
		commonToGroup.put("common127196", "pisces");
		commonToGroup.put("common154165", "pisces");
		commonToGroup.put("common154210", "pisces");
		commonToGroup.put("common151308", "pisces");
		commonToGroup.put("common126507", "pisces");
		commonToGroup.put("common154343", "pisces");
		commonToGroup.put("common151290", "pisces");
		commonToGroup.put("common126916", "pisces");

		locationNames = new HashMap<String, Integer>();
		locationNames.put("loc01", R.string.loc01);
		locationNames.put("loc02", R.string.loc02);
		locationNames.put("loc03", R.string.loc03);
		locationNames.put("loc04a", R.string.loc04a);
		locationNames.put("loc04b", R.string.loc04b);
		locationNames.put("loc04c", R.string.loc04c);
		locationNames.put("loc04d", R.string.loc04d);
		locationNames.put("loc05", R.string.loc05);
		locationNames.put("loc06", R.string.loc06);
		locationNames.put("loc07", R.string.loc07);
		locationNames.put("loc08a", R.string.loc08a);
		locationNames.put("loc08b", R.string.loc08b);
		locationNames.put("loc08c", R.string.loc08c);
		locationNames.put("loc08d", R.string.loc08d);
		locationNames.put("loc09", R.string.loc09);
		locationNames.put("loc10a", R.string.loc10a);
		locationNames.put("loc10b", R.string.loc10b);
		locationNames.put("loc11a", R.string.loc11a);
		locationNames.put("loc11b", R.string.loc11b);
		locationNames.put("loc12", R.string.loc12);

		checkValues = new String[] { null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null };

		valuesMap = new HashMap<String, Integer>();
		valuesMap.put("Z", R.string.Z);
		valuesMap.put("A", R.string.A);
		valuesMap.put("M", R.string.M);

		profileParts = new String[][] { { "lopend", "true", "1" },
				{ "snorkelend", "true", "2" }, { "luchttemp", "false", "3" }, { "watertemp", "false", "4" } };
		profilePartsMap = new HashMap<String, Integer>();
		profilePartsMap.put("lopend", R.string.wandelend);
		profilePartsMap.put("snorkelend", R.string.snorkelend);
		profilePartsMap.put("watertemp", R.string.watertemp);
		profilePartsMap.put("luchttemp", R.string.luchttemp);

		if (sightingChoices.isEmpty()) {
			checkboxChoices.add("");
			sightingChoices.add("0");
			sightingChoices.add("?");
			sightingChoices.add("Z");
			sightingChoices.add("A");
			sightingChoices.add("M");
			defaultChoice = "?";
		}
		if (defaultableSightingChoices.isEmpty()) {
			defaultableSightingChoices.add("?");
			defaultableSightingChoices.add("0");
		}

		allgroups = LibApp.getCurrentResources().getString(R.string.allgroups);

		// necessary byte array for getting expansion file
		// null if no expansionfile is available
		salt = new byte[] { 12, 67, -15, 6, -55, -87, 99, -12, 40, 65, 25, 88,
				-19, -66, 102, -10, -31, -92, -61, 3 };

		// necessary base64 public key for getting expansion file
		// null if no expansionfile is available
		base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmaFPjc6cRgkmp/CJjprCdEWHSl6ekwmaDwBUsKcutcfS5Va2gDpbsi4ZlER/ay3nSt3LF/r1d5ULutYBc2EMV5cGtsoF54hVfLj2Aaw+fcnsbwSEbVGlopdHNwVYSxPDoirUID1zETFFmUOOFT9nEkJZups5VKIMUaPRZjOIkXY1lONXQB7FAJmmCPMdJ26Ef0P6cYiOhUA0jeLIXMAK9gTjJRFQmVJkKKxIwJNVblmQ0OZQ7Kc9wgkCuXwu9WIqucqZSKtGqsCArLW4kCm5MmudYzr2rNllv7rU9Wtmm1ZBMVBcSo/wNToQc+E1iHCHd6h5Ga/bsgG7NQ3KtUTljQIDAQAB";
	}

	public String getPossibleLanguage() {
		return possibleLanguage;
	}

	public void setPossibleLanguage(String language) {
		this.possibleLanguage = language;
	}

	public Integer[] getIds() {
		return ids;
	}

	public Integer[] getLatinIds() {
		return latinIds;
	}

	public Map<String, Integer> getCommonIdMapping() {
		return commonIds;
	}

	public Map<String, Integer> getGroupIdMapping() {
		return groupIds;
	}

	public Map<String, Integer> getDescriptionIdMapping() {
		return descrIds;
	}

	public String[] getCheckValues() {
		return checkValues;
	}

	public Map<String, Integer> getProfilePartsMapping() {
		return profilePartsMap;
	}

	public Map<String, Integer> getLocationNamesMapping() {
		return locationNames;
	}

	public Map<String, Integer> getValuesMapping() {
		return valuesMap;
	}

	public String getAppName() {
		return appName;
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public String getAllGroups() {
		return allgroups;
	}

	public byte[] getSalt() {
		return salt;
	}

	public String getBase64PublicKey() {
		return base64PublicKey;
	}

	public String getMailBody() {
		return mailBody;
	}

	public String getMailTo() {
		return mailTo;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public String getMailFromPwd() {
		return mailFromPwd;
	}

	public ArrayList<String> getSightingChoices() {
		return sightingChoices;
	}

	public ArrayList<String> getDefaultableSightingChoices() {
		return defaultableSightingChoices;
	}

	public ArrayList<String> getCheckBoxChoices() {
		return checkboxChoices;
	}

	public String getIntroduction() {
		return introduction;
	}

	public String getHelp() {
		return help;
	}

	public String getThanks() {
		return thanks;
	}

	public String getProject() {
		return project_name;
	}

	@Override
	public int getExpansionFileMainVersion() {
		return expansionFileMainVersion;
	}

	@Override
	public int getExpansionFilePatchVersion() {
		return expansionFilePatchVersion;
	}

	@Override
	public Map<String, String> getCommonToGroupMapping() {
		return commonToGroup;
	}
	
	public void onDataVersionUpgrade_FieldGuideAndSightings(int oldVersion, int newVersion) {
		boolean doFieldGuideOnlyUpdate = true;
		FieldGuideAndSightingsEntryDbHelper helper = FieldGuideAndSightingsEntryDbHelper.getInstance(LibApp.getContext());
		switch (oldVersion) {
			case 0:// do what needs to be done. like onUpgrade, to be implemented as fallthrough
			case 1: 
			case 2:
			case 3:
				Log.d(TAG,"onDataVersionUpgrade_FieldGuideAndSightings");
				helper.fillFieldsForVersion004(); //refill FieldGuide and Sightings
				doFieldGuideOnlyUpdate=false; //already done
		}
	}
	
	public void onDataVersionUpgrade_Locations(
			int oldVersion, int newVersion){
		switch (oldVersion) {
		case 0:// do what needs to be done. like onUpgrade, to be implemented as fallthrough
		default:
			Log.d(TAG,"onDataVersionUpgrade_Locations");
			Catalog catalog = LibApp.getInstance().getCurrentCatalog();
			if (catalog != null) {
				DiveDbHelper diveDbHelper = DiveDbHelper.getInstance(LibApp
						.getContext());
				LocationDbHelper locationsHelper = LocationDbHelper.getInstance(LibApp.getContext());
				// will fill locations anew
				catalog.fillLocations(locationsHelper, diveDbHelper);
			}
		}
	}

	
	public void onDataVersionUpgrade_ProfileParts(
			int oldVersion, int newVersion){
		switch (oldVersion) {
		case 0:// do what needs to be done. like onUpgrade, to be implemented as fallthrough
		case 1: 
		case 2:
		case 3:
		case 4:
			Log.d(TAG,"onDataVersionUpgrade_ProfileParts");
			Catalog catalog = LibApp.getInstance().getCurrentCatalog();
			if (catalog != null) {
				// will fill profileparts anew
				ProfilePartDbHelper helper = ProfilePartDbHelper.getInstance(LibApp.getInstance());
				catalog.fillProfileParts(helper);
			}
			Preferences.setInt(Preferences.DATAVERSION_PROFILEPARTS, newVersion);
		}
	}
	
}
