package com.airlenet.netconf.datasource;

public class NetconfConnectionHolder {
    protected long connectionId;
    protected boolean transaction;
    protected final NetconfConnection conn;
    private long useCount = 0;
    protected final NetconfDataSource dataSource;
    protected String stream;
    protected long connectTimeMillis;

    public NetconfConnectionHolder(NetconfDataSource dataSource, NetconfConnection conn, long connectionId) {
        this.dataSource = dataSource;
        this.conn = conn;
        this.connectionId = connectionId;
    }

    public long getUseCount() {
        return useCount;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public String getSessionName() {
        return conn.getSessionName();
    }

    public long getSessionId() {
        return conn.getSessionId();
    }

    public void incrementUseCount() {
        useCount++;
    }


    public long getInputDataInteractionTimeMillis() {
        return conn.inputTimeMillis;
    }


    public long getInputDataInteractionCount() {
        return conn.inputCount;
    }


    public long getOutputDataInteractionTimeMillis() {
        return conn.outputTimeMillis;
    }

    public String getInputDataInteractionMessage() {
        return conn.inputMessage;
    }

    public long getOutputDataInteractionCout() {
        return conn.outputCount;
    }

    public String getOutputDataInteractionMessage() {
        return conn.outputMessage;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public void recycle() {

    }
}
