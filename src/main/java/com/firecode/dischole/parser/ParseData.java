package com.firecode.dischole.parser;

import java.util.Set;
import com.firecode.dischole.WebURL;

public interface ParseData {

    Set<WebURL> getOutgoingUrls();

    void setOutgoingUrls(Set<WebURL> outgoingUrls);

    @Override
    String toString();
}