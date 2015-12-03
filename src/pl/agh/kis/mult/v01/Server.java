package pl.agh.kis.mult.v01;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import jssc.SerialPortException;

public class Server implements Runnable {
	private static final String OUTPUT = "<html><head><title>Example</title></head><body><p>Worked!!!</p></body></html>";
	private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n"
			+ "Content-Type: text/html\r\n" + "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
	private int port;
	private ViscaCtrl viscaCtrl;

	Server(int port) {
		this.port = port;
	}

	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}

	public void run() {
		viscaCtrl = new ViscaCtrl();
		try {
			// viscaCtrl.init();
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket socket = server.accept();
				// System.out.println(socket.getInetAddress());
				// Get response from server
				String response;
				BufferedReader s_in = null;
				s_in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				while ((response = s_in.readLine()) != null) {
					// System.out.println(response);
					if (response.startsWith("GET /?")) {
						String[] paramsArr = response.split("/\\?");
						String params = paramsArr[1];
						params = params.split("HTTP/")[0];
						Map<String, String> paramMap = getQueryMap(params);
						if (paramMap.containsKey("command")) {
							String command = paramMap.get("command");
							command = URLDecoder.decode(command, "UTF-8");
							String resp = "";
							System.out.println(command);
							try {
								resp = viscaCtrl.executeCommand(command);
							} catch (Exception e) {
								resp = e.getStackTrace().toString();
							}
							System.out.println(resp);

							OutputStream out = socket.getOutputStream();

							out.write(resp.getBytes());
							out.flush();
							out.close();

						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				viscaCtrl.closeSerial();
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {

		Thread thread = new Thread(new Server(80));
		thread.start();
	}
}