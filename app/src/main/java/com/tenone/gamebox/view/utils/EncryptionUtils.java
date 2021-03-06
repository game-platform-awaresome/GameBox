package com.tenone.gamebox.view.utils;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@SuppressLint("DefaultLocale")
public class EncryptionUtils {

    public static String getMD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance( "MD5" );
            md5.reset();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        try {
            byteArray = str.getBytes( "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] md5Bytes = md5.digest( byteArray );
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append( "0" );
            }
            hexValue.append( Integer.toHexString( val ) );
        }
        return hexValue.toString().toLowerCase();
    }

    public static String getMd5Up(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance( "MD5" );
            md5.reset();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        try {
            byteArray = str.getBytes( "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] md5Bytes = md5.digest( byteArray );
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append( "0" );
            }
            hexValue.append( Integer.toHexString( val ) );
        }
        return hexValue.toString().toUpperCase();
    }

    public static String getMd5(String key) throws NoSuchAlgorithmException {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        byte[] btInput = key.getBytes();
        // ���MD5ժҪ�㷨�� MessageDigest ����
        MessageDigest mdInst = MessageDigest.getInstance( "MD5" );
        // ʹ��ָ�����ֽڸ���ժҪ
        mdInst.update( btInput );
        // �������
        byte[] md = mdInst.digest();
        // ������ת����ʮ�����Ƶ��ַ�����ʽ
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String( str );
    }


    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString( b[n] & 0XFF ));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    public static String encryptToSHA(String info) {
        byte[] digesta = null;
        try {
            MessageDigest alga = MessageDigest.getInstance( "SHA-1" );
            alga.update( info.getBytes() );
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String rs = byte2hex( digesta );
        return rs;
    }

    public static String getSingTure(Map<String, String> map) {
        String sin = "";
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<String>( keyset );
        Collections.sort( list );
        for (String string : list) {
            sin += (string + map.get( string ));
        }
        return encryptToSHA( sin ).toUpperCase();
    }
}
