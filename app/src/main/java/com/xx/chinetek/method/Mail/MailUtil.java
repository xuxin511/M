package com.xx.chinetek.method.Mail;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Created by GHOST on 2017/10/30.
 */

public class MailUtil {

    public  static void GetMail(MailModel mailModel) throws NoSuchProviderException,MessagingException,Exception {
        Properties prop = System.getProperties();
        prop.put("mail.store.protocol", "imap");
        prop.put("mail.imap.host", mailModel.getMailClientHost());
        Session session = Session.getInstance(prop);
        IMAPStore store = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
        store.connect(mailModel.getAccount(), mailModel.getPassword());
        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
        folder.open(Folder.READ_WRITE);
        // 获取总邮件数
        int total = folder.getMessageCount();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Message[] messages = folder.getMessages();
        if (messages.length > 0) {
            Map<String, Object> map;
            System.out.println("Messages's length: " + messages.length);
            ReciveOneMail pmm = null;
            for (int i = 0; i < messages.length; i++) {
                pmm = new ReciveOneMail((MimeMessage) messages[i]);
                boolean isRead;// 用来判断该邮件是否为已读
                String read;
                Flags flags = messages[i].getFlags();
                if (flags.contains(Flags.Flag.SEEN)) {
                    System.out.println("这是一封已读邮件");
                    isRead = true;
                    read = "已读";
                } else {
                    System.out.println("未读邮件");
                    isRead = false;
                    read = "未读";
                }
                System.out.println("Message " + i + " hasRead: "
                        + isRead);
                System.out.println("Message " + i
                        + "  containAttachment: "
                        + pmm.isContainAttach((Part) messages[i]));
                System.out.println("Message " + i + " form: "
                        + pmm.getFrom());
                System.out.println("Message " + i + " to: "
                        + pmm.getMailAddress("to"));
                System.out.println("Message " + i + " cc: "
                        + pmm.getMailAddress("cc"));
                System.out.println("Message " + i + " bcc: "
                        + pmm.getMailAddress("bcc"));
//                pmm.setDateFormat("yy年MM月dd日 HH:mm");
//                System.out.println("Message " + i + " sentdate: "
//                        + pmm.getSentDate());
                System.out.println("Message " + i + " Message-ID: "
                        + pmm.getMessageId());
                // 获得邮件内容===============
                pmm.getMailContent((Part) messages[i]);
                System.out.println("Message " + i
                        + " bodycontent: \r\n" + pmm.getBodyText());
                String file_path = File.separator + "mnt"
                        + File.separator + "sdcard" + File.separator;
                System.out.println(file_path);
                pmm.setAttachPath(file_path);
                pmm.saveAttachMent((Part) messages[i]);

                map = new HashMap<String, Object>();
                map.put("from", pmm.getFrom());
                map.put("title", pmm.getSubject());
               // map.put("time", pmm.getSentDate());
                map.put("read", read);
                list.add(map);
            }
        }
    }

}
