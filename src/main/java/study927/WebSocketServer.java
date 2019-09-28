package study927;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/hello")
public class WebSocketServer {
//静态变量：用来记录当前在线连接数，应该把他设计成线程安全的
private static int onlineCount = 0;
private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
//与某个客户的连接会话，
private Session session;
/*
 * 连接成功时调用的方法，
 * */
@OnOpen
public void onOpen(Session session) {
	this.session = session;
	webSocketSet.add(this);
	addOnlineCount();
	System.out.println("有人连接加入：当前在线人数为"+getOnlineCount());
}
@OnClose
public void onClose() {
	webSocketSet.remove(this);
	subOnlineCount();
	System.out.println("有一个连接关闭：当前在线人数为"+getOnlineCount());
}
//不管是哪个客户端向服务器请求消息，都会进行调用这个方法
@OnMessage
public void onMessage(String message,Session session) {
	System.out.println("来自客户端的消息："+message);
	//群发消息
	for(WebSocketServer item:webSocketSet) {
		try {
			System.out.println("向客户端发送的消息："+message);
			item.sendMessage(message);
		}catch(IOException e) {
			e.printStackTrace();
			continue;
		}
	}
}
//连接错误时调用的方法
@OnError
public void onError(Session session,Throwable error) {
	System.out.println("发生错误");
	error.printStackTrace();
}


//发送消息
public void sendMessage(String message) throws IOException{
	this.session.getBasicRemote().sendText(message);
}
//获取在线人数
public static synchronized int getOnlineCount() {
	return onlineCount;
}
//人数每调用一次就加一
public static synchronized void addOnlineCount() {
	WebSocketServer.onlineCount++;
}
//人数减一
public static synchronized void subOnlineCount() {
	WebSocketServer.onlineCount--;
}
}
