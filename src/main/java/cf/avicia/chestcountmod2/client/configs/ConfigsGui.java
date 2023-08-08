package cf.avicia.chestcountmod2.client.configs;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ConfigsGui extends Screen {
    public final int settingLineHeight = 27;
    public final int startingHeight = 85;
    public final int settingHeight = 23;
    public List<ConfigsCategory> categories = new ArrayList<>();
    // buttonList exists too, doesn't need to be created
//    public ArrayList<Element> buttonList = new ArrayList<>();
    public ArrayList<ConfigsTextField> textFieldsList = new ArrayList<>();
    public String selectedCategory, savedCategory;
    public Map<String, ArrayList<ConfigsSection>> totalSections = new HashMap<>();
    public int scrollSections; // the index of the first section to be displayed

    public TextFieldWidget searchTextField;
//    public boolean textFieldIsFocused = false;

    public DrawContext drawContext;

    public ConfigsGui() {
        super(Text.of("ChestCountMod Configs"));
    }

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.drawContext = drawContext;
        // Makes blur
        this.renderBackground(drawContext);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(2.0F, 2.0F, 2.0F);
        drawContext.drawCenteredTextWithShadow(textRenderer, "ChestCountMod Configs", this.width / 4 + 1, 11, 0x444444);
        drawContext.drawCenteredTextWithShadow(textRenderer, "ChestCountMod Configs", this.width / 4, 10, 0xFFFFFF);
        drawContext.getMatrices().pop();

        Screens.getButtons(this).clear();

        this.textFieldsList = new ArrayList<>();

        if (searchTextField.getText().length() > 0) {
            drawSections(drawContext, getSectionsBySearch());
        } else if (!selectedCategory.equals("All")) {
            drawSections(drawContext, totalSections.get(selectedCategory));
        }

        // Draw all text field inputs
        for (ConfigsTextField textField : this.textFieldsList) {
            addTextFieldToButtonList(textField);
        }

        addTextFieldToButtonList(searchTextField);
        if (searchTextField.getText().length() == 0 && !searchTextField.isFocused()) {
            searchTextField.setSuggestion("Type here to search...");
        } else {
            searchTextField.setSuggestion("");
        }

        try {
            super.render(drawContext, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawSections(DrawContext drawContext, ArrayList<ConfigsSection> sectionsList) {
        drawContext.drawVerticalLine(this.width / 16 + 110, startingHeight - 10, this.height - 10, Color.WHITE.getRGB());
        ArrayList<ConfigsSection> sectionsToShow = new ArrayList<>(sectionsList.subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), sectionsList.size())));

        for (ConfigsSection configsSection : sectionsToShow) {
            int index = sectionsToShow.indexOf(configsSection);
            configsSection.drawSection(this, this.width / 16 + 118, startingHeight + (settingHeight + settingLineHeight + 3) * index);
        }

        if (sectionsToShow.size() == 0) {
            drawContext.drawTextWithShadow(textRenderer, Text.of("[No Settings Found]"), this.width / 16 + 118, startingHeight, new Color(127, 127, 127).getRGB());
        }

        if (sectionsToShow.size() < sectionsList.size()) { // if not all configs fit on screen
            double segmentHeight = (double) ((height / 16 * 15) - startingHeight) / sectionsList.size();
            drawContext.drawVerticalLine(this.width / 16 * 15 + 5, startingHeight, height / 16 * 15, Color.DARK_GRAY.getRGB());
            drawContext.drawVerticalLine(this.width / 16 * 15 + 5, startingHeight + (int) (segmentHeight * scrollSections), startingHeight + (int) (segmentHeight * (scrollSections + sectionsToShow.size())), new Color(32, 110, 225).getRGB());
        }

        addCategories(categories);
    }

    // sets the selected category
    public void setCategory(String title) {
        scrollSections = 0;
        Screens.getButtons(this).clear();
        this.textFieldsList = new ArrayList<>();
        addCategories(categories);
        if (title == null) {
            for (ConfigsCategory category : categories) {
                category.enabled = false;
            }
            return;
        }

        if (title.equals("All")) {
            savedCategory = selectedCategory;
        }
        selectedCategory = title;

        for (ConfigsCategory category : categories) {
            category.enabled = category.title.equals(title);
        }
    }

    @Override
    public void init() {
        super.init();
        scrollSections = 0;
        this.totalSections = new HashMap<>();
        this.categories = new ArrayList<>();
        selectedCategory = "";

        for (Config config : ConfigsHandler.configsArray) {
            this.addSection(config);
        }

        setCategory(categories.get(0).title);
        searchTextField = new SearchTextField(textRenderer, width / 16, startingHeight - settingHeight - 10, 200, 17, this);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String oldCategory = selectedCategory;

        if (oldCategory.equals("All")) {
            oldCategory = savedCategory;
        }

        super.resize(client, width, height);
        this.init();
        setCategory(oldCategory);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {

        if (amount != 0) {
            try {
                this.scroll((int) amount, (int) mouseX);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public ArrayList<ConfigsSection> getSectionsBySearch() {
        return getSectionsBySearch(selectedCategory);
    }

    public ArrayList<ConfigsSection> getSectionsBySearch(String category) {
        String search = searchTextField.getText();
        ArrayList<ConfigsSection> returnSections = new ArrayList<>();

        ArrayList<ConfigsSection> allSections = new ArrayList<>();
        if (category.equals("All")) {
            for (String key : totalSections.keySet()) allSections.addAll(totalSections.get(key));
        } else {
            allSections = totalSections.get(category);
        }

        for (ConfigsSection section : allSections) {
            if (section.title.toLowerCase().contains(search.toLowerCase())) returnSections.add(section);
        }

        return returnSections;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (searchTextField.isFocused()) {
            scrollSections = 0;
        }
        searchTextField.keyPressed(keyCode, scanCode, modifiers);


        if (keyCode == 15) {
            if (hasShiftDown()) {
                previousCategory();
            } else {
                nextCategory();
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void addButton(ConfigsButton button) {
        if (!Screens.getButtons(this).contains(button)) {
            Screens.getButtons(this).add(button);
        }
    }

    public void addCategories(List<ConfigsCategory> categories) {
        for (ConfigsCategory category : categories)
            if (!Screens.getButtons(this).contains(category)) {
                Screens.getButtons(this).add(category);
            }
    }

    public void addTextField(ConfigsTextField textField) {
        textFieldsList.add(textField);
    }

    public void addTextFieldToButtonList(TextFieldWidget textField) {
        if (!Screens.getButtons(this).contains(textField)) {
            Screens.getButtons(this).add(textField);
        }
    }


    private void nextCategory() {
        int currentIndex = categories.stream().map(e -> e.title).toList().indexOf(selectedCategory);
        if (currentIndex == categories.size() - 1) {
            setCategory(categories.get(0).title);
        } else {
            setCategory(categories.get(currentIndex + 1).title);
        }
    }

    private void previousCategory() {
        int currentIndex = categories.stream().map(e -> e.title).toList().indexOf(selectedCategory);
        if (currentIndex == 0) {
            setCategory(categories.get(categories.size() - 1).title);
        } else {
            setCategory(categories.get(currentIndex - 1).title);
        }
    }

    @Override
    public void close() {
        for (ConfigsTextField textField : this.textFieldsList) {
            if (Pattern.matches(textField.finalValidation, textField.getText())) {
                textField.configsSection.updateConfigs(textField.getText());
            }
        }

        super.close();
    }

    public void scroll(int amount, int mouseX) {
        if (mouseX < this.width / 16 + 100) return;
        int configHeight = settingLineHeight + settingHeight;
        int settingsOnScreen = (this.height - startingHeight) / configHeight;

        // no need to scroll if every setting fits on screen
        if (searchTextField.getText().length() > 0) {
            if (settingsOnScreen > getSectionsBySearch().size()) return;
        } else {
            if (settingsOnScreen > totalSections.get(selectedCategory).size()) return;
        }

        scrollSections -= amount;
        if (scrollSections < 0) scrollSections = 0;
        if (searchTextField.getText().length() > 0) {
            if (scrollSections > getSectionsBySearch().size() - settingsOnScreen)
                scrollSections = getSectionsBySearch().size() - settingsOnScreen;
        } else {
            if (scrollSections > totalSections.get(selectedCategory).size() - settingsOnScreen)
                scrollSections = totalSections.get(selectedCategory).size() - settingsOnScreen;
        }

        Screens.getButtons(this).clear();
        this.textFieldsList = new ArrayList<>();
        addCategories(categories);


        ArrayList<ConfigsSection> sectionList;
        if (searchTextField.getText().length() > 0) {
            sectionList = getSectionsBySearch();
        } else {
            sectionList = new ArrayList<>(totalSections.get(selectedCategory).subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), totalSections.get(selectedCategory).size())));
        }

        sectionList.forEach((ConfigsSection configsSection) -> {
            int configPlacement = sectionList.indexOf(configsSection);
            if (configsSection.button != null) {
                configsSection.button.setY(configPlacement * settingLineHeight + startingHeight - 4 + (settingHeight * (configPlacement + 1)));
                addButton(configsSection.button);
            }


            if (configsSection.textField != null) {
                configsSection.textField.setY(configPlacement * settingLineHeight + startingHeight + 2 + (settingHeight * (configPlacement + 1)));
                this.textFieldsList.add(configsSection.textField);
            }
        });
    }

    public void addSection(Config config) {
        String configValue = ConfigsHandler.getConfig(config.configsKey);
        if (!configValue.equals("")) {
            config.defaultValue = configValue;
        }

        ConfigsSection sectionToAdd;

        int configPlacement = 0;
        if (totalSections.get(config.configsCategory) != null) {
            configPlacement = totalSections.get(config.configsCategory).size();
        }
        if (config instanceof ConfigToggle) {
            String[] choices = new String[]{"Enabled", "Disabled"};
            if (config.defaultValue.equals("Edit")) {
                choices = new String[]{"Edit"};
            }
            int width = Stream.of(choices).mapToInt(textRenderer::getWidth).max().getAsInt() + 10;

            ConfigsButton configButton = new ConfigsButton(this.width / 16 + 121, configPlacement * settingLineHeight + startingHeight - 4 + (settingHeight * (configPlacement + 1)), width, choices, config.defaultValue);
            sectionToAdd = new ConfigsSection(config.configsCategory, config.sectionText, configButton, config.configsKey);
        } else {
            ConfigsTextField textField = new ConfigsTextField(((ConfigInput) config).allowedInputs, ((ConfigInput) config).finalValidation, textRenderer, this.width / 16 + 122, configPlacement * settingLineHeight + startingHeight - 2 + (settingHeight * (configPlacement + 1)), this.width / 4, 16);
            textField.setText(config.defaultValue);
            if (((ConfigInput) config).maxLength != 0) {
                textField.setMaxLength(((ConfigInput) config).maxLength);
            }

            sectionToAdd = new ConfigsSection(config.configsCategory, config.sectionText, textField, config.configsKey);
        }

        // add the ConfigsSection to the configs map
        if (totalSections.containsKey(config.configsCategory)) {
            ArrayList<ConfigsSection> s = totalSections.get(config.configsCategory);
            s.add(sectionToAdd);
            totalSections.put(config.configsCategory, s);
        } else {
            totalSections.put(config.configsCategory, new ArrayList<>(Collections.singletonList(sectionToAdd)));
        }

        // make sure all categories that a config can be in are present in categories
        boolean containsCategory = false;
        for (ConfigsCategory category : categories) {
            if (category.title.equals(config.configsCategory)) {
                containsCategory = true;
                break;
            }
        }
        if (!containsCategory) {
            categories.add(new ConfigsCategory(this.width / 16, startingHeight + categories.size() * settingLineHeight, config.configsCategory, this));
        }
    }
}
