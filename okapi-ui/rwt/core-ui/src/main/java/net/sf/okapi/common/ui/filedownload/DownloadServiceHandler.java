// http://wiki.eclipse.org/RAP/FAQ#How_to_provide_a_download_link.3F

package net.sf.okapi.common.ui.filedownload;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;

public class DownloadServiceHandler implements IServiceHandler {
	
	public void service() throws IOException, ServletException {
	    final String serverFileName = RWT.getRequest().getParameter("server-filename");
	    final String clientFileName = RWT.getRequest().getParameter("client-filename");
	    final byte[] download = FileUtils.readFileToByteArray(new File(serverFileName));
	    
	    // Send the file in the response
	    HttpServletResponse response = RWT.getResponse();
	    response.setContentType("application/octet-stream");
	    response.setContentLength(download.length);
	    String contentDisposition = "attachment; filename=\"" + 
	    		clientFileName + "\"";
	    response.setHeader("Content-Disposition", contentDisposition);
	    response.getOutputStream().write(download);
	}
}
