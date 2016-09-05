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

package at.asitplus.regkassen.core.base.receiptdata;

/**
 * Simple representation of a receipt, only the summed up TAX-SET values are modeled
 */
public class SimplifiedReceipt {
    protected Double taxSetNormal;
    protected Double taxSetErmaessigt1;
    protected Double taxSetErmaessigt2;
    protected Double taxSetNull;
    protected Double taxSetBesonders;

    public Double getTaxSetNormal() {
        return taxSetNormal;
    }

    public void setTaxSetNormal(Double taxSetNormal) {
        this.taxSetNormal = taxSetNormal;
    }

    public Double getTaxSetErmaessigt1() {
        return taxSetErmaessigt1;
    }

    public void setTaxSetErmaessigt1(Double taxSetErmaessigt1) {
        this.taxSetErmaessigt1 = taxSetErmaessigt1;
    }

    public Double getTaxSetErmaessigt2() {
        return taxSetErmaessigt2;
    }

    public void setTaxSetErmaessigt2(Double taxSetErmaessigt2) {
        this.taxSetErmaessigt2 = taxSetErmaessigt2;
    }

    public Double getTaxSetNull() {
        return taxSetNull;
    }

    public void setTaxSetNull(Double taxSetNull) {
        this.taxSetNull = taxSetNull;
    }

    public Double getTaxSetBesonders() {
        return taxSetBesonders;
    }

    public void setTaxSetBesonders(Double taxSetBesonders) {
        this.taxSetBesonders = taxSetBesonders;
    }
}
