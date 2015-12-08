package pl.agh.kis.multi.visca;

import jssc.SerialPort;
import jssc.SerialPortException;
import pl.agh.kis.multi.visca.command.*;

public class ViscaCtrl {
    private SerialPort serialPort = null;
    private ChainCommand chainCommand;

    public ViscaCtrl() {
        serialPort = new SerialPort("com3");
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.addEventListener(serialPortEvent -> {
                try {
                    String msg = serialPort.readHexString(serialPortEvent.getEventValue());
                    System.out.println("Serial received: " + msg);
                } catch (SerialPortException e) {
                    System.err.println("Error during reading serial port. " + e);
                }
            }, SerialPort.MASK_RXCHAR);
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
        chainCommand.setNext(new ZoomInCommand());
        chainCommand.setNext(new ZoomOutCommand());
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

        if(commandString.contains("close"))
        {
                System.exit(0);
        }
        else executeSingleCommand(commandString);

    }



    private void executeSingleCommand(String commandString) throws UnknownCommandException, SerialPortException {
               chainCommand.execute(commandString, serialPort);
    }


}
