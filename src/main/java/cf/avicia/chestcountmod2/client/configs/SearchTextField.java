package cf.avicia.chestcountmod2.client.configs;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SearchTextField extends TextFieldWidget {
    private final ConfigsGui configsGui;

    private String oldText = "";
    public SearchTextField(TextRenderer textRenderer, int x, int y, int par5Width, int par6Height, ConfigsGui configsGui) {
        super(textRenderer, x, y, par5Width, par6Height, Text.of(""));
        this.configsGui = configsGui;
        this.setChangedListener(this::onChange);
    }

    public void onChange(String newText) {
        if (!this.isFocused()) return;

        if (newText.length() > 0) {
            if (oldText.length() == 0) {
                configsGui.categories.add(new ConfigsCategory(configsGui.width / 16,
                        configsGui.startingHeight + configsGui.categories.size() * configsGui.settingLineHeight,
                        "All", configsGui));
                configsGui.categories.get(configsGui.categories.size() - 1).hasSearchItem = true;
                configsGui.setCategory("All");
            }

            ArrayList<ConfigsSection> selectionSections = configsGui.getSectionsBySearch("All");
            configsGui.categories.forEach(e -> e.hasSearchItem = false);
            selectionSections.forEach(selectionSection -> configsGui.categories.stream().filter(e -> e.title.equals(selectionSection.configsCategory)).findFirst().get().hasSearchItem = true);

            configsGui.drawSections(configsGui.drawContext, selectionSections);
        } else {
            configsGui.categories = configsGui.categories.stream().filter(e -> !e.title.equals("All")).collect(Collectors.toList());
            configsGui.setCategory(configsGui.savedCategory);

            configsGui.categories.forEach(e -> e.hasSearchItem = false);
        }
        oldText = newText;
    }
}
