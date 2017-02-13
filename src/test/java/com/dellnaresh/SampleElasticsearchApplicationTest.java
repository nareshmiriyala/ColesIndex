package com.dellnaresh;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.OutputCapture;
import org.springframework.core.NestedCheckedException;

import java.net.ConnectException;

import static org.junit.Assert.*;

/**
 * Created by nareshm on 13/12/2015.
 */
public class SampleElasticsearchApplicationTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testDefaultSettings() throws Exception {
        try {
            new SpringApplicationBuilder(SampleElasticsearchApplication.class)
                    .properties(
                            "spring.data.elasticsearch.properties.path.data:target/data",
                            "spring.data.elasticsearch.properties.path.logs:target/logs")
                    .run();
        }
        catch (IllegalStateException ex) {
            if (serverNotRunning(ex)) {
                return;
            }
        }
        String output = this.outputCapture.toString();
        assertTrue("Wrong output: " + output,
                output.contains("firstName='Alice', lastName='Smith'"));
    }

    private boolean serverNotRunning(IllegalStateException ex) {
        @SuppressWarnings("serial")
        NestedCheckedException nested = new NestedCheckedException("failed", ex) {
        };
        if (nested.contains(ConnectException.class)) {
            Throwable root = nested.getRootCause();
            if (root.getMessage().contains("Connection refused")) {
                return true;
            }
        }
        return false;
    }
}