package org.elasticsearch.utils;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 */
public class TokenizerMap {

    public final static String STANDARD = "standard";
    public final static String NGRAM = "ngram";

    private static final Map<String, Tokenizer> tokenizerMap = new HashMap<>();

    static {
        tokenizerMap.put("standard", new StandardTokenizer());
        tokenizerMap.put("ngram", new NGramTokenizer(1, 1));
    }

    /**
     * 通过指定 name 返回 Tokenizer，默认为 StandardTokenizer
     * @param name
     * @return
     */
    public static Tokenizer getTokenizer(String name) {
        return tokenizerMap.getOrDefault(name, new StandardTokenizer());
    }

}
