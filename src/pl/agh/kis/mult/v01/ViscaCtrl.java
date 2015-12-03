package pl.agh.kis.mult.v01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import pl.agh.kis.mult.v01.command.*;

public class ViscaCtrl implements SerialPortEventListener {
	private SerialPort serialPort = null;
	private String definingMacro = null;
	private HashMap<String, ArrayList<String>> macroMap = new HashMap<>();
	private ChainCommand chainCommand;
	
	public ViscaCtrl() throws SerialPortException {
		serialPort = new SerialPort("com1");
//        serialPort.openPort();
//		serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//        serialPort.addEventListener(this);
//        serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
		chainCommand = new UpCommand();
		ChainCommand down = new DownCommand();
		ChainCommand left = new LeftCommand();
		ChainCommand right = new RightCommand();
		ChainCommand home = new HomeCommand();
		ChainCommand stop = new StopCommand();
		ChainCommand setAddress = new SetAddressCommand();
		chainCommand.setNext(down);
		down.setNext(left);
		left.setNext(right);
		right.setNext(home);
		home.setNext(stop);
		stop.setNext(setAddress);
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

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if(serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() == 10 )
        {
            try {
                byte[] response = serialPort.readBytes(10);
            } catch (SerialPortException e) {
                System.err.println("Error receiving bytes from port");
            }
        }

    }
}
