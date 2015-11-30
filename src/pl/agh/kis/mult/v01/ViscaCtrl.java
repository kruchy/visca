package pl.agh.kis.mult.v01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jssc.SerialPort;
import jssc.SerialPortException;
import pl.agh.kis.mult.v01.command.*;

public class ViscaCtrl {
	private SerialPort serialPort = null;
	private String definingMacro = null;
	private HashMap<String, ArrayList<String>> macroMap = new HashMap<>();
	private ChainCommand chainCommand;
	public void init() throws SerialPortException {
		serialPort = new SerialPort("com1");
//		serialPort.setParams(9600, 8, 1, 0);
		chainCommand = new UpCommand();
		ChainCommand down = new DownCommand();
		ChainCommand right = new RightCommand();
		ChainCommand left = new LeftCommand();
		chainCommand.setNext(down);
		down.setNext(right);
		right.setNext(left);
	}

	public void closeSerial() throws SerialPortException {
		serialPort.closePort();
	}

	public String executeCommand(String commandString)
			throws SerialPortException,UnknownCommandException {



		if (commandString.contains("macro-start")) {
			String macroName = commandString.split("\\s+")[1];
            System.out.println("Started defining macro "+macroName);
            macroMap.put(macroName, new ArrayList<>());
			definingMacro = macroName;
		} else if (commandString.contains("macro-stop")) {
            System.out.println("Stopped defining macro");
			definingMacro = null;
		} else if (commandString.contains("macro-execute")) {
			String macroName = commandString.split("\\s+")[1];

            if(macroMap.containsKey(macroName)) {

                System.out.println("Started executing macro "+ macroName);
					ArrayList<String> commandList = macroMap.get(macroName);
				Iterator<String> commandIterator = commandList.iterator();
				while (commandIterator.hasNext()) {
                    chainCommand.execute(commandIterator.next(), serialPort);
				}
			}
		} else if (definingMacro != null) {

			if (macroMap.get(definingMacro) == null) {
                ArrayList<String> list = new ArrayList<>();
                list.add(commandString);
				macroMap.put(definingMacro,list);
			} else {
				macroMap.get(definingMacro).add(commandString);
			}
		}
        else if (definingMacro == null)
                chainCommand.execute(commandString,serialPort);
		return "ok";
	}

	public String executeCommand(ChainCommand command)
			throws SerialPortException {
		serialPort.writeBytes(command.getCommand());
		return "ok " + command.getCommandName();
	}

	public String executeMacro(ChainCommand command) throws SerialPortException {

		while (command != null) {
			executeCommand(command);
			command = command.getNext();
		}
		return "ok";
	}

}
