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

import org.apache.commons.math3.util.Precision;

/**
 * Simple class that represents an item on a receipt. An item consists of a title, a value and the taxtype
 */
public class Item {
    //title of the item, e.g. Schnitzel
    protected String titel;

    //net value in € of the item, 2 decimal places
    protected double value;

    /**
     * REF TO SPECIFICATION: Detailspezifikation/Abs 4
     */
    protected TaxType taxType = TaxType.SATZ_NORMAL;

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public double getValue() {
        return Precision.round(value, 2);
    }

    public void setValue(double value) {
        this.value = Precision.round(value, 2);
    }

    public TaxType getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    @Override
    public String toString() {
        String position = "Item: " + titel + "\n";
        position += "Value (€): " + value + "\n";
        position += "Tax Type: " + taxType + "\n";
        return position;
    }
}
