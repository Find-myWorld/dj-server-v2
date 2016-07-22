
package com.example.iimp_znxj_new2014.model.response;

import java.io.Serializable;

public class BaseServerResponse implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 2561159070460523099L;
    
    private String action;
    
    private Object data;
    
	public String getMessage() {
		return action;
	}
	public void setMessage(String message) {
		this.action = message;
	}

	public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }
    
}

