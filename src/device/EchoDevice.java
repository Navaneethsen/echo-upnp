package device;

// import service.*;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;

import java.io.File;

public class EchoDevice extends Device implements ActionListener
{
    private final static String DESCRIPTION_FILE_NAME = "description/description.xml";

    private StateVariable textVar;

    public static void main(String[] args)
    {
        EchoDevice dev = null;
        try
        {
            dev = new EchoDevice();
        }
        catch (InvalidDescriptionException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Running...");

        // This is a hack to keep the server alive.
        // If the device had a GUI, the AWT/Swing would keep it alive
        Object keepAlive = new Object();
        synchronized (keepAlive)
        {
            try
            {
                // Wait for a "notify" from another thread
                // that will never be sent.
                // So we stay alive for ever
                keepAlive.wait();
            }
            catch (java.lang.InterruptedException e)
            {
                // do nothing
            }
        }
    }

    public EchoDevice() throws InvalidDescriptionException
    {
        super(new File(DESCRIPTION_FILE_NAME));

        Action getEchoAction = getAction("Echo");
        getEchoAction.setActionListener(this);

        ServiceList serviceList = getServiceList();
        // there should only be an echo service
        Service service = serviceList.getService(0);
        // service.setQueryListener(this);

        // we don't use this
        textVar = getStateVariable("Text");

        setLeaseTime(60);
        start();
    }

    public String echo(String txt)
    {
        return ("Echo from \"" + getFriendlyName() + "\": " + txt);
    }

    public boolean actionControlReceived(Action action)
    {
        String actionName = action.getName();
        if (actionName.equals("Echo"))
        {
            System.out.println("Echo action control");

            Argument inArg = action.getArgument("InText");
            String txt = inArg.getValue();

            Argument returnArg = action.getArgument("ReturnText");
            String returnTxt = echo(txt);
            returnArg.setValue(returnTxt);
            return true;
        }
        return false;
    }
}

