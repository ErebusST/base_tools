/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.situ.config.EmailConfig;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 邮件服务工具类
 *
 * @author ErebusST
 * @date 2017/6/15 13:56
 */
@Component
public class EmailUtils {

    @Autowired
    EmailConfig emailConfig;
    @Setter
    String from = "";//设置邮件发送源邮箱
    @Setter
    String to = "";// 设置邮件发送目的邮箱
    @Setter
    String cc = "";// 设置邮件发送抄送
    @Setter
    String username = "";//设置登录服务器校验用户
    @Setter
    String password = "";//设置登录服务器校验密码
    @Setter
    String subject = "";// 邮件主题
    @Setter
    String content = "";// 邮件正文

    List<String> attachmentFiles = new ArrayList<>();// 附件文件集合


    /**
     * <br>
     * 方法说明：往附件组合中添加附件 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void addAttachmentFile(String fname) {
        attachmentFiles.add(fname);
    }


    /**
     * <br>
     * 方法说明：乱码处理 <br>
     * 输入参数：String strText <br>
     * 返回类型：
     */
    public String transferChinese(String strText) throws UnsupportedEncodingException {
        try {
            return MimeUtility.encodeText(strText);
        } catch (Exception e) {
            throw e;
        }
    }


    public boolean sendMail() throws Exception {
        try {
            Map<String, String> setting = emailConfig.getSetting();
            Properties props = new Properties();

            props.putAll(setting);

            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            Session session = Session.getInstance(props, authenticator);
            // 构造MimeMessage 并设定基本的值
            MimeMessage message = new MimeMessage(session);
            InternetAddress fromAddress = new InternetAddress(from);
            message.setFrom(fromAddress);
            InternetAddress[] toAddresses = InternetAddress.parse(to);
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            InternetAddress[] ccAddresses = InternetAddress.parse(cc);
            message.setRecipients(Message.RecipientType.CC, ccAddresses);

            message.setSubject(subject);
            // 构造Multipart
            Multipart multipart = new MimeMultipart();
            // 向Multipart添加正文
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html; charset=utf-8");

            // 向MimeMessage添加（Multipart代表正文）
            multipart.addBodyPart(mimeBodyPart);


            // 向Multipart添加附件
            for (String file : attachmentFiles) {
                MimeBodyPart mbpFile = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(file);
                mbpFile.setDataHandler(new DataHandler(fileDataSource));
                String fileName = fileDataSource.getName();
                fileName = transferChinese(fileName);
                mbpFile.setFileName(fileName);
                // 向MimeMessage添加（Multipart代表附件）
                multipart.addBodyPart(mbpFile);
            }

            attachmentFiles.clear();
            // 向Multipart添加MimeMessage
            message.setContent(multipart);
            message.setSentDate(new Date());
            // 发送邮件
            Transport.send(message);
        } catch (Exception ex) {
            throw ex;
        }
        return true;
    }



    String LINE_SEPARATOR = "<br/>";



}