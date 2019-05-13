package cn.com.cybertech.tools;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class RedisUtils {

    public static String toBase64String(String source) throws UnsupportedEncodingException {
        return Base64.encodeBase64String(source.getBytes(CodeUtil.cs));
    }

    public static byte[] buildByteArray(byte[] left, byte[] right, int blankLenth) {
        byte[] ba = new byte[left.length + right.length + blankLenth];
        System.arraycopy(left, 0, ba, 0, left.length);
        System.arraycopy(right, 0, ba, left.length + 1, right.length);
        return ba;
    }

    public static byte[] buildByteArray(byte[] left, byte[] middle, byte[] right, int blankLenth) {
        byte[] ba = new byte[left.length + middle.length + right.length + blankLenth];
        System.arraycopy(left, 0, ba, 0, left.length);
        System.arraycopy(middle, 0, ba, left.length + 1, middle.length);
        System.arraycopy(right, 0, ba, left.length + middle.length + 2, right.length);
        return ba;
    }

    public static byte[] buildBytesArray(byte[]... content) {
        int len = 0;
        for (byte[] bs : content) {
            len += (bs.length);
        }
        len += (content.length - 1);
        byte[] ba = new byte[len];
        int index = 0;
        for (byte[] bs : content) {
            System.arraycopy(bs, 0, ba, index, bs.length);
            index += (bs.length + 1);
        }
        return ba;
    }

    public static byte[] buildBytesArray(Collection<byte[]> content) {
        if (CollectionUtils.isEmpty(content)) {
            return new byte[0];
        }
        int len = 0;
        for (byte[] bs : content) {
            len += (bs.length);
        }
        //len += (content.size() - 1);
        byte[] ba = new byte[len];
        int index = 0;
        for (byte[] bs : content) {
            System.arraycopy(bs, 0, ba, index, bs.length);
            index += (bs.length);//+1
        }
        return ba;
    }

    public static byte[] buildBytesArrayWithSeperator0(Collection<byte[]> content) {
        if (CollectionUtils.isEmpty(content)) {
            return new byte[0];
        }
        int len = 0;
        for (byte[] bs : content) {
            len += (bs.length);
        }
        len += (content.size() - 1);
        byte[] ba = new byte[len];
        int index = 0;
        for (byte[] bs : content) {
            System.arraycopy(bs, 0, ba, index, bs.length);
            index += (bs.length + 1);
        }
        return ba;
    }
}
