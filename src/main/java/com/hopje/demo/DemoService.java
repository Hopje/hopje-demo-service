package com.hopje.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@SpringBootApplication
@RestController
@RequestMapping("/")
@Api(value = "demoservice", description = "Operations for demo & test purposes")
public class DemoService {

    @Autowired
    private Config config;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ApiOperation(value = "Root of service, showing configuration and hostname")
    public String home() {
        StringBuffer result = new StringBuffer("<h1>Root of hopje demo service</h1><br/>");
        try {
            result.append("<br/>This Inet host: " + InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            result.append("<br/>This Inet host: Unknown");
        }
        result.append("<br/>This env HOSTNAME: " + System.getenv().getOrDefault("HOSTNAME", "Unknown"));
        result.append("<br/>Configured service delay in milliseconds: " + this.config.getServiceDelayMs());
        result.append("<br/>Service operations:<br/>");
        result.append("/demo/echo<br/>");
        result.append("/demo/mul/{num1}/{num2}<br/>");
        result.append("<br/>");
        return result.toString();
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "/demo/echo")
    @ApiOperation(value = "Echo service. Returns the input")
    public @ResponseBody String echo(@RequestBody String input) {
        System.out.println("echo " + input + " on " + new Date());
        return input;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "demo/mul/{num1}/{num2}", produces = "text/plain")
    @ApiOperation(value = "Multiply service. Returns num1*num2 with configurable service delay.")
    public String mul(@PathVariable("num1") Integer num1, @PathVariable("num2") Integer num2)
            throws InterruptedException, UnknownHostException {
        String result = String.format("host %s says: %d x %d = %d", InetAddress.getLocalHost().getHostName(), num1,
                num2, (num1 * num2));
        System.out.println(result);
        long serviceDelay = this.config.getServiceDelayMs();
        Thread.sleep(serviceDelay);
        return result;
    }

}
