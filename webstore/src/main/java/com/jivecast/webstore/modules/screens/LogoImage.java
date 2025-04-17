package com.jivecast.webstore.modules.screens;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/*
 *  ------------------------------------------------------------------
 *  Jeffery L Painter, <jeff@jivecast.com>
 *
 *  Copyright (c) 2018-2020 JiveCast
 *  All Rights Reserved.
 *
 *  THE PROGRAM IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
 *  LIMITATION, WARRANTIES THAT THE PROGRAM IS FREE OF
 *  DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR
 *  NON-INFRINGING. THE ENTIRE RISK AS TO THE QUALITY AND
 *  PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD ANY PART
 *  OF THE PROGRAM PROVE DEFECTIVE IN ANY RESPECT, YOU
 *  (NOT JIVECAST) ASSUME THE COST OF ANY NECESSARY SERVICING,
 *  REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES
 *  AN ESSENTIAL PART OF THIS LICENSE. NO USE OF
 *  THE PROGRAM IS AUTHORIZED HEREUNDER EXCEPT
 *  UNDER THIS DISCLAIMER.
 *
 *  ------------------------------------------------------------------
 */

public abstract class LogoImage extends RawScreen {
	// create an instance of the logging facility
	private static Log log = LogFactory.getLog(LogoImage.class);

	/**
	 * We can display logo images to anyone who requests them
	 */
	protected boolean isAuthorized(RunData data) throws Exception {
		return true;
	}

	@Override
	protected String getContentType(PipelineData pipelineData) {
		return "image/jpg";
	}

	@Override
	protected void doOutput(PipelineData pipelineData) throws Exception {
		RunData data = (RunData) pipelineData;
		if (isAuthorized(data)) {
			ByteArrayOutputStream baos = buildImage(data);
			if (baos != null) {
				javax.servlet.http.HttpServletResponse response = data.getResponse();
				// We have to set the size to workaround a bug in IE (see com.lowagie iText FAQ)
				data.getResponse().setContentLength(baos.size());
				javax.servlet.ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
			} else {
				throw new Exception("output stream from image is null");
			}
		}
	}

	/**
	 * Classes that implement this ADT must override this to build the pdf file.
	 *
	 * @param data
	 *            RunData
	 * @return ByteArrayOutputStream
	 * @exception Exception
	 *                Description of the Exception
	 */
	protected abstract ByteArrayOutputStream buildImage(RunData data) throws Exception;

}
