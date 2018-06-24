package com.wumple.pantography.common;

/**
 * Mod constants for versioning, etc - many from Gradle
 * @see Pantography
 */
public final class Reference {

	// Mod Constants
	public static final String MOD_ID = "GRADLE:MOD_ID";
	public static final String MOD_NAME = "GRADLE:MOD_NAME";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION";
	public static final String INTERNALVERSION = "GRADLE:INTERNALVERSIONMAJOR-" + BUILD;
	public static final String DEPENDENCIES = "GRADLE:DEPENDENCIES";
	public static final String UPDATEJSON = "GRADLE:UPDATEJSON";

	// Network Contants
	public static final String NETWORK_CHANNEL = MOD_ID;

	// Proxy Constants
	///public static final String PROXY_SERVER = "com.wumple.palimpsest.common.core.proxy.ServerProxy";
	///public static final String PROXY_CLIENT = "com.wumple.palimpsest.core.proxy.ClientProxy";
	///public static final String GUI_FACTORY  = "com.wumple.palimpsest.client.core.proxy.GuiFactory";

	// IMC Keys
	///public static final String BLACKLIST_ITEM = "blackListItem";
}
