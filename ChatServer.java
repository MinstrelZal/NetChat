
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class ChatServer
{
	public static void main(String args[])
	{
		ServerSocket server=null;
		Socket you=null;
		Hashtable peopleList;                                 //�����������߿ͻ���ͨ�ŵķ������̵߳�ɢ�б�
		
		peopleList=new Hashtable();
		while(true)
		{
			try
			{
				server=new ServerSocket(6666);
			}
			catch(IOException e1)
			{
				System.out.println("���ڼ���");
			}
			try
			{
				you=server.accept();                                //�����Ϳͻ��˵����ӵ��׽���
				InetAddress address=you.getInetAddress();
				System.out.println("�û���IP��"+address);
			}	
			catch(IOException e)
			{
			}
			if(you!=null)
			{
				Server_thread peopleThread=new Server_thread(you,peopleList);
				peopleThread.start();                                 //��ÿͻ���ͨ�ŵķ���������ʼ�����߳�
			}	
			else{
				continue;
			}	
		}
	}
}
	
class Server_thread extends Thread
{
		String name=null,sex=null;                                   //�����ߵ��ǳƺ��Ա�
		Socket socket=null;
		File file=null;
		DataOutputStream out=null;
		DataInputStream in=null;
		Hashtable peopleList=null;
		Server_thread(Socket t,Hashtable list)
		{
			peopleList=list;
			socket=t;
			try
			{
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
			}
			catch(IOException e)
			{
			}	
		}
		public void run()
		{
			while(true)
			{
				String s=null;
				try
				{//�ȴ����������̣߳�ֱ���յ���Ϣ���ͻ��˷�������Ϣ
					s=in.readUTF();
					if(s.startsWith("�ǳ�:"))                    //����û��ύ���ǳƺ��Ա�
					{
						name=s.substring(s.indexOf(":")+1,s.indexOf("�Ա�"));    //��ȡ�û���Ϣ�е��ǳ�
						sex=s.substring(s.indexOf("�Ա�")+2);                    //��ȡ�Ա�
						
						boolean boo=peopleList.containsKey(name);                //���ɢ�б����Ƿ����н������ǳƵ�������
				        if(boo==false)
				        {
							
							peopleList.put(name,this);                           //����ǰ�߳���ӵ�ɢ�б��ǳ���Ϊ�ؼ���
							out.writeUTF("��������:");
							Enumeration enum1=peopleList.elements();
							
							while(enum1.hasMoreElements())                         //��ȡ���е���ͻ���ͨ�ŵķ������߳�
							{
								Server_thread th=(Server_thread)enum1.nextElement();//����ǰ�����ߵ��ǳƺ��Ա�֪ͨ���е��û�
								th.out.writeUTF("������:"+name+"�Ա�"+sex);        //Ҳ�����������ߵ�����֪ͨ���̣߳���ǰ�û���
								if(th!=this)
								{
									out.writeUTF("������:"+th.name+"�Ա�"+th.sex);
								}
							}
						}
						else
						{//������û��ǳ��Ѵ��ڣ���ʾ�û���������
							out.writeUTF("����������:");
						}
					  }
					  else if(s.startsWith("������������:"))
					  {
						 String message=s.substring(s.indexOf(":")+1);
						 Enumeration enum1=peopleList.elements();                    //��ȡ���е���ͻ���ͨ�ŵķ������߳�
						 while(enum1.hasMoreElements())
						 {
							((Server_thread)enum1.nextElement()).out.writeUTF("������������:"+message);
						 }
					  }			
				      else if(s.startsWith("�û��뿪:"))
				      {
					       Enumeration enum1=peopleList.elements();               //��ȡ���е���ͻ���ͨ�ŵķ������߳�
					       while(enum1.hasMoreElements())                         //֪ͨ���������ߣ����û�������
					       {
						       try
						       {
							        Server_thread th=(Server_thread)enum1.nextElement();
							        if(th!=this&&th.isAlive())
							        {
							        	th.out.writeUTF("�û�����:"+name);
							    	}
							   }
					           catch(IOException eee)
					           {
					 	       }		
					    	}
					       if(name!=null)
				                peopleList.remove(name);
				            socket.close();                                                          //�رյ�ǰ����
				            System.out.println(name+"�ͻ��뿪��");
				            break;		                                                             //�������̵߳Ĺ������߳�����
				      }
					
				     else if(s.startsWith("˽����������:"))
				     {
				         	String ���Ļ�=s.substring(s.indexOf(":")+1,s.indexOf("#"));
				         	String toPeople=s.substring(s.indexOf("#")+1);//�ҵ�Ҫ�������Ļ����߳�
					
					        Server_thread toThread=(Server_thread)peopleList.get(toPeople);
				            if(toThread!=null)
				            {
				                 toThread.out.writeUTF("˽����������:"+���Ļ�);
				         	}
				            else
				            {//֪ͨ��ǰ�û����������Ļ������Ѿ�������
					             out.writeUTF("˽����������:"+toPeople+"�Ѿ�����");
					        }
						
					  }	
				      else if(s.startsWith("�����ļ�:"))
				      {
				    	  String message=s.substring(s.indexOf(":")+1);
				          int buffersize = 1024;
				    	  byte[] buf = new byte[buffersize];
				    	  int passedlen = 0;
				    	  long len = 0;
				    	  String filename = " ";
				    	  try
				    	  {
				    		  filename = in.readUTF();
				    		  len = in.readLong();
				    		  System.out.println("�ļ���Ϊ:" + filename);
				    		  System.out.println("�ļ�����Ϊ:" + len + "    KB");
				    		  System.out.println("��ʼת���ļ�!");
					    	  Enumeration enum1=peopleList.elements();                    //��ȡ���е���ͻ���ͨ�ŵķ������߳�
					    	  while(enum1.hasMoreElements())
							  {
					    		  Server_thread temp = (Server_thread)enum1.nextElement();
					    		  temp.out.writeUTF("�����ļ�:"+message);
					    		  temp.out.flush();
							      temp.out.writeUTF(filename);
							      temp.out.flush();
							      temp.out.writeLong(len);
							      temp.out.flush();
							  }
					    	  while(true)
					    	  {
					    		  int read = 0;
					    		  if(in != null)
					    		  {
					    			  read = in.read(buf);
					    		  }
					    		  if(read == -1)
					    		  {
					    			  break;
					    		  }
					    		  Enumeration enum2=peopleList.elements();                    //��ȡ���е���ͻ���ͨ�ŵķ������߳�
					    		  while(enum2.hasMoreElements())
					    		  {
					    			  ((Server_thread)enum2.nextElement()).out.write(buf, 0, read);
					    		  }
					    		  passedlen += read;
					    		  System.out.println("�ļ�ת����:" + (passedlen * 100 / len) + "%");
					    	  }
				    	  }
				    	  catch(IOException event)
				    	  {
				    	  }
				      }
				      else if(s.startsWith("˽���ļ�:"))
				      {
				    	  String message=s.substring(s.indexOf(":")+1,s.indexOf("#"));
				    	  String toPeople=s.substring(s.indexOf("#")+1);//�ҵ�Ҫ�����ļ����߳�
				    	  Server_thread toThread=(Server_thread)peopleList.get(toPeople);
				          if(toThread!=null)
				          {
				              toThread.out.writeUTF("˽���ļ�:"+message);
				          }
				          else
				          {//֪ͨ��ǰ�û����������Ļ������Ѿ�������
					          out.writeUTF("˽����������:"+toPeople+"�Ѿ�����");
					      }
				          int buffersize = 1024;
				    	  byte[] buf = new byte[buffersize];
				    	  int passedlen = 0;
				    	  long len = 0;
				    	  String filename = " ";
				    	  try
				    	  {
				    		  filename = in.readUTF();
				    		  len = in.readLong();
				    		  System.out.println("�ļ���Ϊ:" + filename);
				    		  System.out.println("�ļ�����Ϊ:" + len + "    KB");
				    		  System.out.println("��ʼת���ļ�!");
				    		  toThread.out.flush();
				    		  toThread.out.writeUTF(filename);
				    		  toThread.out.flush();
				    		  toThread.out.writeLong(len);
				    		  toThread.out.flush();
					    	  while(true)
					    	  {
					    		  int read = 0;
					    		  if(passedlen == len)
					    		  {
					    		      break;
					    	      }
					    		  if(in != null)
					    		  {
					    			  read = in.read(buf);
					    		  }
					    		  if(read == -1 || passedlen == len)
					    		  {
					    			  break;
					    		  }
					    		  toThread.out.write(buf, 0, read);
					    		  passedlen += read;
					    		  System.out.println("�ļ�ת����:" + (passedlen * 100 / len) + "%");
					    	  }
					    	  System.out.println("�ļ�ת�����");
				    	  }
				    	  catch(IOException event)
				    	  {
				    	  }
				      }
				}
			    catch(IOException ee)                               //�������߹ر��������������IOException
			   {
				    Enumeration enum1=peopleList.elements();         //��ȡ���е���ͻ���ͨ�ŵķ������߳�
				
				    while(enum1.hasMoreElements())                   //֪ͨ���������ߣ����û�����
				    {
					   try
					   {
					    	Server_thread th=(Server_thread)enum1.nextElement();
						    if(th!=this&&th.isAlive())
						    {
							     th.out.writeUTF("�û�����:"+name);
							}
						}
					    catch(IOException eee)
					    {
						}	
					}
				    if(name!=null)
				         peopleList.remove(name);
				    try                                              //�رյ�ǰ����
				    {
					    socket.close();
				    }	
				    catch(IOException eee)
				    {
				    }
			        System.out.println(name+"�û��뿪��");
			        break;	                                            //�������̵߳Ĺ������߳�����
			  }
				
		  }
	 }	

}
		