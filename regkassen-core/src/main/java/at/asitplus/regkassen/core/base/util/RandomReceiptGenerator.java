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

import at.asitplus.regkassen.core.base.receiptdata.Item;
import at.asitplus.regkassen.core.base.receiptdata.RawReceiptData;
import at.asitplus.regkassen.core.base.receiptdata.TaxType;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;

/**
 * helper class for generating random receipts with different number of items/tax types, values
 */
public class RandomReceiptGenerator {

    public static List<RawReceiptData> generateRandomReceipts(int maxNumberOfReceipts) {
        List<RawReceiptData> rawReceipts = new ArrayList<>();
        for (int i = 0; i < maxNumberOfReceipts; i++) {
            RawReceiptData rawReceiptData = new RawReceiptData();
            List<Item> randomItems = generateRandomItems(1, 10, 0.1, 50.2);
            for (Item item : randomItems) {
                rawReceiptData.addItem(item);
            }
            rawReceipts.add(rawReceiptData);
        }
        return rawReceipts;
    }

    public static List<Item> generateRandomItems(int minNumberOfEntries, int maxNumberOfEntries, double minValue, double maxValue) {
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        List<Item> items = new ArrayList<>();
        int numberOfEntries = randomDataGenerator.nextInt(minNumberOfEntries, maxNumberOfEntries);
        TaxType[] taxTypes = TaxType.values();
        for (int i = 0; i < numberOfEntries; i++) {
            double value = Precision.round(randomDataGenerator.nextUniform(minValue, maxValue), 2);
            int taxTypeIndex = randomDataGenerator.nextInt(0, taxTypes.length - 1);
            TaxType taxType = taxTypes[taxTypeIndex];

            Item item = new Item();
            item.setValue(value);
            item.setTitel("Item " + i + " , TaxType: " + taxType);
            item.setTaxType(taxType);
            items.add(item);
        }
        return items;
    }
}
