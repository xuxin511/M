package com.xx.chinetek.method.Mail;

import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * Created by GHOST on 2017/10/30.
 */

public class SendOneMail {
    private MailModel info;
    private String path;
    public SendOneMail(String path){
        this.path = path;
    }
    /**
     * 以文本格式发送邮件
     * @param info 待发送的邮件信息
     * @return
     */
    public void sendTextMail(MailModel info){
        this.info = info;
        //判断是否需要身份验证
        Properties properties = info.getProperties();
        //1、根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession  = Session.getDefaultInstance(properties);
        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        sendMailSession.setDebug(true);
        try {

            //2、通过session得到transport对象,以便连接邮箱并发送
            Transport transport = sendMailSession.getTransport();
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给SMTP服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            transport.connect("smtp.163.com", ".com", "11");
            //4、创建邮件消息
            Message mailMessage = createSimpleMail(sendMailSession);
            //5、发送邮件消息
            transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 发送内容带有图片的邮件
     */
    public void sendImageEmail(MailModel info){
        this.info = info;
        Properties properties = info.getProperties();
        //1、根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession  = Session.getDefaultInstance(properties);
        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        sendMailSession.setDebug(true);
        try {

            //2、通过session得到transport对象,以便连接邮箱并发送
            Transport transport = sendMailSession.getTransport();
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给SMTP服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            transport.connect("smtp.163.com", "1112222222.com", "3333333");
            //4、创建邮件消息
            Message mailMessage = createImageMail(sendMailSession);
            //5、发送邮件消息
            transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 发送带有附件的邮件
     * @param info
     */
    public void sendAttachment(MailModel info,ArrayList<String> list){
        this.info = info;
        Properties properties = info.getProperties();
        //1、根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession  = Session.getDefaultInstance(properties);
        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        sendMailSession.setDebug(true);
        try {
            //2、通过session得到transport对象,以便连接邮箱并发送
            Transport transport = sendMailSession.getTransport();
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给SMTP服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            transport.connect(info.getMailServerHost(), info.getAccount(), info.getPassword());
            //4、创建邮件消息
            Message mailMessage = createAttachmentMail(sendMailSession,list);
            //5、发送邮件消息
            transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送复杂邮件
     * @param info
     */
    public void sendMixedMail(MailModel info,ArrayList<String> list){

        this.info = info;
        Properties properties = info.getProperties();
        //1、根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession  = Session.getDefaultInstance(properties);
        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        sendMailSession.setDebug(true);
        try {
            //2、通过session得到transport对象,以便连接邮箱并发送
            Transport transport = sendMailSession.getTransport();
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给SMTP服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            transport.connect("smtp.163.com", "11122222222@163.com", "333333");
            //4、创建邮件消息
            Message mailMessage = createMixedMail(sendMailSession,list);
            //5、发送邮件消息
            transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建复杂的邮件（包括图片和多附件的邮件）
     * @param sendMailSession
     * @param list 是选中CSV文件的集合
     * @return
     */
    private Message createMixedMail(Session sendMailSession,ArrayList<String> list) {
        // 创建邮件
        MimeMessage message = null;
        try {
            message = new MimeMessage(sendMailSession);

            // 设置邮件的基本信息
            //创建邮件发送者地址
            Address from = new InternetAddress(info.getFromAddress());
            //设置邮件消息的发送者
            message.setFrom(from);
            //创建邮件的接受者地址，并设置到邮件消息中
            Address to = new InternetAddress(info.getToAddress());
            //设置邮件消息的接受者, Message.RecipientType.TO属性表示接收者的类型为TO
            message.setRecipient(Message.RecipientType.TO, to);
            //邮件标题
            message.setSubject(info.getSubject());

            // 正文
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(info.getContent(),
                    "text/html;charset=UTF-8");

			/*// 图片
			MimeBodyPart image = new MimeBodyPart();
			image.setDataHandler(new DataHandler(new FileDataSource(Environment.getExternalStorageDirectory().getPath()+"/猫咪.jpg")));
			image.setContentID("猫咪.jpg");*/

            // 附件1
            MimeBodyPart attach = new MimeBodyPart();
            DataHandler dh = new DataHandler(new FileDataSource(Environment.getExternalStorageDirectory().getPath()+"/zed_authorize.txt"));
            attach.setDataHandler(dh);
            attach.setFileName(dh.getName());

            // 附件2
            MimeBodyPart attach2 = new MimeBodyPart();
            DataHandler dh2 = new DataHandler(new FileDataSource(Environment.getExternalStorageDirectory().getPath()+"/error_log_z9.txt"));
            attach2.setDataHandler(dh2);
            attach2.setFileName(MimeUtility.encodeText(dh2.getName()));

			/*// 描述关系:正文和图片
			MimeMultipart mp1 = new MimeMultipart();
			mp1.addBodyPart(image);
			mp1.setSubType("related");*/

            // 描述关系:正文和附件
            MimeMultipart mp2 = new MimeMultipart();
            mp2.addBodyPart(text);
            mp2.addBodyPart(attach);
            mp2.addBodyPart(attach2);

			/*// 代表正文的BodyPart
			MimeBodyPart content = new MimeBodyPart();
			content.setContent(mp1);
			mp2.addBodyPart(content);*/

            mp2.setSubType("mixed");
            message.setContent(mp2);
            message.saveChanges();

            // 将创建的Email写入到E盘存储
            //message.writeTo(new FileOutputStream("E:\\MixedMail.eml"));
        } catch (Exception e) {
            Log.e("TAG", "创建复杂邮件失败");
            e.printStackTrace();
        }
        // 返回创建好的的邮件
        return message;

    }
    /**
     * 创建带有附件的邮件
     * @param sendMailSession
     * @param list 选中的CSV文件的集合
     * @return
     */
    private Message createAttachmentMail(Session sendMailSession,ArrayList<String> list) {
        //创建邮件
        MimeMessage message = null;
        try {
            message = new MimeMessage(sendMailSession);
            // 设置邮件的基本信息
            //创建邮件发送者地址
            Address from = new InternetAddress(info.getFromAddress());
            //设置邮件消息的发送者
            message.setFrom(from);
            //创建邮件的接受者地址，并设置到邮件消息中
            Address to = new InternetAddress(info.getToAddress());
            //设置邮件消息的接受者, Message.RecipientType.TO属性表示接收者的类型为TO
            message.setRecipient(Message.RecipientType.TO, to);
            //邮件标题
            message.setSubject(info.getSubject());

            // 创建邮件正文，为了避免邮件正文中文乱码问题，需要使用CharSet=UTF-8指明字符编码
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(info.getContent(), "text/html;charset=UTF-8");

            // 创建容器描述数据关系
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(text);
            for (int i = 0; i < list.size(); i++) {
                // 创建邮件附件
                MimeBodyPart attach = new MimeBodyPart();
                DataHandler dh = new DataHandler(new FileDataSource(path + list.get(i)));
                attach.setDataHandler(dh);
                attach.setFileName(MimeUtility.encodeText(dh.getName()));
                mp.addBodyPart(attach);
            }
            mp.setSubType("mixed");
            message.setContent(mp);
            message.saveChanges();
            // 将创建的Email写入到E盘存储
            //message.writeTo(new FileOutputStream("E:\\attachMail.eml"));
        } catch (Exception e) {
            Log.e("TAG", "创建带附件的邮件失败");
            e.printStackTrace();
        }
        // 返回生成的邮件
        return message;
    }
    /**
     *生成一封邮件正文带图片的邮件
     * @param sendMailSession
     * @return
     */
    private MimeMessage createImageMail(Session sendMailSession) {
        //创建邮件
        MimeMessage mailMessage = null;
        try {
            mailMessage = new MimeMessage(sendMailSession);
            // 设置邮件的基本信息
            //创建邮件发送者地址
            Address from = new InternetAddress(info.getFromAddress());
            //设置邮件消息的发送者
            mailMessage.setFrom(from);
            //创建邮件的接受者地址，并设置到邮件消息中
            Address to = new InternetAddress(info.getToAddress());
            //设置邮件消息的接受者, Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            //邮件标题
            mailMessage.setSubject(info.getSubject());
            // 准备邮件数据
            // 准备邮件正文数据
            MimeBodyPart text = new MimeBodyPart();
            text.setContent("这是一封邮件正文带图片<img src='cid:xxx.jpg'>的邮件", "text/html;charset=UTF-8");
            // 准备图片数据
            MimeBodyPart image = new MimeBodyPart();
            DataHandler dh = new DataHandler(new FileDataSource("src\\1.jpg"));
            image.setDataHandler(dh);
            image.setContentID("xxx.jpg");
            // 描述数据关系
            MimeMultipart mm = new MimeMultipart();
            mm.addBodyPart(text);
            mm.addBodyPart(image);
            mm.setSubType("related");
            mailMessage.setContent(mm);
            mailMessage.saveChanges();
            //将创建好的邮件写入到E盘以文件的形式进行保存
            mailMessage.writeTo(new FileOutputStream("E:\\ImageMail.eml"));
            //返回创建好的邮件
        } catch (Exception e) {
            Log.e("TAG", "创建带有图片的邮件消息失败");
            e.printStackTrace();
        }
        return mailMessage;
    }
    /**
     * 创建纯文本内容的邮件消息
     * @param sendMailSession
     * @return
     */
    public MimeMessage createSimpleMail(Session sendMailSession){
        MimeMessage mailMessage = null;
        try {
            //根据session创建一条邮件信息
            mailMessage = new MimeMessage(sendMailSession);
            //创建邮件发送者地址
            Address from = new InternetAddress(info.getFromAddress());
            //设置邮件消息的发送者
            mailMessage.setFrom(from);
            //创建邮件的接受者地址，并设置到邮件消息中
            Address to = new InternetAddress(info.getToAddress());
            //设置邮件消息的接受者, Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            //设置邮件标题
            mailMessage.setSubject(info.getSubject());
            //设置邮件内容
            mailMessage.setText(info.getContent());
            //设置邮件发送的时间
            mailMessage.setSentDate(new Date());
        } catch (Exception e) {
            Log.e("TAG", "邮件消息创建失败");
            e.printStackTrace();
        }
        return mailMessage;
    }
}
