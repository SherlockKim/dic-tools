import com.tydic.utils.KuaiDaiLiUtils;
import org.junit.Test;

public class KuaiDaiLiUtilsTest {

    @Test
    public void checkIpTest() throws Exception {
        KuaiDaiLiUtils.checkIp();
    }

    @Test
    public void strTest() {
        String line = "https://i2.chuimg.com/df90ae8d76ee4ef79655ae6429922ca0_912w_684h.jpg?imageView2/1/w/280/h/280/interlace/1/q/90||D:/tmp1/test062/76番茄牛腩||1_df90ae8d76ee4ef79655ae6429922ca0_912w_684h.jpg";
        String[] strArray = line.split("\\|\\|");
        System.out.println(strArray[0] +"\n"+ strArray[1] +"\n" +strArray[2]);
    }
}
