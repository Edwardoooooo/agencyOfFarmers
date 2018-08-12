package scau.zxck.utils;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

public class SendEmailUtil implements Runnable {
    private String email;// 收件人邮箱
    private String code;// 激活码
    private  String content;//网址
    private String from;//自己的邮箱，拿来玩的
    private String accreditCode;
    public void setAccreditCode(String accreditCode) {
        this.accreditCode = accreditCode;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public SendEmailUtil(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public  String getContent() {
        return content;
    }

    public void run() {
//        System.out.println("email:"+email+" "+"code:"+" "+code);
        // 1.创建连接对象javax.mail.Session
        // 2.创建邮件对象 javax.mail.Message
        // 3.发送一封激活邮件
//        from = "1769969562@qq.com";// 发件人电子邮箱
        String host = "smtp.qq.com"; // 指定发送邮件的主机smtp.qq.com(QQ)|smtp.163.com(网易)

        Properties properties = System.getProperties();// 获取系统属性

        properties.setProperty("mail.smtp.host", host);// 设置邮件服务器
        properties.setProperty("mail.smtp.auth", "true");// 打开认证

        try {
            //QQ邮箱需要下面这段代码，163邮箱不需要
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            // 1.获取默认session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, accreditCode); // 发件人邮箱账号、授权码
                }
            });
            // 2.创建邮件对象
            Message message = new MimeMessage(session);
            // 2.1设置发件人
            message.setFrom(new InternetAddress(from));
            // 2.2设置接收人
            InternetAddress internetAddress = new InternetAddress(email);
            message.addRecipient(Message.RecipientType.TO, internetAddress);
            // 2.3设置邮件主题
            message.setSubject("账号激活");
            // 2.4设置邮件内容
            content = "<html><head></head><body><h1>这是一封重置密码邮件,请点击以下链接获取你的新密码，邮件将在10分钟后失效！！！</h1><h3><a href='http://127.0.0.1:8080/ResetPassword/ActiveServlet?code="
                    + code + "'>http://localhost:8080/ResetPassword/ActiveServlet?code=" + code +".com"
                    + "</href></h3></body></html>";
            message.setContent(content, "text/html;charset=UTF-8");
            // 3.发送邮件
            Transport.send(message);
            System.out.println("邮件成功发送!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}