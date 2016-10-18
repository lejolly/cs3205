package sg.edu.nus.comp.cs3205.c3.sessions;

import io.netty.channel.Channel;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;

import java.util.HashMap;

public class C3SessionManager extends AbstractManager {

    private HashMap<Channel, String> ids;

    public C3SessionManager() {

    }

}
