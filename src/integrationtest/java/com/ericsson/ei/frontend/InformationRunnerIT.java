package com.ericsson.ei.frontend;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/integrationtest/resources/features/information.feature", glue = {
        "com.ericsson.ei.frontend" }, plugin = {
                "html:target/cucumber-reports/InformationRunnerIT" })
public class InformationRunnerIT {

}
