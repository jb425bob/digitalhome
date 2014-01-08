package notify;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;

public abstract class PushNotify {

	public static final String  apiKey = "GXBmGVNIBwnf6GEOBWGfEHu8";
	public static final String secretKey = "41fG09ZZAmy3MKKaPQGzYMiacsTZ4GQA";
	private ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);
	public  BaiduChannelClient channelClient = new BaiduChannelClient(pair);
	public abstract int sendNotify(String content) ;
	public abstract int setDeviceType(int deviceType);
}
