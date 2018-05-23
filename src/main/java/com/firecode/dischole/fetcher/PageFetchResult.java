package com.firecode.dischole.fetcher;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.firecode.dischole.Page;

/**
 * @author Yasser Ganjisaffar
 * @author JIANG
 */
public class PageFetchResult {

    protected static final Log logger = LogFactory.getLog(PageFetchResult.class);

    protected int statusCode;
    protected HttpEntity entity = null;
    protected Header[] responseHeaders = null;
    protected String fetchedUrl = null;
    protected String movedToUrl = null;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getFetchedUrl() {
        return fetchedUrl;
    }

    public void setFetchedUrl(String fetchedUrl) {
        this.fetchedUrl = fetchedUrl;
    }

    public boolean fetchContent(Page page, int maxBytes) throws SocketTimeoutException {
        try {
            page.setFetchResponseHeaders(responseHeaders);
            page.load(entity, maxBytes);
            return true;
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (Exception e) {
            logger.info(String.format("Exception while fetching content for: {} [{}]", page.getWebURL().getURL(),
                        e.getMessage()));
        }
        return false;
    }

    public void discardContentIfNotConsumed() {
        try {
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        } catch (IOException ignored) {
            // We can EOFException (extends IOException) exception. It can happen on compressed
            // streams which are not
            // repeatable
            // We can ignore this exception. It can happen if the stream is closed.
        } catch (Exception e) {
            logger.warn("Unexpected error occurred while trying to discard content", e);
        }
    }

    public String getMovedToUrl() {
        return movedToUrl;
    }

    public void setMovedToUrl(String movedToUrl) {
        this.movedToUrl = movedToUrl;
    }
}
