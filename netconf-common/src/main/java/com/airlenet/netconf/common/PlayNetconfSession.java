package com.airlenet.netconf.common;

import com.tailf.jnc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Created by airlenet on 17/8/24.
 * @author airlenet
 */
public class PlayNetconfSession {
    private NetconfSession netconfSession;
    private final PlayNetconfDevice playNetconfDevic;
    private final PlayNotification notification;
    private final boolean resume ;
    private static final Logger logger = LoggerFactory.getLogger(PlayNetconfSession.class);
    public PlayNetconfSession(PlayNetconfDevice playNetconfDevice, NetconfSession defaultPlaySession,PlayNotification notification,boolean resume) {
        this.netconfSession = defaultPlaySession;
        this.playNetconfDevic =playNetconfDevice;
        this.notification = notification;
        this.resume =resume;
        this.receiveNotification(netconfSession);
    }

    public void receiveNotification(NetconfSession netconfSession){
        this.netconfSession =netconfSession;
        if(notification != null && notification.getStream()!=null){
            new Thread(){
                @Override
                public void run() {
                    while (true){
                        if(PlayNetconfSession.this.netconfSession.getCapabilities().hasNotification()){
                            try {
                                PlayNetconfSession.this.netconfSession.receiveNotification();
                                continue;
                            }catch (SessionClosedException e) {
                                logger.error("device " + playNetconfDevic.getMgmt_ip()+" resume:"+ resume + notification.getStream() +" receive notification failed ",e);
                                if(resume){//关闭session然后重建
                                    try {
                                        playNetconfDevic.closeSession(notification.getStream());
                                    } catch (Exception ex){
                                        logger.error("device "+playNetconfDevic.getMgmt_ip()+" close session failed for rebuild session",e);
                                    }
                                    notification.resume();
                                } else {
                                    playNetconfDevic.closeNetconfSession(notification.getStream());
                                }
                                break;
                            }catch (IOException e) {
                                logger.error("device "+ playNetconfDevic.getMgmt_ip()+" resume:"+ resume + notification.getStream() +" receive notification failed ",e);
                                if(resume){
                                    notification.resume();
                                } else {
                                    playNetconfDevic.closeNetconfSession(notification.getStream());
                                }
                                break;
                            } catch (JNCException e) {
                                logger.error("device " + playNetconfDevic.getMgmt_ip()+" resume:"+ resume + notification.getStream() +" receive notification failed ",e);
                                if(resume){
                                    notification.resume();
                                } else {
                                    playNetconfDevic.closeNetconfSession(notification.getStream());
                                }
                                break;
                            }catch (Exception e) {
                                logger.error("device " + playNetconfDevic.getMgmt_ip()+" resume:"+ resume + notification.getStream() +" receive notification failed ",e);
                                if(e instanceof SAXException){
                                    continue;
                                }
                                if(resume){
                                    notification.resume();
                                } else {
                                    playNetconfDevic.closeNetconfSession(notification.getStream());
                                }
                                break;
                            }
                        }
                    }
                }
            }.start();
        }
    }
    /**
     * 添加监听数据流
     * @param stream
     * @throws IOException
     * @throws JNCException
     */
    public void createSubscription(String stream) throws IOException, JNCException {
        this.getNetconfSession().createSubscription(stream);
    }
    public boolean isOpenTransaction() {
        return playNetconfDevic.isOpenTransaction();
    }

    public boolean isCandidate() {
        return netconfSession.hasCapability(Capabilities.CANDIDATE_CAPABILITY);
    }

    public boolean isConfirmedCommit() {
        return this.netconfSession.hasCapability(Capabilities.CONFIRMED_COMMIT_CAPABILITY);
    }
    public boolean isWritableRunning(){
        return this.netconfSession.hasCapability(Capabilities.WRITABLE_RUNNING_CAPABILITY);
    }
    public long getSessionId(){
        return this.netconfSession.sessionId;
    }
    public void editConfig(Element configTree) throws IOException, JNCException {
        if (isOpenTransaction()) {
            if (isCandidate()) {
                this.netconfSession.editConfig(NetconfSession.CANDIDATE, configTree);
            } else {
                netconfSession.editConfig(configTree);
            }
        } else {
            if (isCandidate()) {
                try {
                    netconfSession.discardChanges();//现将 上次没有提交的配置 还原
                    netconfSession.lock(NetconfSession.CANDIDATE);
                    netconfSession.copyConfig(NetconfSession.RUNNING, NetconfSession.CANDIDATE);
                    this.netconfSession.editConfig(NetconfSession.CANDIDATE, configTree);
                    if (isConfirmedCommit()) {
                        netconfSession.confirmedCommit(60);// candidates are now updated 1分钟内没有确认 则还原配置
                    }
                    netconfSession.commit();//now commit them 确认提交
                } finally {
                    netconfSession.unlock(NetconfSession.CANDIDATE);
                }
            } else {
                netconfSession.editConfig(configTree);
            }
        }
    }

    public NodeSet get() throws IOException, JNCException {
        return this.netconfSession.get();
    }

    public NodeSet get(String xpath) throws IOException, JNCException {
        return this.netconfSession.get(xpath);
    }
    public NodeSet get(Element element) throws IOException, JNCException {
        return this.netconfSession.get(element);
    }
    public NodeSet getConfig(String xpath) throws IOException,JNCException{
        return  this.netconfSession.getConfig(xpath);
    }

    public NodeSet getConfig(Element element) throws IOException,JNCException{
        return  this.netconfSession.getConfig(element);
    }
    public NodeSet getConfig() throws IOException,JNCException{
        return  this.netconfSession.getConfig();
    }

    public NodeSet callRpc(Element element) throws IOException, JNCException {
        return this.netconfSession.callRpc(element);
    }

    public NetconfSession getNetconfSession(){
        return this.netconfSession;
    }

    public void addNetconfSessionListenerList(PlayNetconfListener listener) {
        notification.addListenerList(listener);
    }

    public PlayNotification getNotification() {
        return notification;
    }

}
