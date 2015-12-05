package pl.agh.kis.mult.v01.command;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Created by Krzysiek on 2015-12-04.
 */
public class WaitCommand extends ChainCommand {
    public WaitCommand() {
        super("wait");
    }

    @Override
    public byte[] getCommand() {
        return new byte[]{(byte)0x81,(byte)0x01,(byte)0x06,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x03,tail};
    }

}
