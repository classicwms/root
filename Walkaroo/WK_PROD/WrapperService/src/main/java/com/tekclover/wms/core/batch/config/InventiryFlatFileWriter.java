package com.tekclover.wms.core.batch.config;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class InventiryFlatFileWriter implements FlatFileHeaderCallback {

	private final String header;

	public InventiryFlatFileWriter(String header) {
		this.header = header;
	}

	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write(header);
	}
}