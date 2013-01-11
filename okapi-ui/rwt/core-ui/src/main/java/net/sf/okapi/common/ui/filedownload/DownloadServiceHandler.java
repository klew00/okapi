// http://wiki.eclipse.org/RAP/FAQ#How_to_provide_a_download_link.3F

package net.sf.okapi.common.ui.filedownload;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.sf.okapi.common.Util;

import org.apache.commons.io.FileUtils;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;

public class DownloadServiceHandler implements IServiceHandler {
	
	public void service() throws IOException, ServletException {
	    final String fileName = RWT.getRequest().getParameter("filename");
	    final byte[] download = FileUtils.readFileToByteArray(new File(fileName));
	    
	    // Send the file in the response
	    HttpServletResponse response = RWT.getResponse();
	    response.setContentType("application/octet-stream");
	    response.setContentLength(download.length);
	    String contentDisposition = "attachment; filename=\"" + 
	    		Util.getFilename(fileName, true) + "\"";
	    response.setHeader("Content-Disposition", contentDisposition);
	    response.getOutputStream().write(download);
	}
}
