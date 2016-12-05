package demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import com.example.demo.R;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * @file_name 文件名：Main.java
 * @author 作者:王越
 * @date 创建时间：2016年11月25日 下午3:55:04
 * @version 版本：1.0
 * @desc 描述：
 */
public class Main extends AppCompatActivity {
	TextView tv;
	String content, a;
	String allContent = "";
	String filepath = Environment.getExternalStorageDirectory() + "/GeoBIM-Layout/a.gpro";
	byte string_buffer[] = new byte[4];;
	int string_len;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);
		tv = (TextView) findViewById(R.id.tv);

		a = readFile();

		tv.setText(a);

	}

	public String readFile() {
		try {
			// 文件输入流
			File file = new File(filepath);
			FileInputStream fin = new FileInputStream(file);
			// 文件大小
			int total_len = fin.available();
			Log.i("aaa", "文件大小:" + total_len);
			// 创建字节数组，规定长度
			byte buffer[] = new byte[total_len];
			// 读文件
			fin.read(buffer);
			// 预读字节长度
			for (int pre_index = 0; pre_index < total_len;) {
				// 解析头标记Id，char类型
				byte head_buffer[] = new byte[1];
				pre_index = getBytesFromBuffer(buffer, pre_index, 1, head_buffer);
				char head = ByteUtil.getChar(head_buffer);
				String head_mark = String.valueOf(head);
				switch (head_mark) {
				case "I":
					// 解析Id值，int类型
					byte id_buffer[] = new byte[4];
					pre_index = getBytesFromBuffer(buffer, pre_index, 4, id_buffer);
					int id_len = ByteUtil.getInt(id_buffer);
					allContent = allContent + "ID:" + id_len + "\n";
					break;
				case "N":
					pre_index = readProperty(pre_index, buffer);
					// 解析Int值，int类型
					byte int_buffer[] = new byte[4];
					pre_index = getBytesFromBuffer(buffer, pre_index, 4, int_buffer);
					int int_len = ByteUtil.getInt(int_buffer);
					allContent = allContent + int_len + "\n";
					break;
				case "D":
					pre_index = readProperty(pre_index, buffer);
					// 解析Double值，double类型
					byte double_buffer[] = new byte[8];
					pre_index = getBytesFromBuffer(buffer, pre_index, 8, double_buffer);
					double d = ByteUtil.getDouble(double_buffer);
					allContent = allContent + d + "\n";
					break;
				case "S":
					pre_index = readProperty(pre_index, buffer);
					// 解析字符串
					// 获取属性名的长度
					pre_index = getBytesFromBuffer(buffer, pre_index, 4, string_buffer);
					string_len = ByteUtil.getInt(string_buffer);
					// 获取属性名
					byte[] strByte = new byte[string_len];
					pre_index = getBytesFromBuffer(buffer, pre_index, string_len, strByte);
					try {
						content = new String(strByte, "utf-8");// 转成文字
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					allContent = allContent + content + "\n";
					break;
				}
			}

			fin.close();
		}

		catch (

		Exception e) {
			e.printStackTrace();
		}
		return allContent;
	}

	/**
	 * 读取属性名，参数：pre_index：当前位置，buffer：文件内容
	 */
	public int readProperty(int pre_index, byte[] buffer) {
		allContent = allContent + "    \t";
		// 获取属性名的长度
		pre_index = getBytesFromBuffer(buffer, pre_index, 4, string_buffer);
		string_len = ByteUtil.getInt(string_buffer);
		// 获取属性名
		byte[] strByte = new byte[string_len];
		pre_index = getBytesFromBuffer(buffer, pre_index, string_len, strByte);
		try {
			content = new String(strByte, "utf-8").trim() + ":";// 转成文字
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		allContent = allContent + content;
		return pre_index;
	}

	public static int getBytesFromBuffer(byte[] buffer, int pre_index, int ncount, byte[] recive_buffer) {
		for (int i = 0; i < ncount; i++) {
			recive_buffer[i] = buffer[pre_index + i];
		}
		return pre_index + ncount;
	}
}
