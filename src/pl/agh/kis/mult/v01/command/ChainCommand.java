package pl.agh.kis.mult.v01.command;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.Arrays;


/**
 * Created by Krzysiek on 2015-11-27.
 */
public abstract class ChainCommand {

    private String commandName;

    protected ChainCommand next;
    public static byte currentAddress = (byte) 0x81;
    protected boolean daisyChaining = false;

    protected byte tail = (byte) 0xFF;
    protected static byte[] heads = {(byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87};

    public boolean isDaisyChaining() {
        return daisyChaining;
    }

    public void setDaisyChaining(boolean daisyChaining) {
        this.daisyChaining = daisyChaining;
    }

    public ChainCommand(String commandName) {
        this.commandName = commandName;
    }

    public ChainCommand getNext() {
        return next;
    }

    public String secondParam;

    public void setNext(ChainCommand next) {
        if (this.next == null)
            this.next = next;
        else
            this.next.setNext(next);
    }

    public static int waitInterval = 1000;

    public abstract byte[] getCommand();

    public static void setCam(int number)
    {
        if(number > 0 && number < heads.length)
        {
            currentAddress = heads[number-1];
        }
    }

    public void execute(String command, SerialPort serialPort) throws UnknownCommandException, SerialPortException {

        if (serialPort.isOpened() && command.equals(commandName)) {
            {


            if(isDaisyChaining())
            {
                byte[] template = getCommand();
                for(int i = 0 ; i < heads.length ; i++)
                {
                    template[0] = heads[i];
                    serialPort.writeBytes(template);
                }
            }
                byte[] template = getCommand();
                template[0] = currentAddress;
                serialPort.writeBytes(template);
            }
        } else if (!serialPort.isOpened() && command.equals(commandName)) {

            System.out.println(command);
        } else {
            if (next != null) {
                next.execute(command, serialPort);
            } else {
                throw new UnknownCommandException();
            }
        }

    }

    public String getCommandName() {
        return commandName;
    }

}
