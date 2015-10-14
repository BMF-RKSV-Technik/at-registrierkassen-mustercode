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

package at.asitplus.regkassen.core.base.receiptdata;

/**
 * Tax-type (Steuersatz) "Betrag-Satz-X" according to Detailspezifikation Abs 4
 */
public enum TaxType {
    SATZ_NORMAL("Satz-Normal"),
    SATZ_ERMAESSIGT_1("Satz-Ermaessigt-1"),
    SATZ_ERMAESSIGT_2("Satz-Ermaessigt-2"),
    SATZ_NULL("Satz-Null"),
    SATZ_BESONDERS("Satz-Besonders");

    protected String taxTypeString;

    TaxType(String taxTypeString) {
        this.taxTypeString = taxTypeString;
    }

    public String getTaxTypeString() {
        return taxTypeString;
    }
}
