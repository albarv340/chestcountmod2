package cf.avicia.chestcountmod2.client.configs;

public class Config {
    public String configsCategory, sectionText, defaultValue, configsKey;

    public Config(String configsCategory, String sectionText, String defaultValue, String configsKey) {
        this.configsCategory = configsCategory;
        this.sectionText = sectionText;
        this.defaultValue = defaultValue;
        this.configsKey = configsKey;
    }
}
