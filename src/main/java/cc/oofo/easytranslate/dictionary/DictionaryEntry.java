package cc.oofo.easytranslate.dictionary;

public class DictionaryEntry {
    private String source;
    private String target;
    private boolean caseSensitive;
    private boolean enabled;

    public DictionaryEntry() {
        // 用于序列化
    }

    public DictionaryEntry(String source, String target, boolean caseSensitive, boolean enabled) {
        this.source = source;
        this.target = target;
        this.caseSensitive = caseSensitive;
        this.enabled = enabled;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
} 