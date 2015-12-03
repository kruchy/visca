package pl.agh.kis.mult.v01.command;

/**
 * Created by Krzysiek on 2015-12-03.
 */
public class WideZoomCommand extends ChainCommand {
    public WideZoomCommand() {
        super("wide");
    }

    @Override
    public byte[] getCommand() {
        return new byte[]{(byte)0x81,(byte)0x01,(byte)0x04,(byte)0x07,(byte)0x03,tail};
    }
}
