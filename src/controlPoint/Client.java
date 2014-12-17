package controlPoint;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;

public class Client extends ControlPoint implements NotifyListener
{

    public static void main(String[] args)
    {
        new Client();
    }

    public Client()
    {
        addNotifyListener(this);
        start();
        loop();
    }

    public void loop()
    {
        for (int n = 0; n < 10; n++)
        {
            System.out.println("Sending: hello " + n);
            echoToAll("hello " + n);
            try
            {
                Thread.currentThread().sleep(1000);
            }
            catch (Exception e)
            {
            }
        }
        System.exit(0);
    }

    private void echoToAll(String txt)
    {
        DeviceList devList = getDeviceList();
        System.out.println("Discovered devices: " + devList.size());

        Device dev = null;
        for (int n = 0; n < devList.size(); n++)
        {
            dev = devList.getDevice(n);
            // System.out.println("  Type: " +  dev.getDeviceType());
            if (dev.getDeviceType().equals("urn:schemas-upnp-org:device:echo:1"))
            {
                ServiceList services = dev.getServiceList();
                for (int m = 0; m < services.size(); m++)
                {
                    Service svc = services.getService(m);
                    if (svc.getServiceType().equals("urn:schemas-upnp-org:service:echo:1"))
                    {
                        echoToOne(svc, txt);
                    }
                }
            }
        }
    }

    private void echoToOne(Service svc, String txt)
    {
        Action echoAction = svc.getAction("Echo");
        Argument inArg = echoAction.getArgument("InText");
        inArg.setValue(txt);
        if (echoAction.postControlAction())
        {
            Argument arg = echoAction.getArgument("ReturnText");
            String value = arg.getValue();
            System.out.println(value);
        }
    }

    public void deviceNotifyReceived(SSDPPacket ssdpPacket)
    {
        // uncomment this to see all discovery packets
        // System.out.println("New SSDPPacket, all " + ssdpPacket);
    }
}

