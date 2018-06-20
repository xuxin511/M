package com.xx.chinetek.method.Mail;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMail;

/**
 * Created by GHOST on 2017/10/30.
 */

public class MailUtil {

    public  static void GetMail(MailModel mailModel, MyHandler<BaseActivity> mHandler ) throws NoSuchProviderException,MessagingException,Exception {
        Properties prop = System.getProperties();
        prop.put("mail.store.protocol", "imap");
        prop.put("mail.imap.host", mailModel.getMailClientHost());
        Session session = Session.getInstance(prop);
        IMAPStore store = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
        store.connect(mailModel.getAccount(), mailModel.getPassword());
        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
        folder.open(Folder.READ_WRITE);
        // 获取总邮件数
        int total = 0;
        Message[] messages = folder.getMessages();
        if (messages.length > 0) {
            ReciveOneMail pmm = null;
            for (int i = 0; i < messages.length; i++) {
                pmm = new ReciveOneMail((MimeMessage) messages[i]);
//                Flags flags = messages[i].getFlags();
//                if (flags.contains(Flags.Flag.SEEN)) { //已读
//                    continue;
//                }
                if(!pmm.isContainAttach((Part) messages[i])){ //没有附件
                    continue;
                }
                if(!pmm.getSubject().contains(ParamaterModel.FilterContent)){
                    continue;
                }
                // 获得邮件内容===============
                pmm.getMailContent((Part) messages[i]);
                String file_path = ParamaterModel.DownDirectory + File.separator;
                pmm.setAttachPath(file_path);
                pmm.saveAttachMent((Part) messages[i]);
                total++;
            }
        }
        android.os.Message msg = mHandler.obtainMessage(RESULT_SyncMail,total);
        mHandler.sendMessage(msg);
    }

    public  static void SendMail(MailModel mailModel, File[] Files, MyHandler<BaseActivity> mHandler ){
        try {

            List<String> list = new ArrayList<>();
            for (File file : Files) {
                list.add(file.getAbsolutePath());
            }
            SendOneMail sendOneMail = new SendOneMail();
            sendOneMail.sendAttachment(mailModel, list);
            for (int i = 0; i < Files.length; i++) {
                File f = Files[i];
                f.delete();
            }
            if(mHandler!=null) {
                android.os.Message msg = mHandler.obtainMessage(RESULT_SyncMail, BaseApplication.context.getString(R.string.Msg_UploadSuccess) +(Files.length==1?Files.length: Files.length/2));
                mHandler.sendMessage(msg);
            }
        }catch (Exception ex){
            if(mHandler!=null) {
                android.os.Message msg = mHandler.obtainMessage(RESULT_SyncMail, BaseApplication.context.getString(R.string.Msg_UploadFailue) + ex.getMessage());
                mHandler.sendMessage(msg);
            }
        }
    }

}


// System.out.println("Message " + i + " hasRead: "
//         + isRead);
//         System.out.println("Message " + i
//         + "  containAttachment: "
//         + pmm.isContainAttach((Part) messages[i]));
//         System.out.println("Message " + i + " form: "
//         + pmm.getFrom());
//         System.out.println("Message " + i + " to: "
//         + pmm.getMailAddress("to"));
//         System.out.println("Message " + i + " cc: "
//         + pmm.getMailAddress("cc"));
//         System.out.println("Message " + i + " bcc: "
//         + pmm.getMailAddress("bcc"));
////                pmm.setDateFormat("yy年MM月dd日 HH:mm");
////                System.out.println("Message " + i + " sentdate: "
////                        + pmm.getSentDate());
//         System.out.println("Message " + i + " Message-ID: "
//         + pmm.getMessageId());