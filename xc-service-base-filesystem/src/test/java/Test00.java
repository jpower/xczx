import com.xuecheng.framework.upload.FastDFSClient;
import org.junit.Test;

/**
 * Created by 周大侠
 * 2019-04-06 17:15
 */
public class Test00 {
    @Test
    public void fun1() throws Exception {
//        System.out.println("abc.22".substring(4));
        FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
        // 执行上传
        String fileId = fastDFSClient.uploadFile("sdfd".getBytes(), "txt");
        System.out.println(fileId);
    }
 }
