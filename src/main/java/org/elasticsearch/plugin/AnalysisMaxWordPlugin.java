package org.elasticsearch.plugin;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugin.provider.MaxWordAnalyzerProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * max word plugin
 *
 * @version 1.0
 */
public class AnalysisMaxWordPlugin extends Plugin implements AnalysisPlugin{

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> extra = new HashMap<>();

        extra.put("max_word_index", MaxWordAnalyzerProvider::getIndexMaxWordAnalyzerProvider);
        extra.put("max_word_search", MaxWordAnalyzerProvider::getSearchMaxWordAnalyzerProvider);

        return extra;
    }


}
