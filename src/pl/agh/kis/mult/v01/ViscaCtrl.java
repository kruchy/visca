package pl.agh.kis.mult.v01;

import java.util.*;

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
    public boolean canWriteToPort() {
        return serialPort.isOpened();
    }

    public ViscaCtrl() {
        serialPort = new SerialPort("com3");
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.addEventListener(this);
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            System.err.println("Cannot write to port");
        }

        chainCommand = new UpCommand();
        chainCommand.setNext(new DownCommand());
        chainCommand.setNext(new LeftCommand());
        chainCommand.setNext(new RightCommand());
        chainCommand.setNext(new HomeCommand());
        chainCommand.setNext(new StopCommand());
        chainCommand.setNext(new SetAddressCommand());
        chainCommand.setNext(new TeleZoomCommand());
        chainCommand.setNext(new WideZoomCommand());
        chainCommand.setNext(new WaitCommand());
        try {
            executeCommand("set");
        } catch (SerialPortException e) {
            e.printStackTrace();
        } catch (UnknownCommandException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeSerial() throws SerialPortException {
        serialPort.closePort();
    }

    public void executeCommand(String commandString)
            throws SerialPortException, UnknownCommandException, InterruptedException {

        if (commandString.contains("macro-start")) {
            startMacro(commandString);
        } else if (commandString.contains("macro-stop")) {
            stopMacro();
        } else if (commandString.contains("macro-execute")) {
            executeMacro(commandString);
        } else if (commandString.contains("macro-list")) {
            listMacros();
        }
        else if(commandString.contains("set cam"))
        {
            setNewCam(commandString);
        }
        else if (definingMacro != null) {
            putNewMacro(commandString);
        } else if (definingMacro == null)
        {
            executeSingleCommand(commandString);
        } else if(commandString.contains("close"))
        {
            System.exit(0);
        }
    }

    private void setInterval(String commandString) throws UnknownCommandException, InterruptedException, SerialPortException {
        String[] command = commandString.split(" ");
        ChainCommand.waitInterval = Integer.valueOf(command[1]);
        putNewMacro(command[0]);
    }

    private void setNewCam(String commandString) {
        String[] command = commandString.split(" ");
        chainCommand.setCam(Integer.valueOf(command[2]));
    }

    private void listMacros() {
        for (String entry : macroMap.keySet())
        {
          System.out.println("MACRO "+ entry);
            Arrays.asList(macroMap.get(entry)).stream().forEach(System.out::println);
        }
    }

    private void executeSingleCommand(String commandString) throws UnknownCommandException, SerialPortException {
        if(commandString.contains("wait"))
        {
            int interval = Integer.valueOf(commandString.split(" ")[1]);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        chainCommand.execute(commandString.split(" ")[0], serialPort);
    }

    private void putNewMacro(String commandString) {
        if (macroMap.get(definingMacro) == null) {
            ArrayList<String> list = new ArrayList<>();
            list.add(commandString);
            macroMap.put(definingMacro, list);
        } else {
            macroMap.get(definingMacro).add(commandString);
        }
    }

    private void executeMacro(String commandString) throws UnknownCommandException, SerialPortException, InterruptedException {
        macroName = commandString.split("\\s+")[1];
        if (macroMap.containsKey(macroName)) {

            System.out.println("Started executing macro " + macroName);
            ArrayList<String> commandList = macroMap.get(macroName);
            Iterator<String> commandIterator = commandList.iterator();
            while (commandIterator.hasNext()) {
                executeSingleCommand(commandIterator.next());
                Thread.sleep(1000);
                executeSingleCommand("stop");
            }
        }
    }

    private void stopMacro() {
        System.out.println("Stopped defining macro");
        definingMacro = null;
    }

    private void startMacro(String commandString) {
        macroName = commandString.split("\\s+")[1];
        System.out.println("Started defining macro " + macroName);
        macroMap.put(macroName, new ArrayList<>());
        definingMacro = macroName;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() == 10) {
            try {
                byte[] response = serialPort.readBytes(10);
                System.out.println(response);
            } catch (SerialPortException e) {
                System.err.println("Error receiving bytes from port");
            }
        }

    }
}
