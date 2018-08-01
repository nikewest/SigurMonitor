package sigur;

import java.util.Properties;

public interface SigurSettingsManager {

	Properties getConnectionSettings();
	Properties getEventHandlerSettings();
	Properties getSyncSettings();
}
