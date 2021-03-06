
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.applet.*;
import java.util.Hashtable;

public class ChatArea extends Panel implements ActionListener,Runnable
{
	Socket socket=null;                            //和服务器间建立的连接的套接字
	DataInputStream in=null;                       //读取服务器信息的输入流
	DataOutputStream out=null;                     //向服务器发送信息的输出流
	Thread threadMessage=null;                     //读取服务器信息的线程
	TextArea 谈话显示区,私聊显示区=null;
	TextField 送出消息=null, 文件路径=null;
	Button 确定,刷新谈话区,刷新私聊区, 发送文件;
	Label 提示条=null;
	String name=null;                              //聊天者的昵称
	Hashtable listTable;                           //存放在线聊天者昵称的散列表
	List listComponent=null;                       //显示在线聊天者昵称的的List组件
	Choice privateChatList;                        //选择私人聊天者的下拉列表
	int width,height;                              //聊天区的宽和高
	String savepath = "C:\\Users\\188\\Desktop\\test\\";     //文件存储路径
	 public ChatArea(String name,Hashtable listTable,int width,int height)
	{
		setLayout(null);
		setBackground(Color.magenta);
		this.width=width;
		this.height=height;
		setSize(width,height);
		this.listTable=listTable;
		this.name=name;
		threadMessage=new Thread(this);
		谈话显示区=new TextArea(10,10);
		私聊显示区=new TextArea(10,10);
		确定=new Button("送出信息到：");
		发送文件=new Button("发送文件到：");
		刷新谈话区=new Button("刷新谈话区");
		刷新私聊区=new Button("刷新私聊区");
		提示条=new Label("双击聊天者可私聊",Label.CENTER);
		送出消息=new TextField(28);
		文件路径=new TextField(28);
		确定.addActionListener(this);
		发送文件.addActionListener(this);
		送出消息.addActionListener(this);
		文件路径.addActionListener(this);
		刷新谈话区.addActionListener(this);
		刷新私聊区.addActionListener(this);
		listComponent=new List();
		listComponent.addActionListener(this);             //双击列表中的聊天者的昵称，可选中与之私聊
		
		privateChatList=new Choice();
		privateChatList.add("大家(*)");
		privateChatList.select(0);                          //默认情况下，用户输入内容发送给所有聊天者
		
		add(谈话显示区);
		谈话显示区.setBounds(10,10,(width-120)/2,(height-120));
		add(私聊显示区);
		私聊显示区.setBounds(10+(width-120)/2,10,(width-120)/2,(height-120));
		add(listComponent);
		listComponent.setBounds(10+(width-120),10,100,(height-160));
		add(提示条);
		提示条.setBounds(10+(width-120),10+(height-160),110,60);
		Panel pSouth=new Panel();
		pSouth.add(送出消息);
		pSouth.add(确定);
		pSouth.add(privateChatList);
		pSouth.add(刷新谈话区);
		pSouth.add(刷新私聊区);
		add(pSouth);
		pSouth.setBounds(10,20+(height-120),width-20,40);
		Panel pSouth2=new Panel();
		pSouth2.add(文件路径);
		pSouth2.add(发送文件);
		add(pSouth2);
		pSouth2.setBounds(10,60+(height-120),width-250,40);
	}
		
	public void setName(String s)
	{
		name=s;
	}
	public void setSocketConnection(Socket socket,DataInputStream in,DataOutputStream out)
	{
		this.socket=socket;
		this.in=in;
		this.out=out;
		try                                   //启动线程，接受服务器信息
		{
			threadMessage.start();
		}
		catch(Exception e)
		{
		}
	}
	public void actionPerformed(ActionEvent e)
	{
		  if(e.getSource()==确定||e.getSource()==送出消息)
		  {
		   	  String message=" ";
		   	  String people=privateChatList.getSelectedItem();
		   	  people=people.substring(0,people.indexOf("("));    //获取信息发送对象的昵称
		   	
		   	  message=送出消息.getText();
		   	  if(message.length()>0)
		      {//将聊天的内容及对象发送给服务器
		   		  try
		   		  {
		   			  if(people.equals("大家"))
		   			  {
		   				  out.writeUTF("公共聊天内容:"+name+"说:"+"\n"+"   "+message);
		   			  }
		   			  else
		   			  {
		   				  out.writeUTF("私人聊天内容:"+name+"悄悄地说:"+"\n"+"   "+message+"#"+people);
		   			  }
		   		  }
		   		  catch(IOException event)
		   		  {
		   		  }
		   	   }
            }	
		   	else if(e.getSource()==listComponent)
		   	{
		   		privateChatList.insert(listComponent.getSelectedItem(),0);
		   		privateChatList.repaint();
		   	}
		    else if(e.getSource()==刷新谈话区)
		    {
		     	谈话显示区.setText(null);
		   	}
		   	else if(e.getSource()==刷新私聊区)
		   	{
		   		私聊显示区.setText(null);
		   	}
		   	else if(e.getSource()==发送文件||e.getSource()==文件路径)
		   	{
		   		String filepath=" ";
			   	String people=privateChatList.getSelectedItem();
			   	people=people.substring(0,people.indexOf("("));    //获取信息发送对象的昵称
			   	
			   	filepath=文件路径.getText();
			   	if(filepath.length()>0)
			    {//将聊天的内容及对象发送给服务器
			   		try
			   		{
			   			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
			   			File file = new File(filepath);
				   		int buffersize = 1024;
				   		byte[]bufArray=new byte[buffersize];
			   			if(people.equals("大家"))
			   			{
			   				out.writeUTF("公共文件:"+name+"发送了文件:"+"\n"+"   "+file.getName());
			   			}
			   			else
			   			{
			   				out.writeUTF("私人文件:"+name+"悄悄地发送了文件:"+"\n"+"   "+file.getName()+"#"+people);
			   			}
			   			out.flush();
		   				out.writeUTF(file.getName());
				   		out.flush();
				   		out.writeLong((long) file.length());
				   		out.flush();
				   		while(true)
				   		{
				   			int read = 0;
				   			if(dis != null)
				   			{
				   				read = dis.read(bufArray);
				   			}
				   			if(read == -1)
				   			{
				   				break;
				   			}
				   			out.write(bufArray, 0, read);
				   		}
		   				out.flush();
			   			try
			   			{
			   				if(dis != null)
			   				{
			   					dis.close();
			   				}
			   			}
			   			catch(IOException event)
			   			{
			   			}
			   		  }
			   		  catch(FileNotFoundException event)
			   		  { 
			   		  }
			   		  catch(IOException event)
			   		  {
			   		  }
			   	 }
		   	}
	 }	
	 public void run()
	 {
		 while(true)
		 {
			String s=null;
			try
			{
				s=in.readUTF();                           //等待（阻塞本线程，直到收到信息）服务器信息
				if(s.startsWith("公共聊天内容:"))         //读取服务器发来的信息
				{
					String content=s.substring(s.indexOf(":")+1);
					谈话显示区.append("\n"+content);
				}
				if(s.startsWith("私人聊天内容:"))         //读取服务器发来的信息
				{
					String content=s.substring(s.indexOf(":")+1);
					私聊显示区.append("\n"+content);
				}	
				else if(s.startsWith("聊天者:"))
				{//显示新加入的聊天者的信息
					String people=s.substring(s.indexOf(":")+1,s.indexOf("性别"));
					String sex=s.substring(s.indexOf("性别")+2);//先将昵称和性别存放到散列表（关键是昵称）
					listTable.put(people,people+"("+sex+")");
					listComponent.add((String)listTable.get(people));
					listComponent.repaint();                    //刷新List组件，显示新用户昵称
				}	
				else if(s.startsWith("用户离线:"))
				{//删除已离线的聊天者信息
					String awayPeopleName=s.substring(s.indexOf(":")+1);
					listComponent.remove((String)listTable.get(awayPeopleName));
					listComponent.repaint();
					谈话显示区.append("\n"+(String)listTable.get(awayPeopleName)+"离线");
					listTable.remove(awayPeopleName);
				}
				else if(s.startsWith("公共文件:"))
				{
					String content = s.substring(s.indexOf(":")+1);
					谈话显示区.append("\n"+content);
					int buffersize = 1024;
			    	byte[] buf = new byte[buffersize];
			    	int passedlen = 0;
			    	long len = 0;
			    	String filename = " ";
			    	filename = in.readUTF();
		    		len = in.readLong();
		    		System.out.println("文件名为:" + filename);
		    		System.out.println("文件长度为:" + len + "    KB");
		    		System.out.println("开始接收文件!");
		    		content = "文件名为:" + filename;
		    		谈话显示区.append("\n"+content);
		    		content = "文件长度为:" + len + "    KB";
		    		谈话显示区.append("\n"+content);
		    		content = "开始接收文件!";
		    		谈话显示区.append("\n"+content);
		    		String savefile = this.savepath + filename;
		    		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savefile))));
			    	try
			    	{
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
				    		if(read == -1)
				    		{
				    			break;
				    		}
				    		dos.write(buf, 0, read);
				    		passedlen += read;
				    		System.out.println("文件接收了:" + (passedlen * 100 / len) + "%");
				    		content = "文件接收了:" + (passedlen * 100 / len) + "%";
				    		谈话显示区.append("\n"+content);
			    		}
			    		System.out.println("接收完成, 文件另存为" + savefile);
			    		content = "接收完成, 文件另存为" + savefile;
			    		谈话显示区.append("\n"+content);
			    		dos.close();
			    	}
			    	catch(Exception e)
			    	{
			    	}
				}
				else if(s.startsWith("私人文件:"))
				{
					String content = s.substring(s.indexOf(":")+1);
					私聊显示区.append("\n"+content);
					int buffersize = 1024;
			    	byte[] buf = new byte[buffersize];
			    	int passedlen = 0;
			    	long len = 0;
			    	String filename = " ";
			    	filename = in.readUTF();
		    		len = in.readLong();
		    		System.out.println("文件名为:" + filename);
		    		System.out.println("文件长度为:" + len + "    KB");
		    		System.out.println("开始接收文件!");
		    		content = "文件名为:" + filename;
		    		私聊显示区.append("\n"+content);
		    		content = "文件长度为:" + len + "    KB";
		    		私聊显示区.append("\n"+content);
		    		content = "开始接收文件!";
		    		私聊显示区.append("\n"+content);
		    		String savefile = this.savepath + filename;
		    		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savefile))));
			    	try
			    	{
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
				    		if(read == -1)
				    		{
				    			break;
				    		}
				    		dos.write(buf, 0, read);
				    		passedlen += read;
				    		System.out.println("文件接收了:" + (passedlen * 100 / len) + "%");
				    		content = "文件接收了:" + (passedlen * 100 / len) + "%";
				    		私聊显示区.append("\n"+content);
			    		}
			    		System.out.println("接收完成, 文件另存为" + savefile);
			    		content = "接收完成, 文件另存为" + savefile;
			    		私聊显示区.append("\n"+content);
			    		dos.close();
			    	}
			    	catch(Exception e)
			    	{
			    	}
				}
				Thread.sleep(5);
			}
			catch(IOException e)                                  //服务器关闭套接字连接时，导致IOException
			{
				listComponent.removeAll();
				listComponent.repaint();
				listTable.clear();
				谈话显示区.setText("和服务器的连接已断开\n必须刷新浏览器才能再次进入聊天室。");
				break;
			}	
			catch(InterruptedException e)
			{
			}	
		}
	}
}