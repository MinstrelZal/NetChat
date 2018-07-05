
import java.awt.*;
import java.io.*;
import java.net.*;
import java.applet.*;
import java.util.Hashtable;

public class ClientChat extends Applet implements Runnable
{
	public static final int DEFAULT_WEIDH=820;
	public static final int DEFAULT_HEIGHT=520;
	Socket socket=null;
	DataInputStream in=null;
	DataOutputStream out=null;
	InputNameTextField �û��ύ�ǳƽ���=null;
	ChatArea �û��������=null;
	Hashtable listTable;                           //��������������ǳƵ�ɢ�б�
	Label ��ʾ��;
	Panel north,center;
	Thread thread;
	public void init()
	{
		setSize(DEFAULT_WEIDH,DEFAULT_HEIGHT);
//		int width=getSize().width;                 //��ȡjava appletС����Ŀ�
//		int height=getSize().height;               //��ȡjava appletС����ĸ�
		int width=800;
		int height=500;

		listTable=new Hashtable();
		setLayout(new BorderLayout());
		�û��ύ�ǳƽ���=new InputNameTextField(listTable);
		int h=�û��ύ�ǳƽ���.getSize().height;
		�û��������=new ChatArea("",listTable,width,height-(h+5));
		�û��������.setVisible(true);
		��ʾ��=new Label("�������ӵ������������Ժ򣮣���",Label.CENTER);
		��ʾ��.setForeground(Color.red);
		north=new Panel(new FlowLayout(FlowLayout.LEFT));
		center=new Panel();
		north.add(�û��ύ�ǳƽ���);
		north.add(��ʾ��);
		center.add(�û��������);
		add(north,BorderLayout.NORTH);
		add(center,BorderLayout.CENTER);
		validate();
				}
	
	public void start()                             //�����ǰ���׽���
	{
		if(socket!=null&&in!=null&&out!=null)
		{
			try
			{
				socket.close();
				in.close();
				out.close();
				�û��������.setVisible(false);
			}
			catch(Exception ee)
			{
			}
		}
		try
		{
			socket=new Socket(this.getCodeBase().getHost(),6666);
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException ee)
		{
			��ʾ��.setText("����ʧ��");
		}
		if(socket!=null)                             //������ӳɹ�������ʾ�û������ǳ�
		{
			InetAddress address=socket.getInetAddress();
			��ʾ��.setText("���ӣ�"+address+"�ɹ�");
			�û��ύ�ǳƽ���.setSocketConnection(socket,in,out);
			north.validate();
		}	
		if(thread==null)                            //Ϊ���û�����һ�����߳�
	    {
			thread=new Thread(this);
		    thread.start();
		} 	
    }
	public void stop()
	{
		try
		{
			socket.close();
			thread=null;
		}
		catch(IOException e)
		{
			this.showStatus(e.toString());
		}
	}
	public void run()
	{
		while(thread!=null)
		{
			if(�û��ύ�ǳƽ���.get�ܷ�����()==true)
			{
				�û��������.setVisible(true);
				�û��������.setName(�û��ύ�ǳƽ���.getName());
				�û��������.setSocketConnection(socket,in,out);
				��ʾ��.setText("ף������죡");
				center.validate();
				break;
			}
			try
			{
				Thread.sleep(100);
			}			
			catch(Exception e)
			{
			}	
		}
	}
}
