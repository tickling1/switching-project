package com.switching.study_matching_site;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class CustomP6spyFormat implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }
        return sql.trim() + ";";
    }
}
