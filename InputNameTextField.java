
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.io.*;
import java.util.Hashtable;

public class InputNameTextField extends Panel implements ActionListener,Runnable
{
	TextField nameFile=null;                        //���������û��ǳƵ��ı���
	String name=null;                               
	Checkbox male=null,female=null;                 //ѡ���Ա�ĵ�ѡ��
	CheckboxGroup group=null;
	Button ����������=null,�˳�������=null;
	Socket socket=null;                             //�ͷ��������ӵ��׽���
	DataInputStream in=null;                        //��ȡ��������������Ϣ
	DataOutputStream out=null;                      //�������������Ϣ
	Thread thread=null;                             //�����ȡ��������������Ϣ���߳�
	boolean �ܷ�����=false;
	Hashtable listTable;                            //��������������ǳƵ�ɢ�б�
	
	public InputNameTextField(Hashtable listTable)
	{
		this.listTable=listTable;
		nameFile=new TextField(10);
		group=new CheckboxGroup();
		male=new Checkbox("��",true,group);
		female=new Checkbox("Ů",false,group);
		����������=new Button("����");
		�˳�������=new Button("�˳�");
		����������.addActionListener(this);
		�˳�������.addActionListener(this);
		thread=new Thread(this);
		add(new Label("�ǳ�: "));
		add(nameFile);
		add(male);
		add(female);
		add(����������);
		add(�˳�������);
		�˳�������.setEnabled(false);
	}
		
	public void set�ܷ�����(boolean b)
	{
		�ܷ�����=b;
	}
	public boolean get�ܷ�����()
	{
		return �ܷ�����; 
	}
	public String getName()
	{
		return name;
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
		try{
			thread.start();
		}
		catch(Exception e)
		{
			nameFile.setText(" "+e);
		}	
	}
    public Socket getSocket()
    {
    	return socket;
    }		
    
    public void actionPerformed(ActionEvent e)
    {
    	if(e.getSource()==����������)
    	{
    		�˳�������.setEnabled(true);
    		if(�ܷ�����==true)
    		{
               nameFile.setText(name+"����������!");  
    		}
    		else
    		{
    			this.setName(nameFile.getText());
    			String sex=group.getSelectedCheckbox().getLabel();
    			if(socket!=null&&name!=null)
    			{//���û���Ϣ���͸���������
    				try
    				{
    					out.writeUTF("�ǳ�:"+name+"�Ա�:"+sex);
    				}
    				catch(IOException ee)
    				{
    					nameFile.setText("�޷���ͨ������"+ee);
    				}
    			 }
    		  }	
    	  }
    	  if(e.getSource()==�˳�������)
    	  {//֪ͨ�������û��Ѿ��뿪
    		  try
    		  {
    			  out.writeUTF("�û��뿪:");
    		  }
    		  catch(IOException ee)
    		  {
    		  }	
    	 }
    }	
    public void run()
    {
    	String message=null;
    	while(true)
    	{//���ܷ�������������Ϣ�������з�������
    		  if(in!=null)
    		  {
    		  	  try
    		  	  {
    		  		  message=in.readUTF();
    		  	  }
    		  	  catch(IOException e)
    		  	  {
    		  		   nameFile.setText("�ͷ����������ѶϿ�"+e);
    		  	  }
    	      }	
    		  if(message!=null)
    		  {
    			  if(message.startsWith("��������:"))
                  {
        	            �ܷ�����=true;
        	            break;
        	      }	
                  else if(message.startsWith("������:"))
                  {
        	          String people=message.substring(message.indexOf(":")+1);    //��Ŀǰ���ߵ��������ǳ���ӵ�ɢ�б���
        	          listTable.put(people,people);
        	      }		  	
                  else if(message.startsWith("����������:"))
                  {
        	           �ܷ�����=false;
        	           nameFile.setText("���ǳ��ѱ�ռ��,��ѡ�������ǳ�.");
        	      }
    		  }
             
    	}
    }
}