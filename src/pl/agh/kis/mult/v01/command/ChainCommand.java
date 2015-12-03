package pl.agh.kis.mult.v01.command;

import jssc.SerialPort;
import jssc.SerialPortException;


/**
 * Created by Krzysiek on 2015-11-27.
 */
public abstract class ChainCommand {

    private String commandName;

    protected ChainCommand next;

    protected boolean daisyChaining = false;

    protected byte tail = (byte) 0xFF;
    protected byte[] heads = {(byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87};

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

    public void setNext(ChainCommand next) {
        if (this.next == null)
            this.next = next;
        else
            this.next.setNext(next);
    }

    public abstract byte[] getCommand();

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
                serialPort.writeBytes(getCommand());
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
