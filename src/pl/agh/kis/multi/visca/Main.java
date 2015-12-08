package pl.agh.kis.multi.visca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pl.agh.kis.multi.visca.command.UnknownCommandException;

/**
 * Created by Krzysiek on 2015-11-27.
 */
public class Main {
	public static void main(String[] args) {


        ViscaCtrl viscaCtrl = null;
        viscaCtrl = new ViscaCtrl();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(
				System.in));
		String line = null;
		do {
			try {
				line = buffer.readLine();
                viscaCtrl.executeCommand(line);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (UnknownCommandException e) {
                System.out.println("Command not supported");
            } catch (Exception e) {
				e.printStackTrace();
			}
		}
		while(!line.equals("close"));



	}
}
