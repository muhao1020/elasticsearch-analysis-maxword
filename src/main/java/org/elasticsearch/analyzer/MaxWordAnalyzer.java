package org.elasticsearch.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.filter.ExtendFilter;
import org.elasticsearch.utils.TokenizerMap;

/**
 * max word analyzer
 *
 * @version 1.0
 */
public class MaxWordAnalyzer extends Analyzer {

    // 使用的 Tokenier
    private String tokenizer = TokenizerMap.STANDARD;
    // 默认为建立 索引模式， 如果为 查询模式 indexMode = false
    private final boolean indexMode;
    // 是否使用 first char position ，默认使用，如果为 false，则变为 lcp_analyzer
    private boolean useFirstPos = true;
    // 是否显示 offset，默认随着 indexMode 变化
    private boolean showOffset;

    public MaxWordAnalyzer(boolean indexMode) {
        this.indexMode = indexMode;

    }

    public String getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(String tokenizer) {
        this.tokenizer = tokenizer;
    }

    public boolean isUseFirstPos() {
        return useFirstPos;
    }

    public void setUseFirstPos(boolean useFirstPos) {
        this.useFirstPos = useFirstPos;
    }

    public boolean isShowOffset() {
        return showOffset;
    }

    public void setShowOffset(boolean showOffset) {
        this.showOffset = showOffset;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer src = TokenizerMap.getTokenizer(tokenizer);
        TokenStream tok = new ExtendFilter(src, indexMode)
                .setShowOffset(showOffset)
                .setUseFirstPos(useFirstPos);

        return new TokenStreamComponents(src, tok);
    }
}
