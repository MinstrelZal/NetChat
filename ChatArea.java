
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.applet.*;
import java.util.Hashtable;

public class ChatArea extends Panel implements ActionListener,Runnable
{
	Socket socket=null;                            //�ͷ������佨�������ӵ��׽���
	DataInputStream in=null;                       //��ȡ��������Ϣ��������
	DataOutputStream out=null;                     //�������������Ϣ�������
	Thread threadMessage=null;                     //��ȡ��������Ϣ���߳�
	TextArea ̸����ʾ��,˽����ʾ��=null;
	TextField �ͳ���Ϣ=null, �ļ�·��=null;
	Button ȷ��,ˢ��̸����,ˢ��˽����, �����ļ�;
	Label ��ʾ��=null;
	String name=null;                              //�����ߵ��ǳ�
	Hashtable listTable;                           //��������������ǳƵ�ɢ�б�
	List listComponent=null;                       //��ʾ�����������ǳƵĵ�List���
	Choice privateChatList;                        //ѡ��˽�������ߵ������б�
	int width,height;                              //�������Ŀ�͸�
	String savepath = "C:\\Users\\188\\Desktop\\test\\";     //�ļ��洢·��
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
		̸����ʾ��=new TextArea(10,10);
		˽����ʾ��=new TextArea(10,10);
		ȷ��=new Button("�ͳ���Ϣ����");
		�����ļ�=new Button("�����ļ�����");
		ˢ��̸����=new Button("ˢ��̸����");
		ˢ��˽����=new Button("ˢ��˽����");
		��ʾ��=new Label("˫�������߿�˽��",Label.CENTER);
		�ͳ���Ϣ=new TextField(28);
		�ļ�·��=new TextField(28);
		ȷ��.addActionListener(this);
		�����ļ�.addActionListener(this);
		�ͳ���Ϣ.addActionListener(this);
		�ļ�·��.addActionListener(this);
		ˢ��̸����.addActionListener(this);
		ˢ��˽����.addActionListener(this);
		listComponent=new List();
		listComponent.addActionListener(this);             //˫���б��е������ߵ��ǳƣ���ѡ����֮˽��
		
		privateChatList=new Choice();
		privateChatList.add("���(*)");
		privateChatList.select(0);                          //Ĭ������£��û��������ݷ��͸�����������
		
		add(̸����ʾ��);
		̸����ʾ��.setBounds(10,10,(width-120)/2,(height-120));
		add(˽����ʾ��);
		˽����ʾ��.setBounds(10+(width-120)/2,10,(width-120)/2,(height-120));
		add(listComponent);
		listComponent.setBounds(10+(width-120),10,100,(height-160));
		add(��ʾ��);
		��ʾ��.setBounds(10+(width-120),10+(height-160),110,60);
		Panel pSouth=new Panel();
		pSouth.add(�ͳ���Ϣ);
		pSouth.add(ȷ��);
		pSouth.add(privateChatList);
		pSouth.add(ˢ��̸����);
		pSouth.add(ˢ��˽����);
		add(pSouth);
		pSouth.setBounds(10,20+(height-120),width-20,40);
		Panel pSouth2=new Panel();
		pSouth2.add(�ļ�·��);
		pSouth2.add(�����ļ�);
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
		try                                   //�����̣߳����ܷ�������Ϣ
		{
			threadMessage.start();
		}
		catch(Exception e)
		{
		}
	}
	public void actionPerformed(ActionEvent e)
	{
		  if(e.getSource()==ȷ��||e.getSource()==�ͳ���Ϣ)
		  {
		   	  String message=" ";
		   	  String people=privateChatList.getSelectedItem();
		   	  people=people.substring(0,people.indexOf("("));    //��ȡ��Ϣ���Ͷ�����ǳ�
		   	
		   	  message=�ͳ���Ϣ.getText();
		   	  if(message.length()>0)
		      {//����������ݼ������͸�������
		   		  try
		   		  {
		   			  if(people.equals("���"))
		   			  {
		   				  out.writeUTF("������������:"+name+"˵:"+"\n"+"   "+message);
		   			  }
		   			  else
		   			  {
		   				  out.writeUTF("˽����������:"+name+"���ĵ�˵:"+"\n"+"   "+message+"#"+people);
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
		    else if(e.getSource()==ˢ��̸����)
		    {
		     	̸����ʾ��.setText(null);
		   	}
		   	else if(e.getSource()==ˢ��˽����)
		   	{
		   		˽����ʾ��.setText(null);
		   	}
		   	else if(e.getSource()==�����ļ�||e.getSource()==�ļ�·��)
		   	{
		   		String filepath=" ";
			   	String people=privateChatList.getSelectedItem();
			   	people=people.substring(0,people.indexOf("("));    //��ȡ��Ϣ���Ͷ�����ǳ�
			   	
			   	filepath=�ļ�·��.getText();
			   	if(filepath.length()>0)
			    {//����������ݼ������͸�������
			   		try
			   		{
			   			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
			   			File file = new File(filepath);
				   		int buffersize = 1024;
				   		byte[]bufArray=new byte[buffersize];
			   			if(people.equals("���"))
			   			{
			   				out.writeUTF("�����ļ�:"+name+"�������ļ�:"+"\n"+"   "+file.getName());
			   			}
			   			else
			   			{
			   				out.writeUTF("˽���ļ�:"+name+"���ĵط������ļ�:"+"\n"+"   "+file.getName()+"#"+people);
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
				s=in.readUTF();                           //�ȴ����������̣߳�ֱ���յ���Ϣ����������Ϣ
				if(s.startsWith("������������:"))         //��ȡ��������������Ϣ
				{
					String content=s.substring(s.indexOf(":")+1);
					̸����ʾ��.append("\n"+content);
				}
				if(s.startsWith("˽����������:"))         //��ȡ��������������Ϣ
				{
					String content=s.substring(s.indexOf(":")+1);
					˽����ʾ��.append("\n"+content);
				}	
				else if(s.startsWith("������:"))
				{//��ʾ�¼���������ߵ���Ϣ
					String people=s.substring(s.indexOf(":")+1,s.indexOf("�Ա�"));
					String sex=s.substring(s.indexOf("�Ա�")+2);//�Ƚ��ǳƺ��Ա��ŵ�ɢ�б��ؼ����ǳƣ�
					listTable.put(people,people+"("+sex+")");
					listComponent.add((String)listTable.get(people));
					listComponent.repaint();                    //ˢ��List�������ʾ���û��ǳ�
				}	
				else if(s.startsWith("�û�����:"))
				{//ɾ�������ߵ���������Ϣ
					String awayPeopleName=s.substring(s.indexOf(":")+1);
					listComponent.remove((String)listTable.get(awayPeopleName));
					listComponent.repaint();
					̸����ʾ��.append("\n"+(String)listTable.get(awayPeopleName)+"����");
					listTable.remove(awayPeopleName);
				}
				else if(s.startsWith("�����ļ�:"))
				{
					String content = s.substring(s.indexOf(":")+1);
					̸����ʾ��.append("\n"+content);
					int buffersize = 1024;
			    	byte[] buf = new byte[buffersize];
			    	int passedlen = 0;
			    	long len = 0;
			    	String filename = " ";
			    	filename = in.readUTF();
		    		len = in.readLong();
		    		System.out.println("�ļ���Ϊ:" + filename);
		    		System.out.println("�ļ�����Ϊ:" + len + "    KB");
		    		System.out.println("��ʼ�����ļ�!");
		    		content = "�ļ���Ϊ:" + filename;
		    		̸����ʾ��.append("\n"+content);
		    		content = "�ļ�����Ϊ:" + len + "    KB";
		    		̸����ʾ��.append("\n"+content);
		    		content = "��ʼ�����ļ�!";
		    		̸����ʾ��.append("\n"+content);
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
				    		System.out.println("�ļ�������:" + (passedlen * 100 / len) + "%");
				    		content = "�ļ�������:" + (passedlen * 100 / len) + "%";
				    		̸����ʾ��.append("\n"+content);
			    		}
			    		System.out.println("�������, �ļ����Ϊ" + savefile);
			    		content = "�������, �ļ����Ϊ" + savefile;
			    		̸����ʾ��.append("\n"+content);
			    		dos.close();
			    	}
			    	catch(Exception e)
			    	{
			    	}
				}
				else if(s.startsWith("˽���ļ�:"))
				{
					String content = s.substring(s.indexOf(":")+1);
					˽����ʾ��.append("\n"+content);
					int buffersize = 1024;
			    	byte[] buf = new byte[buffersize];
			    	int passedlen = 0;
			    	long len = 0;
			    	String filename = " ";
			    	filename = in.readUTF();
		    		len = in.readLong();
		    		System.out.println("�ļ���Ϊ:" + filename);
		    		System.out.println("�ļ�����Ϊ:" + len + "    KB");
		    		System.out.println("��ʼ�����ļ�!");
		    		content = "�ļ���Ϊ:" + filename;
		    		˽����ʾ��.append("\n"+content);
		    		content = "�ļ�����Ϊ:" + len + "    KB";
		    		˽����ʾ��.append("\n"+content);
		    		content = "��ʼ�����ļ�!";
		    		˽����ʾ��.append("\n"+content);
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
				    		System.out.println("�ļ�������:" + (passedlen * 100 / len) + "%");
				    		content = "�ļ�������:" + (passedlen * 100 / len) + "%";
				    		˽����ʾ��.append("\n"+content);
			    		}
			    		System.out.println("�������, �ļ����Ϊ" + savefile);
			    		content = "�������, �ļ����Ϊ" + savefile;
			    		˽����ʾ��.append("\n"+content);
			    		dos.close();
			    	}
			    	catch(Exception e)
			    	{
			    	}
				}
				Thread.sleep(5);
			}
			catch(IOException e)                                  //�������ر��׽�������ʱ������IOException
			{
				listComponent.removeAll();
				listComponent.repaint();
				listTable.clear();
				̸����ʾ��.setText("�ͷ������������ѶϿ�\n����ˢ������������ٴν��������ҡ�");
				break;
			}	
			catch(InterruptedException e)
			{
			}	
		}
	}
}