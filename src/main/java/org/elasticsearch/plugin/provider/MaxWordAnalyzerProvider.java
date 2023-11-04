package org.elasticsearch.plugin.provider;

import org.elasticsearch.analyzer.MaxWordAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.filter.ExtendFilter;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.utils.Configuration;

/**
 * @version 1.0
 */
public class MaxWordAnalyzerProvider extends AbstractIndexAnalyzerProvider<MaxWordAnalyzer> {

    private final MaxWordAnalyzer analyzer;

    private MaxWordAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings, boolean indexMode) {
        super(indexSettings, name, settings);
        // 用于初始化词库
        Configuration configuration=new Configuration(env,settings);

        boolean useFirstPos = settings.getAsBoolean("use_first_position", ExtendFilter.DEFAULT_USE_FIRST_POSITION);
        // show_offset 的默认值是跟着 useFirstPos 变化，
        Boolean showOffset = settings.getAsBoolean("show_offset", null);
        if (showOffset == null) {
            showOffset = useFirstPos;
        }
        analyzer = new MaxWordAnalyzer(indexMode);
        analyzer.setUseFirstPos(useFirstPos);
        analyzer.setShowOffset(showOffset);
    }

    public static MaxWordAnalyzerProvider getIndexMaxWordAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new MaxWordAnalyzerProvider(indexSettings, env, name, settings, true);
    }

    public static MaxWordAnalyzerProvider getSearchMaxWordAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new MaxWordAnalyzerProvider(indexSettings, env, name, settings, false);
    }

    @Override
    public MaxWordAnalyzer get() {
        return analyzer;
    }
}
