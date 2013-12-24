package TCPchat;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/*Jeff Kekenj 4759171
 * COSC 3P01 Project
 * server
 */

public class TCPServer {
    private static int connectionID;
    private ArrayList<ClientThread> arrList;
    private int port = 22222;
    private boolean continueOn = true;
    
    public TCPServer() {
        this.port = port;
        arrList = new ArrayList<ClientThread>();
    }

    public void beginConnection() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (continueOn) {
                Socket socket = serverSocket.accept();
                if(!continueOn)
                 break;
                 ClientThread t = new ClientThread(socket);  
                 arrList.add(t);									
                 t.start();
            }
        } catch (Exception e) {
        }
    }

    public static void main(String args[]) {
        TCPServer server = new TCPServer();
        server.beginConnection();
    }

    class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream socketInput;
        ObjectOutputStream socketOutput;
        ByteArrayOutputStream byteArrayOutputStream;
        OutputStream os;
        int id;
        String username;
        ChatMessage cm;
        String date;

        ClientThread(Socket socket) {
            id = ++connectionID;
            this.socket = socket;
            try {
                socketOutput = new ObjectOutputStream(socket.getOutputStream());
                socketInput = new ObjectInputStream(socket.getInputStream());
                os = new ObjectOutputStream(socket.getOutputStream());                
                username = (String) socketInput.readObject();
                System.out.println(username + " has connected");
            } catch (IOException e) {return;} catch (ClassNotFoundException e) {}
        }

        public void run() {
            boolean keepGoing = true;
            while (keepGoing) {                
                try {
                    cm = (ChatMessage) socketInput.readObject();
                } catch (IOException e) { break; } catch (ClassNotFoundException e2) {
                    break;
                }
                switch (cm.getType()) {
                    case ChatMessage.MESSAGE:                        
                        broadcastMessage(username + ": " + cm.getMessage());
                        break;
                    //FILE
                    case ChatMessage.FILE: 
                        broadcastBytes(cm.getByte());
                        System.out.println("here testing ");
                        try {
                            FileOutputStream cache = new FileOutputStream("C:\\Test\\Stuff\\super.pdf");
                            cache.write(cm.getByte());
                            cache.close();
                        } catch (Exception e) {}
                        break;  
                        }
            }
            close();
        }
        
       private synchronized void broadcastBytes(byte[] data) {
		for(int i = arrList.size(); --i >= 0;) {
			ClientThread ct = arrList.get(i);
			if(!ct.writeFile(data)) {
				arrList.remove(i);
			}
		}
	}//used to broadcast bytes for files for users
        
        private synchronized void broadcastMessage(String message) {
                System.out.println(message);	
		for(int i = arrList.size(); --i >= 0;) {
			ClientThread ct = arrList.get(i);
			if(!ct.writeMsg(message)) {
				arrList.remove(i);
			}
		}
	}//Used to broadcast message to users

        private void close() {
            try {
                if (socketOutput != null) {socketOutput.close();}
            } catch (Exception e) {}
            try {
                if (socketInput != null) {
                    socketInput.close();
                }
            } catch (Exception e) {
            };
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {}
        }

        private boolean writeMsg(String msg) {
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                socketOutput.writeObject(msg);
            } catch (IOException e) {}
            return true;
        }
        
       private boolean writeFile(byte[] data) {
           //SERVER CACHED COPY
           //Uses Server chached copy to broadcast file to other users
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                //Using Server Chached copy, broadcast file to 
                //all users currently on network
                socketOutput.writeObject(data);
            } catch (IOException e) {}
            return true;  
        }     
    }
}
