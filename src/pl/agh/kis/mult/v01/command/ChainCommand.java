package pl.agh.kis.mult.v01.command;

import jssc.SerialPort;
import jssc.SerialPortException;


/**
 * Created by Krzysiek on 2015-11-27.
 */
public abstract class ChainCommand {

    private String commandName;

    protected ChainCommand next;

    protected byte head = (byte) 0x88;
    protected byte tail = (byte) 0xFF;

    public ChainCommand(String commandName)
    {
        this.commandName = commandName;
    }

    public ChainCommand getNext()
    {
        return next;
    }

    public void setNext(ChainCommand next)
    {
        if(this.next == null)
          this.next = next;
        else
            this.next.setNext(next);
    }

    public abstract byte[] getCommand();

    public void execute(String command, SerialPort serialPort) throws UnknownCommandException, SerialPortException
    {
        if(serialPort.isOpened() && command.equals(commandName)) {
            {
                serialPort.writeBytes(getCommand());
            }
        }
        else if(!serialPort.isOpened() && command.equals(commandName))
        {

            System.out.println(command);
        }
        else
        {
            if(next != null)
            {
                next.execute(command,serialPort);
            }
            else
            {
                throw new UnknownCommandException();
            }
        }

    }

    public String getCommandName() {
        return commandName;
    }

}
