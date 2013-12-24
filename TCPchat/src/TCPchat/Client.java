package TCPchat;

import java.net.*;
import java.io.*;
import java.util.*;

/*Jeff Kekenj 4759171
 * COSC 3P01 Project
 * Client chat
 */

public class Client {
    public ObjectInputStream socketInput;
    public ObjectOutputStream socketOutput;
    public ByteArrayInputStream bInput;
    public InputStream is;
    public Socket socket;
    public String server;
    public String username = "Guest";
    public int port = 22222; 
    
    public String fileDirectory = "";
    
    Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
    }
        
    public boolean begin(){
        try {
            socket = new Socket(server, port);
        } catch(Exception e){
            return false;
        }
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(msg);
        
        fileDirectory = fileFind();
        
                try
		{
			socketInput  = new ObjectInputStream(socket.getInputStream());
			socketOutput = new ObjectOutputStream(socket.getOutputStream());
                        is = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException eIO) {
			return false;
		}

		new Client.ServerListener().start();
		try
		{
			socketOutput.writeObject(username);
		}
		catch (IOException eIO) {
			return false;
		}
		return true;
	}      
    	void sendMessage(ChatMessage msg) {
		try {
			socketOutput.writeObject(msg);
		}
		catch(IOException e) {	}
	}        
        
        public String fileFind() {
            File dir = new File("C:\\Test");
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".pdf");
                }
            });
            for (File allFiles : files) {
                //System.out.println(allFiles);
                return allFiles.getAbsolutePath();
            }
            return "";
        }
            
        public static void main (String args[]){ 
            int portNumber = 22222;
            String serverAddress = "localhost";
            String userName = "Guest";    
            System.out.println("Typing 'SEND' will send text file");
            System.out.println("Enter Username: ");
            Scanner scan = new Scanner(System.in);    
            userName =  scan.nextLine();
            //set up user with default settings for lab
            //first thing entered is username
            Client client = new Client("localhost", 22222,userName); 
            if (!client.begin()) return;
            while (true) {
                System.out.print("> ");
                String msg = scan.nextLine();
                if (msg.equalsIgnoreCase("SEND")) {
                    //System.out.println("Please provide directory of file"); 
                    //String directory = "C:\\Test\\a4_solu.pdf";
                    //single instance atm
                    //System.out.println("hey hey "+client.fileFind());                    
                    //String directory = "";
                    File file = new File(client.fileFind());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    try {
                        FileInputStream fis = new FileInputStream(file);                   
                        for (int readNum; (readNum = fis.read(buf)) != -1;) {
                            bos.write(buf, 0, readNum);
                            System.out.println("read " + readNum + " bytes,");
                        }
                    } catch (IOException ex) {}
                    byte[] bytes = bos.toByteArray();
                    client.sendMessage(new ChatMessage(ChatMessage.FILE, "",bytes));
                } else {
                    //Send out the client message
                    if (!(msg.equals("SEND"))){
                        client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg,null));
                    }//So that the send message isn't included                    
                }
            }                      
        }              

    class ServerListener extends Thread {
        public void run() {
            while (true) {
                //listen for sending o ut files and accept them
                try {               
                    Object j = socketInput.readObject();
                    byte[] test = (byte [])j;
                    
                    try {
                            FileOutputStream cache = new FileOutputStream("C:\\Test\\Hax\\hax.pdf");
                            cache.write(test);
                            cache.close();
                    } catch (Exception e) {}
                } catch (Exception e) {}
                //listen for text
                try {
                    String msg = (String) socketInput.readObject();
                    System.out.println(msg);
                    System.out.println("> " + msg);
                    System.out.println();
                } catch (IOException e) {
                } catch (ClassNotFoundException e2) {
                }
            }
        }
    }          
}







