/* 
 * @(#)IpAddressAndPrefixLength.java        1.0 09/09/17
 *
 * This file has been auto-generated by JNC, the
 * Java output format plug-in of pyang.
 * Origin: module "tailf-common", revision: "2017-01-26".
 */

package com.airlenet.yang.model.tailfCommon;

import com.tailf.jnc.YangException;
import com.tailf.jnc.YangUnion;

/**
 * This class represents an element from 
 * the namespace 
 * generated to "/Users/lig/Documents/workspace/play-netconf/yang-model/src/main/java/com.airlenet.yang.model/tailfCommon/ip-address-and-prefix-length"
 * <p>
 * See line 689 in
 * /Users/lig/Documents/workspace/play-netconf/yang-model/src/main/yang/module/tailf/tailf-common.yang
 *
 * @version 1.0 2017-09-09
 * @author Auto Generated
 */
public class IpAddressAndPrefixLength extends YangUnion {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for IpAddressAndPrefixLength object from a string.
     * @param value Value to construct the IpAddressAndPrefixLength from.
     */
    public IpAddressAndPrefixLength(String value) throws YangException {
        super(value,
            new String[] {
                "com.airlenet.yang.model.tailfCommon.Ipv4AddressAndPrefixLength",
                "com.airlenet.yang.model.tailfCommon.Ipv6AddressAndPrefixLength",
            }
        );
        check();
    }

    /**
     * Sets the value using a string value.
     * @param value The value to set.
     */
    public void setValue(String value) throws YangException {
        super.setValue(value);
        check();
    }

    /**
     * Checks all restrictions (if any).
     */
    public void check() throws YangException {
    }

}
