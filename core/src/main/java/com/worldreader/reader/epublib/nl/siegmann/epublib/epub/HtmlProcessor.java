package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.OutputStream;

public interface HtmlProcessor {
	
	void processHtmlResource(Resource resource, OutputStream out);
}
