package cf.avicia.chestcountmod2.client.configs;

public class ConfigToggle extends Config {
    public String[] choices;

    public ConfigToggle(String configsCategory, String sectionText, String defaultValue, String configsKey) {
        super(configsCategory, sectionText, defaultValue, configsKey);
        this.choices = new String[]{"Enabled", "Disabled"};
    }
}
