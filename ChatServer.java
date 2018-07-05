
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
		Hashtable peopleList;                                 //存放与各聊天者客户端通信的服务器线程的散列表
		
		peopleList=new Hashtable();
		while(true)
		{
			try
			{
				server=new ServerSocket(6666);
			}
			catch(IOException e1)
			{
				System.out.println("正在监听");
			}
			try
			{
				you=server.accept();                                //建立和客户端的连接的套接字
				InetAddress address=you.getInetAddress();
				System.out.println("用户的IP："+address);
			}	
			catch(IOException e)
			{
			}
			if(you!=null)
			{
				Server_thread peopleThread=new Server_thread(you,peopleList);
				peopleThread.start();                                 //与该客户端通信的服务器，开始启动线程
			}	
			else{
				continue;
			}	
		}
	}
}
	
class Server_thread extends Thread
{
		String name=null,sex=null;                                   //聊天者的昵称和性别
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
				{//等待（阻塞本线程，直到收到信息）客户端发来的信息
					s=in.readUTF();
					if(s.startsWith("昵称:"))                    //如果用户提交了昵称和性别
					{
						name=s.substring(s.indexOf(":")+1,s.indexOf("性别"));    //截取用户信息中的昵称
						sex=s.substring(s.indexOf("性别")+2);                    //截取性别
						
						boolean boo=peopleList.containsKey(name);                //检查散列表中是否已有叫做该昵称的聊天者
				        if(boo==false)
				        {
							
							peopleList.put(name,this);                           //将当前线程添加到散列表，昵称作为关键字
							out.writeUTF("可以聊天:");
							Enumeration enum1=peopleList.elements();
							
							while(enum1.hasMoreElements())                         //获取所有的与客户端通信的服务器线程
							{
								Server_thread th=(Server_thread)enum1.nextElement();//将当前聊天者的昵称和性别通知所有的用户
								th.out.writeUTF("聊天者:"+name+"性别"+sex);        //也将其他聊天者的姓名通知本线程（当前用户）
								if(th!=this)
								{
									out.writeUTF("聊天者:"+th.name+"性别"+th.sex);
								}
							}
						}
						else
						{//如果该用户昵称已存在，提示用户重新输入
							out.writeUTF("不可以聊天:");
						}
					  }
					  else if(s.startsWith("公共聊天内容:"))
					  {
						 String message=s.substring(s.indexOf(":")+1);
						 Enumeration enum1=peopleList.elements();                    //获取所有的与客户端通信的服务器线程
						 while(enum1.hasMoreElements())
						 {
							((Server_thread)enum1.nextElement()).out.writeUTF("公共聊天内容:"+message);
						 }
					  }			
				      else if(s.startsWith("用户离开:"))
				      {
					       Enumeration enum1=peopleList.elements();               //获取所有的与客户端通信的服务器线程
					       while(enum1.hasMoreElements())                         //通知其他聊天者，该用户已离线
					       {
						       try
						       {
							        Server_thread th=(Server_thread)enum1.nextElement();
							        if(th!=this&&th.isAlive())
							        {
							        	th.out.writeUTF("用户离线:"+name);
							    	}
							   }
					           catch(IOException eee)
					           {
					 	       }		
					    	}
					       if(name!=null)
				                peopleList.remove(name);
				            socket.close();                                                          //关闭当前连接
				            System.out.println(name+"客户离开了");
				            break;		                                                             //结束本线程的工作，线程死亡
				      }
					
				     else if(s.startsWith("私人聊天内容:"))
				     {
				         	String 悄悄话=s.substring(s.indexOf(":")+1,s.indexOf("#"));
				         	String toPeople=s.substring(s.indexOf("#")+1);//找到要接受悄悄话的线程
					
					        Server_thread toThread=(Server_thread)peopleList.get(toPeople);
				            if(toThread!=null)
				            {
				                 toThread.out.writeUTF("私人聊天内容:"+悄悄话);
				         	}
				            else
				            {//通知当前用户，接受悄悄话的人已经离线了
					             out.writeUTF("私人聊天内容:"+toPeople+"已经离线");
					        }
						
					  }	
				      else if(s.startsWith("公共文件:"))
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
				    		  System.out.println("文件名为:" + filename);
				    		  System.out.println("文件长度为:" + len + "    KB");
				    		  System.out.println("开始转发文件!");
					    	  Enumeration enum1=peopleList.elements();                    //获取所有的与客户端通信的服务器线程
					    	  while(enum1.hasMoreElements())
							  {
					    		  Server_thread temp = (Server_thread)enum1.nextElement();
					    		  temp.out.writeUTF("公共文件:"+message);
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
					    		  Enumeration enum2=peopleList.elements();                    //获取所有的与客户端通信的服务器线程
					    		  while(enum2.hasMoreElements())
					    		  {
					    			  ((Server_thread)enum2.nextElement()).out.write(buf, 0, read);
					    		  }
					    		  passedlen += read;
					    		  System.out.println("文件转发了:" + (passedlen * 100 / len) + "%");
					    	  }
				    	  }
				    	  catch(IOException event)
				    	  {
				    	  }
				      }
				      else if(s.startsWith("私人文件:"))
				      {
				    	  String message=s.substring(s.indexOf(":")+1,s.indexOf("#"));
				    	  String toPeople=s.substring(s.indexOf("#")+1);//找到要接收文件的线程
				    	  Server_thread toThread=(Server_thread)peopleList.get(toPeople);
				          if(toThread!=null)
				          {
				              toThread.out.writeUTF("私人文件:"+message);
				          }
				          else
				          {//通知当前用户，接受悄悄话的人已经离线了
					          out.writeUTF("私人聊天内容:"+toPeople+"已经离线");
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
				    		  System.out.println("文件名为:" + filename);
				    		  System.out.println("文件长度为:" + len + "    KB");
				    		  System.out.println("开始转发文件!");
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
					    		  System.out.println("文件转发了:" + (passedlen * 100 / len) + "%");
					    	  }
					    	  System.out.println("文件转发完成");
				    	  }
				    	  catch(IOException event)
				    	  {
				    	  }
				      }
				}
			    catch(IOException ee)                               //当聊天者关闭浏览器，将导致IOException
			   {
				    Enumeration enum1=peopleList.elements();         //获取所有的与客户端通信的服务器线程
				
				    while(enum1.hasMoreElements())                   //通知其他聊天者，该用户离线
				    {
					   try
					   {
					    	Server_thread th=(Server_thread)enum1.nextElement();
						    if(th!=this&&th.isAlive())
						    {
							     th.out.writeUTF("用户离线:"+name);
							}
						}
					    catch(IOException eee)
					    {
						}	
					}
				    if(name!=null)
				         peopleList.remove(name);
				    try                                              //关闭当前连接
				    {
					    socket.close();
				    }	
				    catch(IOException eee)
				    {
				    }
			        System.out.println(name+"用户离开了");
			        break;	                                            //结束本线程的工作，线程死亡
			  }
				
		  }
	 }	

}
		