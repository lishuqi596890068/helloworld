package study927;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/hello")
public class WebSocketServer {
//��̬������������¼��ǰ������������Ӧ�ð�����Ƴ��̰߳�ȫ��
private static int onlineCount = 0;
private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
//��ĳ���ͻ������ӻỰ��
private Session session;
/*
 * ���ӳɹ�ʱ���õķ�����
 * */
@OnOpen
public void onOpen(Session session) {
	this.session = session;
	webSocketSet.add(this);
	addOnlineCount();
	System.out.println("�������Ӽ��룺��ǰ��������Ϊ"+getOnlineCount());
}
@OnClose
public void onClose() {
	webSocketSet.remove(this);
	subOnlineCount();
	System.out.println("��һ�����ӹرգ���ǰ��������Ϊ"+getOnlineCount());
}
//�������ĸ��ͻ����������������Ϣ��������е����������
@OnMessage
public void onMessage(String message,Session session) {
	System.out.println("���Կͻ��˵���Ϣ��"+message);
	//Ⱥ����Ϣ
	for(WebSocketServer item:webSocketSet) {
		try {
			System.out.println("��ͻ��˷��͵���Ϣ��"+message);
			item.sendMessage(message);
		}catch(IOException e) {
			e.printStackTrace();
			continue;
		}
	}
}
//���Ӵ���ʱ���õķ���
@OnError
public void onError(Session session,Throwable error) {
	System.out.println("��������");
	error.printStackTrace();
}


//������Ϣ
public void sendMessage(String message) throws IOException{
	this.session.getBasicRemote().sendText(message);
}
//��ȡ��������
public static synchronized int getOnlineCount() {
	return onlineCount;
}
//����ÿ����һ�ξͼ�һ
public static synchronized void addOnlineCount() {
	WebSocketServer.onlineCount++;
}
//������һ
public static synchronized void subOnlineCount() {
	WebSocketServer.onlineCount--;
}
}
