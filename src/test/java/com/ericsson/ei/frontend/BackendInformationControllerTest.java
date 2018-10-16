/*
   Copyright 2017 Ericsson AB.
   For a full list of individual contributors, please see the commit history.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.ericsson.ei.frontend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BackendInformationControllerTest {

    private static final String BACKEND_INSTANCE_FILE_PATH = "src/test/resources/backendInstances/backendInstance.json";
    private static final String BACKEND_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/backendInstances.json";
    private static final String BACKEND_RESPONSE_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/expectedResponseInstances.json";
<<<<<<< HEAD

    private static final String FRONT_END_REST = "/backend";
=======
>>>>>>> 87609f7ef0801d02531c44d6ec1dc37dce79b4c3

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BackEndInstancesUtils utils;

    private JsonObject instance;
    private JsonArray instances;
    private JsonArray instancesWithActive;
    private List<BackEndInformation> information;

    @Before
    public void before() throws Exception {
        instance = new JsonParser().parse(new FileReader(BACKEND_INSTANCE_FILE_PATH)).getAsJsonObject();
        instances = new JsonParser().parse(new FileReader(BACKEND_INSTANCES_FILE_PATH)).getAsJsonArray();
        instancesWithActive = new JsonParser().parse(new FileReader(BACKEND_RESPONSE_INSTANCES_FILE_PATH)).getAsJsonArray();
        information = new ArrayList<>();
        for(JsonElement element : instances) {
            information.add(new ObjectMapper().readValue(element.toString(), BackEndInformation.class));
        }
    }

    @Test
    public void testGetInstances() throws Exception {
        when(utils.getBackEndsAsJsonArray()).thenReturn(instances);
<<<<<<< HEAD
        mockMvc.perform(MockMvcRequestBuilders.get(FRONT_END_REST)
=======
        mockMvc.perform(MockMvcRequestBuilders.get("/get-instances")
>>>>>>> 87609f7ef0801d02531c44d6ec1dc37dce79b4c3
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(instancesWithActive.toString()))
            .andReturn();
    }

    @Test
    public void testSwitchInstance() throws Exception {
        when(utils.getBackEndInformationList()).thenReturn(information);
<<<<<<< HEAD
        mockMvc.perform(MockMvcRequestBuilders.put(FRONT_END_REST)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(instancesWithActive.toString()))
            .andExpect(status().isOk())
=======
        mockMvc.perform(MockMvcRequestBuilders.post("/switch-backend")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(instancesWithActive.toString()))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testAddInstance() throws Exception {
        when(utils.checkIfInstanceAlreadyExist(any())).thenReturn(false);
        when(utils.getBackEndsAsJsonArray()).thenReturn(new JsonArray());
        mockMvc.perform(MockMvcRequestBuilders.post("/add-instances")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(instance.toString()))
            .andExpect(status().isOk())
>>>>>>> 87609f7ef0801d02531c44d6ec1dc37dce79b4c3
            .andReturn();
    }

    @Test
    public void testAddInstance() throws Exception {
        when(utils.checkIfInstanceAlreadyExist(any())).thenReturn(false);
        when(utils.getBackEndsAsJsonArray()).thenReturn(new JsonArray());
        mockMvc.perform(MockMvcRequestBuilders.post(FRONT_END_REST)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(instance.toString()))
<<<<<<< HEAD
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testDeleteInstance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(FRONT_END_REST)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(instance.toString()))
=======
>>>>>>> 87609f7ef0801d02531c44d6ec1dc37dce79b4c3
            .andExpect(status().isOk())
            .andReturn();
    }
}
