package me.bottdev.breezevelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;

@Plugin(
        id = "breezevelocity",
        name = "BreezeVelocity",
        version = "1.0"
)
public class BreezeVelocity {

    private final ProxyServer server;

    @Inject
    public BreezeVelocity(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        try {



        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public void registerDynamicServer(String name, String host, int port) {
        ServerInfo info = new ServerInfo(name, new InetSocketAddress(host, port));
        server.registerServer(info);

        System.out.println("Registered dynamic server: " + name);
    }

}
