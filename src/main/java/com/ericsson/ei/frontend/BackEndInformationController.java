/*
   Copyright 2018 Ericsson AB.
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

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BackEndInformationController {

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    @RequestMapping(value = "/get-instances", method = RequestMethod.GET)
    public ResponseEntity<String> getInstances(Model model) {
        return new ResponseEntity<>(backEndInstancesUtils.getInstances().toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/switch-backend", method = RequestMethod.POST)
    public ResponseEntity<String> switchBackEndInstance(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            backEndInstancesUtils.setInstances(new JsonParser().parse(body).getAsJsonArray());
            backEndInstancesUtils.writeIntoFile();
            for (BackEndInformation backEndInformation : backEndInstancesUtils.getInformation()) {
                if (backEndInformation.isActive()) {
                    backEndInstancesUtils.setBackEndProperties(backEndInformation);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/switch-backend", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBackEndInstance(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            backEndInstancesUtils.setInstances(new JsonParser().parse(body).getAsJsonArray());
            backEndInstancesUtils.writeIntoFile();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/add-instances", method = RequestMethod.POST)
    public ResponseEntity<String> addInstanceInformation(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonObject instance = new JsonParser().parse(body).getAsJsonObject();
            if (!backEndInstancesUtils.checkIfInstanceAlreadyExist(instance)) {
                backEndInstancesUtils.getInstances().add(instance);
                backEndInstancesUtils.writeIntoFile();
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Instance already exist", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/switchBackend", method = RequestMethod.POST)
    public ResponseEntity<String> switchBackEndInstanceByMainPage(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            List<BackEndInformation> info = new ArrayList<>();
            for (BackEndInformation backEndInformation : backEndInstancesUtils.getInformation()) {
                backEndInformation.setActive(false);
                if (backEndInformation.getName().equals(body)) {
                    backEndInstancesUtils.setBackEndProperties(backEndInformation);
                    backEndInformation.setActive(true);
                }
                info.add(backEndInformation);
            }
            backEndInstancesUtils.setInformation(info);
            JsonArray result = (JsonArray) new Gson().toJsonTree(backEndInstancesUtils.getInformation(), new TypeToken<List<BackEndInformation>>() {
            }.getType());
            backEndInstancesUtils.setInstances(result);
            backEndInstancesUtils.writeIntoFile();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
