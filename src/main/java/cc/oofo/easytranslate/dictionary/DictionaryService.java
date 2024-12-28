package cc.oofo.easytranslate.dictionary;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@State(
    name = "cc.oofo.easytranslate.dictionary.DictionaryService",
    storages = @Storage("EasyTranslateDictionary.xml")
)
@Service(Service.Level.PROJECT)
public class DictionaryService implements PersistentStateComponent<DictionaryService> {
    private List<DictionaryEntry> entries = new ArrayList<>();

    public List<DictionaryEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<DictionaryEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(DictionaryEntry entry) {
        entries.add(entry);
    }

    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    public void updateEntry(int index, DictionaryEntry entry) {
        if (index >= 0 && index < entries.size()) {
            entries.set(index, entry);
        }
    }

    public String processText(String text) {
        String result = text;
        for (DictionaryEntry entry : entries) {
            if (!entry.isEnabled()) {
                continue;
            }

            String source = entry.getSource();
            if (!entry.isCaseSensitive()) {
                source = "(?i)" + Pattern.quote(source);
            } else {
                source = Pattern.quote(source);
            }

            result = result.replaceAll(source, entry.getTarget());
        }
        return result;
    }

    @Override
    public @Nullable DictionaryService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DictionaryService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static DictionaryService getInstance(@NotNull Project project) {
        return project.getService(DictionaryService.class);
    }
} 