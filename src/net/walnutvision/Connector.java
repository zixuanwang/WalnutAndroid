package net.walnutvision;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import android.util.Log;

public class Connector {
	private static final String TAG = "CONNECTOR";
	private static int LABEL_ID = 100;
	private static int IMAGE_ID = 200;
	private static int REQUEST_LABEL_ID = 300;
	private String mRemoteImageId = null;
	private AnnotationDbAdapter mDbHelper = null;
	private static final String hostName = "192.168.42.86";//change this
	private static final int hostPort = 6666;
	private Socket mSocket = null;
	public String label;

	public void open() {
		DataOutputStream dout = null;
		try {
			mSocket = new Socket(hostName, hostPort);
			dout = new DataOutputStream(mSocket.getOutputStream());
			dout.writeBytes("IW\n");
			dout.writeBytes(ImageWebActivity.mUsername + "\n");
		} catch (Exception e) {
			mSocket = null;
		} finally {

		}
	}

	public void close() {
		try {
			mSocket.close();
			mSocket = null;
		} catch (Exception e) {

		}

	}

	public void sendLabel(String imagePath, float x, float y, float width,
			float height, String label) {
		if (mSocket == null) {
			open();
		}
		DataOutputStream dout = null;
		try {
			dout = new DataOutputStream(mSocket.getOutputStream());
			dout.writeInt(LABEL_ID);
			File file = new File(imagePath);
			String imageName = file.getName();
			dout.writeBytes(imageName + "\n");
			dout.writeInt((int)x);
			dout.writeInt((int)y);
			dout.writeInt((int)width);
			dout.writeInt((int)height);
			dout.writeBytes(label + "\n");
		} catch (Exception e) {
			mSocket = null;
		} finally {

		}
	}

	public void sendImage(String imagePath) {
		if (mSocket == null) {
			open();
		}
		DataOutputStream dout = null;
		DataInputStream din = null;
		BufferedInputStream in = null;
		try {
			File file = new File(imagePath);
			String imageName = file.getName();
			int fileSize = (int) file.length();
			dout = new DataOutputStream(mSocket.getOutputStream());
			din = new DataInputStream(mSocket.getInputStream());
			dout.writeInt(IMAGE_ID);// Image Type
			dout.writeBytes(imageName + "\n");// Image Name
			dout.writeInt(fileSize);// Image Size
			in = new BufferedInputStream(new FileInputStream(imagePath));
			byte[] bytesIn = new byte[fileSize];
			in.read(bytesIn, 0, fileSize);
			dout.write(bytesIn, 0, fileSize);// Image Data
		} catch (Exception e) {
			mSocket = null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public String requestImageLabel(String imagePath) {
		if (mSocket == null) {
			open();
		}
		DataOutputStream dout = null;
		DataInputStream din = null;
		String result = "";
		try {
			dout = new DataOutputStream(mSocket.getOutputStream());
			din = new DataInputStream(mSocket.getInputStream());
			dout.writeInt(REQUEST_LABEL_ID);
			File file = new File(imagePath);
			String imageName = file.getName();

			dout.writeBytes(imageName + "\n");
			int labelCount = din.readInt();
			Log.i(TAG, "" + labelCount);
			// BufferedReader d = new BufferedReader(new
			// InputStreamReader(mSocket
			// .getInputStream()));
			for (int i = 0; i < labelCount; i++) {
				String label = din.readLine();
				Log.i(TAG, label);
				if (i == labelCount - 1) {
					result += label;
				} else {
					result = result + label + ", ";
				}
			}
			return result;
		} catch (Exception e) {
			mSocket = null;
			return "";
		} finally {
		}
	}

	public static boolean isServerRunning() {
		try {
			Socket tmp = new Socket(hostName, hostPort);
			tmp.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}