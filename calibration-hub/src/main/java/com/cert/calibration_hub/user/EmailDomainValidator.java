package com.cert.calibration_hub.user;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class EmailDomainValidator {

    public static boolean domainAcceptsMail(String email) {
        try {
            String domaine = email.substring(email.indexOf('@') + 1);

            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("java.naming.provider.url", "dns:");

            InitialDirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domaine, new String[]{"MX"});
            Attribute mx = attrs.get("MX");

            return mx != null && mx.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}