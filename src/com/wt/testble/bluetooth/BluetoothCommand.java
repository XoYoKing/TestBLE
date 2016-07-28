package com.wt.testble.bluetooth;

public class BluetoothCommand {
	
	public static final String GET_SET = "SR";
	public static final String SET_SLEEP_LIGHT_ON = "SD11";
	public static final String SET_SLEEP_LIGHT_OFF = "SD10";
	public static final String SET_SLEEP_MUSIC_ON = "SD21";
	public static final String SET_SLEEP_MUSIC_OFF = "SD20";
	public static final String SET_WAKEUP_LIGHT_ON = "SD31";
	public static final String SET_WAKEUP_LIGHT_OFF = "SD30";
	public static final String SET_WAKEUP_MUSIC_ON = "SD41";
	public static final String SET_WAKEUP_MUSIC_OFF = "SD40";
	public static final String SET_NIGHT_LIGHT_ON = "SD51";
	public static final String SET_NIGHT_LIGHT_OFF = "SD50";
	
	public static final String SET_TIME = "SM";//SM + mins
	public static final String COMMAND_BEGIN = "#";
	public static final String COMMAND_END = "$";
	public static final String COMMAND_RETURN = "R";
	public static final String COMMAND_RETURN_SUCCESS = "RD00";
	public static final String SET_SLEEP_TIME = "ST1"; // ST1 + H:M + H:M
	public static final String GET_SLEEP_TIME = "ST2";
	public static final String GET_SLEEP_TIME_RETURN = "RST";
	public static final String SET_SLEEP_MUSIC = "SA1"; //SB1 + NUM
	public static final String GET_SLEEP_MUSIC = "SA2";
	public static final String GET_SLEEP_MUSIC_R = "SA";
	public static final String SET_WAKEUP_MUSIC = "SB1";
	public static final String GET_WAKEUP_MUSIC = "SB2";
	public static final String GET_WAKEUP_MUSIC_R = "SB";
	public static final String PRE_LISTEN_MUSIC = "SC";
	public static final String STOP_LISTEN_MUSIC = "SCEND";
	/*
	 * get the data. use GD first and the use GDN get next
	 * data struct : R+时间(8个字节分钟)+打憨次数(2字节)+干预次数(1字节)+声音大小(3高2中1低)
	 * RGD0 means no record.
	 * */
	/*/
	//WT 如下老版本鼾声数据获取指令废弃 201604040930 START 
	public static final String GET_FIRST_DATA = "GDN";
	public static final String GET_DATA_NEXT = "GDN";
	public static final String GET_DATA_RESULT = "RGD0";
	public static final String GET_DATA_DUMPLICATE = "GDM";
	public static final String GET_DATA_DUMPLICATE_RETURN = "M";
	public static final String GET_DATA_DUMPLICATE_RESULT = "MGD0";
	//WT 如下老版本鼾声数据获取指令废弃 201604040930 END 
	//*/
	//*/
	//WT 如下新版本鼾声数据获取指令 201604040930 START 
	public static final String DATA_SPLIT = " ";
	//上位机获取鼾声数据命令:“#GD1”+“ ”+笔数$
	//注:笔数用 ASCII 码来表示,一次获取的笔数不大于 10 笔,比如获取一笔则“01”, 十笔则“10”。
	//下位机回复:
	//(1)没有数据:"#RGD0$"
	//(2)有数据:#GD1+“ ”+数据 1 数据 2 ....数据 10 $
	//注:回复的数据笔数依据上位机需求数量确定,如果上位机需求的数据笔数大于下位 机所剩余的笔数,则下位机将剩余的笔数发给上位机。最后一笔数据的后面会加一个结束符 $
	public static final String DATA_GET_SNORE 			= "GD1";	//GD1 01$
	public static final String DATA_GET_SNORE_10 		= "GD1 01";	//获取10笔数据
	public static final String DATA_GET_SNORE_RETURN 	= "GD1";
	public static final String DATA_GET_SNORE_NO_DATA 	= "RGD0";	//没有数据了
	//上位机获取鼾声数据总笔数
	//上位机发送:“#GD2$” 下位机回复:“#GD2”+“ “+总笔数$
	//注:总笔数为 0-130000,以 ASCII 码形式发送默认为 6 位,如 000000,000001,....130000
	public static final String DATA_GET_SNORE_COUNT 		= "GD2";
	public static final String DATA_GET_SNORE_COUNT_RETURN 	= "GD2";
	public static final String DATA_GET_SNORE_COUNT_RESULT 	= "RGD0";//没有数据了
	//鼾声数据的格式
	//正常数据格式:时间(10 个字节,年月日时分)+打憨次数(2 字节)+干预次数(1 字节) +声音大小(3 高 2 中 1 低), 共 14 字节
	
	//上位机获取头部位置数据命令:“#GD3”+“ ”+笔数$
	//注:笔数用 ASCII 码来表示,一次获取的笔数不大于 10 笔,比如获取一笔则“01”, 十笔则“10”。
	//下位机回复:
	//(3)没有数据:"#RGD0$"
	//(4)有数据:“#GD3 数据 1 数据 2 ....数据 10$”
	//注:回复的数据笔数依据上位机需求数量确定,如果上位机需求的数据笔数大于下位 机所剩余的笔数,则下位机将剩余的笔数发给上位机。最后一笔数据的后面加一个结束符 $
	public static final String DATA_GET_HEAD 			= "GD3";	//GD1 01$
	public static final String DATA_GET_HEAD_10 		= "GD3 01";	//获取10笔数据
	public static final String DATA_GET_HEAD_RETURN 	= "GD3";
	public static final String DATA_GET_HEAD_NO_DATA 	= "RGD0";	//没有数据了	
	//上位机获取头部位置数据总笔数 上位机发送:“#GD4$” 
	//下位机回复:“#GD4”+“ “+总笔数$
	//注:总笔数为 0-130000,以 ASCII 码形式发送默认为 6 位,如 000000,000001,....130000
	public static final String DATA_GET_HEAD_COUNT 			= "GD4";
	public static final String DATA_GET_HEAD_COUNT_RETURN 	= "GD4";
	public static final String DATA_GET_HEAD_COUNT_RESULT 	= "RGD0";//没有数据了	
	//头部位置数据的格式:
	//正常时的数据格式:时间(10 个字节分钟,年月日时分)+头部位置(1 字节),共 11 字节 头部位置:0 中间 , 1 左边,2 右边
		
	//WT 如下新版本鼾声数据获取指令 201604040930 END 
	//*/
	public static final String CREATE_TEST_DATA 			= "TS";//生成测试数据
}
