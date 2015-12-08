package pl.agh.kis.multi.visca.command;

/**
 * Created by Krzysiek on 2015-11-28.
 */
public class HomeCommand extends ChainCommand {

    public HomeCommand()
    {
        super("home");
    }

    @Override
    public byte[] getCommand() {
        return new byte[]{(byte)0x81,(byte)0x01,(byte)0x06,(byte)0x04,tail};
    }
}
