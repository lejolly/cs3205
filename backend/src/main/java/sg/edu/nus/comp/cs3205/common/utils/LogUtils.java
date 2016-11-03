package sg.edu.nus.comp.cs3205.common.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.slf4j.LoggerFactory;

public class LogUtils {

    public static void configureLogger(String filename) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            loggerContext.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(LogUtils.class.getClassLoader().getResource(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
