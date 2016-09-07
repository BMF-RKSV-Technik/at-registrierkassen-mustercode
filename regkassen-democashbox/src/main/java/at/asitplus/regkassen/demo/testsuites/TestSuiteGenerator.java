/*
 * Copyright (C) 2015
 * A-SIT Plus GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.asitplus.regkassen.demo.testsuites;

import at.asitplus.regkassen.core.base.cashboxsimulation.CashBoxSimulation;
import at.asitplus.regkassen.verification.common.rpc.RKObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for loading and parsing the integrated test suites
 */
public class TestSuiteGenerator {
    public static List<CashBoxSimulation> getSimulationRuns() {
        try {
            //define the to-be-executed test suites
            List<String> testSuites = new ArrayList<>();
            testSuites.add("TESTSUITE_TEST_SZENARIO_1.json");
            testSuites.add("TESTSUITE_TEST_SZENARIO_2.json");
            testSuites.add("TESTSUITE_TEST_SZENARIO_3.json");
            testSuites.add("TESTSUITE_TEST_SZENARIO_4.json");
            testSuites.add("TESTSUITE_TEST_SZENARIO_5.json");
            testSuites.add("TESTSUITE_TEST_SZENARIO_6.json");
            testSuites.add("TESTSUITE_TEST_SZENARIO_7.json");
            testSuites.add("TESTSUITE_TEST_SZENARIO_8.json");
//            int index = 1;

            //prepare cashbox simulation files
            List<CashBoxSimulation> cashBoxSimulationList = new ArrayList<>();
            for (String testSuiteIdentifier : testSuites) {
                //load from file system
                InputStream inputStream = TestSuiteGenerator.class.getClassLoader().getResourceAsStream(testSuiteIdentifier);
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                IOUtils.copy(inputStream, bOut);
                inputStream.close();
                bOut.close();

                //parse JSON structure
                CashBoxSimulation cashBoxSimulation = RKObjectMapper.load(new String(bOut.toByteArray()),CashBoxSimulation.class);
                //check for missing receipt types
//                List<CashBoxInstruction> cashBoxInstructionList = cashBoxSimulation.getCashBoxInstructionList();
//                for (CashBoxInstruction cashBoxInstruction:cashBoxInstructionList) {
//                    if (cashBoxInstruction.getTypeOfReceipt()==null) {
//                        System.out.println("Index " + index);
//                        System.out.println(cashBoxInstruction.getReceiptIdentifier());
//
//                    }
//                }
//                index++;

                cashBoxSimulationList.add(cashBoxSimulation);
            }

            return cashBoxSimulationList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
