package tech.jriascos.model;

import java.lang.reflect.Array;
import tech.jriascos.model.Definitions;

public class Words {
    private String word;
    private Definitions[] definitions;
    private String[] synonyms;
    private String[] antonyms;

    public Words(String word, Definitions[] definitions, String[] synonyms, String[] antonyms) {
        this.word = word;
        this.definitions = definitions;
        this.synonyms = synonyms;
        this.antonyms = antonyms;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Definitions[] getDefinitions() {
        return this.definitions;
    }

    public void setDefinitions(Definitions[] definitions) {
        this.definitions = definitions;
    }

    public String[] getSynonyms() {
        return this.synonyms;
    }

    public void setSynonyms(String[] synonyms) {
        this.synonyms = synonyms;
    }
    public String[] getAntonyms() {
        return this.antonyms;
    }

    public void setAntonyms(String[] antonyms) {
        this.antonyms = antonyms;
    }


}