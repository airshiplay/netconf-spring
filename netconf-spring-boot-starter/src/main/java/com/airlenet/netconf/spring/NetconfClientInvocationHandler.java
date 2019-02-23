package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.MultiNetconfDataSource;
import com.airlenet.netconf.datasource.NetconfPooledConnection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetconfClientInvocationHandler implements InvocationHandler {
    final MultiNetconfDataSource multiNetconfDataSource;

    public NetconfClientInvocationHandler(MultiNetconfDataSource multiNetconfDataSource) {
        this.multiNetconfDataSource = multiNetconfDataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        Object invoke = null;

        if (method.getParameters()[0].getType() == String.class) {
            String url = args[0].toString();
            String username = args[1].toString();
            String password = args[2].toString();

            Object[] connectionArgs = new Object[args.length - 3];
            System.arraycopy(args, 3, connectionArgs, 0, args.length - 3);
            NetconfPooledConnection connection = multiNetconfDataSource.getDataSource(url, username, password).getConnection();
            if (method.getReturnType() == NetconfPooledConnection.class) {
                return connection;
            }
            Class[] objects = Arrays.stream(method.getParameters()).skip(3).flatMap(parameter -> Stream.of(parameter.getType())).collect(Collectors.toList()).toArray(new Class[args.length - 3]);

            Method connectionMethod = NetconfPooledConnection.class.getMethod(method.getName(), objects);
            invoke = connectionMethod.invoke(connection, connectionArgs);
            connection.close();
        } else if (method.getParameters()[0].getType() == NetconfDevice.class) {
            NetconfDevice netconfDevice = (NetconfDevice) args[0];
            Object[] connectionArgs = new Object[args.length - 1];
            System.arraycopy(args, 1, connectionArgs, 0, args.length - 1);

            NetconfPooledConnection connection = multiNetconfDataSource.getDataSource(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword()).getConnection();

            if (method.getReturnType() == NetconfPooledConnection.class) {
                return connection;
            }

            Class[] objects = Arrays.stream(method.getParameters()).skip(1).flatMap(parameter -> Stream.of(parameter.getType())).collect(Collectors.toList()).toArray(new Class[args.length - 1]);

            Method connectionMethod = NetconfPooledConnection.class.getMethod(method.getName(), objects);
            invoke = connectionMethod.invoke(connection, new Object[]{connectionArgs});
            connection.close();
        }

        return invoke;
    }
}
