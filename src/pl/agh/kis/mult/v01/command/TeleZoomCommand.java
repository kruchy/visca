package pl.agh.kis.mult.v01.command;

/**
 * Created by Krzysiek on 2015-12-03.
 */
public class TeleZoomCommand extends ChainCommand {

    public TeleZoomCommand()
    {
        super("tele");
    }
    @Override
    public byte[] getCommand() {
        return new byte[]{(byte)0x81, (byte)0x01, (byte)0x04, (byte)0x07, (byte)0x02,tail};
    }
}
