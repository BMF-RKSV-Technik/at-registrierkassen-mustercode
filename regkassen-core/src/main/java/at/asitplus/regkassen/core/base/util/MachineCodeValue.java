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

package at.asitplus.regkassen.core.base.util;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public enum MachineCodeValue {
    RK_SUITE(0),
    CASHBOX_ID(1),                          //Kassen-ID
    RECEIPT_IDENTIFIER(2),                  //Belegnummer
    RECEIPT_DATE_AND_TIME(3),                //Beleg-Datum-Uhrzeit
    SUM_TAX_SET_NORMAL(4),                  //Betrag-Satz-Normal
    SUM_TAX_SET_ERMAESSIGT1(5),             //Betrag-Satz-Ermaessigt-1
    SUM_TAX_SET_ERMAESSIGT2(6),             //Betrag-Satz-Ermaessigt-2
    SUM_TAX_SET_NULL(7),                    //Betrag-Satz-Null
    SUM_TAX_SET_BESONDERS(8),               //Betrag-Satz-Besonders
    ENCRYPTED_TURN_OVER_VALUE(9),           //Stand-Umsatz-Zaehler-AES256-ICM
    CERTIFICATE_SERIAL_NUMBER(10),           //Zertifikat-Seriennummer
    CHAINING_VALUE_PREVIOUS_RECEIPT(11),    //Sig-Voriger-Beleg
    SIGNATURE_VALUE(12);                    //Signatur

    protected int index;

    public int getIndex() {
        return index;
    }

    MachineCodeValue(int index) {
        this.index = index;
    }


    @SerializedName("Kassen-ID")
    protected String cashBoxID;

    @SerializedName("Belegnummer")
    protected long receiptIdentifier;

    @SerializedName("Beleg-Datum-Uhrzeit")
    protected Date receiptDateAndTime;

    @SerializedName("Betrag-Satz-Normal")
    protected double sumTaxSetNormal;

    @SerializedName("Betrag-Satz-Ermaessigt-1")
    protected double sumTaxSetErmaessigt1;

    @SerializedName("Betrag-Satz-Ermaessigt-2")
    protected double sumTaxSetErmaessigt2;

    @SerializedName("Betrag-Satz-Null")
    protected double sumTaxSetNull;

    @SerializedName("Betrag-Satz-Besonders")
    protected double sumTaxSetBesonders;

    @SerializedName("Stand-Umsatz-Zaehler-AES256-ICM")
    protected String encryptedTurnoverValue;

    @SerializedName("Zertifikat-Seriennummer")
    protected String signatureCertificateSerialNumber;

    @SerializedName("Sig-Voriger-Beleg")
    protected String signatureValuePreviousReceipt;
}
