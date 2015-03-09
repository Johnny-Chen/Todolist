package com.liujiang.todolist.network;

import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnNet
{
    /*private static final String URLVAR="http://10.110.5.38:8080/LoginRegister/";*/
    /*private static final String URLVAR="http://192.168.8.100:8080/LoginRegister/";*/
    private static final String URLVAR="http://1.haiguotuzhi.sinaapp.com/";
    //��·������Ϊһ���������޸ĵ�ʱ��Ҳ�ø���
    //ͨ��url��ȡ��������  connection
    public HttpURLConnection getConn(String urlpath)
    {
        String finalurl=URLVAR+urlpath;
        HttpURLConnection connection = null;
        try {
            URL url=new URL(finalurl);
            connection=(HttpURLConnection) url.openConnection();
            connection.setDoInput(true);  //����������
            connection.setDoOutput(true); //���������
            connection.setUseCaches(false);  //������ʹ�û���
            connection.setRequestMethod("POST");  //����ʽ
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return connection;

    }
    public HttpPost gethttPost(String uripath)
    {
        HttpPost httpPost=new HttpPost(URLVAR+uripath);

        System.out.println(URLVAR+uripath);
        return httpPost;
    }

}
