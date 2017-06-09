package com.dhlee.camel.tramsform;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.support.ServiceSupport;

public final class CaseDataFormat extends ServiceSupport implements DataFormat {

	public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
		byte[] bytes = exchange.getContext().getTypeConverter().mandatoryConvertTo(byte[].class, graph);
        String caseType = exchange.getProperty("CASE",String.class);
        stream.write(caseStream(bytes, caseType));        
        
    } 
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        byte[] bytes = exchange.getContext().getTypeConverter().mandatoryConvertTo(byte[].class, stream);
		String caseType = exchange.getProperty("CASE",String.class);
        return caseStream(bytes, caseType);      
    }
 
    private byte[] caseStream(byte[] data, String caseType) {
    	String input = new String(data);
    	
    	if("UPPER".equals(caseType)) {
        	return input.toUpperCase().getBytes();
        }
        else {
        	return  input.toLowerCase().getBytes();
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        // noop
    }
 
    @Override
    protected void doStop() throws Exception {
        // noop
    }
}

