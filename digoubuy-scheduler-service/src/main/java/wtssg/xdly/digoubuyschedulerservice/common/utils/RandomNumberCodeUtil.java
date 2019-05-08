package wtssg.xdly.digoubuyschedulerservice.common.utils;

import java.util.Random;

public class RandomNumberCodeUtil {

    /**
     * 生成验证码
     * @return 随机4位数字串
     */
    public static String verCode() {
        Random random = new Random();
        String s = String.valueOf(random.nextInt());
        return s.substring(2, 6);
    }

    public static String randomNumber() {
        Random random = new Random();

        return String.valueOf(Math.abs(random.nextInt()*-10));
    }
}
