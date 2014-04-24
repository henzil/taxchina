package netlib.net;

/**
 * 
 * @author henzil
 * @E-mail lizhen@dns.com.cn
 * @version create time:2013-7-24_下午9:25:31
 * SERVER：直接请求网络 
 * SERVER_LOCAL：先请求网络，网络无数据的情况下请求本地 
 * SERVER_LOCAL：先请求本地，本地无数据的情况下请求网络
 * LOCAL：直接请求本地
 */

public enum DataMode {
	SERVER, SERVER_LOCAL, LOCAL_SERVER, LOCAL
}