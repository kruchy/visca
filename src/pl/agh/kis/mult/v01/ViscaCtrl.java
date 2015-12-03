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
    private String macroName;

    public boolean canWriteToPort()
    {
        return serialPort.isOpened();
    }

	public ViscaCtrl() {
		serialPort = new SerialPort("com1");
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.addEventListener(this);
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            System.err.println("Cannot write to port");
        }
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
            startMacro(commandString);
		} else if (commandString.contains("macro-stop")) {
            stopMacro();
		} else if (commandString.contains("macro-execute")) {
            executeMacro(commandString);
		} else if (definingMacro != null) {
            putNewMacro(commandString);
        }
        else if (definingMacro == null)
            executeSingleCommand(commandString);
        return "ok";
	}

    private void executeSingleCommand(String commandString) throws UnknownCommandException, SerialPortException {
        chainCommand.execute(commandString,serialPort);
    }

    private void putNewMacro(String commandString) {
        if (macroMap.get(definingMacro) == null) {
ArrayList<String> list = new ArrayList<>();
list.add(commandString);
            macroMap.put(definingMacro,list);
        } else {
            macroMap.get(definingMacro).add(commandString);
        }
    }

    private void executeMacro(String commandString) throws UnknownCommandException, SerialPortException {
        macroName = commandString.split("\\s+")[1];
        if(macroMap.containsKey(macroName)) {

            System.out.println("Started executing macro "+ macroName);
                ArrayList<String> commandList = macroMap.get(macroName);
            Iterator<String> commandIterator = commandList.iterator();
            while (commandIterator.hasNext()) {
                executeSingleCommand(commandIterator.next());
            }
        }
    }

    private void stopMacro() {
        System.out.println("Stopped defining macro");
        definingMacro = null;
    }

    private void startMacro(String commandString) {
        macroName = commandString.split("\\s+")[1];
        System.out.println("Started defining macro "+ macroName);
        macroMap.put(macroName, new ArrayList<>());
        definingMacro = macroName;
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
