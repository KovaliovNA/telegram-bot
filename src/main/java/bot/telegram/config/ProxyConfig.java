package bot.telegram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@Configuration
public class ProxyConfig {

    @Value("${bot.proxy.enable}")
    private boolean enable;
    @Value("${bot.proxy.host}")
    private String host;
    @Value("${bot.proxy.port}")
    private Integer port;
    @Value("${bot.proxy.user}")
    private String user;
    @Value("${bot.proxy.pass}")
    private String pass;

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        ApiContextInitializer.init();
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        return enable ? activateProxy(botOptions) : botOptions;
    }

    private DefaultBotOptions activateProxy(DefaultBotOptions botOptions) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass.toCharArray());
            }
        });

        botOptions.setProxyHost(host);
        botOptions.setProxyPort(port);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        return botOptions;
    }
}
