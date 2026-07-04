package com.learnrank.common.exception;

import java.util.List;

public record ErrorResponse(
	    String code, String message, String timestamp, String traceId, List<Detail> details
	) {
	    public record Detail(String field, String issue) {}
	}
