package com.jzel.toughvault.config;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import lombok.Data;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ssh")
@Data
public class SshConfig {

  private String privateKey;
  private String publicKey;
  private String privateKeyBase64;
  private SshSessionFactory sessionFactory;
  private TransportConfigCallback transportConfigCallback;

  @PostConstruct
  void init() {
    privateKey = new String(Base64.getDecoder().decode(privateKeyBase64));
    sessionFactory = new JschConfigSessionFactory() {
      @Override
      protected void configure(OpenSshConfig.Host host, Session session) {
        session.setConfig("StrictHostKeyChecking", "no");
      }

      @Override
      protected JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException {
        JSch jsch = super.getJSch(hc, fs);
        jsch.removeAllIdentity();
        jsch.addIdentity("TouGH-Vault-SSH", privateKey.getBytes(), publicKey.getBytes(),
            EMPTY.getBytes());
        return jsch;
      }
    };
    transportConfigCallback = transport -> ((SshTransport) transport).setSshSessionFactory(sessionFactory);
  }
}
