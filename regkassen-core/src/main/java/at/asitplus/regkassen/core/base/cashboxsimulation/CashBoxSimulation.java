/*
 * Copyright (C) 2015, 2016
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

package at.asitplus.regkassen.core.base.cashboxsimulation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all data required to initialize a simplified RKSV-enabled cashbox
 * This code is not relevant for a productive cashbox since it is used to setup simulation runs that have a predefined
 * list of to-be-created receipts
 */
public class CashBoxSimulation {
    //cash-box-id to be used for the simulation run by the cashbox
    protected String cashBoxId;
    //AES key for the cashbox
    protected String base64AesKey;
    //company ID (Ordnungsbegriff): Steuernummer, UID or GLN: examples in this order e.g. S:081953820, U:ATU57780814, G:9110005102096
    protected String companyID;
    //label for simulation run, that will be used by the verification tools to uniquely identify results of simulation runs
    protected String simulationRunLabel;
    //number of signature devices that should be used for the test-run
    protected int numberOfSignatureDevices;

    protected List<CashBoxInstruction> cashBoxInstructionList = new ArrayList<CashBoxInstruction>();

    public String getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(final String cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public String getBase64AesKey() {
        return base64AesKey;
    }

    public void setBase64AesKey(final String base64AesKey) {
        this.base64AesKey = base64AesKey;
    }

    public List<CashBoxInstruction> getCashBoxInstructionList() {
        return cashBoxInstructionList;
    }

    public void setCashBoxInstructionList(final List<CashBoxInstruction> cashBoxInstructionList) {
        this.cashBoxInstructionList = cashBoxInstructionList;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(final String companyID) {
        this.companyID = companyID;
    }

    public int getNumberOfSignatureDevices() {
        return numberOfSignatureDevices;
    }

    public void setNumberOfSignatureDevices(final int numberOfSignatureDevices) {
        this.numberOfSignatureDevices = numberOfSignatureDevices;
    }

    public String getSimulationRunLabel() {
        return simulationRunLabel;
    }

    public void setSimulationRunLabel(final String simulationRunLabel) {
        this.simulationRunLabel = simulationRunLabel;
    }
}
