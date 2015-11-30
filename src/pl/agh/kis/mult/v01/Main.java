package pl.agh.kis.mult.v01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jssc.SerialPort;
import jssc.SerialPortException;
import pl.agh.kis.mult.v01.command.*;

/**
 * Created by Krzysiek on 2015-11-27.
 */
public class Main {
	public static void main(String[] args) {

		 ChainCommand up = new UpCommand();
		 ChainCommand down = new DownCommand();
		 ChainCommand left = new LeftCommand();
		 ChainCommand right = new RightCommand();
		 up.setNext(down);
		 down.setNext(left);
		 left.setNext(right);

		ViscaCtrl viscaCtrl = new ViscaCtrl();

        try {
            viscaCtrl.init();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				System.in));
		String line = null;
		do {
			try {
				line = buffer.readLine();
                viscaCtrl.executeCommand(line);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (SerialPortException e) {
				e.printStackTrace();
			} catch (UnknownCommandException e) {
                e.printStackTrace();
            }
        }
		while(!line.equals("close"));



	}
}
