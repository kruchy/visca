package pl.agh.kis.multi.visca.command;

/**
 * Created by Krzysiek on 2015-12-03.
 */
public class ZoomInCommand extends ChainCommand {

    public ZoomInCommand()
    {
        super("zoomIn");
    }
    @Override
    public byte[] getCommand() {
        return new byte[]{(byte)0x81, (byte)0x01, (byte)0x04, (byte)0x07, (byte)0x02,tail};
    }
}
