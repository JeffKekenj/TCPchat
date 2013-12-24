package TCPchat;

import java.io.Serializable;

/*Jeff Kekenj 4759171
 * COSC 3P01 Project
 * chat message
 */

public class ChatMessage implements Serializable {   
	protected static final long serialVersionUID = 1112122200L;
	static final int TEST = 0, MESSAGE = 1, LOGOUT = 2, FILE = 3;
	private int type;
	private String message;	
        private byte[] data;
	ChatMessage(int type, String message, byte[] data) {
		this.type = type;
		this.message = message;
                this.data = data;
	}	
	int getType() {
		return type;
	}
	String getMessage() {
		return message;
	}        
        byte[] getByte(){
            return data;
        }
}